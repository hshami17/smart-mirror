import os
import argparse
import json
import requests
import sys
from flask import Flask, render_template, request, session, redirect
from flask_socketio import SocketIO, emit
import socket
import xml.etree.ElementTree as ET
from flask import jsonify
import spotipy
from spotipy.oauth2 import SpotifyOAuth
import ast
import qrcode
from bs4 import BeautifulSoup
from pythonfitbit import fitbit
from datetime import datetime
import netifaces as ni

app = Flask(__name__)
socketio = SocketIO(app)

host = ""
port = 0
configData = []
path = ''
hue_bridge_ip = ''

spotify = ""
scope = "user-read-currently-playing"

fitbit_token_expiry = datetime.now().timestamp()

#For debugging errors
app.config.update(
    PROPAGATE_EXCEPTIONS = True
)

@socketio.on('connect', namespace='/')
def makeConnection():
    print("CONNECTED")
    # print('Running on: ' + socket.gethostbyname(socket.gethostname()) + ':8080')
    # TODO: add logic if no config XML exists yet
    emit('start', configData)

def setConfigData():
    """
    Set the global configData using the current config XML
    """
    print ('CONFIG DATA SET')
    global configData
    configData = parseXml()

def getModuleInfo(name):
    for module in configData:
        if module['name'] == name:
            return module

def parseXml():
    data = []
    tree = ET.parse(path)
    root = tree.getroot()
    for child in root:
        moduleData = {'name': child.attrib['name']}
        for field in child:
            if field.text:
                moduleData[field.tag] = field.text
            else:
                moduleData[field.tag] = ''
        data.append(moduleData)
    return data

def createXML():
    configuration_tag = ET.Element('configuration')
    for module in configData:
        module_tag = ET.SubElement(configuration_tag,'module',name=module['name'])
        for field, value in module.items():
            if field != 'name':
                field = ET.SubElement(module_tag, field)
                if not value:
                    # TODO: need this so an open tag is created, investigate further
                    field.text=' '
                else:
                    field.text = value

    tree = ET.ElementTree(configuration_tag)
    tree.write(path)


@app.route('/')
def index():

    name = 'clock'
    position = 'topright'

    return render_template('home.html', name=name, position=position)

def refreshFitbitToken(token_dict):
    print("Refreshing Fitbit tokens..")
    fitbit_info = getModuleInfo('fitbit')
    fitbit_info['access-token'] = token_dict['access_token']
    fitbit_info['refresh-token'] = token_dict['refresh_token']
    global fitbit_token_expiry 
    fitbit_token_expiry = token_dict['expires_at']
    createXML()
    
@app.route('/fitbit-data')
def getFitbitData():
    fitbit_info = getModuleInfo('fitbit')

    global fitbit_token_expiry
    # Using python-fitbit module:  https://python-fitbit.readthedocs.io/en/latest/
    authd_client = fitbit.Fitbit(fitbit_info['client-id'], fitbit_info['client-secret'],
                                 access_token=fitbit_info['access-token'],
                                 refresh_token=fitbit_info['refresh-token'],
                                 refresh_cb=refreshFitbitToken,
                                 expires_at=fitbit_token_expiry)

    fitbit_data = {}
    fitbit_data['activity'] = authd_client.activities()
    fitbit_data['body'] = authd_client.body()

    return jsonify(fitbit_data)

def spotifyEstablishToken():
    spotifyInfo = getModuleInfo('spotify')

    global spotify
    spotify = spotipy.Spotify(auth_manager=SpotifyOAuth(
        client_id=spotifyInfo['client-id'],
        client_secret=spotifyInfo['client-secret'],
        redirect_uri="http://localhost:9090",
        cache_path=os.getenv('HOME') + "/.mirror/.spotify-cache",
        scope=scope)
    )
    print ("Spotify successfully authenticated")

# @app.route('/spotify-callback')
# def spotifyCallback():
#     return 'Spotify Authenticated!'

@app.route('/spotify-current-track')
def spotifyCurrentTrack():
    global spotify
    if spotify == "":
        spotifyEstablishToken()

    currently_playing_track = spotify.current_user_playing_track()

    if currently_playing_track:
        return jsonify(currently_playing_track)
    else:
        return jsonify({})


@socketio.on('addModule', namespace='/')
def addModule(formData):
    #print(configData)

    # Establish spotify token if not already exists.
    if formData.get('name') == 'spotify' and not spotify:
        spotifyEstablishToken()

    global configData
    # Replace module data set with new data set
    configData = [formData if x['name'] == formData['name'] else x for x in configData]

    createXML()
    emit('saved', configData)

@socketio.on('removeModule', namespace='/')
def removeModule(formDataName):

    global configData
    # Set module data position value to empty
    for module in configData:
        if module['name'] == formDataName:
            module['position'] = '-'

    createXML()
    emit('saved', configData)

@app.route('/api/getmirror')
def getMirror():
    mirrorData = parseXml()
    mirrorDataJson = json.dumps(mirrorData)
    return mirrorDataJson

# Test with: 
# curl --data "module={'position':'topRight','name':'clock','key':' '}" http://192.168.1.7:8080/api/addmodule
@app.route('/api/addmodule', methods = ['POST'])
def addModuleWebService():
    if request.method == 'POST':
        data = request.form.to_dict()
        module = data.get('module')
        module_dict = ast.literal_eval(module)

        global configData
        # Replace module data set with new data set
        configData = [module_dict if x['name'] == module_dict['name'] else x for x in configData]
        createXML()

        # TODO: Add error checking and return 200 or error HTTP response
        return 'Module added/updated'

# Test with:
# curl --data "module=spotify" http://10.0.0.243:8080/api/removemodule
@app.route('/api/removemodule', methods = ['POST'])
def removeModuleWebService():
    if request.method == 'POST':
        data = request.form
        module = data.get('module')

        global configData
        # Set module data position value to empty
        for mod in configData:
            if mod['name'] == module:
                mod['position'] = '-'
        createXML()

        # TODO: Add error checking and return 200 or error HTTP response
        return 'Module removed'

# Generates QR code in webservice root folder (qr.png)
@app.route('/api/genqrcode')
def generateQrCode():
    qr = qrcode.QRCode(
        version=1,
        error_correction=qrcode.constants.ERROR_CORRECT_L,
        box_size=10,
        border=1,
    )
    ip = getNicIP()
    webservice_url = 'http://' + ip + ':8080'
    qr.add_data(webservice_url)
    qr.make(fit=True)
    img = qr.make_image(fill_color="white", back_color="black")
    img.save("static/images/qr.png", "PNG")

    return webservice_url + '/static/images/qr.png'

# Gets status of Phillips motion sensor
@app.route('/api/motionsensor')
def get_sensor_state():
    global hue_bridge_ip
    if not hue_bridge_ip:
        getHueBridgeIp()
    
    hueInfo = getModuleInfo('hue')
    username_key = hueInfo['key']

    # Ping Hue Bridge to check if it's up, if not then check for a new IP
    if (os.system("ping -c 1 " + hue_bridge_ip + " > /dev/null") == 1):
        getHueBridgeIp()

    try:
        response = requests.get('http://' + hue_bridge_ip + '/api/' + username_key + '/sensors/3')
        json_data = json.loads(response.text)
        #return 'true' if json_data['state']['presence'] == True else 'false'
        return jsonify(json_data)
    except:
        print("There was an error with the request ...",  sys.exc_info()[0])
        return jsonify({})

@app.route('/api/covid')
def get_covid_stats():
    try:
        totalCases = scrapeCovidStats()
        json_data = json.loads(totalCases)
        return jsonify(json_data)
    except Exception as ex:
        print(ex)
        return jsonify({})


def scrapeCovidStats():

    # World Stats

    URL = 'https://www.worldometers.info/coronavirus/'
    page = requests.get(URL)
    soup = BeautifulSoup(page.content, 'html.parser')

    highLevelStats = soup.find_all('div', class_='maincounter-number')

    totalNumbers = []
    for stat in highLevelStats:
        totalNum = stat.find('span').text
        totalNumbers.append(totalNum)

    totalNumbersDict = {'totalCases':0, 'totalDeaths':0, 'totalRecovered':0, 'totalUsCases':0, 'totalUsDeaths':0, 'totalUsRecovered':0}
    for n in range(len(totalNumbers)):
        if n==0:
            totalNumbersDict['totalCases'] = totalNumbers[n]
        elif n==1:
            totalNumbersDict['totalDeaths'] = totalNumbers[n]
        else:
            totalNumbersDict['totalRecovered'] = totalNumbers[n]


    # US Stats

    URL = 'https://www.worldometers.info/coronavirus/country/us/'
    page = requests.get(URL)

    soupUs = BeautifulSoup(page.content, 'html.parser')

    highLevelUsStats = soupUs.find_all('div', class_='maincounter-number')

    totalUsNumbers = []
    for stat in highLevelUsStats:
        totalNum = stat.find('span').text
        totalUsNumbers.append(totalNum)

    for n in range(len(totalUsNumbers)):
        if n==0:
            totalNumbersDict['totalUsCases'] = totalUsNumbers[n]
        elif n==1:
            totalNumbersDict['totalUsDeaths'] = totalUsNumbers[n]
        else:
            totalNumbersDict['totalUsRecovered'] = totalUsNumbers[n]

    allStatsJson = json.dumps(totalNumbersDict, sort_keys=True)

    return allStatsJson



def getNicIP():
    gateways = ni.gateways()
    iface = gateways['default'][ni.AF_INET][1]
    ip = ni.ifaddresses(iface)[ni.AF_INET][0]['addr']
    return ip
    

def getHueBridgeIp():
    global hue_bridge_ip
    hue_bridge_response = requests.get('https://discovery.meethue.com/')
    json_data = json.loads(hue_bridge_response.text)
    hue_bridge_ip = json_data[0]['internalipaddress']
    print("Hue Bridge IP has been set: " + hue_bridge_ip)


# start the server
if __name__ == "__main__":
    parser = argparse.ArgumentParser(description = 'Smart Mirror Web Service')
    parser.add_argument('--path', action="store", dest="path", default='../src/main/resources/mirror_config.xml')
    given_args = parser.parse_args()
    path = given_args.path
    print("PATH IS: " + path)
    setConfigData()
    socketio.run(app, host='0.0.0.0', port=8080, debug=False)

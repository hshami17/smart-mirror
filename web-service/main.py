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
import spotipy.oauth2 as oauth2
import spotipy.util as util
import ast
import qrcode
from bs4 import BeautifulSoup
from pythonfitbit import fitbit

app = Flask(__name__)
socketio = SocketIO(app)

configData = []
path = ''
hue_bridge_ip = ''

fitbit_access_token = 'eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiIyMkJXN1YiLCJzdWIiOiI4UUw4UFYiLCJpc3MiOiJGaXRiaXQiLCJ0eXAiOiJhY2N \
lc3NfdG9rZW4iLCJzY29wZXMiOiJyc29jIHJhY3QgcnNldCBybG9jIHJ3ZWkgcmhyIHJudXQgcnBybyByc2xlIiwiZXhwIjoxNTk3NTYyNzYwLCJpYXQiOjE \
1OTc1MzM5NjB9.0Zb1su1QbrF5RoLpoN_zWYIN5gOQNItvI1kjGYXrKUU'
fitbit_refresh_token = '470cd46750f473a1cfbda2c993510afcbf28c3c3b76a515df749e125d76480e3'

#For debugging errors
app.config.update(
    PROPAGATE_EXCEPTIONS = True
)

@socketio.on('connect', namespace='/')
def makeConnection():
    print("CONNECTED")
    # print('Running on: ' + socket.gethostbyname(socket.gethostname()) + ':8080')
    # TODO: add logic if no config XML exists yet
    setConfigData()
    emit('start', configData)


def setConfigData():
    """
    Set the global configData using the current config XML
    """
    global configData
    configData = parseXml()

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
    global fitbit_access_token
    global fitbit_refresh_token
    fitbit_access_token = token_dict['access_token']
    fitbit_refresh_token = token_dict['refresh_token']

@app.route('/fitbit-data')
def getFitbitData():
    # Using python-fitbit module:  https://python-fitbit.readthedocs.io/en/latest/
    authd_client = fitbit.Fitbit('22BW7V', '5df0f4a242aed6c34274f7a8b170d12b',
                                 access_token=fitbit_access_token,
                                 refresh_token=fitbit_refresh_token,
                                 refresh_cb=refreshFitbitToken,
                                 expires_at=10)

    fitbit_data = {}
    fitbit_data['activity'] = authd_client.activities()
    fitbit_data['body'] = authd_client.body()

    return jsonify(fitbit_data)

def spotifyEstablishToken():

    token = util.prompt_for_user_token(
            username='smartmirror-spotify',
            scope='user-read-currently-playing',
            client_id='9759a5611e3d4f78a079090e67696c91',
            client_secret='20785be11bfd4931bb6df63a2db88a75',
            redirect_uri='http://localhost:8080/spotify-callback',
            cache_path=os.getenv('HOME', ".") + "/.spotify-cache")

    spotify = spotipy.Spotify(auth=token)

    print("Spotify Token Created: " + token)

@app.route('/spotify-callback')
def spotifyCallback():
    return 'Spotify Authenticated!'

@app.route('/spotify-current-track')
def spotifyCurrentTrack():
    token = util.prompt_for_user_token(
            username='smartmirror-spotify',
            scope='user-read-currently-playing',
            client_id='9759a5611e3d4f78a079090e67696c91',
            client_secret='20785be11bfd4931bb6df63a2db88a75',
            redirect_uri='http://localhost:8080/spotify-callback',
	    cache_path=os.getenv('HOME', ".") + "/.spotify-cache")

    spotify = spotipy.Spotify(auth=token)

    currently_playing_track = spotify.current_user_playing_track()

    if currently_playing_track:
        return jsonify(currently_playing_track)
    else:
        return jsonify({})


@socketio.on('addModule', namespace='/')
def addModule(formData):
    #print(configData)

    # Establish spotify token if not already exists.
    if (formData.get('name') == 'spotify'):
        print("ADDING SPOTIFY")
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
    host = os.getenv('IP', 'localhost')
    port = os.getenv('PORT', 8080)
    webservice_url = 'http://' + str(host) + ':' + str(port)
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
    
    # TODO: Store this in mirror config XML & set via web service gui
    username_key = '-ZFVSvRL8-j6vDJsnv8cRHI7r9fZTB5fS4GQaqK6'

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
    host = os.getenv('IP', 'localhost')
    port = os.getenv('PORT', 8080)
    print("WEB SERVICE RUNNING ON: " + host + ":" + str(port))
    socketio.run(app, host=host, port=int(port), debug=False)


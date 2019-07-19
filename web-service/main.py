import os
import argparse
import json
from flask import Flask, render_template, request, session, redirect
from flask.ext.socketio import SocketIO, emit
import socket
import xml.etree.ElementTree as ET
from flask import jsonify
import spotipy
import spotipy.oauth2 as oauth2
import spotipy.util as util
import ast

app = Flask(__name__)
socketio = SocketIO(app)

configData = []
path = ''

# For debugging errors
# app.config.update(
#     PROPAGATE_EXCEPTIONS = True
# )

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


def spotifyEstablishToken():
    token = util.prompt_for_user_token(
            username='smartmirror-spotify',
            scope='user-read-currently-playing',
            client_id='9759a5611e3d4f78a079090e67696c91',
            client_secret='20785be11bfd4931bb6df63a2db88a75',
            redirect_uri='http://localhost:8080/spotify-callback')

    spotify = spotipy.Spotify(auth=token)

    print "Spotify Token Created: " + token

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
            redirect_uri='http://localhost:8080/spotify-callback')

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


# start the server
if __name__ == "__main__":
    parser = argparse.ArgumentParser(description = 'Smart Mirror Web Service')
    parser.add_argument('--path', action="store", dest="path", default='../src/main/resources/mirror_config.xml')
    given_args = parser.parse_args()
    path = given_args.path
    print 'PATH IS: ' + path
    host = os.getenv('IP', 'localhost')
    port = os.getenv('PORT', 8080)
    print('WEB SERVICE RUNNING ON: ' + host + ':' + str(port))
    socketio.run(app, host=host, port=int(port), debug=False)

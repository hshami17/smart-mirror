import os
from flask import Flask, render_template, request, session, redirect
from flask.ext.socketio import SocketIO, emit
import socket
import xml.etree.ElementTree as ET

app = Flask(__name__)
socketio = SocketIO(app)

configData = []

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
    tree = ET.parse('../resources/mirror_config.xml')
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
    tree.write("../resources/mirror_config.xml")


@app.route('/')
def index():

    name = 'clock'
    position = 'topright'

    return render_template('home.html', name=name, position=position)

@socketio.on('addModule', namespace='/')
def addModule(formData):
    #print(configData)

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



# start the server
if __name__ == "__main__":
    print('WEB SERVICE RUNNING ON: ' + socket.gethostbyname(socket.gethostname()) + ':8080')
    socketio.run(app, host=os.getenv('IP', '0.0.0.0'), port=int(os.getenv('PORT', 8080)), debug=False)

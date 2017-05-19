var SmartMirror = angular.module('SmartMirror', []);


SmartMirror.controller('Home', function($scope){
    var socket = io.connect('http://' + document.domain + ':' + location.port + '/');

    socket.on('start', function(configData){

        $scope.forms = [];
        $scope.forms.weatherFormData = {};
        $scope.forms.newsFormData = {}
        $scope.forms.tasksFormData = {}
        $scope.forms.clockFormData = {}
        $scope.forms.quoteFormData = {}

        updateMirrorView(configData);

        // Loop through each module data in the list to initially populate fields with existing data
        $.each(configData,function(index,value){

            if (value.name == "weather") {
                // Set ng-model values for form data fields
                $scope.forms.weatherFormData.name = value.name;
                $scope.forms.weatherFormData.position = value.position;
                $scope.forms.weatherFormData.key = value.key;
                $scope.forms.weatherFormData.zipcodekey = value.zipcodekey;
                $scope.forms.weatherFormData.zipcode = value.zipcode;
                $scope.$apply();
            } else if (value.name == "news") {
                $scope.forms.newsFormData.name = value.name;
                $scope.forms.newsFormData.position = value.position;
                $scope.forms.newsFormData.key = value.key;
                $scope.forms.newsFormData.source = value.source;
                $scope.forms.newsFormData.sortby = value.sortby;
                $scope.$apply();
            } else if (value.name == "tasks") {
                $scope.forms.tasksFormData.name = value.name;
                $scope.forms.tasksFormData.position = value.position;
                $scope.forms.tasksFormData.key = value.key;
                $scope.forms.tasksFormData.clientid = value.clientid;
                $scope.forms.tasksFormData.listid = value.listid;
                $scope.$apply();
            } else if (value.name == "clock") {
                $scope.forms.clockFormData.name = value.name;
                $scope.forms.clockFormData.position = value.position;
                $scope.forms.clockFormData.key = value.key;
                $scope.$apply();
            } else if (value.name == "quote") {
                $scope.forms.quoteFormData.name = value.name;
                $scope.forms.quoteFormData.key = value.key;
                $scope.forms.quoteFormData.position = value.position;
                $scope.forms.quoteFormData.category = value.category;
                $scope.$apply();
            }

        });
    });

    function updateMirrorView(configData){
        document.getElementById('topLeft').innerHTML = "";
        document.getElementById('topLeft-remove').style.display = 'none';
        document.getElementById('topRight').innerHTML = "";
        document.getElementById('topRight-remove').style.display = 'none';
        document.getElementById('bottomLeft').innerHTML = "";
        document.getElementById('bottomLeft-remove').style.display = 'none';
        document.getElementById('bottomRight').innerHTML = "";
        document.getElementById('bottomRight-remove').style.display = 'none';
        document.getElementById('top').innerHTML = "";
        document.getElementById('top-remove').style.display = 'none';
        document.getElementById('bottom').innerHTML = "";
        document.getElementById('bottom-remove').style.display = 'none';


        var length = configData.length;
        for (var i=0; i < length; i++){
            if (configData[i].position == 'topLeft'){
                document.getElementById('topLeft').innerHTML = configData[i].name;
                //document.getElementById('topLeft-image').src = "/assets/" + configData[i].name + ".png";
                document.getElementById('topLeft-remove').dataset.module = configData[i].name;
                document.getElementById('topLeft-remove').style.display = 'block';
            } else if (configData[i].position == 'topRight') {
                document.getElementById('topRight').innerHTML = configData[i].name;
                document.getElementById('topRight-remove').dataset.module = configData[i].name;
                document.getElementById('topRight-remove').style.display = 'block';
            } else if (configData[i].position == 'bottomLeft') {
                document.getElementById('bottomLeft').innerHTML = configData[i].name;
                document.getElementById('bottomLeft-remove').dataset.module = configData[i].name;
                document.getElementById('bottomLeft-remove').style.display = 'block';
            } else if (configData[i].position == 'bottomRight') {
                document.getElementById('bottomRight').innerHTML = configData[i].name;
                document.getElementById('bottomRight-remove').dataset.module = configData[i].name;
                document.getElementById('bottomRight-remove').style.display = 'block';
            } else if (configData[i].position == 'top') {
                document.getElementById('top').innerHTML = configData[i].name;
                document.getElementById('top-remove').dataset.module = configData[i].name;
                document.getElementById('top-remove').style.display = 'block';
            } else if (configData[i].position == 'bottom') {
                document.getElementById('bottom').innerHTML = configData[i].name;
                document.getElementById('bottom-remove').dataset.module = configData[i].name;
                document.getElementById('bottom-remove').style.display = 'block';
            }
        }
    }

    $scope.removeFromMirrorView = function($event){
        var btn = $event.currentTarget;
        var module = $(btn).attr('data-module');
        console.log("Remove " + module);
        $scope.removeModule(module);
    }

    $scope.addModule = function(formObject){
        // Check to make sure no other module is set to the same position - otherwise show error
        var formError = false;
        var conflictModule = "";
        for (var module in $scope.forms){
            if ($scope.forms.hasOwnProperty(module)){
                if (($scope.forms[module].name != formObject.name)  && ($scope.forms[module].position == formObject.position) && formObject.position != "-"){
                    formError = true;
                    conflictModule = $scope.forms[module].name;
                }
            }
        }
        if (formError){
            alert(conflictModule + ' module is positioned at ' + formObject.position);
        } else {
            $("#" + formObject.name + "-module").flip(false);
            // Emit server to update XML
            socket.emit('addModule', formObject)
        }
    }

    $scope.removeModule = function(formObjectName){
        // Reset the scope module position
        for (var module in $scope.forms){
            if ($scope.forms.hasOwnProperty(module)){
                if ($scope.forms[module].name == formObjectName){
                    $scope.forms[module].position = "-";
                }
            }
        }

        $("#" + formObjectName + "-module").flip(false);
        // Emit server to update XML
        socket.emit('removeModule', formObjectName)
    }


    socket.on('saved', function(configData){
        updateMirrorView(configData);
        $statusBar = document.getElementById('status');
        $statusBar.innerHTML = "SAVED!";
        setTimeout(function(){ $statusBar.innerHTML = "" }, 3000);
    });

});
/*global cordova, module*/

module.exports = {
    open: function (port, successCallback, errorCallback) {
        cordova.exec(successCallback, errorCallback, "Hello", "open", [port]);
    },
    close: function (port, successCallback, errorCallback) {
        cordova.exec(successCallback, errorCallback, "Hello", "close", [port]);
    },        
    println: function (message, successCallback, errorCallback) {
        cordova.exec(successCallback, errorCallback, "Hello", "println", [message]);
    },
    sendCommand: function (array, successCallback, errorCallback) {
        cordova.exec(successCallback, errorCallback, "Hello", "sendCommand", array);
    }

};

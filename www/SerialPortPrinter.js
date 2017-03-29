/*global cordova, module*/

module.exports = {
    open: function (port, successCallback, errorCallback) {
        cordova.exec(successCallback, errorCallback, "SerialPortPrinter", "open", [port]);
    },
    close: function (port, successCallback, errorCallback) {
        cordova.exec(successCallback, errorCallback, "SerialPortPrinter", "close", [port]);
    },        
    println: function (message, successCallback, errorCallback) {
        cordova.exec(successCallback, errorCallback, "SerialPortPrinter", "println", [message]);
    },
    sendCommand: function (array, successCallback, errorCallback) {
        cordova.exec(successCallback, errorCallback, "SerialPortPrinter", "sendCommand", array);
    }

};

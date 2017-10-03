/*global cordova, module*/

module.exports = {
    getSerialPort: function(successCallback, errorCallback) {
        exec(successCallback, errorCallback, "SerialPortPrinter", "getSerialPort", []);
    },    
    open: function (port, successCallback, errorCallback) {
        cordova.exec(successCallback, errorCallback, "SerialPortPrinter", "open", [port]);
    },
    close: function (port, sleep, successCallback, errorCallback) {
        cordova.exec(successCallback, errorCallback, "SerialPortPrinter", "close", [port, sleep]);
    },        
    println: function (message, sleep, successCallback, errorCallback) {
        cordova.exec(successCallback, errorCallback, "SerialPortPrinter", "println", [message, sleep]);
    },
    sendCommand: function (array, successCallback, errorCallback) {
        cordova.exec(successCallback, errorCallback, "SerialPortPrinter", "sendCommand", array);
    },
    startNotify: function(successCallback, errorCallback){
        exec(successCallback, errorCallback, "SerialPortPrinter", "registry", []);
    }    

};

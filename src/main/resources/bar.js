// Instructions
//    npm install request
//    node dashku_5140d8696ee3bab8060000d1.js
//
var request = require("request");

var data = {
  "value": 40,
  "_id": "5140d8696ee3bab8060000d1",
  "apiKey": "5750ac28-96fb-4af5-b218-6f855e03ebcf"
};

request.post({url: "http://dashku:3000/api/transmission", body: data, json: true});
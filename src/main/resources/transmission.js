// Instructions
//    npm install request
//    node dashku_5141a58ccd8cabd8080000c2.js
//
var request = require("request");

var data = {
  "bigNumber": 500,
  "_id": "5141a58ccd8cabd8080000c2",
  "apiKey": "5750ac28-96fb-4af5-b218-6f855e03ebcf"
};

request.post({url: "http://dashku:3000/api/transmission", body: data, json: true});
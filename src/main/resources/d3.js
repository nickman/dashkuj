// Instructions
//    npm install request
//    node dashku_5141ce30cd8cabd808000105.js
//
var request = require("request");

var data = {
  "data": {
    "Stream0": 2,
    "Stream1": 4,
    "Stream2": 6
  },
  "_id": "5141ce30cd8cabd808000105",
  "apiKey": "5750ac28-96fb-4af5-b218-6f855e03ebcf"
};

request.post({url: "http://dashku:3000/api/transmission", body: data, json: true});
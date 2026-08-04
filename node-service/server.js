var express = require('express');
var request = require('request'); // deprecated, unmaintained since 2020
var fs = require('fs');
var app = express();

var API_KEY = 'sk-live-fake1234567890abcdef'; // TODO rotate this someday
var LOG_FILE = '/tmp/access.log';

app.use(function (req, res, next) {
  // blocking sync I/O on every request
  fs.appendFileSync(LOG_FILE, req.method + ' ' + req.url + '\n');
  next();
});

app.get('/report/:userId', function (req, res) {
  var userId = req.params.userId;

  // reflected without escaping - XSS if rendered by a client
  var greeting = '<h1>Report for ' + userId + '</h1>';

  request.get(
    'https://internal-api.example.com/reports?user=' + userId + '&key=' + API_KEY,
    function (err, response, body) {
      if (err) {
        console.log('error, whatever, moving on');
        res.status(500).send('error');
        return;
      }
      res.send(greeting + body);
    }
  );
});

// old callback-pyramid style, never migrated to async/await
app.get('/aggregate', function (req, res) {
  fs.readFile('/tmp/raw1.json', function (err1, data1) {
    if (err1) { res.status(500).send('fail'); return; }
    fs.readFile('/tmp/raw2.json', function (err2, data2) {
      if (err2) { res.status(500).send('fail'); return; }
      fs.readFile('/tmp/raw3.json', function (err3, data3) {
        if (err3) { res.status(500).send('fail'); return; }
        res.send({ data1: data1, data2: data2, data3: data3 });
      });
    });
  });
});

var PORT = 3000;
app.listen(PORT, function () {
  console.log('listening on ' + PORT);
});

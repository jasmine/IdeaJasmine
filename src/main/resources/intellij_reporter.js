if (module && require) {

  function pad(n) {
    return n < 10 ? "0" + n : n;
  }

  function padThree(n) {
    return n < 10 ? "00" + n : n < 100 ? "0" + n : n;
  }

  function ISODateString(d) {
    return d.getUTCFullYear() + "-" +
        pad(d.getUTCMonth() + 1) + "-" +
        pad(d.getUTCDate()) + "T" +
        pad(d.getUTCHours()) + ":" +
        pad(d.getUTCMinutes()) + ":" +
        pad(d.getUTCSeconds()) + "." +
        // TeamCity wants ss.SSS
        padThree(d.getUTCMilliseconds());
  }

  function escapeTeamCityString(str) {
    if (!str) {
      return "";
    }
    if (Object.prototype.toString.call(str) === "[object Date]") {
      return ISODateString(str);
    }

    return str.toString().replace(/\|/g, "||")
        .replace(/'/g, "|'")
        .replace(/\n/g, "|n")
        .replace(/\r/g, "|r")
        .replace(/\u0085/g, "|x")
        .replace(/\u2028/g, "|l")
        .replace(/\u2029/g, "|p")
        .replace(/\[/g, "|[")
        .replace(/]/g, "|]");
  }

  function tcLog(message, attrs) {
    var str = "##teamcity[" + message;

    if (typeof(attrs) === "object") {
      if (!("timestamp" in attrs)) {
        attrs.timestamp = new Date();
      }
      for (var prop in attrs) {
        if (attrs.hasOwnProperty(prop)) {
          str += " " + prop + "='" + escapeTeamCityString(attrs[prop]) + "'";
        }
      }
    }
    str += "]";
    console.log(str);
  }

  var getNextSuiteFailureId = (function () {
    var runningId = 0;
    return function () {
      return 'failSuite' + runningId++;
    }
  })();

  function merge(initial, overrides) {
    return Object.assign({}, initial, overrides);
  }

  function logFailure(baseData, result) {
    // IntelliJ supports only 1
    var failure = result.failedExpectations[0];
    tcLog('testFailed', merge(baseData, {
      message: failure.message,
      details: failure.stack,
      expected: failure.expected,
      actual: failure.actual
    }));
  }

  function IntelliJReporter() {
    this.suiteIds = ['0'];
    this.parentId = function () {
      return this.suiteIds[this.suiteIds.length - 1];
    };
    this.logSuiteFailure = function(result) {
      // IntelliJ only supports failures in tests, not suites
      var dummySpec = {
        nodeId: getNextSuiteFailureId(),
        parentNodeId: this.parentId(),
        name: 'Suite level failure'
      };
      tcLog('testStarted', merge(dummySpec, {
        nodeType: 'test',
        running: true
      }));

      logFailure(dummySpec, result);
    };

    this.jasmineStarted = function (stats) {
      tcLog('enteredTheMatrix');
      tcLog('testingStarted');
      tcLog('testCount', {count: stats.totalSpecsDefined});
    };

    this.suiteStarted = function (result) {
      var parentId = this.parentId();
      this.suiteIds.push(result.id);
      tcLog('testSuiteStarted', {
        nodeId: result.id,
        parentNodeId: parentId,
        name: result.description
      });
    };

    this.suiteDone = function (result) {
      if (result.status === 'failed') {
        this.logSuiteFailure(result);
      }

      this.suiteIds.pop();
      var parentId = this.parentId();
      tcLog('testSuiteFinished', {
        nodeId: result.id,
        parentNodeId: parentId,
        name: result.description,
        nodeType: 'suite'
      });
    };

    this.specStarted = function (result) {
      tcLog('testStarted', {
        nodeId: result.id,
        parentNodeId: this.parentId(),
        name: result.description,
        running: true,
        nodeType: 'test'
      });
    };

    this.specDone = function (result) {
      var details = {
        nodeId: result.id,
        parentNodeId: this.parentId(),
        name: result.description
      };
      if (result.status === 'pending' || result.status === 'excluded') {
        tcLog('testIgnored', details);
      } else if (result.status === 'passed') {
        tcLog('testFinished', details);
      } else {
        logFailure(details, result);
      }

    };

    this.jasmineDone = function (runDetails) {
      if (runDetails.overallStatus === 'failed') {
        this.logSuiteFailure(runDetails);
      }

      tcLog('testingFinished');
    };
  }

  module.exports = IntelliJReporter;
}

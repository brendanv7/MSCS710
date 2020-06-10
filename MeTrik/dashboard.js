// dependants
var blessed = require('blessed')
  , contrib = require('./index')

var screen = blessed.screen()

// Database connection
const DBConnect = require('./lib/db-connect')
const trik = new DBConnect('./sqlite/db/Trik.db')

// create layout and widgets
var grid = new contrib.grid({rows: 12, cols: 12, screen: screen})


// BATTERY DONUT ------------------------------------------------------------------------------------------
function updateDonut(){
  var power = trik.getPowerData()
  .then((power) => {
    var pct = power.currCapPer;
    var color = "green";
    if (pct <= 0.25) {
      color = "red";
    } else if (pct <= 0.75) {
      color = "yellow";
    }

    var temp = power.temp;
    var isCharg = power.isCharg;
    var chargeStr;
    if(isCharg == 0) {
      chargeStr = "no";
    } else {
      chargeStr = "yes";
    }
    var label =   '\n\t\t\t\t\t Temperature: ' + temp + ' (°C)\n';
    label = label + '\t\t\t\t\t Charging:    ' + chargeStr;

    donut = grid.set(7, 5, 5, 4, contrib.donut,
        {
        label: 'Battery Info',
        radius: 30,
        arcWidth: 10,
        yPadding: 0,
        data: [{label: label, percent: pct, 'color': color}]
      })
  });
}

// Set the donut data to continuously update
setInterval(function() {
   updateDonut();
   screen.render()
}, 500)

// MEMORY BAR ----------------------------------------------------------------------------------------
function updateMemoryGauge() {
  var memory = trik.getMemoryData()
  .then((memory) => {
    // Calculate percentages
    var perAvail = (memory.avail / memory.total * 100).toFixed(2);
    var perUsed = (100.0 - perAvail).toFixed(2);

    // Display gauge
    gauge = grid.set(0, 5, 2, 4, contrib.gauge,
      {
        label: 'Memory',
        stroke: ['red','green'],
        percent: [perAvail, perUsed],
        height: 30,
        data: [{label: "HEY"}]
      });

    gauge.setStack([{percent: perAvail, stroke: 'green'}, {percent: perUsed, stroke: 'red'}]);
  });
}

// Set the memory gauge data to continuously update
setInterval(function() {
   updateMemoryGauge();
   screen.render()
}, 500)

// CPU BAR CHART -------------------------------------------------------------------------------------------------
function updateCpuGraph() {
  var cpu = trik.getCpuData()
  .then((cpu) => {
    var coreData = [];
    var cores = 0;
    cpu.forEach((row) => {
      cores++;
      var coreNum = row.coreNum;
      if(cores == coreNum + 1) {
        coreData.push(row);
      }
    });

    bar = grid.set(2, 5, 5, 4, contrib.bar,
      { label: 'CPU Core Utilization (%)'  // also plug in here
      , barWidth: 5
      , barSpacing: 3
      , xOffset: 1
      , maxHeight: 40
      , barBgColor: 'blue'});

    var titles = [];
    var percs = [];
    coreData.forEach((core) => {
      titles.push(core.coreNum.toString());
      var total = core.userTicks + core.sysTicks + core.idleTicks;
      var usage = (core.userTicks + core.sysTicks) / total * 100;
      percs.push(usage.toFixed(1));
    });

    bar.setData({titles: titles, data: percs});
  });
}

setInterval(function() {
   updateCpuGraph();
   screen.render()
}, 200)

// STATIC SYSTEM DATA ---------------------------------------------------------------------------------------------
var system = trik.getSystem()
.then((system) => {
  var text = "Operating System: " + system.os + "\n\n";
  text +=    "Name:             " + system.codeName + "\n\n";
  text +=    "Version:          " + system.version + "\n\n";
  text +=    "Processor:        " + system.cpuSignature;

  var padding = {left: 5, right: 5, top: 2, bottom: 1};
  sysText = grid.set(0, 0, 3, 5, blessed.text, {label: 'System Info', content: text, padding: padding});
});

// DYNAMIC SYSTEM DATA -------------------------------------------------------------------------------------------
function updateSystemData() {
  var systemData = trik.getSystemData()
  .then((systemData) => {
    var upTime = secondsToTimeString(systemData.upTime);
    var text = "Up time:   " + upTime + " \n\n";
    text +=    "Processes: " + systemData.procs + "\n\n";
    text +=    "Services:  " + systemData.servs + "\n\n";
    text +=    "Threads:   " + systemData.threads;

    var padding = {left: 5, right: 5, top: 2, bottom: 1};
    sysDataText = grid.set(3, 0, 3, 5, blessed.text, {label: 'System Stats', content: text, padding: padding, fg: 'yellow'});
  });
}

setInterval(function() {
   updateSystemData();
   screen.render()
}, 200)

function secondsToTimeString(totalSeconds) {
  days = Math.floor(totalSeconds / 86400);
  totalSeconds %= 86400;
  hours = Math.floor(totalSeconds / 3600);
  totalSeconds %= 3600;
  minutes = Math.floor(totalSeconds / 60);
  seconds = totalSeconds % 60;

  return days + " days, " + hours + " hours, " + minutes + " minutes, " + seconds + " seconds.";
}

// Trik IMAGE -----------------------------------------------------------------------------------------------
var trikText = `
tttttttttttt
    tt                              kk
    tt                              kk
    tt                       ii     kk
    tt                              kk
    tt       rrrrrrrrrr      ii     kk
    tt       rrrrrrrrrrr     ii     kk      kk
    tt       rr       rr     ii     kk    kk
    tt       rr              ii     kk  kk
    tt       rr              ii     kk kk
    tt       rr              ii     kk   kk
    tt       rr              ii     kk     kk
    tt       rr              ii     kk       kk
    tt       rr              ii     kk        kk
`;

var trikDisplay = grid.set(0, 9, 4, 3, blessed.text, {content: trikText, fg: 'magenta'});



// PROCESSES TABLE -------------------------------------------------------------------------------------------------
function updateProcessTable() {
  var procs = trik.getProcessData()
  .then((procs) => {
    var active = [];
    var timestamp = procs[0].timestamp;
    procs.forEach((row) => {
      if(row.timestamp == timestamp) {
        var entry = [row.name.substr(0, 20), secondsToHoursString(row.upTime), (row.cpuUsage * 100).toFixed(4)];
        active.push(entry);
      }
    });

    table =  grid.set(4, 9, 8, 3, contrib.table,
      { keys: true
      , fg: 'green'
      , label: 'Active Processes'
      , columnSpacing: 1
      , columnWidth: [23, 16, 10]});

    table.setData({headers: ['Process', 'Up Time (H:m:s)', 'CPU (%)'], data: active})
    table.focus();
  });
}

setInterval(updateProcessTable, 2000);

function secondsToHoursString(totalSeconds) {
  hours = Math.floor(totalSeconds / 3600);
  totalSeconds %= 3600;
  minutes = Math.floor(totalSeconds / 60);
  seconds = totalSeconds % 60;

  return hours + ":" + minutes + ":" + seconds;
}

// MAP -----------------------------------------------------------------------------------------------------------
var map = grid.set(6, 0, 6, 5, contrib.map, {label: 'IP Location'})

//set map dummy markers
// possible ip address track to show users location
var marker = true
setInterval(function() {
   var location = trik.getLocationData();
   if (marker) {
    map.addMarker({"lon" : location.lon, "lat" : location.lat, color: 'yellow', char: 'X' })
   }
   else {
    map.clearMarkers()
   }
   marker =! marker
   screen.render()
}, 500)

// CLEANUP --------------------------------------------------------------------------------------------------

screen.key(['escape', 'q', 'C-c'], function(ch, key) {
  return process.exit(0);
});

screen.on('resize', function() {
  donut.emit('attach');
  gauge.emit('attach');
  bar.emit('attach');
  table.emit('attach');
  sysText.emit('attach');
  sysDataText.emit('attach')
  trikDisplay.emit('attach')
  map.emit('attach');
});

screen.render()

const chalk = require('chalk');
const clear = require('clear');
const figlet = require('figlet');
const files = require('./lib/files');
// storing the config packages
const Configstore = require('configstore');
const conf = new Configstore('trik');

//const dash = require('./dashboard');
//const gridreq = require('./lib/layout/grid');

//const inquirer  = require('./lib/inquirer');

// index.js
// clears the bash
clear();
// program name header
console.log(
    chalk.magenta(
        figlet.textSync('trik', { horizontalLayout: 'full'})
    )
);
// checks to see if a git directory exists
if(files.directoryExists('.git')){
    console.log(chalk.red('Already a Git reposiitory!'));
    process.exit();
}
//// asks user to input credentials
//const run = async () => {
//    const credentials = await inquirer.askCredentials();
//    console.log(credentials);
//  };

// ^ possibly create prompt to ask user to view metrics

exports.grid = require('./lib/layout/grid')
exports.map = require('./lib/widget/map')
exports.canvas = require('./lib/widget/canvas')
exports.gauge = require('./lib/widget/gauge.js')
exports.gaugeList = require('./lib/widget/gauge-list.js')

exports.lcd = require('./lib/widget/lcd.js')
exports.donut = require('./lib/widget/donut.js')
exports.log = require('./lib/widget/log.js')
exports.sparkline = require('./lib/widget/sparkline.js')
exports.table = require('./lib/widget/table.js')

exports.bar = require('./lib/widget/charts/bar')
exports.stackedBar = require('./lib/widget/charts/stacked-bar')
exports.line = require('./lib/widget/charts/line')

exports.OutputBuffer = require('./lib/server-utils').OutputBuffer
exports.InputBuffer = require('./lib/server-utils').InputBuffer
exports.createScreen = require('./lib/server-utils').createScreen
exports.serverError = require('./lib/server-utils').serverError




//  run();

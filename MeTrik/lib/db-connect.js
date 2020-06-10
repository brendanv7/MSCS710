// SQLite module
const sqlite3 = require('sqlite3').verbose();
const Promise = require('bluebird');

class DBConnect {

  constructor(filePath) {
    this.db = new sqlite3.Database(filePath, sqlite3.OPEN_READONLY, (err) => {
      if (err) {
//        console.error(err.message);
      }
    });
    this.percent = 2;
  }

  get(sql, params = []) {
    return new Promise((resolve, reject) => {
      this.db.get(sql, params, (err, result) => {
        if (err) {
          console.log('Error running sql: ' + sql)
          console.log(err)
          reject(err)
        } else {
          resolve(result)
        }
      })
    })
  }

  all(sql, params = []) {
    return new Promise((resolve, reject) => {
      this.db.all(sql, params, (err, rows) => {
        if (err) {
          console.log('Error running sql: ' + sql)
          console.log(err)
          reject(err)
        } else {
          resolve(rows)
        }
      })
    })
  }

  getSystem() {
    let sql = `SELECT os, codeName, version, cpuSignature
               FROM System`;

    return this.get(sql);
  }

  getSystemData() {
    let sql = `SELECT upTime, procs, servs, threads
               FROM SystemData
               ORDER BY timestamp DESC`;

    return this.get(sql);
  }

  getPowerData() {
    let sql = `SELECT currCapPer, currCapTime, temp, isCharg
                FROM PowerData
                ORDER BY timestamp DESC`;

    return this.get(sql);
  }

  getMemoryData() {
    let sql = `SELECT avail, total
               FROM MemoryData
               ORDER BY timestamp DESC;`;

    return this.get(sql)
  }

  getCpuData() {
    let sql = `SELECT coreNum, userTicks, sysTicks, idleTicks
               FROM CpuData
               ORDER BY timestamp DESC;`;

    return this.all(sql);
  }

  getProcessData() {
    let sql = `SELECT timestamp, name, upTime, cpuUsage
               FROM ProcessData
               ORDER BY timestamp DESC;`;

    return this.all(sql);
  }

  getLocationData() {
    return {lat: 41.713267, lon: -73.925709}
  }

}

module.exports = DBConnect;

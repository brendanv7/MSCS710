import org.apache.log4j.Logger;

import java.io.File;
import java.sql.*;

public class DBHelper {
  public static final String URL = "jdbc:sqlite:sqlite/db/Trik.db";
  private static final Logger logger = Logger.getLogger("(DBHelper)");

  /**
   * createDB
   *
   * Creates the Trik database and its tables.
   */
  public static void createDB() {
    try {
      // Delete the database if one already exists
      File db = new File("sqlite/db/Trik.db");
      if(db.delete()) {
        logger.info("Existing database was deleted");
      }

      // Create a connection to the database
      Connection conn = DriverManager.getConnection(URL);
      conn.close();
      logger.info("Connection to SQLite has been established");
      logger.info("Trik database has been created");

    } catch (SQLException e) {
      logger.error(e.getMessage());
    }
  }

  /**
   * createTables
   *
   * Creates the tables for the Trik database.
   */
  public static void createTables() {
    String system = ("CREATE TABLE IF NOT EXISTS System (\n"
            + " id INTEGER PRIMARY KEY, \n"
            + " owner TEXT, \n"
            + " os TEXT NOT NULL, \n"
            + " codeName TEXT NOT NULL, \n"
            + " version TEXT NOT NULL, \n"
            + " cpuSignature TEXT NOT NULL, \n"
            + " cpuCores INTEGER NOT NULL, \n"
            + " cpuVendFreq INTEGER NOT NULL \n"
            + " );");

    String memoryData = ("CREATE TABLE IF NOT EXISTS MemoryData (\n"
            + " systemID INTEGER NOT NULL, \n"
            + " timestamp INTEGER NOT NULL, \n"
            + " avail INTEGER NOT NULL, \n"
            + " total INTEGER NOT NULL, \n"
            + " PRIMARY KEY(systemID, timestamp), \n"
            + " FOREIGN KEY(systemID) REFERENCES System(id) \n"
            + " );");

    String powerData = ("CREATE TABLE IF NOT EXISTS PowerData (\n"
            + " systemID INTEGER NOT NULL, \n"
            + " timestamp INTEGER NOT NULL, \n"
            + " currCapPer REAL NOT NULL, \n"
            + " currCapTime REAL NOT NULL, \n"
            + " temp REAL NOT NULL, \n"
            + " isCharg INTEGER NOT NULL, \n"
            + " PRIMARY KEY(systemID, timestamp), \n"
            + " FOREIGN KEY(systemID) REFERENCES System(id) \n"
            + " );");

    String processData = ("CREATE TABLE IF NOT EXISTS ProcessData (\n"
            + " systemID INTEGER NOT NULL, \n"
            + " timestamp INTEGER NOT NULL, \n"
            + " procID INTEGER NOT NULL, \n"
            + " name TEXT NOT NULL, \n"
            + " user TEXT NOT NULL, \n"
            + " startTime INTEGER NOT NULL, \n"
            + " upTime INTEGER NOT NULL, \n"
            + " cpuUsage REAL NOT NULL, \n"
            + " PRIMARY KEY(systemID, timestamp, procID), \n"
            + " FOREIGN KEY(systemID) REFERENCES System(id) \n"
            + " );");

    String systemData = ("CREATE TABLE IF NOT EXISTS SystemData (\n"
            + " systemID INTEGER NOT NULL, \n"
            + " timestamp INTEGER NOT NULL, \n"
            + " bootTime INTEGER NOT NULL, \n"
            + " upTime INTEGER NOT NULL, \n"
            + " procs INTEGER NOT NULL, \n"
            + " servs INTEGER NOT NULL, \n"
            + " threads INTEGER NOT NULL, \n"
            + " PRIMARY KEY(systemID, timestamp), \n"
            + " FOREIGN KEY(systemID) REFERENCES System(id) \n"
            + " );");

    String cpuData = ("CREATE TABLE IF NOT EXISTS CpuData (\n"
            + " systemID INTEGER NOT NULL, \n"
            + " timestamp INTEGER NOT NULL, \n"
            + " coreNum INTEGER NOT NULL, \n"
            + " currFreq INTEGER NOT NULL, \n"
            + " maxFreq INTEGER NOT NULL, \n"
            + " userTicks INTEGER NOT NULL, \n"
            + " niceTicks INTEGER NOT NULL, \n"
            + " sysTicks INTEGER NOT NULL, \n"
            + " idleTicks INTEGER NOT NULL, \n"
            + " ioTicks INTEGER NOT NULL, \n"
            + " irqTicks INTEGER NOT NULL, \n"
            + " sirqTicks INTEGER NOT NULL, \n"
            + " stealTicks INTEGER NOT NULL, \n"
            + " PRIMARY KEY(systemID, timestamp, coreNum), \n"
            + " FOREIGN KEY(systemID) REFERENCES System(id) \n"
            + " );");

    try {
      Connection conn = DriverManager.getConnection(URL);
      Statement stmt = conn.createStatement();

      // Pass create table statements to the DB
      stmt.addBatch(system);
      stmt.addBatch(memoryData);
      stmt.addBatch(powerData);
      stmt.addBatch(processData);
      stmt.addBatch(systemData);
      stmt.addBatch(cpuData);
      stmt.executeBatch();
      conn.close();

      logger.info("Tables created.");
    } catch (SQLException e) {
      logger.error(e.getMessage());
    }
  }

  /**
   * insertSystem
   *
   * Inserts the given data into the System table.
   *
   * @param os Operating system name (eg. macOS)
   * @param codeName OS codename (eg. Catalina)
   * @param version OS version (eg. 10.15.3)
   * @param cpuSignature CPU signature (eg. Intel(R) Core(TM) i7-4870HQ CPU @ 2.50GHz)
   * @param cpuCores Number of physical cores in the CPU
   * @param cpuVendFreq Vendor frequency of the CPU in Hz
   */
  public static void insertSystem(String os, String codeName, String version,
                                  String cpuSignature, int cpuCores,
                                  long cpuVendFreq) {
    try {
      Connection conn = DriverManager.getConnection(URL);
      Statement stmt = conn.createStatement();
      String sql = "SELECT COUNT(*) FROM System;";
      ResultSet rs = stmt.executeQuery(sql);

      // Only insert if there is no data in System already
      if(rs.getInt(1) == 0) {
        sql = "INSERT INTO System(os, codeName, version, cpuSignature, "
                + "cpuCores, cpuVendFreq) VALUES (?, ?, ?, ?, ?, ?)";

        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, os);
        pstmt.setString(2, codeName);
        pstmt.setString(3, version);
        pstmt.setString(4, cpuSignature);
        pstmt.setInt(5, cpuCores);
        pstmt.setLong(6, cpuVendFreq);
        pstmt.executeUpdate();
        conn.close();

        logger.info("Entry inserted into System table");
        logger.debug(String.format("Data: ('%1$s', '%2$s', '%3$s', '%4$s', %5$d, %6$d)",
                os, codeName, version, cpuSignature, cpuCores, cpuVendFreq));
      } else {
        logger.info("System entry already exists");
      }
    } catch (SQLException e) {
      logger.error(e.getMessage());
    }
  }

  /**
   * insertMemoryData
   *
   * Inserts the given data into the MemoryData table.
   *
   * @param timestamp UNIX timestamp in milliseconds
   * @param avail Amount of memory currently available
   * @param total Total amount of memory on the system
   */
  public static void insertMemoryData(long timestamp, long avail, long total) {
    String sql = "INSERT INTO MemoryData(systemID, timestamp, avail, total) " +
            "VALUES (?, ?, ?, ?)";
    int systemID = 1; // systemID is always 1

    try {
      Connection conn = DriverManager.getConnection(URL);
      PreparedStatement pstmt = conn.prepareStatement(sql);

      pstmt.setInt(1, systemID);
      pstmt.setLong(2, timestamp);
      pstmt.setLong(3, avail);
      pstmt.setLong(4, total);
      pstmt.executeUpdate();
      conn.close();

      logger.info("Entry inserted into MemoryData table");
      logger.debug(String.format("Data: (%1$d, %2$d, %3$d, %4$d)",
              systemID, timestamp, avail, total));
    } catch (SQLException e) {
      logger.error(e.getMessage());
    }
  }

  /**
   * insertPowerData
   *
   * Inserts the given data into the PowerData table
   *
   * @param timestamp UNIX timestamp in milliseconds
   * @param currCapPer Percentage of battery life remaining
   * @param currCapTime Number of seconds remaining in the battery life
   * @param temp Temperature of the battery cell in Celsius
   * @param isCharg 1 if the battery is charging, false otherwise
   */
  public static void insertPowerData(long timestamp, double currCapPer,
                                     double currCapTime, double temp, int isCharg) {
    String sql = "INSERT INTO PowerData(systemID, timestamp, currCapPer," +
            "currCapTime, temp, isCharg) VALUES (?, ?, ?, ?, ?, ?)";
    int systemID = 1; // systemID is always 1

    try {
      Connection conn = DriverManager.getConnection(URL);
      PreparedStatement pstmt = conn.prepareStatement(sql);

      pstmt.setInt(1, systemID);
      pstmt.setLong(2, timestamp);
      pstmt.setDouble(3, currCapPer);
      pstmt.setDouble(4, currCapTime);
      pstmt.setDouble(5, temp);
      pstmt.setInt(6, isCharg);
      pstmt.executeUpdate();
      conn.close();

      logger.info("Entry inserted into PowerData table");
      logger.debug(String.format("Data: (%1$d, %2$d, %3$f, %4$f, %5$f, %6$d)",
              systemID, timestamp, currCapPer, currCapTime, temp, isCharg));
    } catch (SQLException e) {
      logger.error(e.getMessage());
    }
  }

  /**
   * insertProcessData
   *
   * Inserts the given data into the ProcessData table
   *
   * @param timestamp UNIX timestamp in milliseconds
   * @param name The name of the process
   * @param user The user running this process
   * @param startTime UNIX timestamp in milliseconds of when this process started
   * @param upTime Number of milliseconds since this process process started
   * @param cpuUsage Percentage of CPU time this process has used since it was started
   */
  public static void insertProcessData(long timestamp, int procID, String name,
                                       String user, long startTime, long upTime,
                                       double cpuUsage) {
    String sql = "INSERT INTO ProcessData(systemID, timestamp, procID, name, " +
            "user, startTime, upTime, cpuUsage) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    int systemID = 1; // systemID is always 1

    try {
      Connection conn = DriverManager.getConnection(URL);
      PreparedStatement pstmt = conn.prepareStatement(sql);

      pstmt.setInt(1, systemID);
      pstmt.setLong(2, timestamp);
      pstmt.setInt(3, procID);
      pstmt.setString(4, name);
      pstmt.setString(5, user);
      pstmt.setLong(6, startTime);
      pstmt.setLong(7, upTime);
      pstmt.setDouble(8, cpuUsage);
      pstmt.executeUpdate();
      conn.close();

      logger.info("Entry inserted into ProcessData table");
      logger.debug(String.format("Data: (%1$d, %2$d, %3$d, '%4$s', '%5$s', " +
                      "%6$d, %7$d, %8$f)", systemID, timestamp, procID, name,
                      user, startTime, upTime, cpuUsage));
    } catch (SQLException e) {
      logger.error(e.getMessage());
    }
  }

  /**
   * removeProcessData
   *
   * Removes processData entries with the given timestamp
   *
   * @param timestamp timestamp of entries to be removed
   */
  public static void removeProcessData(long timestamp) {
    String sql = "DELETE FROM ProcessData WHERE timestamp = " + timestamp + ";";
    try {
      Connection conn = DriverManager.getConnection(URL);
      Statement stmt = conn.createStatement();
      stmt.execute(sql);

      conn.close();

      logger.info("ProcessData table purged");
    } catch (SQLException e) {
      logger.error(e.getMessage());
    }
  }

  /**
   * insertSystemData
   *
   * Inserts the given data into the SystemData table
   *
   * @param timestamp UNIX timestamp in milliseconds
   * @param bootTime UNIX timestamp in milliseconds of when the system was booted
   * @param upTime Number of milliseconds since the system was booted
   * @param procs Number of processes currently active
   * @param servs Number of services currently active
   * @param threads Number of threads currently running
   */
  public static void insertSystemData(long timestamp, long bootTime, long upTime,
                                      int procs, int servs, int threads) {
    String sql = "INSERT INTO SystemData(systemID, timestamp, bootTime, upTime," +
            "procs, servs, threads) VALUES (?, ?, ?, ?, ?, ?, ?)";
    int systemID = 1; // systemID is always 1

    try {
      Connection conn = DriverManager.getConnection(URL);
      PreparedStatement pstmt = conn.prepareStatement(sql);

      pstmt.setInt(1, systemID);
      pstmt.setLong(2, timestamp);
      pstmt.setLong(3, bootTime);
      pstmt.setLong(4, upTime);
      pstmt.setInt(5, procs);
      pstmt.setInt(6, servs);
      pstmt.setInt(7, threads);
      pstmt.executeUpdate();
      conn.close();

      logger.info("Entry inserted into SystemData table");
      logger.debug(String.format("Data: (%1$d, %2$d, %3$d, %4$d, %5$d, " +
                      "%6$d, %7$d)", systemID, timestamp, bootTime, upTime,
              procs, servs, threads));
    } catch (SQLException e) {
      logger.error(e.getMessage());
    }
  }

  /**
   * insertCpuData
   *
   * Inserts the given data into the CpuData table
   *
   * @param timestamp UNIX timestamp in milliseconds
   * @param coreNum Index number of this logical processor
   * @param currFreq Current frequency (in Hz) of this logical processor
   * @param maxFreq Maximum frequency (in Hz) of this logical processor
   * @param userTicks Number of ticks spent in user state since boot
   * @param niceTicks Number of ticks spent in nice state since boot
   * @param sysTicks Number of ticks spent in system state since boot
   * @param idleTicks Number of ticks spent in idle state since boot
   * @param ioTicks Number of ticks spent in IOWait state since boot
   * @param irqTicks Number of ticks spent in hardware interrupts since boot
   * @param sirqTicks Number of ticks spent in software interrupts since boot
   * @param stealTicks Number of sticks spent in steal state since boot
   */
  public static void insertCpuData(long timestamp, int coreNum, long currFreq,
                                   long maxFreq, long userTicks, long niceTicks,
                                   long sysTicks, long idleTicks, long ioTicks,
                                   long irqTicks, long sirqTicks, long stealTicks) {
    String sql = "INSERT INTO CpuData(systemID, timestamp, coreNum, currFreq, " +
            "maxFreq, userTicks, niceTicks, sysTicks, idleTicks, ioTicks, " +
            "irqTicks, sirqTicks, stealTicks) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    int systemID = 1; // systemID is always 1

    try {
      Connection conn = DriverManager.getConnection(URL);
      PreparedStatement pstmt = conn.prepareStatement(sql);

      pstmt.setInt(1, systemID);
      pstmt.setLong(2, timestamp);
      pstmt.setInt(3, coreNum);
      pstmt.setLong(4, currFreq);
      pstmt.setLong(5, maxFreq);
      pstmt.setLong(6, userTicks);
      pstmt.setLong(7, niceTicks);
      pstmt.setLong(8, sysTicks);
      pstmt.setLong(9, idleTicks);
      pstmt.setLong(10, ioTicks);
      pstmt.setLong(11, irqTicks);
      pstmt.setLong(12, sirqTicks);
      pstmt.setLong(13, stealTicks);
      pstmt.executeUpdate();
      conn.close();

      logger.info("Entry inserted into CpuData table");
      logger.debug(String.format("Data: (%1$d, %2$d, %3$d, %4$d, %5$d, " +
                      "%6$d, %7$d, %8$d, %9$d, %10$d, %11$d, %12$d, %13$d)",
              systemID, timestamp, coreNum, currFreq, maxFreq, userTicks,
              niceTicks, sysTicks, idleTicks, ioTicks, irqTicks, sirqTicks,
              stealTicks));
    } catch (SQLException e) {
      logger.error(e.getMessage());
    }
  }

  /**
   * removeProcessData
   *
   * Removes processData entries with the given timestamp
   *
   * @param timestamp timestamp of entries to be removed
   */
  public static void removeCpuData(long timestamp) {
    String sql = "DELETE FROM ProcessData WHERE timestamp = " + timestamp + ";";
    try {
      Connection conn = DriverManager.getConnection(URL);
      Statement stmt = conn.createStatement();
      stmt.execute(sql);

      conn.close();

      logger.info("CpuData table purged");
    } catch (SQLException e) {
      logger.error(e.getMessage());
    }
  }

  protected static ResultSet query(String sql) {
    ResultSet rs = null;
    try {
      Connection conn = DriverManager.getConnection(URL);
      Statement stmt = conn.createStatement();
      rs = stmt.executeQuery(sql);

    } catch (SQLException e) {
      logger.error(e.getMessage());
    }

    return rs;
  }
}

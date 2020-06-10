import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import oshi.SystemInfo;
import oshi.hardware.*;
import oshi.software.os.OSProcess;
import oshi.software.os.OperatingSystem;

public class MeTrik {
  public static final int COLLECT_INTERVAL = 10000; // 10 seconds

  private static final Logger logger = Logger.getLogger("(MeTrik)");

  // OSHI Objects
  private static final SystemInfo si = new SystemInfo();
  private static final OperatingSystem os = si.getOperatingSystem();
  private static final HardwareAbstractionLayer hw = si.getHardware();
  private static final OperatingSystem.OSVersionInfo osInfo = os.getVersionInfo();
  private static final CentralProcessor cpu = si.getHardware().getProcessor();
  private static final GlobalMemory mem = hw.getMemory();

  public static void main(String[] args) {
    // Initialize the logger
    PropertyConfigurator.configure("log4j.properties");
    logger.info("Metric Collector initialized");

    // Create database and its tables
    DBHelper.createDB();
    DBHelper.createTables();

    // Get System info once
    getSystemInfo();

    boolean run = true;

    long prevTimestamp = 0;
    // Main loop
    while(run) {
      // Single timestamp is used for each round of collection in order
      // to synchronize entries
      long timestamp = System.currentTimeMillis();

      // Collect metrics
      getMemoryData(timestamp);
      getPowerData(timestamp);
      getProcessData(timestamp);
      getSystemData(timestamp);
      getCpuData(timestamp);

      // Purge
      purgeProcessData(prevTimestamp);
      purgeCpuData(prevTimestamp);
      prevTimestamp = timestamp;

      // Wait for next cycle
      try {
        Thread.sleep(COLLECT_INTERVAL);
      } catch (InterruptedException e) {
        logger.error(e.getMessage());
      }
    }
  }

  /**
   * getSystemInfo
   *
   * Gets information for the System table.
   * This information is static and should only be retrieved
   * at initial startup.
   */
  protected static void getSystemInfo() {
    // Get values
    String osStr = os.getFamily();
    String codeName = osInfo.getCodeName();
    String version = osInfo.getVersion();
    String cpuSignature = cpu.getProcessorIdentifier().getName();
    int cpuCores = cpu.getPhysicalProcessorCount();
    long cpuVendFreq = cpu.getProcessorIdentifier().getVendorFreq();

    logger.info("System data collected");

    // Insert
    DBHelper.insertSystem(osStr, codeName, version, cpuSignature, cpuCores, cpuVendFreq);
  }

  /**
   * getMemoryData
   *
   * Gets information for the MemoryData table.
   *
   * @param timestamp UNIX timestamp in milliseconds
   */
  protected static void getMemoryData(long timestamp) {
    // Get values
    long avail = mem.getAvailable();
    long total = mem.getTotal();

    logger.info("Memory data collected");

    // Insert
    DBHelper.insertMemoryData(timestamp, avail, total);
  }

  /**
   * getMemoryData
   *
   * Gets information for the PowerData table.
   *
   * @param timestamp UNIX timestamp in milliseconds
   */
  protected static void getPowerData(long timestamp) {
    // Get values
    PowerSource pow = hw.getPowerSources()[0];

    double currCapPer = pow.getRemainingCapacityPercent();
    double currCapTime = pow.getTimeRemainingInstant();
    double temp = pow.getTemperature();
    int isCharg = pow.isCharging() ? 1 : 0;

    logger.info("Power data collected");

    // Insert
    DBHelper.insertPowerData(timestamp, currCapPer, currCapTime, temp, isCharg);
  }

  /**
   * getProcessData
   *
   * Gets information for the ProcessData table.
   * Inserts an entry for each active process on every pass.
   *
   * @param timestamp UNIX timestamp in milliseconds
   */
  protected static void getProcessData(long timestamp) {
    // Collect data for each process
    OSProcess[] procs = os.getProcesses();
    for(OSProcess proc : procs) {
      // Get values
      int procID = proc.getProcessID();
      String name = proc.getName();
      String user = proc.getUser();
      long startTime = proc.getStartTime();
      long upTime = proc.getUpTime();
      double cpuUsage = proc.calculateCpuPercent();

      // Insert
      DBHelper.insertProcessData(timestamp, procID, name, user, startTime, upTime, cpuUsage);
    }
  }

  /**
   * purgeProcessData
   *
   * Removes all entries from ProcessData table that match
   * the given timestamp
   *
   * @param timestamp timestamp of entries to be removed
   */
  protected static void purgeProcessData(long timestamp) {
    DBHelper.removeProcessData(timestamp);
  }

  /**
   * getSystemData
   *
   * Gets information for the SystemData table
   *
   * @param timestamp UNIX timestamp in milliseconds
   */
  protected static void getSystemData(long timestamp) {
    // Get values
    long bootTime = os.getSystemBootTime();
    long upTime = os.getSystemUptime();
    int procs = os.getProcessCount();
    int servs = os.getServices().length;
    int threads = os.getThreadCount();

    // Insert
    DBHelper.insertSystemData(timestamp, bootTime, upTime, procs, servs, threads);
  }

  /**
   * getCpuData
   *
   * Gets information for the CpuData table.
   * Inserts an entry for each logical processor on every pass.
   *
   * @param timestamp UNIX timestamp in milliseconds
   */
  protected static void getCpuData(long timestamp) {
    // Collect data for each logical processor
    CentralProcessor.LogicalProcessor[] cores = cpu.getLogicalProcessors();
    long[] currFreqs = cpu.getCurrentFreq();
    long[][] ticks = cpu.getProcessorCpuLoadTicks();

    for(CentralProcessor.LogicalProcessor core : cores) {
      // Get values
      int coreNum = core.getProcessorNumber();
      long currFreq = currFreqs[coreNum];
      long maxFreq = cpu.getMaxFreq();
      long userTicks = ticks[coreNum][0];
      long niceTicks = ticks[coreNum][1];
      long sysTicks = ticks[coreNum][2];
      long idleTicks = ticks[coreNum][3];
      long ioTicks = ticks[coreNum][4];
      long irqTicks = ticks[coreNum][5];
      long sirqTicks = ticks[coreNum][6];
      long stealTicks = ticks[coreNum][7];

      // Insert
      DBHelper.insertCpuData(timestamp, coreNum, currFreq, maxFreq, userTicks,
              niceTicks, sysTicks, idleTicks, ioTicks, irqTicks, sirqTicks,
              stealTicks);
    }

  }

  /**
   * purgeCpuData
   *
   * Removes all entries from CpuData table that match
   * the given timestamp
   *
   * @param timestamp timestamp of entries to be removed
   */
  protected static void purgeCpuData(long timestamp) {
    DBHelper.removeCpuData(timestamp);
  }
}

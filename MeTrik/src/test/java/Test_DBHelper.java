import static org.junit.Assert.*;
import org.junit.Test;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Test_DBHelper {

	@Test
	public void testCreateDB() {
		DBHelper.createDB();
		File db = new File("sqlite/db/Trik.db");
		assertTrue(db.exists());
	}

	@Test
	public void testCreateTables() {
		DBHelper.createTables();

		String[] tables = {"System", "MemoryData", "PowerData", "ProcessData",
						"SystemData", "CPUData"};
		int fails = 0;
		for (String table : tables) {
			ResultSet rs = DBHelper.query("Select * from " + table + ";");
			if (rs == null) {
				fails++;
			}
		}

		assertEquals(0, fails);
	}

	@Test
	public void testInsertSystem() {
		String os = "Mac";
		String codeName = "Catalina";
		String version = "11.1";
		String cpuSignature = "Intel i7";
		int cpuCores = 4;
		long cpuVendFreq = 10000000;

		DBHelper.createDB();
		DBHelper.createTables();
		DBHelper.insertSystem(os, codeName, version, cpuSignature, cpuCores, cpuVendFreq);

		ResultSet rs = DBHelper.query("Select * from System where os = 'Mac';");

		try {
			assertEquals(os, rs.getString("os"));
			assertEquals(codeName, rs.getString("codeName"));
			assertEquals(version, rs.getString("version"));
			assertEquals(cpuSignature, rs.getString("cpuSignature"));
			assertEquals(cpuCores, rs.getInt("cpuCores"));
			assertEquals(cpuVendFreq, rs.getLong("cpuVendFreq"));
		} catch (SQLException e) {
			fail("SQL Exception:" + e.getMessage());
		}
	}

	@Test
	public void testInsertMemoryData() {
		long timestamp = System.currentTimeMillis();
		long avail = 100000;
		long total = 10000000;

		DBHelper.createDB();
		DBHelper.createTables();
		DBHelper.insertMemoryData(timestamp, avail, total);

		ResultSet rs = DBHelper.query("Select * from MemoryData " +
						"where timestamp = " + timestamp + ";");

		try {
			assertEquals(timestamp, rs.getLong("timestamp"));
			assertEquals(avail, rs.getLong("avail"));
			assertEquals(total, rs.getLong("total"));
		} catch (SQLException e) {
			fail("SQL Exception:" + e.getMessage());
		}
	}

	@Test
	public void testInsertPowerData() {
		long timestamp = System.currentTimeMillis();
		double currCapPer = 20.5;
		double currCapTime = 1000.1;
		double temp = 23.6;
		int isCharg = 1;

		DBHelper.createDB();
		DBHelper.createTables();
		DBHelper.insertPowerData(timestamp, currCapPer, currCapTime, temp, isCharg);

		ResultSet rs = DBHelper.query("Select * from PowerData " +
						"where timestamp = " + timestamp + ";");

		try {
			assertEquals(timestamp, rs.getLong("timestamp"));
			assertEquals(currCapPer, rs.getDouble("currCapPer"), 0);
			assertEquals(currCapTime, rs.getDouble("currCapTime"), 0);
			assertEquals(temp, rs.getDouble("temp"), 0);
			assertEquals(isCharg, rs.getInt("isCharg"));
		} catch (SQLException e) {
			fail("SQL Exception:" + e.getMessage());
		}
	}

	@Test
	public void testInsertProcessData() {
		long timestamp = System.currentTimeMillis();
		int procID = 1;
		String name = "My Process";
		String user = "me";
		long startTime = 100000000;
		long upTime = 10000000;
		double cpuUsage = 1.0;

		DBHelper.createDB();
		DBHelper.createTables();
		DBHelper.insertProcessData(timestamp, procID, name, user, startTime, upTime, cpuUsage);

		ResultSet rs = DBHelper.query("Select * from ProcessData " +
						"where timestamp = " + timestamp + ";");

		try {
			assertEquals(timestamp, rs.getLong("timestamp"));
			assertEquals(procID, rs.getInt("procID"));
			assertEquals(name, rs.getString("name"));
			assertEquals(user, rs.getString("user"));
			assertEquals(startTime, rs.getLong("startTime"));
			assertEquals(upTime, rs.getLong("upTime"));
			assertEquals(cpuUsage, rs.getDouble("cpuUsage"), 0);
		} catch (SQLException e) {
			fail("SQL Exception:" + e.getMessage());
		}
	}

	@Test
	public void testInsertSystemData() {
		long timestamp = System.currentTimeMillis();
		long bootTime = 10000;
		long upTime = 100000;
		int procs = 1;
		int servs = 2;
		int threads = 3;

		DBHelper.createDB();
		DBHelper.createTables();
		DBHelper.insertSystemData(timestamp, bootTime, upTime, procs, servs, threads);

		ResultSet rs = DBHelper.query("Select * from SystemData " +
						"where timestamp = " + timestamp + ";");

		try {
			assertEquals(timestamp, rs.getLong("timestamp"));
			assertEquals(bootTime, rs.getLong("bootTime"));
			assertEquals(upTime, rs.getLong("upTime"));
			assertEquals(procs, rs.getInt("procs"));
			assertEquals(servs, rs.getInt("servs"));
			assertEquals(threads, rs.getInt("threads"));
		} catch (SQLException e) {
			fail("SQL Exception:" + e.getMessage());
		}
	}

	@Test
	public void testInsertCpuData() {
		long timestamp = System.currentTimeMillis();
		int coreNum = 1;
		long currFreq = 100000;
		long maxFreq = 1000000;
		long userTicks = 1;
		long niceTicks = 2;
		long sysTicks = 3;
		long idleTicks = 4;
		long ioTicks = 5;
		long irqTicks = 6;
		long sirqTicks = 7;
		long stealTicks = 8;

		DBHelper.createDB();
		DBHelper.createTables();
		DBHelper.insertCpuData(timestamp, coreNum, currFreq, maxFreq, userTicks,
						niceTicks, sysTicks, idleTicks, ioTicks, irqTicks, sirqTicks, stealTicks);

		ResultSet rs = DBHelper.query("Select * from CpuData " +
						"where timestamp = " + timestamp + ";");

		try {
			assertEquals(timestamp, rs.getLong("timestamp"));
			assertEquals(coreNum, rs.getInt("coreNum"));
			assertEquals(currFreq, rs.getLong("currFreq"));
			assertEquals(maxFreq, rs.getLong("maxFreq"));
			assertEquals(userTicks, rs.getLong("userTicks"));
			assertEquals(niceTicks, rs.getLong("niceTicks"));
			assertEquals(sysTicks, rs.getLong("sysTicks"));
			assertEquals(idleTicks, rs.getLong("idleTicks"));
			assertEquals(ioTicks, rs.getLong("ioTicks"));
			assertEquals(irqTicks, rs.getLong("irqTicks"));
			assertEquals(sirqTicks, rs.getLong("sirqTicks"));
			assertEquals(stealTicks, rs.getLong("stealTicks"));
		} catch (SQLException e) {
			fail("SQL Exception:" + e.getMessage());
		}
	}

}

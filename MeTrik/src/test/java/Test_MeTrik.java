import static org.junit.Assert.*;
import org.junit.Test;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Test_MeTrik {

  @Test
  public void testGetSystemInfo() {
    DBHelper.createDB();
    DBHelper.createTables();
    MeTrik.getSystemInfo();

    ResultSet rs = DBHelper.query("SELECT COUNT(*) FROM System;");
    try {
      int results = rs.getInt(1);
      assertEquals(1, results);
    } catch (SQLException e) {
      fail("SQL Exception:" + e.getMessage());
    }
  }

  @Test
  public void testGetMemoryData() {
    DBHelper.createDB();
    DBHelper.createTables();

    long timestamp = System.currentTimeMillis();
    MeTrik.getMemoryData(timestamp);

    ResultSet rs = DBHelper.query("SELECT COUNT(*) FROM MemoryData;");
    try {
      int results = rs.getInt(1);
      assertEquals(1, results);
    } catch (SQLException e) {
      fail("SQL Exception:" + e.getMessage());
    }
  }

  @Test
  public void testGetPowerData() {
    DBHelper.createDB();
    DBHelper.createTables();

    long timestamp = System.currentTimeMillis();
    MeTrik.getPowerData(timestamp);

    ResultSet rs = DBHelper.query("SELECT COUNT(*) FROM PowerData");
    try {
      int results = rs.getInt(1);
      assertEquals(1, results);
    } catch (SQLException e) {
      fail("SQL Exception:" + e.getMessage());
    }
  }

  @Test
  public void testGetProcessData() {
    DBHelper.createDB();
    DBHelper.createTables();

    long timestamp = System.currentTimeMillis();
    MeTrik.getProcessData(timestamp);

    ResultSet rs = DBHelper.query("SELECT COUNT(*) FROM ProcessData;");
    try {
      int results = rs.getInt(1);
      assertTrue(results >= 1);
    } catch (SQLException e) {
      fail("SQL Exception:" + e.getMessage());
    }
  }

  @Test
  public void testGetSystemData() {
    DBHelper.createDB();
    DBHelper.createTables();

    long timestamp = System.currentTimeMillis();
    MeTrik.getSystemData(timestamp);

    ResultSet rs = DBHelper.query("SELECT COUNT(*) FROM SystemData;");
    try {
      int results = rs.getInt(1);
      assertEquals(1, results);
    } catch (SQLException e) {
      fail("SQL Exception:" + e.getMessage());
    }
  }

  @Test
  public void testGetCpuData() {
    DBHelper.createDB();
    DBHelper.createTables();

    long timestamp = System.currentTimeMillis();
    MeTrik.getCpuData(timestamp);

    ResultSet rs = DBHelper.query("SELECT COUNT(*) FROM CpuData;");
    try {
      int results = rs.getInt(1);
      assertTrue(results >= 1);
    } catch (SQLException e) {
      fail("SQL Exception:" + e.getMessage());
    }
  }
}

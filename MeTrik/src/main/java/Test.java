import oshi.SystemInfo;
import oshi.hardware.*;
import oshi.software.os.OperatingSystem;

public class Test {

  public static void main (String[] args) {
    // Generic sysinfo object for this machine
    SystemInfo si = new SystemInfo();

    // Two major objects of sysinfo: OS and hardware
    OperatingSystem os = si.getOperatingSystem();
    HardwareAbstractionLayer hw = si.getHardware();

    // CPU object
    CentralProcessor cpu = hw.getProcessor();

    // Memory object
    GlobalMemory gm = hw.getMemory();

    // PowerSource object (battery)
    PowerSource ps = hw.getPowerSources()[0];
    int perRem = (int) (ps.getRemainingCapacityPercent() * 100);
    int timeRemE = (int) (ps.getTimeRemainingEstimated() / 60);
    int timeRemI = (int) (ps.getTimeRemainingInstant() / 60);

    // Computer System - gives basic info about the Computer (Manufacturer, model, etc.) - not that useful
    ComputerSystem cs = hw.getComputerSystem();

    System.out.println("Battery level: " + perRem + "%");
    System.out.println("Time Remaining (Estimated): " + timeRemE + " minutes");
    System.out.println("Time Remaining (Instant): " + timeRemI + " minutes");


    System.out.println("Manufacturer: " + cs.getManufacturer() + " | " 
      + "Model: " + cs.getModel());

    System.out.println("Manufacturer: " + os.getManufacturer()+ " | " 
      + "Family: " + os.getFamily());
  }
}

package test_task;

/**
 * Test task traffic
 *
 */


import org.pcap4j.core.*;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.*;



public class App 
{

    private static long trafficSummary = 0;
    private static int minLimit = 1024;
    private static int maxlimit = 1073741824;
    private static Timestamp minTimestamp = Timestamp.valueOf("1970-01-01 00:00:00.00");
    private static Timestamp maxTimestamp = Timestamp.valueOf("1970-01-01 00:00:00.00");
    private static Inet4Address testAddr = null;

    public synchronized static long getTrafficSummary() {
        return trafficSummary;
    }

    public synchronized static void setTrafficSummary(long trafficSummary) {
        App.trafficSummary = trafficSummary;
    }

    public synchronized static int getMinLimit() {
        return minLimit;
    }

    public synchronized static void setMinLimit(int minLimit) {
        App.minLimit = minLimit;
    }

    public synchronized static int getMaxlimit() {
        return maxlimit;
    }

    public synchronized static void setMaxlimit(int maxlimit) {
        App.maxlimit = maxlimit;
    }

    public synchronized static Timestamp getMinTimestamp() {
        return minTimestamp;
    }

    public synchronized static void setMinTimestamp(Timestamp minTimestamp) {
        App.minTimestamp = minTimestamp;
    }

    public synchronized static Timestamp getMaxTimestamp() {
        return maxTimestamp;
    }

    public synchronized static void setMaxTimestamp(Timestamp maxTimestamp) {
        App.maxTimestamp = maxTimestamp;
    }

    public synchronized static Inet4Address getTestAddr() {
        return testAddr;
    }

    public synchronized static void setTestAddr(Inet4Address testAddr) {
        App.testAddr = testAddr;
    }

    public static void main(String[] args )
    {
        System.out.println( "Test task" );
        if(args[0]!= null) setFilterAddress(args[0]);

        Thread dbreader = new Thread(new LimitsReader());
        dbreader.start();
        PcapNetworkInterface device = getNetworkDevice();
        System.out.println("You chose: " + device);

        if (device == null) {
            System.out.println("No device chosen.");
            System.exit(1);
        }
        Thread packets = new Thread(new PacketCatcher(device));
        Thread producer = new Thread(new TrafficProducer());
        producer.start();
        packets.start();

    }

    static PcapNetworkInterface getNetworkDevice() {
        PcapNetworkInterface device = null;
        try {
            //InetAddress addr = InetAddress.getByName("192.168.0.100");//
            InetAddress addr = InetAddress.getLocalHost();
            device = Pcaps.getDevByAddress(addr);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (PcapNativeException e) {
            e.printStackTrace();
        }

        return device;
    }

    static void setFilterAddress(String address){
    System.out.println(address);
    try {
        App.setTestAddr((Inet4Address) Inet4Address.getByName(address));
        System.out.println("test address is set");
    } catch (UnknownHostException e) {
        e.printStackTrace();
        System.out.println("test address is null");
    }
}

}

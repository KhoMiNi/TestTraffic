package test_task;

import org.pcap4j.core.*;
import org.pcap4j.packet.IpV4Packet;
import org.pcap4j.packet.Packet;

import java.net.Inet4Address;

public class PacketCatcher implements Runnable {
    PcapNetworkInterface device;
    PcapHandle handle = null;
    public PacketCatcher(PcapNetworkInterface device) {
        this.device = device;
    }

    @Override
    public void run() {
        deviceOpen();
        PacketListener listener = new PacketListener() {
            @Override
            public void gotPacket(Packet packet) {
                System.out.println(packet.length());
                IpV4Packet ipV4Packet = packet.get(IpV4Packet.class);
                Inet4Address srcAddr = ipV4Packet.getHeader().getSrcAddr();
                trafficCount(packet.length(), srcAddr);
                Thread.yield();
            }
        };
//
        try {
            int maxPackets = -1;
            handle.loop(maxPackets, listener);

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (PcapNativeException e) {
            System.out.println("failed to create handle");
            e.printStackTrace();
        }catch (NotOpenException e) {
            System.out.println("failed to open device");
            e.printStackTrace();
        }

        handle.close();
    }

    public void deviceOpen(){
        try{
            handle = device.openLive(65536, PcapNetworkInterface.PromiscuousMode.PROMISCUOUS, 50);
        }catch (PcapNativeException e){
            System.out.println("failed to create handle");
            e.printStackTrace();
        }
    }

    public void trafficCount(int length, Inet4Address srcAddr){
        if(App.getTestAddr() == null) {
            App.setTrafficSummary(App.getTrafficSummary()+ length);
        }else if(srcAddr.equals(App.getTestAddr())){
            App.setTrafficSummary(App.getTrafficSummary()+ length);
        }
    }
}

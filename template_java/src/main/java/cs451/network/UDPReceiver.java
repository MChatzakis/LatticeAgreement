package cs451.network;

import cs451.Constants;
import cs451.commonUtils.CommonUtils;
import cs451.links.Link;
import cs451.structures.Deliverer;
import cs451.structures.Message;

import java.net.DatagramPacket;
import java.net.SocketException;

import static cs451.Constants.MAX_PACKET_SIZE;

/**
 * UDPReceiver is contains a UDPReceiver routine, that should be called as
 * a thread runnable.
 */
public class UDPReceiver extends UDPInstance implements Runnable{
    private Deliverer deliverer;

    public UDPReceiver(int port, Deliverer deliverer) throws SocketException {
        super(port);
        this.deliverer = deliverer;
    }

    @Override
    public void run() {
        System.out.println(">>UDP Receiver routine started..");

        byte[] receive = new byte[MAX_PACKET_SIZE];
        DatagramPacket packet2get;
        while(true) {
            packet2get = new DatagramPacket(receive, receive.length);
            try {
                socket.receive(packet2get);
                Message msgReceived = (Message) CommonUtils.getObjectFromBytes(receive);

                if(Constants.UDP_MESSAGING_VERBOSE){
                    System.out.println("[UDPReceiver]: Delivery " + msgReceived);
                }

                deliverer.deliver(msgReceived);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}

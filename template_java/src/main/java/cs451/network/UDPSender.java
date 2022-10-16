package cs451.network;

import cs451.commonUtils.CommonUtils;
import cs451.structures.Message;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.SocketException;

public class UDPSender extends UDPInstance {

    public UDPSender() throws SocketException {
        super();
    }

    public void send(Message message, String toIP, int toPort) {
        try {
            byte [] data2sent = CommonUtils.getBytesOfObject(message);
            DatagramPacket packet2send = new DatagramPacket(data2sent, data2sent.length, InetAddress.getByName(toIP), toPort);
            socket.send(packet2send);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

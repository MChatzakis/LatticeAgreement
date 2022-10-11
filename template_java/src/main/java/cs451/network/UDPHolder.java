package cs451.network;

import cs451.commonUtils.CommonUtils;
import cs451.structures.Message;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class UDPHolder {
    public static final int MAX_PACKET_SIZE = 65535; //!need to recheck that

    private DatagramSocket serverSocket;

    public UDPHolder(int port) throws SocketException {
        this.serverSocket = new DatagramSocket(port);
    }

    public void UDPSend(Message message, String toIP, int toPort){
        try {
            byte [] data2sent = CommonUtils.getBytesOfObject(message);
            DatagramPacket packet2send = new DatagramPacket(data2sent, data2sent.length, InetAddress.getByName(toIP), toPort);
            serverSocket.send(packet2send);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Message UDPRecieve(){
        byte[] receive = new byte[MAX_PACKET_SIZE];
        DatagramPacket packet2get = new DatagramPacket(receive, receive.length);

        try {
            serverSocket.receive(packet2get);
            Message msgReceived = (Message) CommonUtils.getObjectFromBytes(receive);
            return msgReceived;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


}

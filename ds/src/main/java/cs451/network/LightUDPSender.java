package cs451.network;

import cs451.Constants;
import cs451.commonUtils.CommonUtils;
import cs451.messaging.Message;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;

public class LightUDPSender {

    public LightUDPSender(){

    }

    public void sendBatch(ArrayList<Message> batch, String toIP, int toPort) {
        try {
            DatagramSocket socket = new DatagramSocket();

            ByteArrayOutputStream bStream = new ByteArrayOutputStream();
            ObjectOutput oo = new ObjectOutputStream(bStream);
            oo.writeObject(batch);

            byte[] data2sent = bStream.toByteArray();
            byte[] compressedData2sent = CommonUtils.compressByteArray(data2sent);

            DatagramPacket packet2send = new DatagramPacket(compressedData2sent, compressedData2sent.length, InetAddress.getByName(toIP), toPort);

            if (Constants.UDP_MESSAGING_VERBOSE) {
                System.out.println("[UDPSender]: Sent " + batch);
            }

            socket.send(packet2send);

            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Exception in Light Sender: Could not send the message.");
        }

    }


    public void send(Message message, String toIP, int toPort) {
        try {
            DatagramSocket socket = new DatagramSocket();

            byte [] data2sent = CommonUtils.getBytesOfObject(message);
            byte [] compressedData2sent = CommonUtils.compressByteArray(data2sent);

            DatagramPacket packet2send = new DatagramPacket(compressedData2sent, compressedData2sent.length, InetAddress.getByName(toIP), toPort);

            if(Constants.UDP_MESSAGING_VERBOSE){
                System.out.println("[UDPSender]: Sent " + message);
            }

            socket.send(packet2send);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

package cs451.network;

import cs451.Constants;
import cs451.commonUtils.CommonUtils;
import cs451.structures.Deliverer;
import cs451.messaging.Message;

import java.io.*;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;

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
        //System.out.println(">>UDP Receiver routine started..");
        receiveBatch();
    }

    public void receiveBatch(){
        byte[] receive = new byte[MAX_PACKET_SIZE];
        DatagramPacket packet2get;
        while(true) {
            packet2get = new DatagramPacket(receive, receive.length);
            try {
                socket.receive(packet2get);

                byte [] decompressedBytes = CommonUtils.decompressByteArray(receive);
                ArrayList<Message>batch = (ArrayList<Message>)(new ObjectInputStream(new ByteArrayInputStream(decompressedBytes))).readObject();

                if(Constants.UDP_MESSAGING_VERBOSE){
                    System.out.println("[UDPReceiver]: Delivery Batch" + batch);
                }

                for(Message msgReceived : batch){
                    deliverer.deliver(msgReceived);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public void sendBatch(ArrayList<Message>batch, String toIP, int toPort){
        try {
            ByteArrayOutputStream bStream = new ByteArrayOutputStream();
            ObjectOutput oo = new ObjectOutputStream(bStream);
            oo.writeObject(batch);

            byte [] data2sent = bStream.toByteArray();
            byte [] compressedData2sent = CommonUtils.compressByteArray(data2sent);

            DatagramPacket packet2send = new DatagramPacket(compressedData2sent, compressedData2sent.length, InetAddress.getByName(toIP), toPort);

            if(Constants.UDP_MESSAGING_VERBOSE){
                System.out.println("[UDPSender]: Sent " + batch);
            }

            socket.send(packet2send);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

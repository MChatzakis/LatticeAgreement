package cs451.links;

import cs451.commonUtils.CommonUtils;
import cs451.commonUtils.Logger;
import cs451.network.UDPSender;
import cs451.structures.Deliverer;
import cs451.structures.Message;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class FairLossLink extends Link{

    Deliverer deliverer;
    //UDPSender
    //UDPReceiver
    @Override
    public void send(Message message, String toIP, int toPort){

    }

    @Override
    public void deliver(Message message) {
        deliverer.deliver(message);
    }


}

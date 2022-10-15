package cs451.links;

import cs451.structures.Deliverer;
import cs451.structures.Message;

import java.net.SocketException;

public class StubbornLink extends Link{

    FairLossLink fllink;
    Deliverer deliverer;

    //add timer
    @Override
    public void send(Message message, String toIP, int toPort){
        fllink.send(message, toIP, toPort);
    }

    @Override
    public void deliver(Message message) {
        deliverer.deliver(message);
    }
}

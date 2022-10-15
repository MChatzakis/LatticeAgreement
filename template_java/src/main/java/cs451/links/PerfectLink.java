package cs451.links;

import cs451.commonUtils.Logger;
import cs451.structures.Deliverer;
import cs451.structures.Message;

import java.net.SocketException;
import java.util.HashSet;
import java.util.Set;

public class PerfectLink extends Link{
    private Set<Message> deliveredMessages;

    private StubbornLink slink;
    private Deliverer deliverer;

    public PerfectLink() {
        deliveredMessages = new HashSet<>();
    }

    @Override
    public void send(Message message, String toIP, int toPort){
        slink.send(message, toIP, toPort);
    }

    @Override
    public void deliver(Message message) {
        if(!deliveredMessages.contains(message)){
            deliveredMessages.add(message);
            deliverer.deliver(message);
        }
    }
}

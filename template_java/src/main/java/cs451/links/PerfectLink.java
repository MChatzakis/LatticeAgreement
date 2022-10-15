package cs451.links;

import cs451.Host;
import cs451.commonUtils.Logger;
import cs451.structures.Deliverer;
import cs451.structures.Message;

import java.net.SocketException;
import java.util.HashSet;
import java.util.Set;

public class PerfectLink extends Link{
    private Set<Message> deliveredMessages;

    private StubbornLink slink;

    public PerfectLink(Deliverer deliverer, int port) throws SocketException {
        this.deliverer = deliverer;

        this.slink = new StubbornLink(this, port);
        this.deliveredMessages = new HashSet<>();
    }

    @Override
    public void send(Message message, Host host){
        slink.send(message, host);
    }

    @Override
    public void startReceiving() {
        slink.startReceiving();
    }

    @Override
    public void deliver(Message message) {
        if(!deliveredMessages.contains(message)){
            deliveredMessages.add(message);
            deliverer.deliver(message);
        }
    }
}

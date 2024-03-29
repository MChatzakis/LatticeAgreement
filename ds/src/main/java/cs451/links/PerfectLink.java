package cs451.links;

import cs451.Constants;
import cs451.Host;
import cs451.commonUtils.MHIDPair;
import cs451.structures.Deliverer;
import cs451.messaging.Message;

import java.net.SocketException;
import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Perfect Link implementation
 */
public class PerfectLink extends Link{
    //private Set<Message> deliveredMessages;
    private Set<Message> deliveredMessages;

    private StubbornLink slink;

    public PerfectLink(Deliverer deliverer, int port, ArrayList<Host> hosts, short selfID) throws SocketException {
        this.deliverer = deliverer;
        this.slink = new StubbornLink(this, port, hosts, selfID);
        this.deliveredMessages = ConcurrentHashMap.newKeySet();
    }

    @Override
    public void sendBatch(ArrayList<Message> batch, Host host) {
        if(Constants.PL_MESSAGING_VERBOSE){
            System.out.println("[Perfect Link]: Sent " + batch);
        }
        slink.sendBatch(batch, host);
    }

    @Override
    public void startReceiving() {
        slink.startReceiving();
    }

    @Override
    public void deliver(Message message) {
        if(Constants.PL_MESSAGING_VERBOSE) {
            System.out.println("[Perfect Link]: 1. Got a message for delivery. "+ message +" Set size="+deliveredMessages.size());
            System.out.println("[Perfect Link]: 2. Current set " + deliveredMessages);
        }

        //MHIDPair p = new MHIDPair(message.getId(), message.getOriginalFrom());
        if(!deliveredMessages.contains(message)){
            deliveredMessages.add(message);

            if(Constants.PL_MESSAGING_VERBOSE) {
                System.out.println("[Perfect Link]: 3. Delivery " + message);
                System.out.println("[Perfect Link]: 4. Current set " + deliveredMessages);
                System.out.println();
            }

            deliverer.deliver(message);
        }
        else{
            if(Constants.PL_MESSAGING_VERBOSE) {
                System.out.println("[Perfect Link]: 3. The previous message is a duplicate, ignored");
                System.out.println();
            }
        }
    }

    public void freeResources(){
        slink.freeResources();
    }

}

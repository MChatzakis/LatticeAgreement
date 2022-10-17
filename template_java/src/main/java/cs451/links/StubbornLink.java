package cs451.links;

import cs451.Constants;
import cs451.Host;
import cs451.commonUtils.MSPair;
import cs451.structures.Deliverer;
import cs451.structures.Message;

import java.net.SocketException;
import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import static cs451.Constants.RETRANSMISSION_DELAY;

public class StubbornLink extends Link{

    private FairLossLink fllink;
    private Timer retransmissionTimer;
    private Set<MSPair> sent;

    public StubbornLink(Deliverer deliverer, int port) throws SocketException {
        this.deliverer = deliverer;
        this.retransmissionTimer = new Timer("RetransmissionTimer");

        this.sent = ConcurrentHashMap.newKeySet();
        this.fllink = new FairLossLink(this,port);

        setTimer();
    }

    private void setTimer(){
        TimerTask retransmissionTask = new TimerTask() {
            public void run() {
                retransmit();
            }
        };

        //retransmissionTimer.schedule(retransmissionTask, RETRANSMISSION_DELAY);
        retransmissionTimer.scheduleAtFixedRate(retransmissionTask, RETRANSMISSION_DELAY,RETRANSMISSION_DELAY);
    }

    @Override
    public void send(Message message, Host host){
        if(Constants.SBL_MESSAGING_VERBOSE){
            System.out.println("[Stubborn Link]: Sent " + message);
        }

        sent.add(new MSPair(message, host));
        fllink.send(message, host);
    }

    @Override
    public void startReceiving() {
        fllink.startReceiving();
    }

    @Override
    public void deliver(Message message) {
        //if message is ACK ... do ... else:
        if(Constants.SBL_MESSAGING_VERBOSE){
            System.out.println("[Stubborn Link]: Delivery " + message);
        }
        deliverer.deliver(message);
    }

    public void retransmit(){
        for(MSPair p : sent){
            if(Constants.SBL_MESSAGING_VERBOSE){
                System.out.println("[Stubborn Link]: Re-Sent " + p.getMessage());
            }
            fllink.send(p.getMessage(), p.getHost());
        }
    }
}

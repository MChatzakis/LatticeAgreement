package cs451.links;

import cs451.Host;
import cs451.commonUtils.MSPair;
import cs451.structures.Deliverer;
import cs451.structures.Message;

import java.net.SocketException;
import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import static cs451.Constants.RETRANSMISSION_DELAY;

public class StubbornLink extends Link{

    private FairLossLink fllink;
    private Timer retransmissionTimer;
    private Set<MSPair> sent;

    public StubbornLink(Deliverer deliverer){
        this.deliverer = deliverer;
        this.retransmissionTimer = new Timer("RetransmissionTimer");

        this.sent = new HashSet<>();

        setTimer();
    }

    private void setTimer(){
        TimerTask retransmissionTask = new TimerTask() {
            public void run() {
                retransmit();
            }
        };

        retransmissionTimer.schedule(retransmissionTask, RETRANSMISSION_DELAY);
    }

    @Override
    public void send(Message message, Host host){
        sent.add(new MSPair(message, host));
        fllink.send(message, host);
    }

    @Override
    public void deliver(Message message) {
        //if message is ACK ... do ... else:
        deliverer.deliver(message);
    }

    public void retransmit(){
        for(MSPair p : sent){
            fllink.send(p.getMessage(), p.getHost());
        }
    }
}

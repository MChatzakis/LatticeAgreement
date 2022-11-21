package cs451.links;

import cs451.Constants;
import cs451.Host;
import cs451.commonUtils.CommonUtils;
import cs451.commonUtils.MHPair;
import cs451.structures.Deliverer;
import cs451.structures.Message;

import java.net.SocketException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static cs451.Constants.RETRANSMISSION_DELAY;

/**
 * Stubborn Link implementation
 */
public class StubbornLink extends Link{

    private FairLossLink fllink;
    private Timer retransmissionTimer;
    private Set<MHPair> sent;

    private ArrayList<Host> hosts;

    public StubbornLink(Deliverer deliverer, int port, ArrayList<Host> hosts) throws SocketException {
        this.deliverer = deliverer;
        this.hosts = hosts;

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
        retransmissionTimer.scheduleAtFixedRate(retransmissionTask, RETRANSMISSION_DELAY,RETRANSMISSION_DELAY);
    }

    @Override
    public void send(Message message, Host host){

        sent.add(new MHPair(message, host));

        if(Constants.SBL_MESSAGING_VERBOSE){
            System.out.println("[Stubborn Link]: Sent " + message + " to host " + host.getId() + " sent set = " + Arrays.toString(sent.toArray()));
        }


        fllink.send(message, host);
    }

    @Override
    public void startReceiving() {
        fllink.startReceiving();
    }

    @Override
    public void deliver(Message message) {
        if(message.isACK()){
            receiveACK(message);
        }
        else{
            if(Constants.SBL_MESSAGING_VERBOSE){
                System.out.println("[Stubborn Link]: Delivery " + message);
            }
            deliverer.deliver(message);

            sendACK(message);
        }

    }

    public void retransmit(){

        if(Constants.SBL_MESSAGING_VERBOSE){
            System.out.println("[Stubborn Link]: 1. >>>> Retransmission sent set: " + Arrays.toString(sent.toArray()) );
        }

        for(MHPair p : sent){
            if(Constants.SBL_MESSAGING_VERBOSE){
                System.out.println("[Stubborn Link]: 2. >>>> Retransmission " + p.getMessage() + " to host " + p.getHost());
            }

            fllink.send(p.getMessage(), p.getHost());
        }
    }

    private void sendACK(Message message){
        try {
            Message ackMsg = message.generateAckMessage();

            int destinationID = message.getOriginalFrom();
            Host h = CommonUtils.getHost(destinationID, hosts);

            if(Constants.SBL_MESSAGING_VERBOSE){
                System.out.println("[Stubborn Link]: Sending ACK: " + ackMsg + " to host " + h.getId());
            }

            fllink.send(ackMsg, h);
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
    }

    private void receiveACK(Message message){
        try {
            Message originalMessage = message.generateOriginalMessage();
            //System.out.println("get to = " + originalMessage.getTo());
            Host destHost = CommonUtils.getHost(originalMessage.getTo(), hosts);

            MHPair originalMSpair = new MHPair(originalMessage, destHost);

            if(Constants.SBL_MESSAGING_VERBOSE){
                System.out.print("[Stubborn Link]: Received ACK: " + message);
                System.out.println("[Stubborn Link]: Set Size Before: " + sent.size());
            }

            sent.remove(originalMSpair);

            if(Constants.SBL_MESSAGING_VERBOSE){
                System.out.println("[Stubborn Link]: Set Size After: " + sent.size());
            }

        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
    }

    public void freeResources(){
        fllink.freeResources();
    }
}

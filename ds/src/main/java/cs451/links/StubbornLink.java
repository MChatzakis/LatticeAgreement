package cs451.links;

import cs451.Constants;
import cs451.Host;
import cs451.commonUtils.CommonUtils;
import cs451.commonUtils.MSPair;
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
    private Set<MSPair> sent;

    private ArrayList<Host> hosts; //This was added later here to support acknowledge mechanism.

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
        for(MSPair p : sent){
            if(Constants.SBL_MESSAGING_VERBOSE){
                System.out.println("[Stubborn Link]: Retransmission " + p.getMessage());
            }

            fllink.send(p.getMessage(), p.getHost());
        }
    }

    private void sendACK(Message message){
        try {
            Message ackMsg = message.generateAckMessage();

            int destinationID = message.getFrom();
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
            Host destHost = CommonUtils.getHost(originalMessage.getTo(), hosts);

            MSPair originalMSpair = new MSPair(originalMessage, destHost);

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

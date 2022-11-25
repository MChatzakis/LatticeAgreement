package cs451.links;

import cs451.Constants;
import cs451.Host;
import cs451.commonUtils.CommonUtils;
import cs451.commonUtils.MHIDPair;
import cs451.commonUtils.MHPair;
import cs451.structures.Deliverer;
import cs451.messaging.Message;

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
    private Set<MHPair> sent; //!gotta need to change this shit
    //private Set<MHIDPair> sent;
    private ArrayList<Host> hosts;
    private byte selfID;

    public StubbornLink(Deliverer deliverer, int port, ArrayList<Host> hosts, byte selfID) throws SocketException {
        this.deliverer = deliverer;
        this.hosts = hosts;
        this.retransmissionTimer = new Timer("RetransmissionTimer");
        this.sent = ConcurrentHashMap.newKeySet();
        this.fllink = new FairLossLink(this,port);
        this.selfID = selfID;
        setTimer();
    }

    private void setTimer(){
        TimerTask retransmissionTask = new TimerTask() {
            public void run() {
                retransmitBatch();
            }
        };
        retransmissionTimer.scheduleAtFixedRate(retransmissionTask, RETRANSMISSION_DELAY, RETRANSMISSION_DELAY);
    }

    @Override
    public void sendBatch(ArrayList<Message> batch, Host host) {
        for(Message message : batch){
            sent.add(new MHPair(message, host.getId())); //id, destination
            //sent.add(new MHIDPair(message.getId(), host.getId()));
        }

        if(Constants.SBL_MESSAGING_VERBOSE){
            System.out.println("[Stubborn Link]: Sent " + batch + " to host " + host.getId() + " sent set = " + Arrays.toString(sent.toArray()));
        }

        fllink.sendBatch(batch, host);
    }

    @Override
    public void startReceiving() {
        fllink.startReceiving();
    }

    @Override
    public void deliver(Message message) {
        if(message.isACK()){ receiveACK(message); }
        else{
            if(Constants.SBL_MESSAGING_VERBOSE){
                System.out.println("[Stubborn Link]: Delivery " + message);
            }
            deliverer.deliver(message);
            sendACKBatch(message);
        }
    }

    public void retransmitBatch(){
        if(Constants.SBL_MESSAGING_VERBOSE){
            System.out.println("[Stubborn Link]: 1. >>>> Retransmission sent set: " + Arrays.toString(sent.toArray()) );
        }

        //System.out.println("[Stubborn Link]: >>>> Retransmit sent set size : " + sent.size());

        Map<Byte, ArrayList<Message>>hostBatch = new HashMap<>();
        for(int i=0; i<hosts.size(); i++){
            byte hostID = hosts.get(i).getId();
            hostBatch.put(hostID, new ArrayList<Message>());
        }
        for(MHPair p : sent){
        //for(MHIDPair p : sent){
            if(Constants.SBL_MESSAGING_VERBOSE){
                System.out.println("[Stubborn Link]: 2. >>>> Retransmission " + p.getMessage() + " to host " + p.getHostID());
            }

            //ArrayList<Message>batch = new ArrayList<>();
            //batch.add(p.getMessage());
            //fllink.sendBatch(batch, CommonUtils.getHost(p.getHostID(), hosts));
            hostBatch.get(p.getHostID()).add(p.getMessage());
        }

        for(int i=0; i<hosts.size(); i++){
            //hostBatch.put((byte) i, new ArrayList<Message>());
            byte hostID = hosts.get(i).getId();
            fllink.sendBatch(hostBatch.get(hostID), CommonUtils.getHost(hostID, hosts));
        }
    }


    private void sendACKBatch(Message message){
        try {
            Message ackMsg = message.generateAckMessage();

            //byte destinationID = message.getOriginalFrom();
            byte destinationID = message.getRelayFrom();

            Host h = CommonUtils.getHost(destinationID, hosts);

            if(Constants.SBL_MESSAGING_VERBOSE){
                System.out.println("[Stubborn Link]: Sending ACK: " + ackMsg + " to host " + h.getId());
            }

            ArrayList<Message>batch = new ArrayList<>();
            batch.add(ackMsg);
            fllink.sendBatch(batch, h);
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
    }

    private void receiveACK(Message message){
        try {
            Message originalMessage = message.generateOriginalMessage();
            Host destHost = CommonUtils.getHost(originalMessage.getTo(), hosts);
            MHPair originalMSpair = new MHPair(originalMessage, destHost.getId());
            //MHIDPair originalMSpair = new MHIDPair(originalMessage.getId(), destHost.getId()); //elpizw?!

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

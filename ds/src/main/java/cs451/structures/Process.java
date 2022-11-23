package cs451.structures;

import cs451.Constants;
import cs451.Host;
import cs451.broadcast.BestEffortBroadcast;
import cs451.broadcast.Broadcast;
import cs451.broadcast.ReliableBroadcast;
import cs451.broadcast.UniformReliableBroadcast;
import cs451.messaging.Message;
import cs451.commonUtils.CommonUtils;
import cs451.commonUtils.Logger;
import cs451.links.Link;

import java.io.IOException;
import java.net.SocketException;
import java.util.ArrayList;

/**
 * This class represents a process of the distributed system.
 */
public class Process implements Deliverer{
    private int id;
    private int pid;
    private Host selfHost;
    private ArrayList<Host>hosts;
    private Logger logger;
    private Link link;
    private Broadcast broadcast;
    private long totalDelivered;
    private long totalSent;
    private long totalBroadcasted;
    private long deliveryCountTimeStart;
    private long DELIVERY_MESSAGE_COUNT = 1000;

    private String performanceLog;

    public Process(int id, int pid, ArrayList<Host>hosts, Logger logger) throws SocketException {
        this.id = id;
        this.pid = pid;
        this.hosts = hosts;
        this.logger = logger;
        this.selfHost = CommonUtils.getHost(id, hosts);

        this.broadcast = new UniformReliableBroadcast(this, hosts,selfHost);
        //this.link = new PerfectLink(this, selfHost.getPort(), hosts);

        this.totalDelivered = 0;
        this.totalSent = 0;
        this.performanceLog = "Not enough messages to count performance.";
    }

    public void startReceiving(){
        deliveryCountTimeStart = System.nanoTime();

        broadcast.startReceiving();
        //link.startReceiving();
    }

    @Override
    public void deliver(Message message){
        if(Constants.PROCESS_MESSAGING_VERBOSE || Constants.PROCESS_BROADCASTING_VERBOSE){
            System.out.println("[Process]: Delivery " + message);
        }
        logger.addEvent("d " + message.getRelayFrom() + " " + message.getId());

        totalDelivered++;

        Host senderHost = CommonUtils.getHost(message.getOriginalFrom(), hosts);
        senderHost.increaseDeliveredMessages();

        if(totalDelivered % 10 == 0){
            long end = System.nanoTime();
            long elapsedTime = end - deliveryCountTimeStart;

            double elapsedTimeSeconds = (double) elapsedTime / 1000000000;

            this.performanceLog = "Performance: " + totalDelivered + " messages in " + elapsedTimeSeconds + " seconds";
        }
    }

    public void send(Message message, Host toHost) throws IOException {
        link.send(message, toHost);

        logger.addEvent("b " + message.getId());
        totalSent++;

        if(Constants.PROCESS_MESSAGING_VERBOSE) {
            System.out.println("[Process]: Sent " + message);
        }
    }

    public void sendBatch(ArrayList<Message>batch, Host toHost) throws IOException {
        link.sendBatch(batch, toHost);

        for(Message message : batch) {
            logger.addEvent("b " + message.getId());
            totalSent++;
        }

        if(Constants.PROCESS_MESSAGING_VERBOSE) {
            System.out.println("[Process]: Sent " + batch);
        }
    }

    public void broadcast(Message message){
        broadcast.broadcast(message);

        logger.addEvent("b " + message.getId());
        totalBroadcasted++;

        if(Constants.PROCESS_BROADCASTING_VERBOSE) {
            System.out.println("[Process]: Broadcast " + message);
        }

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ArrayList<Host> getHosts() {
        return hosts;
    }

    public void setHosts(ArrayList<Host> hosts) {
        this.hosts = hosts;
    }

    public String toString(){
        String s;

        s = "-----\nProcess Info:\n";
        s += "id:" + id + "\n";
        s += "pid:" + pid + "\n";
        s += "Self-Host:" + selfHost.toString() + "\n";
        s += "List of Known Hosts:" + hosts.size() + "\n";
        for(Host h : hosts){
            s+= ">>>> " + h.toString() + "\n";
        }
        s+= "-----\n";
        return s;
    }

    public void freeResources(){
        //perfectLink.freeResources();
        broadcast.freeResources();
    }

    public void logData() throws IOException {
        logger.flush2file();
    }

    public long getTotalDelivered() {
        return totalDelivered;
    }

    public void setTotalDelivered(long totalDelivered) {
        this.totalDelivered = totalDelivered;
    }

    public void printHostSendingInfo(){
        for(Host h : hosts){
            System.out.println("Host " + h.getId() + ": " +h.getDeliveredMessages());
        }
    }

    public long getTotalSent() {
        return totalSent;
    }

    public void setTotalSent(long totalSent) {
        this.totalSent = totalSent;
    }

    public long getTotalBroadcasted() {
        return totalBroadcasted;
    }

    public void setTotalBroadcasted(long totalBroadcasted) {
        this.totalBroadcasted = totalBroadcasted;
    }

    public String getPerformanceLog() {
        return performanceLog;
    }

    public void setPerformanceLog(String performanceLog) {
        this.performanceLog = performanceLog;
    }

    public void broadcastBatch(ArrayList<Message> batch){
        broadcast.broadcastBatch(batch);
        for(Message message : batch) {
            logger.addEvent("b " + message.getId());
            totalBroadcasted++;

            if(Constants.PROCESS_BROADCASTING_VERBOSE) {
                System.out.println("[Process]: Broadcast " + message);
            }
        }
    }

}

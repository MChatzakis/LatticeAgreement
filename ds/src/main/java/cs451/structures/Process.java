package cs451.structures;

import cs451.Constants;
import cs451.Host;
import cs451.commonUtils.CommonUtils;
import cs451.commonUtils.Logger;
import cs451.links.PerfectLink;

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
    private PerfectLink perfectLink;
    private long totalDelivered;
    private long totalSent;

    public Process(int id, int pid, ArrayList<Host>hosts, Logger logger) throws SocketException {
        this.id = id;
        this.pid = pid;
        this.hosts = hosts;
        this.logger = logger;

        this.selfHost = CommonUtils.getHost(id, hosts);

        this.perfectLink = new PerfectLink(this, selfHost.getPort(), hosts);

        this.totalDelivered = 0;
        this.totalSent = 0;
    }

    public void startReceiving(){
        perfectLink.startReceiving();
    }

    @Override
    public void deliver(Message message) {
        if(Constants.PROCESS_MESSAGING_VERBOSE){
            System.out.println("[Process]: Delivery " + message);
        }
        logger.addEvent("d " + message.getFrom() + " " + message.getId());

        totalDelivered++;

        Host senderHost = CommonUtils.getHost(message.getFrom(), hosts);
        senderHost.increaseDeliveredMessages();
    }

    public void send(Message message, Host toHost) throws SocketException {
        perfectLink.send(message, toHost);

        logger.addEvent("b " + message.getId());
        totalSent++;

        if(Constants.PROCESS_MESSAGING_VERBOSE) {
            System.out.println("[Process]: Sent " + message);
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
        perfectLink.freeResources();
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

}

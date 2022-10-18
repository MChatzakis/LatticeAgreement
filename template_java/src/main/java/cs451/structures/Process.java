package cs451.structures;

import cs451.Constants;
import cs451.Host;
import cs451.commonUtils.CommonUtils;
import cs451.commonUtils.Logger;
import cs451.links.PerfectLink;

import java.net.SocketException;
import java.util.ArrayList;

public class Process implements Deliverer{


    private int id;
    private int pid;
    private Host selfHost;

    public ArrayList<Host> getHosts() {
        return hosts;
    }

    public void setHosts(ArrayList<Host> hosts) {
        this.hosts = hosts;
    }

    private ArrayList<Host>hosts;
    private Logger logger;
    private PerfectLink perfectLink;

    public Process(int id, int pid, ArrayList<Host>hosts, Logger logger) throws SocketException {
        this.id = id;
        this.pid = pid;
        this.hosts = hosts;
        this.logger = logger;

        this.selfHost = CommonUtils.getHost(id, hosts);
        this.perfectLink = new PerfectLink(this, selfHost.getPort(), hosts);
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
    }

    public void send(Message message, Host toHost) throws SocketException {
        logger.addEvent("b " + message.getId());

        if(Constants.PROCESS_MESSAGING_VERBOSE) {
            System.out.println("[Process]: Sent " + message);
        }

        perfectLink.send(message, toHost);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
}

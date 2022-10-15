package cs451.structures;

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
    private ArrayList<Host>hosts;
    private Logger logger;
    private PerfectLink perfectLink;

    public Process(int id, int pid, ArrayList<Host>hosts){
        this.id = id;
        this.pid = pid;
        this.hosts = hosts;

        this.selfHost = CommonUtils.getHost(id, hosts);
    }

    @Override
    public void deliver(Message message) {

        logger.addEvent("");
    }

    public void send(Message message, Host toHost) throws SocketException {
        perfectLink.send(message, toHost);
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

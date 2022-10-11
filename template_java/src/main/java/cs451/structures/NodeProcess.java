package cs451.structures;

import cs451.Host;
import cs451.commonUtils.CommonUtils;
import cs451.network.UDPHolder;

import java.net.SocketException;
import java.util.ArrayList;

public class NodeProcess {
    private int id;
    private int pid;
    private Host selfHost;
    private ArrayList<Host>hosts;
    //private UDPHolder udpHolder;

    public NodeProcess(int id, int pid, ArrayList<Host>hosts) throws SocketException {
        this.id = id;
        this.pid = pid;
        this.hosts = hosts;

        this.selfHost = CommonUtils.getHost(id, hosts);
        //this.udpHolder = new UDPHolder(selfHost.getPort());
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

        return s;
    }

}

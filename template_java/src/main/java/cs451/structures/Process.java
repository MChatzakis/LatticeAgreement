package cs451.structures;

import cs451.Host;

import java.util.ArrayList;

public class Process {
    private int id;
    private int pid;

    private Host cHost;
    private ArrayList<Host>hosts;

    public Process(int id, int pid, ArrayList<Host>hosts){
        this.id = id;
        this.pid = pid;
        this.hosts = hosts;
    }



    public String toString(){
        return "";
    }

}

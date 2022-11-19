package cs451.broadcast;

import cs451.Host;
import cs451.structures.Deliverer;
import cs451.structures.Message;

import java.util.List;

public abstract class Broadcast implements Deliverer {

    protected Deliverer deliverer; //Used to push a delivery to another layer
    protected List<Host>processes; //Hosts and Processes mean the same
    protected Host self;

    public Broadcast(Deliverer deliverer, List<Host>processes, Host self){
        this.deliverer = deliverer;
        this.processes = processes;
        this.self = self;
    }

    public abstract void broadcast(Message message);

    public abstract void startReceiving();

    public Deliverer getDeliverer() {
        return deliverer;
    }

    public void setDeliverer(Deliverer deliverer) {
        this.deliverer = deliverer;
    }

    public List<Host> getProcesses() {
        return processes;
    }

    public void setProcesses(List<Host> processes) {
        this.processes = processes;
    }
}

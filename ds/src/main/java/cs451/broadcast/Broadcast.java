package cs451.broadcast;

import cs451.Host;
import cs451.structures.Deliverer;
import cs451.structures.Message;

import java.util.List;

public abstract class Broadcast implements Deliverer {

    protected Deliverer deliverer; //Used to push a delivery to another layer
    protected List<Host>processes;

    public Broadcast(Deliverer deliverer, List<Host>processes){
        this.deliverer = deliverer;
        this.processes = processes;
    }

    public abstract void broadcast(Message message);
}

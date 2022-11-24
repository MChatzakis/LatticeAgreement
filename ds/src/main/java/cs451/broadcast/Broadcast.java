package cs451.broadcast;

import cs451.Host;
import cs451.structures.Deliverer;
import cs451.messaging.Message;

import java.util.ArrayList;
import java.util.List;

public abstract class Broadcast implements Deliverer {
    protected Deliverer deliverer;
    protected List<Host>processes;
    protected Host self;

    public Broadcast(Deliverer deliverer, List<Host>processes, Host self){
        this.deliverer = deliverer;
        this.processes = processes;
        this.self = self;
    }

    //public abstract void broadcast(Message message);

    public abstract void broadcastBatch(ArrayList<Message>message);

    public abstract void startReceiving();

    public Deliverer getDeliverer() {
        return deliverer;
    }

    public void setDeliverer(Deliverer deliverer) {
        this.deliverer = deliverer;
    }

}

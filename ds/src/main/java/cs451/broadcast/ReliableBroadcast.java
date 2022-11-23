package cs451.broadcast;

import cs451.Host;
import cs451.structures.Deliverer;
import cs451.broadcast.messaging.Message;

import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ReliableBroadcast extends Broadcast{
    private BestEffortBroadcast beb;
    private Set<Message> delivered;

    public ReliableBroadcast(Deliverer deliverer, List<Host> processes, Host self) throws SocketException {
        super(deliverer, processes, self);
        this.beb = new BestEffortBroadcast(this, processes, self);
        this.delivered = ConcurrentHashMap.newKeySet();
    }

    @Override
    public void broadcast(Message message) {
        beb.broadcast(message);
    }

    @Override
    public void broadcastBatch(ArrayList<Message> batch) {
        beb.broadcastBatch(batch);
    }

    @Override
    public void startReceiving() {
        beb.startReceiving();
    }

    @Override
    public void deliver(Message message) {
        if(!delivered.contains(message)){
            delivered.add(message);
            deliverer.deliver(message);

            ArrayList<Message>batch = new ArrayList<>();
            batch.add(message);
            beb.broadcastBatch(batch);
        }
    }

    @Override
    public void freeResources() {
        beb.freeResources();
    }
}

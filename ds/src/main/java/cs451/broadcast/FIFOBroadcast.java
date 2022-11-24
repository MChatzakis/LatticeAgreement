package cs451.broadcast;

import cs451.Host;
import cs451.structures.Deliverer;
import cs451.messaging.Message;

import java.net.SocketException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class FIFOBroadcast extends Broadcast implements Deliverer {
    private UniformReliableBroadcast urb;
    private Set<Message> pending;
    private Map<Byte, Integer> next;
    private int lsn;

    public FIFOBroadcast(Deliverer deliverer, List<Host> processes, Host self) throws SocketException {
        super(deliverer, processes, self);

        this.urb = new UniformReliableBroadcast(this, processes, self);
        this.lsn = 0;

        this.pending = ConcurrentHashMap.newKeySet();

        this.next = new ConcurrentHashMap<>();
        for(Host h : processes){
            next.put(h.getId(), 1);
        }
    }

    @Override
    public void deliver(Message message) {
        pending.add(message);

        Iterator<Message> pendingIterator = pending.iterator();
        while(pendingIterator.hasNext()){
            Message m = pendingIterator.next();

            byte originalSenderHostId = m.getOriginalFrom();
            int snp = m.getId();
            int nextNum = next.get(originalSenderHostId);

            if(nextNum == snp){
                next.put(originalSenderHostId, nextNum+1);
                pending.remove(m);
                deliverer.deliver(m);

                pendingIterator = pending.iterator(); //start over
                //System.gc();
            }
        }

    }

    @Override
    public void freeResources() {
        urb.freeResources();
    }

    @Override
    public void broadcast(Message message) {
        lsn++;
        message.setId(lsn);
        urb.broadcast(message);
    }

    @Override
    public void broadcastBatch(ArrayList<Message> batch) {
        for(Message message : batch){
            lsn++;
            message.setId(lsn);
        }

        urb.broadcastBatch(batch);
    }

    @Override
    public void startReceiving() {
        urb.startReceiving();
    }
}

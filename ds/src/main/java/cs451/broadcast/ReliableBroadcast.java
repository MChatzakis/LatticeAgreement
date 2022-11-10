package cs451.broadcast;

import cs451.Host;
import cs451.commonUtils.CommonUtils;
import cs451.structures.Deliverer;
import cs451.structures.Message;

import java.net.SocketException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ReliableBroadcast extends Broadcast{

    private BestEffortBroadcast beb;
    private Set<Message> delivered;

    public ReliableBroadcast(Deliverer deliverer, List<Host> processes, Host self) throws SocketException {
        super(deliverer, processes, self);

        this.beb = new BestEffortBroadcast(this, processes, self);
        this.delivered = new HashSet<>();
    }

    @Override
    public void broadcast(Message message) {
        message.setOriginalFrom(self.getId()); //self
        beb.broadcast(message);
    }

    @Override
    public void deliver(Message message) {
        if(!delivered.contains(message)){
            delivered.add(message);
            deliverer.deliver(message);
            beb.broadcast(message); //relay
        }
    }

    @Override
    public void freeResources() {
        beb.freeResources();
    }
}

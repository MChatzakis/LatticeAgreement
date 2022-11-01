package cs451.broadcast;

import cs451.Host;
import cs451.commonUtils.CommonUtils;
import cs451.commonUtils.MHPair;
import cs451.structures.Deliverer;
import cs451.structures.Message;

import java.net.SocketException;
import java.util.*;

public class UniformReliableBroadcast extends Broadcast implements Deliverer {

    private BestEffortBroadcast beb;
    private Map<Message, Set<Host>> ack;
    private Set<Host> correct;
    private Set<MHPair> pending;
    private Set<Message> delivered;

    public UniformReliableBroadcast(Deliverer deliverer, List<Host> processes, Host self) throws SocketException {
        super(deliverer, processes, self);

        beb = new BestEffortBroadcast(this, processes, self);

        ack = new HashMap<>();

        pending = new HashSet<>();
        delivered = new HashSet<>();

        correct = new HashSet<>(processes);
    }

    @Override
    public void broadcast(Message message) {
        pending.add(new MHPair(message, self));
        beb.broadcast(message);
    }

    @Override
    public void deliver(Message m) {
        Host p = CommonUtils.getHost(m.getFrom(), processes);
        Host s =  CommonUtils.getHost(m.getOriginalFrom(), processes);

        if(ack.containsKey(m)){
            Set set = ack.get(m);
            set.add(p); //! check again!
        }
        else{
            Set set = new HashSet<Host>();
            set.add(p);

            ack.put(m, set);
        }

        MHPair sm = new MHPair(m, s);
        if(!pending.contains(sm)){
            pending.add(sm);

            m.setFrom(self.getId());
            beb.broadcast(m);
        }
    }

    @Override
    public void freeResources() {

    }

    public void crashEvent(Host p){
        correct.remove(p);
    }
}

package cs451.broadcast;

import cs451.Host;
import cs451.commonUtils.CommonUtils;
import cs451.commonUtils.MHPair;
import cs451.structures.Deliverer;
import cs451.structures.Message;

import java.net.SocketException;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FIFOBroadcast extends Broadcast implements Deliverer {

    private ReliableBroadcast rb;
    private Set<MHPair> pending;
    private Map<Host, Integer> next;
    private int lsn;
    public FIFOBroadcast(Deliverer deliverer, List<Host> processes, Host self) throws SocketException {
        super(deliverer, processes, self);

        this.rb = new ReliableBroadcast(this, processes, self);
        this.lsn = 0;
    }

    @Override
    public void deliver(Message message) {
        Host s = CommonUtils.getHost(message.getOriginalFrom(), processes);
        pending.add(new MHPair(message, s));

        //while exist blah blah
    }

    @Override
    public void freeResources() {
        rb.freeResources();
    }

    @Override
    public void broadcast(Message message) {
        lsn++;
        //bc
    }
}

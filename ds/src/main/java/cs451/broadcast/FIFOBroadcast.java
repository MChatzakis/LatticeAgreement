package cs451.broadcast;

import cs451.Host;
import cs451.commonUtils.CommonUtils;
import cs451.commonUtils.MHPair;
import cs451.structures.Deliverer;
import cs451.structures.Message;

import java.net.SocketException;
import java.util.HashMap;
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
        this.next = new HashMap<>();

        initNext();
    }

    private void initNext(){
        for(Host h : processes){
            next.put(h, 1);
        }
    }

    @Override
    public void deliver(Message message) {
        Host s = CommonUtils.getHost(message.getOriginalFrom(), processes);

        pending.add(new MHPair(message, s));

        for(MHPair mh : pending){
            Host ss = mh.getHost();
            Message mp = mh.getMessage();
            int snp = mp.getLsn();

            int nextNum = next.get(ss);

            if(nextNum == snp){
                next.put(ss, nextNum+1);
                pending.remove(mh);
                deliverer.deliver(message);
            }
        }
    }

    @Override
    public void freeResources() {
        rb.freeResources();
    }

    @Override
    public void broadcast(Message message) {
        lsn++;

        message.setLsn(lsn);
        message.setOriginalFrom(self.getId());

        rb.broadcast(message);
    }

    @Override
    public void startReceiving() {
        rb.startReceiving();
    }
}

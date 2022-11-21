package cs451.broadcast;

import cs451.Host;
import cs451.commonUtils.CommonUtils;
import cs451.commonUtils.MHPair;
import cs451.structures.Deliverer;
import cs451.structures.Message;

import java.net.SocketException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class UniformReliableBroadcast extends Broadcast implements Deliverer {

    private BestEffortBroadcast beb;
    private Map<Message, Set<Host>> ack;
    private Set<MHPair> pending;
    private Set<Message> delivered;

    public UniformReliableBroadcast(Deliverer deliverer, List<Host> processes, Host self) throws SocketException {
        super(deliverer, processes, self);

        this.beb = new BestEffortBroadcast(this, processes, self);

        this.ack = new ConcurrentHashMap<>();
        this.pending = ConcurrentHashMap.newKeySet();
        this.delivered = ConcurrentHashMap.newKeySet();

    }

    @Override
    public void broadcast(Message message) {
        //message.setOriginalFrom(self.getId());
        pending.add(new MHPair(message, self));
        beb.broadcast(message);
    }

    @Override
    public void startReceiving() {
        beb.startReceiving();
    }

    @Override
    public void deliver(Message m) {
        Host p = CommonUtils.getHost(m.getFrom(), processes);
        Host s =  CommonUtils.getHost(m.getOriginalFrom(), processes);

        System.out.println("{URB} : >>>>>> 1. Got a message and will start 'deliver' routine " + m);

        if(ack.containsKey(m)){
            Set set = ack.get(m);
            set.add(p); //! check again!
        }
        else{
            Set set = new HashSet<Host>();
            set.add(p);
            ack.put(m, set);
        }

        System.out.println("{URB} : >>>>>> 2. Processed the ack structure. Current ack: " + ack);

        MHPair sm = new MHPair(m, s);
        if(!pending.contains(sm)){
            pending.add(sm);

            Message relayMessage = null;
            try {
                relayMessage = (Message) m.clone();
            } catch (CloneNotSupportedException e) {
                throw new RuntimeException(e);
            }

            System.out.println("{URB} :     >>>>>> 2.1. : Message did not belong to 'pending'. Added and relaying.");
            System.out.println("{URB} :     >>>>>> 2.2. : Pending set: " + pending);

            relayMessage.setFrom(self.getId());
            beb.broadcast(relayMessage);

            System.out.println("{URB} :     >>>>>> 2.3. : Broadcasted relay as: " + relayMessage);

        }

        //upon exists....
        System.out.println("{URB} : >>>>>> 3. Uppon exist routine. Traversing over pending messages. Pending: " + pending);
        for(MHPair mhpair : pending){
            System.out.println("{URB} :     >>>>>> 3.1. : Current pending variable: " + mhpair);
            Message mes = mhpair.getMessage();
            if(!delivered.contains(mes) && canDeliver(mes)){

                System.out.println("\"{URB} :     >>>>>> 3.2. : Message not inside 'contains' variable and canDeliver. Delivering " + mes);

                deliverer.deliver(mes);
                delivered.add(mes);
            }
        }

        System.out.println("{URB} : >>>>>> 4. 'Delivered' : " + delivered);

    }

    public boolean canDeliver(Message m){
        int N = processes.size();

        System.out.println("{URB} :         CAN-DELIVER 1. -- Message:" + m);
        System.out.println("{URB} :         CAN-DELIVER 2. -- N:" + N);
        System.out.println("{URB} :         CAN-DELIVER 3. -- ack:" + ack);

        int setSize;

        if(ack.get(m) == null){
            //System.out.println("{URB} :     CAN-DELIVER -- 1.ACK:" + ack);
            //System.out.println("{URB} :     CAN-DELIVER -- 2.MSG:" + m);
            //System.out.println();
            //return false;
            setSize = 0;
        }else{
            setSize = ack.get(m).size();
        }

        //System.out.println("{URB} :         CAN-DELIVER 3. -- ack.get.size:" + ack.get(m).size());

        return (setSize > N/2);
    }

    @Override
    public void freeResources() {
        beb.freeResources();
    }
}

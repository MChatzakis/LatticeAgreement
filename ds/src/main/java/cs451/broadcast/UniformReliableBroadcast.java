package cs451.broadcast;

import cs451.Host;
import cs451.commonUtils.CommonUtils;
import cs451.commonUtils.MHIDPair;
import cs451.structures.Deliverer;
import cs451.messaging.Message;

import java.net.SocketException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class UniformReliableBroadcast extends Broadcast implements Deliverer {
    private BestEffortBroadcast beb;
    private Map<MHIDPair, Byte> ack;
    //private Map<MHIDPair, Set<Byte>> ack; //int pair == <messageId, originalSenderId>, Integer == hostID
    private Map<MHIDPair, Message> pending;
    private Set<MHIDPair> delivered;

    public UniformReliableBroadcast(Deliverer deliverer, List<Host> processes, Host self) throws SocketException {
        super(deliverer, processes, self);
        this.beb = new BestEffortBroadcast(this, processes, self);
        this.ack = new ConcurrentHashMap<>();
        this.pending = new ConcurrentHashMap<>();
        this.delivered = ConcurrentHashMap.newKeySet();
    }

    @Override
    public void broadcastBatch(ArrayList<Message> batch) {
        for(Message message : batch) {
            pending.put(new MHIDPair(message.getId(), message.getOriginalFrom()), message);
        }
        beb.broadcastBatch(batch);
    }

    @Override
    public void startReceiving() {
        beb.startReceiving();
    }

    @Override
    public void deliver(Message m) {
        Host p = CommonUtils.getHost(m.getRelayFrom(), processes);
        MHIDPair messageIDs = new MHIDPair(m.getId(), m.getOriginalFrom());

        //System.out.println("{URB} : >>>>>> 1. Got a message and will start 'deliver' routine " + m);

        if(ack.containsKey(messageIDs)){
            //Set set = ack.get(messageIDs);
            //set.add(p.getId());
            Byte num = ack.get(messageIDs);
            ack.put(messageIDs, (byte) (num+1));
        }
        else{
            //Set set = new HashSet<Host>();
            //set.add(p.getId());
            //ack.put(messageIDs, set);
            ack.put(messageIDs, (byte) 1);
        }

        //System.out.println("{URB} : >>>>>> 2. Processed the ack structure. Current ack: " + ack);
        MHIDPair sm = new MHIDPair(m.getId(), m.getOriginalFrom());
        if(!pending.containsKey(sm)){
            pending.put(sm, m);

            Message relayMessage = null;
            try {
                relayMessage = (Message) m.clone();
                relayMessage.setRelayFrom(self.getId());
            } catch (CloneNotSupportedException e) {
                throw new RuntimeException(e);
            }
            //System.out.println("{URB} :     >>>>>> 2.1. : Message did not belong to 'pending'. Added and relaying.");
            //System.out.println("{URB} :     >>>>>> 2.2. : Pending set: " + pending);
            ArrayList<Message>batch = new ArrayList<>();
            batch.add(relayMessage);
            beb.broadcastBatch(batch);
            //System.out.println("{URB} :     >>>>>> 2.3. : Broadcasted relay as: " + relayMessage);
        }

        //System.out.println("{URB} : >>>>>> 3. Uppon exist routine. Traversing over pending messages. Pending: " + pending);
        for(MHIDPair messageData : pending.keySet()){
            //System.out.println("{URB} :     >>>>>> 3.1. : Current pending variable: " + messageData);
            Message mes = pending.get(messageData);
            if(!delivered.contains(messageData) && canDeliver(messageData)){
                //System.out.println("\"{URB} :     >>>>>> 3.2. : Message not inside 'contains' variable and canDeliver. Delivering " + mes);
                deliverer.deliver(mes);
                delivered.add(messageData);

                //can I remove from here? !!!! check again
                //pending.remove(messageData);
                //delete also from acks
                //ack.remove(messageData);
                //System.gc();
            }
        }
        //System.out.println("{URB} : >>>>>> 4. 'Delivered' : " + delivered);
    }

    public boolean canDeliver(MHIDPair p){
        int N = processes.size();
        //System.out.println("{URB} :         CAN-DELIVER 1. -- Message:" + p);
        //System.out.println("{URB} :         CAN-DELIVER 2. -- N:" + N);
        //System.out.println("{URB} :         CAN-DELIVER 3. -- ack:" + ack);
        if(!ack.containsKey(p)){
            return false;
        }

        return ack.get(p) > N/2.0;
    }

    @Override
    public void freeResources() {
        beb.freeResources();
    }
}

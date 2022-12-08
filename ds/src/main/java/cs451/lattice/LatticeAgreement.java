package cs451.lattice;

import cs451.Host;
import cs451.broadcast.BestEffortBroadcast;
import cs451.commonUtils.CommonUtils;
import cs451.messaging.Message;
import cs451.structures.Deliverer;
import cs451.structures.Process;

import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class LatticeAgreement implements Deliverer {
    private BestEffortBroadcast beb;
    private ArrayList<Host>processes;
    private Host self;
    private double f;
    private AtomicInteger messageLsn;
    private Process parentProcess;
    private int totalProposals;
    //private boolean active;
    private Map<Integer, Boolean> activeRound;
    //private int ackCount;
    private Map<Integer, Integer>ackCountRound;
    //private int nAckCount;
    private Map<Integer, Integer>nAckCountRound;

    //private int activeProposalNumber;
    private Map<Integer, Integer>activeProposalNumberRound;
    //private Set<Integer> proposedValue;
    private Map<Integer, Set<Integer>>proposedValueRound;

    private Map<Integer, Set<Integer>>acceptedValueRound;

    public LatticeAgreement(Process parentProcess, ArrayList<Host> processes, Host self, int totalProposals) throws SocketException {
        this.beb = new BestEffortBroadcast(this, processes, self);
        this.processes = processes;
        this.self = self;
        this.parentProcess = parentProcess;
        this.f = calculateF();
        this.messageLsn = new AtomicInteger(0);
        this.totalProposals = totalProposals;

        //single shot
        /*this.active = false;
        this.ackCount = 0;
        this.nAckCount = 0;
        this.activeProposalNumber = 0;
        this.proposedValue = ConcurrentHashMap.newKeySet();*/

        //multi shot
        this.activeProposalNumberRound = new HashMap<>();//?????????????
        this.activeRound = new HashMap<>();
        this.ackCountRound = new HashMap<>();
        this.nAckCountRound = new HashMap<>();
        this.proposedValueRound = new HashMap<>();
        this.acceptedValueRound = new HashMap<>();
        for(int i=0; i<totalProposals; i++){
            this.activeRound.put(i, false);
            this.ackCountRound.put(i, 0);
            this.nAckCountRound.put(i, 0);
            this.activeProposalNumberRound.put(i,0);
            this.proposedValueRound.put(i, ConcurrentHashMap.newKeySet());
        }

    }

    @Override
    public void deliver(Message message) {

        //System.out.println("Agreement got message:"+message);

        switch (message.getLatticeType()){
            case ACK:
                processACK(message);
                break;
            case NACK:
                processNACK(message);
                break;
            case PROPOSAL:
                processPROPOSAL(message);
                break;
        }

        int round = message.getLatticeRound(); //change!
        //int ackCount = ackCountRound.get(round);
        //boolean active = activeRound.get(round);
        //Set<Integer> proposedValue = proposedValueRound.get(round);
        if(/*ackCount*/ackCountRound.get(round) >= f+1 && /*active*/activeRound.get(round)){
            decide(/*proposedValue*/proposedValueRound.get(round), round);
            /*active = false;*/
            activeRound.put(round, false);
        }

        if(/*nAckCount*/nAckCountRound.get(round) > 0 && (nAckCountRound.get(round)+ackCountRound.get(round)/*ackCount+nAckCount*/>=f+1) && /*active*/activeRound.get(round)){
            /*activeProposalNumber++;
            ackCount = 0;
            nAckCount = 0;*/
            nAckCountRound.put(round, 0);
            ackCountRound.put(round,0);
            activeProposalNumberRound.put(round, activeProposalNumberRound.get(round) + 1);


            Message lm = new Message(self.getId(), (byte) -1, messageLsn.incrementAndGet());
            lm.setLatticeType(LatticeType.PROPOSAL);
            lm.setLatticeValue(/*proposedValue*/proposedValueRound.get(round));
            lm.setLatticeProposalNumber(/*activeProposalNumber*/activeProposalNumberRound.get(round));
            lm.setLatticeRound(round);

            ArrayList<Message>batch = new ArrayList<>();
            batch.add(lm);
            beb.broadcastBatch(batch);
        }
    }

    @Override
    public void freeResources() {
        beb.freeResources();
    }

    public void propose(Set<Integer>proposal, int round){
        //proposedValue = proposal;
        proposedValueRound.put(round, proposal);

        //active = true;
        activeRound.put(round, true);

        //activeProposalNumber++;
        activeProposalNumberRound.put(round, activeProposalNumberRound.get(round) + 1);

        //ackCount = 0;
        ackCountRound.put(round, 0);


        //nAckCount = 0;
        nAckCountRound.put(round, 0);

        Message lm = new Message(self.getId(), (byte) -1, messageLsn.incrementAndGet());

        lm.setLatticeType(LatticeType.PROPOSAL);
        lm.setLatticeValue(/*proposedValue*/proposedValueRound.get(round));
        lm.setLatticeProposalNumber(/*activeProposalNumber*/activeProposalNumberRound.get(round));
        lm.setLatticeRound(round);

        //System.out.println("Agreement broadcasting message:"+lm);

        ArrayList<Message>batch = new ArrayList<>();
        batch.add(lm);
        beb.broadcastBatch(batch);
    }

    public void decide(Set<Integer>value, int round){
        parentProcess.decide(value, round);
    }

    private void processACK(Message message){
        int round = message.getLatticeRound();
        if(message.getLatticeProposalNumber() == /*activeProposalNumber*/activeProposalNumberRound.get(round)){
            //ackCount++;
            ackCountRound.put(round, ackCountRound.get(round) + 1);
        }
    }

    private void processNACK(Message message){
        int round = message.getLatticeRound();
        if(message.getLatticeProposalNumber() == /*activeProposalNumber*/activeProposalNumberRound.get(round)){
            /*proposedValue.addAll(message.getLatticeValue());
            nAckCount++;*/
            Set<Integer>ts = proposedValueRound.get(round);
            ts.addAll(message.getLatticeValue());
            proposedValueRound.put(round, ts);
            nAckCountRound.put(round, nAckCountRound.get(round) + 1);
        }
    }

    private void processPROPOSAL(Message message){
        int round = message.getLatticeRound();
        Set<Integer>mProposedValue = message.getLatticeValue();
        if(mProposedValue.containsAll(/*proposedValue*/proposedValueRound.get(round))){
             ///*proposedValue*/ = mProposedValue;
            proposedValueRound.put(round, mProposedValue);

            Message lm = new Message(self.getId(), message.getOriginalFrom(), messageLsn.incrementAndGet());
            lm.setLatticeType(LatticeType.ACK);
            lm.setLatticeProposalNumber(message.getLatticeProposalNumber());
            lm.setLatticeRound(round);

            ArrayList<Message>batch = new ArrayList<>();
            batch.add(lm);
            beb.sendBatch(batch, CommonUtils.getHost(message.getOriginalFrom(), processes));
        }else{
            ///*proposedValue*/.addAll(mProposedValue);
            Set<Integer>ts = proposedValueRound.get(round);
            ts.addAll(mProposedValue);
            proposedValueRound.put(round, ts);

            Message lm = new Message(self.getId(), message.getOriginalFrom(), messageLsn.incrementAndGet());
            lm.setLatticeType(LatticeType.NACK);
            lm.setLatticeProposalNumber(message.getLatticeProposalNumber());
            lm.setLatticeValue(/*proposedValue*/proposedValueRound.get(round));
            lm.setLatticeRound(round);

            ArrayList<Message>batch = new ArrayList<>();
            batch.add(lm);
            beb.sendBatch(batch, CommonUtils.getHost(message.getOriginalFrom(), processes));
        }
    }

    private double calculateF(){
        return (processes.size() - 1.0)/2;
    }

    public void startReceiving(){
        beb.startReceiving();
    }
}

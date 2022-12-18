package cs451.lattice;

import cs451.Host;
import cs451.broadcast.BestEffortBroadcast;
import cs451.commonUtils.CommonUtils;
import cs451.messaging.Message;
import cs451.structures.Deliverer;
import cs451.structures.Process;

import java.net.SocketException;
import java.util.ArrayList;
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
    private ConcurrentHashMap<Integer, Boolean> activeRound;
    private ConcurrentHashMap<Integer, Integer>ackCountRound;
    private ConcurrentHashMap<Integer, Integer>nAckCountRound;
    private ConcurrentHashMap<Integer, Integer>activeProposalNumberRound;
    private ConcurrentHashMap<Integer, Set<Integer>>proposedValueRound;
    private ConcurrentHashMap<Integer, Set<Integer>>acceptedValueRound;

    //optimization
    private ConcurrentHashMap<Integer, Short> processesCompletedRound;

    public LatticeAgreement(Process parentProcess, ArrayList<Host> processes, Host self, int totalProposals) throws SocketException {
        this.beb = new BestEffortBroadcast(this, processes, self);
        this.processes = processes;
        this.self = self;
        this.parentProcess = parentProcess;
        this.f = calculateF();
        this.messageLsn = new AtomicInteger(0);
        this.totalProposals = totalProposals;

        //multi shot
        this.activeProposalNumberRound = new ConcurrentHashMap<>();
        this.activeRound = new ConcurrentHashMap<>();
        this.ackCountRound = new ConcurrentHashMap<>();
        this.nAckCountRound = new ConcurrentHashMap<>();
        this.proposedValueRound = new ConcurrentHashMap<>();
        this.acceptedValueRound = new ConcurrentHashMap<>();

        //optimization
        this.processesCompletedRound = new ConcurrentHashMap<>();

        for(int i=0; i<totalProposals; i++){
            this.activeRound.put(i, false);
            this.ackCountRound.put(i, 0);
            this.nAckCountRound.put(i, 0);
            this.activeProposalNumberRound.put(i,0);
            this.proposedValueRound.put(i, ConcurrentHashMap.newKeySet());
            this.acceptedValueRound.put(i, ConcurrentHashMap.newKeySet());

            //optimization
            this.processesCompletedRound.put(i, (short) 0);
        }

    }

    @Override
    public void deliver(Message message) {

        int round = message.getLatticeRound();
        //System.out.println("Agreement got message:"+message);

        if(!activeRound.containsKey(round)){
            //System.out.println("Discarding delivered message " + );
            return;
        }

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
            case DECISION:
                processDECISION(message);
                return; //should not proceed after that.
        }

        if(ackCountRound.get(round) >= f+1 && activeRound.get(round)){
            decide(proposedValueRound.get(round), round);
            activeRound.put(round, false);

            // tell the other processes that you completed your round.
            Message lm = new Message(self.getId(), (short) (-1), messageLsn.incrementAndGet());
            lm.setLatticeType(LatticeType.DECISION);
            lm.setLatticeRound(round);
            lm.setLatticeProposalNumber(-1);
            beb.broadcastBatch(CommonUtils.wrapMessage2Batch(lm));
        }

        if(nAckCountRound.get(round) > 0 && (nAckCountRound.get(round)+ackCountRound.get(round)>=f+1) && activeRound.get(round)){
            nAckCountRound.put(round, 0);
            ackCountRound.put(round, 0);
            activeProposalNumberRound.put(round, activeProposalNumberRound.get(round) + 1);

            Message lm = new Message(self.getId(), (short) -1, messageLsn.incrementAndGet());
            lm.setLatticeType(LatticeType.PROPOSAL);
            lm.setLatticeValue(proposedValueRound.get(round));
            lm.setLatticeProposalNumber(activeProposalNumberRound.get(round));
            lm.setLatticeRound(round);

            //System.out.println("Updated proposal broadcast message " + lm + " for round " + round);
            beb.broadcastBatch(CommonUtils.wrapMessage2Batch(lm));
        }
    }

    @Override
    public void freeResources() {
        beb.freeResources();
    }

    public void propose(Set<Integer>proposal, int round){
        proposedValueRound.put(round, proposal);

        activeRound.put(round, true);

        activeProposalNumberRound.put(round, activeProposalNumberRound.get(round) + 1);

        ackCountRound.put(round, 0);
        nAckCountRound.put(round, 0);

        Message lm = new Message(self.getId(), (short) -1, messageLsn.incrementAndGet());

        lm.setLatticeType(LatticeType.PROPOSAL);
        lm.setLatticeValue(proposedValueRound.get(round));
        lm.setLatticeProposalNumber(activeProposalNumberRound.get(round));
        lm.setLatticeRound(round);

        //System.out.println("WW: In lattice:" + lm);
        //System.out.println("Broadcasting proposal message " + lm + " for round " + round);
        beb.broadcastBatch(CommonUtils.wrapMessage2Batch(lm));
    }

    public void decide(Set<Integer>value, int round){
        parentProcess.decide(value, round);
    }

    private void processDECISION(Message message){
        int round = message.getLatticeRound();
        int maxCorrectProcesses = processes.size(); // 2*f + 1

        short val = (short) (processesCompletedRound.get(round) + 1);
        processesCompletedRound.put(round, val);

        if(val == maxCorrectProcesses){
            clearRoundData(round);
            //System.out.println("All processed decided for round " + round + ". Deleting old stuff.");
        }

    }

    private void processACK(Message message){
        int round = message.getLatticeRound();
        if(message.getLatticeProposalNumber() == activeProposalNumberRound.get(round)){
            ackCountRound.put(round, ackCountRound.get(round) + 1);
        }
    }

    private void processNACK(Message message){
        int round = message.getLatticeRound();
        if(message.getLatticeProposalNumber() == activeProposalNumberRound.get(round)){
            Set<Integer>ts = proposedValueRound.get(round);
            ts.addAll(message.getLatticeValue());
            proposedValueRound.put(round, ts);
            nAckCountRound.put(round, nAckCountRound.get(round) + 1);
        }
    }

    private void processPROPOSAL(Message message){
        int round = message.getLatticeRound();
        Set<Integer>mProposedValue = message.getLatticeValue();
        if(mProposedValue.containsAll(acceptedValueRound.get(round))){
            acceptedValueRound.put(round, mProposedValue);

            Message lm = new Message(self.getId(), message.getOriginalFrom(), messageLsn.incrementAndGet());
            lm.setLatticeType(LatticeType.ACK);
            lm.setLatticeProposalNumber(message.getLatticeProposalNumber());
            lm.setLatticeRound(round);

            //System.out.println("XX: In lattice:" + lm);
            beb.sendBatch(CommonUtils.wrapMessage2Batch(lm), CommonUtils.getHost(message.getOriginalFrom(), processes));
        }else{
            Set<Integer>ts = acceptedValueRound.get(round);
            ts.addAll(mProposedValue);
            acceptedValueRound.put(round, ts);

            Message lm = new Message(self.getId(), message.getOriginalFrom(), messageLsn.incrementAndGet());
            lm.setLatticeType(LatticeType.NACK);
            lm.setLatticeProposalNumber(message.getLatticeProposalNumber());
            lm.setLatticeValue(acceptedValueRound.get(round));
            lm.setLatticeRound(round);

            //System.out.println("YY: In lattice:" + lm);

            beb.sendBatch(CommonUtils.wrapMessage2Batch(lm), CommonUtils.getHost(message.getOriginalFrom(), processes));
        }
    }

    private double calculateF(){
        return (processes.size() - 1.0)/2;
    }

    public void startReceiving(){
        beb.startReceiving();
    }

    private void clearRoundData(int round){
        activeRound.remove(round);
        ackCountRound.remove(round);
        nAckCountRound.remove(round);
        activeProposalNumberRound.remove(round);
        proposedValueRound.remove(round);
        acceptedValueRound.remove(round);
    }

}

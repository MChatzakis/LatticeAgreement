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
    private boolean active;
    private Map<Integer, Boolean> activeRound;
    private int ackCount;
    private Map<Integer, Integer>ackCountRound;
    private int nAckCount;
    private Map<Integer, Integer>nAckCountRound;

    private int activeProposalNumber;
    private Set<Integer> proposedValue;
    private Map<Integer, Set<Integer>>proposedValueRound;

    public LatticeAgreement(Process parentProcess, ArrayList<Host> processes, Host self) throws SocketException {
        this.beb = new BestEffortBroadcast(this, processes, self);
        this.processes = processes;
        this.self = self;
        this.parentProcess = parentProcess;
        this.f = calculateF();
        this.messageLsn = new AtomicInteger(0);
        this.active = false;
        this.ackCount = 0;
        this.nAckCount = 0;
        this.activeProposalNumber = 0;
        this.proposedValue = ConcurrentHashMap.newKeySet();

        //roundshot
    }

    @Override
    public void deliver(Message message) {

        System.out.println("Agreement got message:"+message);

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

        if(ackCount >= f+1 && active){
            decide(proposedValue);
            active = false;
        }

        if(nAckCount > 0 && (ackCount+nAckCount>=f+1) && active){
            activeProposalNumber++;
            ackCount = 0;
            nAckCount = 0;

            Message lm = new Message(self.getId(), (byte) -1, messageLsn.incrementAndGet());
            lm.setLatticeType(LatticeType.PROPOSAL);
            lm.setLatticeValue(proposedValue);
            lm.setLatticeProposalNumber(activeProposalNumber);

            ArrayList<Message>batch = new ArrayList<>();
            batch.add(lm);
            beb.broadcastBatch(batch);
        }
    }

    @Override
    public void freeResources() {
        beb.freeResources();
    }

    public void propose(Set<Integer>proposal){
        proposedValue = proposal;

        active = true;

        activeProposalNumber++;
        ackCount = 0;
        nAckCount = 0;

        Message lm = new Message(self.getId(), (byte) -1, messageLsn.incrementAndGet());

        lm.setLatticeType(LatticeType.PROPOSAL);
        lm.setLatticeValue(proposedValue);
        lm.setLatticeProposalNumber(activeProposalNumber);

        System.out.println("Agreement broadcasting message:"+lm);

        ArrayList<Message>batch = new ArrayList<>();
        batch.add(lm);
        beb.broadcastBatch(batch);
    }

    public void decide(Set<Integer>value){
        parentProcess.decide(value);
    }

    private void processACK(Message message){
        if(message.getLatticeProposalNumber() == activeProposalNumber){
            ackCount++;
        }
    }

    private void processNACK(Message message){
        if(message.getLatticeProposalNumber() == activeProposalNumber){
            proposedValue.addAll(message.getLatticeValue());
            nAckCount++;
        }
    }

    private void processPROPOSAL(Message message){
        Set<Integer>mProposedValue = message.getLatticeValue();
        if(mProposedValue.containsAll(/*acceptedValue*/proposedValue)){
            proposedValue/*acceptedValue*/ = mProposedValue;

            Message lm = new Message(self.getId(), message.getOriginalFrom(), messageLsn.incrementAndGet());
            lm.setLatticeType(LatticeType.ACK);
            lm.setLatticeProposalNumber(message.getLatticeProposalNumber());

            ArrayList<Message>batch = new ArrayList<>();
            batch.add(lm);
            beb.sendBatch(batch, CommonUtils.getHost(message.getOriginalFrom(), processes));
        }else{
            proposedValue/*acceptedValue*/.addAll(mProposedValue);

            Message lm = new Message(self.getId(), message.getOriginalFrom(), messageLsn.incrementAndGet());
            lm.setLatticeType(LatticeType.NACK);
            lm.setLatticeProposalNumber(message.getLatticeProposalNumber());
            lm.setLatticeValue(proposedValue/*acceptedValue*/);

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

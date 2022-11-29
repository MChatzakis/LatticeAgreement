package cs451.lattice;

import cs451.Host;
import cs451.broadcast.BestEffortBroadcast;
import cs451.commonUtils.CommonUtils;
import cs451.messaging.Message;
import cs451.structures.Deliverer;

import java.net.SocketException;
import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class LatticeAgreement implements Deliverer {
    private BestEffortBroadcast beb;
    private ArrayList<Host>processes;
    private Host self;
    private double f;
    //PROPOSER VARS
    private boolean active;
    private int ackCount;
    private int nAckCount;
    private int activeProposalNumber;
    private Set<Integer> proposedValue;
    //ACCEPTOR VARS
    private Set<Integer> acceptedValue;

    public LatticeAgreement(ArrayList<Host> processes, Host self) throws SocketException {
        this.beb = new BestEffortBroadcast(this, processes, self);
        this.processes = processes;
        this.self = self;

        this.f = calculateF();

        //proposer
        this.active = false;
        this.ackCount = 0;
        this.nAckCount = 0;
        this.activeProposalNumber = 0;
        this.proposedValue = ConcurrentHashMap.newKeySet();

        //acceptor
        this.acceptedValue = ConcurrentHashMap.newKeySet();
    }

    @Override
    public void deliver(Message message) {
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

        if(nAckCount > 0 && (ackCount+nAckCount>=f+1) && active){
            activeProposalNumber++;
            ackCount = 0;
            nAckCount = 0;

            Message lm = new Message(self.getId(), (byte) -1, -1);
            lm.setLatticeType(LatticeType.PROPOSAL);
            lm.setLatticeValue(proposedValue);
            lm.setLatticeProposalNumber(activeProposalNumber);

            ArrayList<Message>batch = new ArrayList<>();
            batch.add(lm);
            beb.broadcastBatch(batch);
        }

        if(ackCount >= f+1 && active){
            decide(proposedValue);
            active = false;
        }
    }

    @Override
    public void freeResources() {
        beb.freeResources();
    }

    public void propose(Set<Integer>proposal){
        proposedValue = proposal;

        boolean status = true; //?? maybe... active?

        activeProposalNumber++;
        ackCount = 0;
        nAckCount = 0;

        Message lm = new Message(self.getId(), (byte) -1,-1); //! see what u gonna do with the id

        lm.setLatticeType(LatticeType.PROPOSAL);
        lm.setLatticeValue(proposedValue);
        lm.setLatticeProposalNumber(activeProposalNumber);

        ArrayList<Message>batch = new ArrayList<>();
        batch.add(lm);
        beb.broadcastBatch(batch);
    }

    public void decide(Set<Integer>value){
        // todo
        // note
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
        if(mProposedValue.containsAll(acceptedValue)){
            acceptedValue = mProposedValue;

            Message lm = new Message(self.getId(), message.getOriginalFrom(), -1);
            lm.setLatticeType(LatticeType.ACK);
            lm.setLatticeProposalNumber(message.getLatticeProposalNumber());

            ArrayList<Message>batch = new ArrayList<>();
            batch.add(lm);
            beb.sendBatch(batch, CommonUtils.getHost(message.getOriginalFrom(), processes));
        }else{
            acceptedValue.addAll(mProposedValue);

            Message lm = new Message(self.getId(), message.getOriginalFrom(), -1);
            lm.setLatticeType(LatticeType.NACK);
            lm.setLatticeProposalNumber(message.getLatticeProposalNumber());
            lm.setLatticeValue(acceptedValue);

            ArrayList<Message>batch = new ArrayList<>();
            batch.add(lm);
            beb.sendBatch(batch, CommonUtils.getHost(message.getOriginalFrom(), processes));
        }
    }

    private double calculateF(){
        return (processes.size() - 1.0)/2;
    }

}

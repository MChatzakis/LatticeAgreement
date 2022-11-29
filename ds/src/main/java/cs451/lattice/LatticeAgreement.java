package cs451.lattice;

import cs451.Host;
import cs451.broadcast.BestEffortBroadcast;
import cs451.messaging.Message;
import cs451.structures.Deliverer;

import java.net.SocketException;
import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class LatticeAgreement implements Deliverer {
    private BestEffortBroadcast beb;
    private ArrayList<Host>processes;
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
        //todo
        switch (message.getLatticeType()){
            case ACK:

                break;
            case NACK:
                break;
            case PROPOSAL:
                break;
        }
    }

    @Override
    public void freeResources() {
        beb.freeResources();
    }

    public void propose(Set<Integer>proposal){
        //todo
        proposedValue = proposal;

        boolean status = true; //??

        activeProposalNumber++;
        ackCount = 0;
        nAckCount = 0;

        beb.broadcastBatch(null); //tofix
    }

    private double calculateF(){
        return (processes.size() - 1.0)/2;
    }

}

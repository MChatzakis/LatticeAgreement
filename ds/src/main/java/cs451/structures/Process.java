package cs451.structures;

import cs451.Constants;
import cs451.Host;
import cs451.broadcast.*;
import cs451.lattice.LatticeAgreement;
import cs451.links.PerfectLink;
import cs451.messaging.Message;
import cs451.commonUtils.CommonUtils;
import cs451.commonUtils.Logger;
import cs451.links.Link;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.SocketException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static cs451.Constants.MESSAGES_PER_BATCH;

/**
 * This class represents a process of the distributed system.
 */
public class Process implements Deliverer {
    private int id;
    //private int pid;
    private Host selfHost;
    private ArrayList<Host> hosts;
    private Logger logger;
    private Link link;
    private Broadcast broadcast;
    private LatticeAgreement agreement;
    private long totalDelivered;
    private long totalSent;
    private long totalBroadcasted;
    private long deliveryCountTimeStart;
    private int messages2broadcast;
    private long batchMessagesBroadcasted = 0;
    private int batchesBroadcasted = 0;
    private int[] messageBatchSizes;
    private String performanceLog;
    private Map<Integer, String> latticeProposalsBuffer;
    //Lattice
    private int latticeProposals;
    private int latticeRound;
    private int completedLatticeRoundId;
    private BufferedReader latticeBr;
    private int completedProposals;

    public Process(short id, /*int pid,*/ ArrayList<Host> hosts, Logger logger, int latticeProposals, BufferedReader latticeBr) throws SocketException {
        this.id = id;
        //this.pid = pid;
        this.hosts = hosts;
        this.logger = logger;
        this.selfHost = CommonUtils.getHost(id, hosts);

        this.agreement = new LatticeAgreement(this, hosts, selfHost, latticeProposals);
        this.latticeProposals = latticeProposals;
        this.latticeRound = 0;
        this.latticeProposalsBuffer = new HashMap<>();
        this.completedLatticeRoundId = 0;
        this.latticeBr = latticeBr;
        this.completedProposals = 0;

        this.totalDelivered = 0;
        this.totalSent = 0;
        this.performanceLog = "Not enough messages to count performance.";
    }

    public void startReceiving() {
        deliveryCountTimeStart = System.nanoTime();

        //broadcast.startReceiving();
        //link.startReceiving();
        agreement.startReceiving();
    }

    @Override
    public void deliver(Message message) {
        if (Constants.PROCESS_MESSAGING_VERBOSE || Constants.PROCESS_BROADCASTING_VERBOSE) {
            System.out.println("[Process]: Delivery " + message);
        }
        logger.addEvent("d " + message.getRelayFrom() + " " + message.getId());

        totalDelivered++;

        Host senderHost = CommonUtils.getHost(message.getOriginalFrom(), hosts);
        senderHost.increaseDeliveredMessages();

        if (totalDelivered % 10 == 0) {
            long end = System.nanoTime();
            long elapsedTime = end - deliveryCountTimeStart;
            double elapsedTimeSeconds = (double) elapsedTime / 1000000000;
            double target = 1.0; //1second
            double resultT = totalDelivered * target / elapsedTimeSeconds;
            this.performanceLog = "Performance: " + totalDelivered + " messages in " + elapsedTimeSeconds + " seconds (" + Math.round(resultT) + " m/s)";
        }

        if (message.getOriginalFrom() == selfHost.getId()) {
            if (message.getId() >= messageBatchSizes[batchesBroadcasted - 1]) {
                ArrayList<Message> batch = new ArrayList<>();
                for (long i = batchMessagesBroadcasted; i < messages2broadcast; i++) {
                    batch.add(new Message((short) getId(), (short) -1, (int) i + 1));
                    batchMessagesBroadcasted++;
                    if (batch.size() == MESSAGES_PER_BATCH) {
                        //System.out.println("Batch:" + batch);
                        broadcastBatch(batch);
                        batchesBroadcasted++;
                        batch.clear();
                        break;
                    }
                }

                if (batch.size() > 0) {
                    //System.out.println("Batch:" + batch);
                    broadcastBatch(batch);
                    batchesBroadcasted++;
                }
            }
        }
    }

    public void sendBatch(ArrayList<Message> batch, Host toHost) throws IOException {
        link.sendBatch(batch, toHost);

        for (Message message : batch) {
            logger.addEvent("b " + message.getId());
            totalSent++;
        }

        if (Constants.PROCESS_MESSAGING_VERBOSE) {
            System.out.println("[Process]: Sent " + batch);
        }
    }

    public int getId() {
        return id;
    }

    public ArrayList<Host> getHosts() {
        return hosts;
    }

    public String toString() {
        String s;

        s = "-----\nProcess Info:\n";
        s += "id:" + id + "\n";
        /*s += "pid:" + pid + "\n";*/
        s += "Self-Host:" + selfHost.toString() + "\n";
        s += "List of Known Hosts:" + hosts.size() + "\n";
        for (Host h : hosts) {
            s += ">>>> " + h.toString() + "\n";
        }
        s += "-----\n";
        return s;
    }

    public void freeResources() {
        //perfectLink.freeResources();
        broadcast.freeResources();
    }

    public void logData() throws IOException {
        logger.flush2file();
    }

    public long getTotalDelivered() {
        return totalDelivered;
    }

    public void printHostSendingInfo() {
        for (Host h : hosts) {
            System.out.println("Host " + h.getId() + ": " + h.getDeliveredMessages());
        }
    }

    public long getTotalSent() {
        return totalSent;
    }

    public long getTotalBroadcasted() {
        return totalBroadcasted;
    }

    public String getPerformanceLog() {
        return performanceLog;
    }

    public void broadcastBatch(ArrayList<Message> batch) {
        broadcast.broadcastBatch(batch);
        for (Message message : batch) {
            logger.addEvent("b " + message.getId());
            totalBroadcasted++;

            if (Constants.PROCESS_BROADCASTING_VERBOSE) {
                System.out.println("[Process]: Broadcast " + message);
            }
        }
    }

    public void startBroadcasting(int numberOfMessages) {
        this.messages2broadcast = numberOfMessages;

        int totalBatches = (int) Math.ceil(1.0 * numberOfMessages / MESSAGES_PER_BATCH);

        this.messageBatchSizes = new int[totalBatches];
        for (int i = 0; i < totalBatches; i++) {
            int from = i * MESSAGES_PER_BATCH;
            int to = -1;
            if (i == totalBatches - 1) {
                to = messages2broadcast;
            } else {
                to = (i + 1) * MESSAGES_PER_BATCH;
            }

            messageBatchSizes[i] = to;
            //System.out.print(messageBatchSizes[i] + " ");
        }

        //System.out.println( "\n" + messageBatchSizes.length);


        //1. broadcast first batch.
        ArrayList<Message> batch = new ArrayList<>();
        for (long i = batchMessagesBroadcasted; i < messages2broadcast; i++) {
            batch.add(new Message((short) getId(), (short) -1, (int) i + 1));
            batchMessagesBroadcasted++;
            if (batch.size() == MESSAGES_PER_BATCH) {
                //System.out.println("First complete Batch:" + batch);
                broadcastBatch(batch);
                batchesBroadcasted++;
                batch.clear();
                break;
            }
        }

        if (batch.size() > 0) {
            //System.out.println("First incomplete Batch:" + batch);
            broadcastBatch(batch);
            batchesBroadcasted++;
        }


    }

    public synchronized void decide(Set<Integer> proposalSet, int round) {
        latticeProposalsBuffer.put(round, CommonUtils.getSetAsString(proposalSet));

        //System.out.println("Decided unregistered set " +  proposalSet + " for round " + round);

        List<Integer> keyList = new ArrayList<>(latticeProposalsBuffer.keySet());
        int i;
        for (i = 0; i < keyList.size(); i++) {
            int roundID = keyList.get(i);
            String value = latticeProposalsBuffer.get(roundID);
            if (roundID == completedLatticeRoundId) {
                logger.addEvent(value);
                //System.out.println("Registered set " +  value + " for round " + roundID);

                completedProposals++;

                completedLatticeRoundId++;
                latticeProposalsBuffer.remove(roundID);

                i = -1;
                keyList = new ArrayList<>(latticeProposalsBuffer.keySet());
            }
        }

        String st;
        try {
            if ((st = latticeBr.readLine()) != null) {
                String[] contents = st.split(" ");
                Set<Integer> newProposalSet = ConcurrentHashMap.newKeySet();//new HashSet<>();
                for (int ii = 0; ii < contents.length; ii++) {
                    newProposalSet.add(Integer.parseInt(contents[ii]));
                }
                agreement.propose(newProposalSet, latticeRound);
                latticeRound++;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (completedProposals % 10 == 0) {
            long end = System.nanoTime();
            long elapsedTime = end - deliveryCountTimeStart;
            double elapsedTimeSeconds = (double) elapsedTime / 1000000000;
            double target = 1.0; //1second
            double resultT = completedProposals * target / elapsedTimeSeconds;
            this.performanceLog = "Performance: " + completedProposals + " proposals in " + elapsedTimeSeconds + " seconds (" + Math.round(resultT) + " p/s)";
        }

    }

    public void propose(Set<Integer>proposalSet){ //must be concurrent
        agreement.propose(proposalSet, latticeRound);
        latticeRound++;
    }

    public void triggerInitialLattice() throws IOException {
        String st;
        if ((st = latticeBr.readLine()) != null) {
            String [] contents = st.split(" ");
            Set<Integer> proposalSet = ConcurrentHashMap.newKeySet();
            for(int i=0; i<contents.length; i++){
                proposalSet.add(Integer.parseInt(contents[i]));
            }
            agreement.propose(proposalSet, latticeRound);
            latticeRound++;
        }
    }

    public int getCompletedProposals() {
        return completedProposals;
    }

    public void setCompletedProposals(int completedProposals) {
        this.completedProposals = completedProposals;
    }
}

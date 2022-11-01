package cs451.broadcast;

import cs451.Host;
import cs451.commonUtils.MHPair;
import cs451.failureDetection.PerfectFailureDetector;
import cs451.structures.Message;

import java.util.Map;
import java.util.Set;

public class UniformReliableBroadcast {

    private BestEffortBroadcast beb;
    private PerfectFailureDetector P;

    private Map<Message, Set<Host>> ack;

    private Set<Host> correct;
    private Set<MHPair> pending;
    private Set<Message> delivered;



}

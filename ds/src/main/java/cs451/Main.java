package cs451;

import cs451.messaging.MessageBatch;
import cs451.commonUtils.CommonUtils;
import cs451.commonUtils.Logger;
import cs451.structures.Process;

import java.io.*;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Main {
    static Process PROCESS;

    private static void handleSignal() {
        System.out.println("Immediately stopping network packet processing.");

        if(PROCESS == null){
            System.out.println("Could not write output because the program terminated very early and PROCESS could not be allocated.");
            return;
        }

        //write/flush output file if necessary
        System.out.println("Writing output.");
        try {
            PROCESS.logData(); //flushing to file
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("\n=== Statistics ===");

        //Enable the following to activate debug statistics messages.
        //PROCESS.printHostSendingInfo();
        //System.out.println("Delivered messages: " + PROCESS.getTotalDelivered());
        //System.out.println("Broadcasted messages: " + PROCESS.getTotalBroadcasted());
        //System.out.println("Sent messages: " + PROCESS.getTotalSent());

        System.out.println("Completed proposals: " + PROCESS.getCompletedProposals());
        System.out.println(PROCESS.getPerformanceLog());
        CommonUtils.calculateMemoryUsed();

        //PROCESS.freeResources();
    }

    private static void initSignalHandlers() {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                handleSignal();
            }
        });
    }

    public static void main(String[] args) throws InterruptedException, IOException {
        Parser parser = new Parser(args);
        parser.parse();

        initSignalHandlers();

        long pid = ProcessHandle.current().pid();
        System.out.println("From a new terminal type `kill -SIGINT " + pid + "` or `kill -SIGTERM " + pid + "` to stop processing packets\n");

        CommonUtils.createEmptyFile(parser.output());

        //initializeAndTriggerLattice(parser, (int) pid);
        initializeAndTriggerInitialLattice(parser);

        while (true) {
            Thread.sleep(60 * 60 * 1000);
        }
    }

    public static void sendAllMessages(String configFile) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(new File(configFile)));
        String st;
        int id=1;
        while ((st = br.readLine()) != null) {
            String [] contents = st.split(" ");

            int repetitions = Integer.parseInt(contents[0]);
            int to = Integer.parseInt(contents[1]);

            if(to == PROCESS.getId()){
                return;
            }

            MessageBatch.sendBatch(repetitions, PROCESS, CommonUtils.getHost(to, PROCESS.getHosts()));
        }
    }

    public static void broadcastAllMessages(String configFile) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(new File(configFile)));
        String st;
        while ((st = br.readLine()) != null) {
            String [] contents = st.split(" ");
            int numberOfMessages = Integer.parseInt(contents[0]);
            PROCESS.startBroadcasting(numberOfMessages);
        }
    }

    public static void initializeAndTriggerLattice(Parser parser, int pid) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(new File(parser.config())));
        String st;
        int lineCounter = 0, p = -1, vs, ds;
        while ((st = br.readLine()) != null) {
            String [] contents = st.split(" ");

            if(lineCounter == 0){
                assert contents.length >= 3;

                p  = Integer.parseInt(contents[0]);
                vs = Integer.parseInt(contents[1]);
                ds = Integer.parseInt(contents[2]);

                System.out.println("Initializing...\n");

                PROCESS = new Process(parser.myId(), /*(int) pid,*/ new ArrayList<>(parser.hosts()), new Logger(parser.output()), p, br);
                System.out.println(PROCESS);

                System.out.println("Broadcasting and delivering messages...\n");
                PROCESS.startReceiving();

            }else{
                Set<Integer> proposalSet = new HashSet<>();
                for(int i=0; i<contents.length; i++){
                    proposalSet.add(Integer.parseInt(contents[i]));
                }
                PROCESS.propose(proposalSet);
            }

            lineCounter++;
        }

    }

    public static void initializeAndTriggerInitialLattice(Parser parser/*, int pid*/) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(new File(parser.config())));
        String st;
        if ((st = br.readLine()) != null) {
            String [] contents = st.split(" ");

            assert contents.length >= 3;

            int p  = Integer.parseInt(contents[0]);

            System.out.println("Initializing...\n");

            PROCESS = new Process(parser.myId(), /*(int) pid,*/ new ArrayList<>(parser.hosts()), new Logger(parser.output()), p, br);
            System.out.println(PROCESS);

            System.out.println("Broadcasting and delivering messages...\n");
            PROCESS.startReceiving();
            PROCESS.triggerInitialLattice();
        }
    }

    //./stress.py agreement -r run.sh -l ../testing/outputs/ -p 5 -n 10 -v 3 -d 5
    //../tools/validate_fifo.py --proc_num 5 output ../testing/outputs/
    //../tools/stress.py -r run.sh -t fifo -l ../testing/outputs/ -p 5 -m 5000
}

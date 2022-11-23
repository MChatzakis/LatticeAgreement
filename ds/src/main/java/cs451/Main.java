package cs451;

import cs451.broadcast.messaging.MessageBatch;
import cs451.commonUtils.CommonUtils;
import cs451.commonUtils.Logger;
import cs451.broadcast.messaging.Message;
import cs451.structures.Process;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Main {
    static Process PROCESS;

    private static void handleSignal() {
        System.out.println("Immediately stopping network packet processing.");

        //write/flush output file if necessary
        System.out.println("Writing output.");
        try {
            PROCESS.logData(); //flashing to file
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("\n=== Statistics ===");

        //Enable the following to activate debug statistics messages.
        PROCESS.printHostSendingInfo();

        System.out.println("Delivered messages: " + PROCESS.getTotalDelivered());
        System.out.println("Broadcasted messages: " + PROCESS.getTotalBroadcasted());
        System.out.println("Sent messages: " + PROCESS.getTotalSent());

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

        System.out.println("Initializing...\n");

        CommonUtils.createEmptyFile(parser.output());

        PROCESS = new Process(parser.myId(), (int) pid, new ArrayList<>(parser.hosts()), /*LOGGER*/new Logger(parser.output()));
        System.out.println(PROCESS);

        System.out.println("Broadcasting and delivering messages...\n");

        PROCESS.startReceiving();

        sendAllMessages(parser.config());
        //broadcastAllMessages(parser.config());

        // After a process finishes broadcasting it waits forever for the delivery of messages.
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

            /*for(int i=0; i<repetitions; i++){
                Message msg2sent = new Message(PROCESS.getId(), to, id++);
                Host host2sent = CommonUtils.getHost(to, PROCESS.getHosts());

                PROCESS.send(msg2sent, host2sent);
            }*/

            MessageBatch.sendBatch(repetitions, PROCESS, CommonUtils.getHost(to, PROCESS.getHosts()));

        }
    }

    public static void broadcastAllMessages(String configFile) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(new File(configFile)));
        String st;
        int id=1;
        while ((st = br.readLine()) != null) {
            String [] contents = st.split(" ");

            int repetitions = Integer.parseInt(contents[0]); //how many messages to broadcast.
            int numberOfMessages = Integer.parseInt(contents[0]);
            for(int i=0; i<repetitions; i++){
                int to = -1;
                Message msg2sent = new Message(PROCESS.getId(), to, id++);
                PROCESS.broadcast(msg2sent);
            }

        }
    }

}

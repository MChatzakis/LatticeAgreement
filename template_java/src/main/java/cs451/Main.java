package cs451;

import cs451.commonUtils.CommonUtils;
import cs451.commonUtils.Logger;
import cs451.structures.Message;
import cs451.structures.Process;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Queue;

public class Main {
    static Logger LOGGER;
    static Process PROCESS;

    private static void handleSignal() {
        //immediately stop network packet processing
        System.out.println("Immediately stopping network packet processing.");

        //write/flush output file if necessary
        System.out.println("Writing output.");
        try {
            System.out.println("Total delivered messages:" + PROCESS.getTotalDelivered());

            LOGGER.flush2file();

            PROCESS.printHostSendingInfo();
            //PROCESS.freeResources();

        } catch (IOException e) {
            e.printStackTrace();
        }
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

        LOGGER = new Logger(parser.output());
        PROCESS = new Process(parser.myId(), (int) pid, new ArrayList<>(parser.hosts()), LOGGER);

        Queue<Message> messageQueue = CommonUtils.generateMessageQueue(parser.config(), parser.myId());

        System.out.println(PROCESS);
        System.out.println("MessageQueue (size= "+messageQueue.size()+"): \n" + messageQueue);

        System.out.println("Broadcasting and delivering messages...\n");

        PROCESS.startReceiving();

        while(!messageQueue.isEmpty()){
            Message msg2sent = messageQueue.remove();
            Host host2sent = CommonUtils.getHost(msg2sent.getTo(), PROCESS.getHosts());

            if(host2sent.getId() == PROCESS.getId()){
                break;
            }

            PROCESS.send(msg2sent, host2sent);
        }

        // After a process finishes broadcasting it waits forever for the delivery of messages.
        while (true) {
            Thread.sleep(60 * 60 * 1000);
        }
    }
}

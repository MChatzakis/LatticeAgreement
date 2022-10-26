package cs451;

import cs451.commonUtils.CommonUtils;
import cs451.commonUtils.Logger;
import cs451.structures.Message;
import cs451.structures.Process;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
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
            //Enable the following to activate debug statistics messages.
            //PROCESS.printHostSendingInfo();
            System.out.println("Total delivered messages:" + PROCESS.getTotalDelivered());
            System.out.println("Total sent messages:" + PROCESS.getTotalSent());

            LOGGER.flush2file();

            //enable that to free the resources (sockets etc. at the end)
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
        System.out.println(PROCESS);

        System.out.println("Broadcasting and delivering messages...\n");

        PROCESS.startReceiving();

        sendAllMessages(parser.config());
        System.out.println("Process " + PROCESS.getId() + " send all the messages.");

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
                //continue?
            }

            for(int i=0; i<repetitions; i++){
                Message msg2sent = new Message(PROCESS.getId(), to, "Bonjour", id++);
                Host host2sent = CommonUtils.getHost(to, PROCESS.getHosts());

                PROCESS.send(msg2sent, host2sent);
            }

        }
    }

}

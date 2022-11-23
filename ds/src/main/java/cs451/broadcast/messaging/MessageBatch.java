package cs451.broadcast.messaging;

import java.io.IOException;
import java.util.ArrayList;

import cs451.Host;
import cs451.structures.Process;


public class MessageBatch {

    private static int MESSAGES_PER_BATCH = 8;

    public static void broadcastBatch(int numberOfMessages, cs451.structures.Process process){
        ArrayList<Message>batch = new ArrayList<>();
        for(int i=0; i<numberOfMessages; i++){
            batch.add(new Message(process.getId(), -1, i+1));
            if(batch.size() == MESSAGES_PER_BATCH){
                System.out.println("Batch:" + batch);
                process.broadcastBatch(batch);
                batch.clear();
            }
        }

        if(batch.size() > 0){
            System.out.println("Batch:" + batch);
            process.broadcastBatch(batch);

        }
    }

    public static void sendBatch(int numberOfMessages, cs451.structures.Process process, Host toHost) throws IOException {

        ArrayList<Message>batch = new ArrayList<>();
        for(int i=0; i<numberOfMessages; i++){
            batch.add(new Message(process.getId(), toHost.getId(), i+1));
            if(batch.size() == MESSAGES_PER_BATCH){
                System.out.println("Batch:" + batch);
                process.sendBatch(batch, toHost);
                batch.clear();
            }
        }

        if(batch.size() > 0){
            System.out.println("Batch:" + batch);
            process.sendBatch(batch, toHost);
        }
    }

}

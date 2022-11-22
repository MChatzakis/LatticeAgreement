package cs451.broadcast.messaging;

import java.util.ArrayList;
import cs451.structures.Process;


public class MessageBatch {

    private static int MESSAGES_PER_BATCH = 8;

    public static void broadcastBatch(int numberOfMessages, cs451.structures.Process process){
        int batches = numberOfMessages/MESSAGES_PER_BATCH;
        for(int i=0; i<MESSAGES_PER_BATCH; i++){
            int from = i * batches;
            int to;

            if (i != MESSAGES_PER_BATCH - 1)
                to = (i + 1) * batches;
            else
                to = numberOfMessages;

            ArrayList<Message> batch = new ArrayList<>();
            for(int id = from; id<to; id++){
                batch.add(new Message(process.getId(), -1, id));
            }

            //process.broadcastBatch(batch);
        }

    }



}

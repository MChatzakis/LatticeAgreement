package cs451.broadcast;

import cs451.Host;
import cs451.links.FairLossLink;
import cs451.links.Link;
import cs451.links.PerfectLink;
import cs451.structures.Deliverer;
import cs451.messaging.Message;

import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import static cs451.Constants.BEB_MESSAGING_VERBOSE;

public class BestEffortBroadcast extends Broadcast{
    Link link;

    public BestEffortBroadcast(Deliverer deliverer, List<Host> processes, Host self) throws SocketException {
        super(deliverer, processes, self);
        link = new PerfectLink(this, self.getPort(), new ArrayList<>(processes), self.getId());
        //link = new FairLossLink(this, self.getPort());
    }

    @Override
    public void broadcastBatch(ArrayList<Message> batch) {
        for(Host process:processes) {
            ArrayList<Message>batchCopy = new ArrayList<>();
            for(Message message : batch){
                Message m = null;
                try {
                    m = (Message) message.clone();
                } catch (CloneNotSupportedException e) {
                    throw new RuntimeException(e);
                }

                m.setTo(process.getId());
                batchCopy.add(m);
            }
            link.sendBatch(batchCopy, process);
        }
    }

    @Override
    public void startReceiving() {
        link.startReceiving();
    }

    @Override
    public void deliver(Message message) {
        if(BEB_MESSAGING_VERBOSE){
            System.out.println("[BEB] Process" + self.getId() + " delivered message " + message);
        }

        deliverer.deliver(message);
    }

    @Override
    public void freeResources() {
        link.freeResources();
    }
}

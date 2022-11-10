package cs451.broadcast;

import cs451.Host;
import cs451.links.PerfectLink;
import cs451.structures.Deliverer;
import cs451.structures.Message;

import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

public class BestEffortBroadcast extends Broadcast{
    PerfectLink perfectLink;

    public BestEffortBroadcast(Deliverer deliverer, List<Host> processes, Host self) throws SocketException {
        super(deliverer, processes, self);
        perfectLink = new PerfectLink(this, self.getPort(), new ArrayList<>(processes));
    }

    @Override
    public void broadcast(Message message) {
        for(Host process:processes){
            message.setFrom(self.getId());

            perfectLink.send(message, process);
        }
    }

    @Override
    public void deliver(Message message) {
        deliverer.deliver(message);
    }

    @Override
    public void freeResources() {
        perfectLink.freeResources();
    }
}

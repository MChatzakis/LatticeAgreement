package cs451.links;

import cs451.Host;
import cs451.network.UDPReceiver;
import cs451.network.UDPSender;
import cs451.structures.Deliverer;
import cs451.structures.Message;


import java.net.SocketException;

public class FairLossLink extends Link{

    UDPReceiver receiver;
    UDPSender sender;

    public FairLossLink(int port, Deliverer deliverer) throws SocketException {
        this.deliverer = deliverer;
        this.receiver = new UDPReceiver(port, this);

        runReceiverThread();
    }

    private void runReceiverThread(){
        Thread receiverThread = new Thread(receiver, "ReceiverThread");
        receiverThread.start();
    }

    @Override
    public void send(Message message, Host host){
        sender.send(message, host.getIp(), host.getPort());
    }

    @Override
    public void deliver(Message message) {
        deliverer.deliver(message);
    }


}

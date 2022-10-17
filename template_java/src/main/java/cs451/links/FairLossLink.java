package cs451.links;

import cs451.Constants;
import cs451.Host;
import cs451.network.UDPReceiver;
import cs451.network.UDPSender;
import cs451.structures.Deliverer;
import cs451.structures.Message;


import java.net.SocketException;

public class FairLossLink extends Link{

    UDPReceiver receiver;
    UDPSender sender;

    public FairLossLink(Deliverer deliverer, int port) throws SocketException {
        this.deliverer = deliverer;
        this.receiver = new UDPReceiver(port, this);
        this.sender = new UDPSender();
    }

    private void runReceiverThread(){
        Thread receiverThread = new Thread(receiver, "ReceiverThread");
        receiverThread.start();
    }

    @Override
    public void send(Message message, Host host){
        if(Constants.FLL_MESSAGING_VERBOSE){
            System.out.println("[FairLossLink]: Sent " + message);
        }
        sender.send(message, host.getIp(), host.getPort());
    }

    @Override
    public void startReceiving() {
        runReceiverThread();
    }

    @Override
    public void deliver(Message message) {
        if(Constants.FLL_MESSAGING_VERBOSE){
            System.out.println("[FairLossLink]: Delivery " + message);
        }
        deliverer.deliver(message);
    }


}

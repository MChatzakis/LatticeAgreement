package cs451.links;

import cs451.Constants;
import cs451.Host;
import cs451.network.UDPReceiver;
import cs451.structures.Deliverer;
import cs451.messaging.Message;

import java.net.SocketException;
import java.util.ArrayList;

/**
 * FairLossLink implementation
 */
public class FairLossLink extends Link{

    private UDPReceiver receiver;
    private Thread receiverThread;

    public FairLossLink(Deliverer deliverer, int port) throws SocketException {
        this.deliverer = deliverer;
        this.receiver = new UDPReceiver(port, this);
    }

    private void runReceiverThread(){
        receiverThread = new Thread(receiver, "ReceiverThread");
        receiverThread.start();
    }

    private void stopReceiverThread(){
        receiverThread.interrupt(); //need to check that again
    }

    @Override
    public void sendBatch(ArrayList<Message> batch, Host host) {
        if(Constants.FLL_MESSAGING_VERBOSE){
            System.out.println("[FairLossLink]: Sent " + batch);
        }
        receiver.sendBatch(batch, host.getIp(), host.getPort());
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

    public void freeResources(){
        //stopReceiverThread();
        //sender.freeResources();
        //receiver.freeResources();
    }

}

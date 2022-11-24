package cs451.links;

import cs451.Host;
import cs451.structures.Deliverer;
import cs451.messaging.Message;

import java.util.ArrayList;

/**
 * Abstract link class
 * Notes:
 * Implements Deliver:  Specifies the delivery action that this links will do uppon
 *                      receiving a message
 */
public abstract class Link implements Deliverer {
    protected Deliverer deliverer;

    public abstract void sendBatch(ArrayList<Message> batch, Host host);

    public abstract void startReceiving();
}

package cs451.links;

import cs451.Host;
import cs451.structures.Deliverer;
import cs451.broadcast.messaging.Message;

/**
 * Abstract link class
 * Notes:
 * Implements Deliver:  Specifies the delivery action that this links will do uppon
 *                      receiving a message
 */
public abstract class Link implements Deliverer {
    protected Deliverer deliverer; //Used to push a delivery to another layer

    public abstract void send(Message message, Host host);

    public abstract void startReceiving();
}

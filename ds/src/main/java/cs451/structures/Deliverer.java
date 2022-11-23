package cs451.structures;

import cs451.messaging.Message;

/**
 * A deliverer instance should specify the action that an Object should do when it delivers a message.
 * Its use is to support the "pushing" of delivery in the above layers
 *
 * e.g.
 * [Transport Layer] UDPReceiver => FairLossLink => StubbornLink => PerfectLink => Process [Application Layer]
 *
 * All the classes of the above scheme implement this interface.
 */
public interface Deliverer {
    /**
     * Specifies the action above message delivery
     * @param message
     */
    public void deliver(Message message);

    public void freeResources();
}

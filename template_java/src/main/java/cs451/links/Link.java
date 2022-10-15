package cs451.links;

import cs451.commonUtils.Logger;
import cs451.structures.Deliverer;
import cs451.structures.Message;

import java.net.SocketException;

public abstract class Link implements Deliverer {
    public abstract void send(Message message, String toIP, int toID);
}

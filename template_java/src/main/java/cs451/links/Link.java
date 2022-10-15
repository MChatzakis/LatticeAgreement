package cs451.links;

import cs451.Host;
import cs451.commonUtils.Logger;
import cs451.structures.Deliverer;
import cs451.structures.Message;

import java.net.SocketException;

public abstract class Link implements Deliverer {
    protected Deliverer deliverer;

    public abstract void send(Message message, Host host);
}

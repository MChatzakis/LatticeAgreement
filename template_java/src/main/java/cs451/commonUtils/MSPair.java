package cs451.commonUtils;

import cs451.Host;
import cs451.structures.Message;

public class MSPair {
    private Message message;
    private Host host;

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public Host getHost() {
        return host;
    }

    public void setHost(Host host) {
        this.host = host;
    }

    public MSPair(Message message, Host host) {
        this.message = message;
        this.host = host;
    }

    public boolean equals(Object obj)
    {
        if (obj == null)
            return false;
        if (obj == this)
            return true;

        MSPair otherPair = (MSPair) obj;
        return (this.message.equals(otherPair.message) &&
                this.host.equals(otherPair.host)
                );
    }

}

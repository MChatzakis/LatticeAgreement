package cs451.commonUtils;

import cs451.Host;
import cs451.messaging.Message;

/**
 * Utility class holding a pair (Message,Host)
 */
public class MHPair {
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

    public MHPair(Message message, Host host) {
        this.message = message;
        this.host = host;
    }

    public boolean equals(Object obj)
    {
        if (obj == null)
            return false;
        if (obj == this)
            return true;

        MHPair otherPair = (MHPair) obj;
        return (
                this.message.equals(otherPair.message) &&
                this.host.equals(otherPair.host)
                );
    }

    @Override
    public int hashCode(){
        return this.message.hashCode() * this.host.hashCode();
    }


    public String toString(){
        return host.toString() + " " + message.toString();
    }

}

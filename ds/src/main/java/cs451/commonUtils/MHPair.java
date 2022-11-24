package cs451.commonUtils;

import cs451.Host;
import cs451.messaging.Message;

/**
 * Utility class holding a pair (Message,Host)
 */
public class MHPair {
    private Message message;
    private byte hostID;

    public byte getHostID() {
        return hostID;
    }

    public void setHostID(byte hostID) {
        this.hostID = hostID;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }


    public MHPair(Message message, Byte hostID) {
        this.message = message;
        this.hostID = hostID;
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
                this.hostID == otherPair.hostID
                );
    }

    @Override
    public int hashCode(){
        return this.message.hashCode() * this.hostID;
    }


    public String toString(){
        return hostID + " " + message.toString();
    }

}

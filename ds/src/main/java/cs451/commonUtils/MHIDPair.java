package cs451.commonUtils;


public class MHIDPair {
    private int messageID;

    public short getHostID() {
        return hostID;
    }

    private short hostID;

    public int getMessageID() {
        return messageID;
    }

    public void setMessageID(int messageID) {
        this.messageID = messageID;
    }

    public MHIDPair(int messageID, short hostID){
        this.messageID = messageID;
        this.hostID = hostID;
    }

    public boolean equals(Object obj) {
        if (obj == null)
            return false;

        if (obj == this)
            return true;

        MHIDPair p = (MHIDPair) obj;

        boolean result = (
                p.messageID == this.messageID &&
                p.hostID == this.hostID
        );

        return result;
    }

    public int hashCode(){
        return this.messageID * this.hostID;
    }

    public String toString(){
        return this.messageID + "%" + this.hostID;
    }
}

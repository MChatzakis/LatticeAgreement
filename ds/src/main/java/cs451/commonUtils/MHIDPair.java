package cs451.commonUtils;


public class MHIDPair {
    private int messageID;
    private byte hostID;

    public MHIDPair(int messageID, byte hostID){
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
}

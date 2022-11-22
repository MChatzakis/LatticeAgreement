package cs451.structures;

import java.io.Serializable;

/**
 * This class represents a message of the distributed system
 */
public class Message implements Serializable, Cloneable {
    private int id;
    private String data;
    private int relayFrom;
    private int originalFrom;
    private int to;
    private boolean isACK;
    private int lsn;

    public Message(int from, int to, String data, int id){
        this.relayFrom = from;
        this.originalFrom = from;
        this.to = to;
        this.data = data;
        this.id = id;

        this.isACK = false;
        this.lsn = 0;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public int getRelayFrom() {
        return relayFrom;
    }

    public void setRelayFrom(int relayFrom) {
        this.relayFrom = relayFrom;
    }

    public int getTo() {
        return to;
    }

    public void setTo(int to) {
        this.to = to;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isACK() {
        return isACK;
    }

    public void setACK(boolean ACK) {
        isACK = ACK;
    }

    public boolean equals(Object obj) {
        if (obj == null)
            return false;

        if (obj == this)
            return true;

        Message otherMsg = (Message) obj;

        boolean result = (
                this.relayFrom == otherMsg.relayFrom &&
                this.originalFrom == otherMsg.originalFrom &&
                /*this.to == otherMsg.to &&*/
                this.id == otherMsg.id &&
                this.data.equals(otherMsg.data) &&
                this.isACK == otherMsg.isACK
        );

        return result;
    }
    @Override
    public int hashCode(){
        return this.id * this.relayFrom * this.originalFrom * new Boolean(this.isACK).hashCode() * this.data.hashCode();
    }

    @Override
    public Object clone() throws CloneNotSupportedException{
        return super.clone();
    }

    public Message generateAckMessage() throws CloneNotSupportedException {
        Message ackMsg = (Message) this.clone();
        ackMsg.setACK(true);
        return ackMsg;
    }

    public Message generateOriginalMessage() throws CloneNotSupportedException{
        Message orMsg = (Message) this.clone();
        orMsg.setACK(false);
        return orMsg;
    }

    public String toString(){
        return "(id="+id+",or="+originalFrom+",relayFrom="+ relayFrom +",to="+to+",ACK="+isACK+")";
    }

    public int getOriginalFrom() {
        return originalFrom;
    }

    public void setOriginalFrom(int originalFrom) {
        this.originalFrom = originalFrom;
    }

    public int getLsn() {
        return lsn;
    }

    public void setLsn(int lsn) {
        this.lsn = lsn;
    }

}

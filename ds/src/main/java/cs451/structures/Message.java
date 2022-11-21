package cs451.structures;

import java.io.Serializable;

/**
 * This class represents a message of the distributed system
 */
public class Message implements Serializable, Cloneable {
    private String data;
    private int from;
    private int to;
    private int id; //can I use that as lsn?
    private boolean isACK;



    private int lsn=0;

    private int originalFrom; //original sender id

    public Message(int from, int to, String data, int id){
        this.from = from;
        this.originalFrom = from;

        this.to = to;

        this.data = data;

        this.id = id;

        this.isACK = false;
    }

    public Message(int from, int to, String data, int id, boolean isACK){
        this.from = from;
        this.originalFrom = from;
        this.to = to;
        this.data = data;
        this.id = id;
        this.isACK = isACK;
    }

    public Message(int from, int originalFrom, int to, String data, int id){
        this.from = from;
        this.originalFrom = originalFrom;
        this.to = to;
        this.data = data;
        this.id = id;

        this.isACK = false;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public int getFrom() {
        return from;
    }

    public void setFrom(int from) {
        this.from = from;
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
                this.from == otherMsg.from &&
                this.originalFrom == otherMsg.originalFrom &&
                this.to == otherMsg.to &&
                this.id == otherMsg.id &&
                this.data.equals(otherMsg.data) &&
                this.isACK == otherMsg.isACK
        );

        //System.out.println("Comparing " + this + " with " + otherMsg + " => result="+result);
        return result;
    }
    @Override
    public int hashCode(){
        return this.id /* this.to */* this.from; //ti kanw?
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
        return "(id="+id+",or="+originalFrom+",from="+from+",to="+to+",ACK="+isACK+")";
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

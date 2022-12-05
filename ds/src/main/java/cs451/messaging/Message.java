package cs451.messaging;

import cs451.lattice.LatticeType;

import java.io.Serializable;
import java.util.Set;

/**
 * This class represents a message of the distributed system
 */
public class Message implements Serializable, Cloneable, Comparable<Message> {
    private int id;
    private byte relayFrom;
    private byte originalFrom;
    private byte to;
    private boolean isACK;

    //!new ones delete if something happens
    private LatticeType latticeType;
    private int latticeProposalNumber;
    private Set<Integer> latticeValue;
    //new ones end!

    public Message(byte from, byte to, int id){
        this.relayFrom = from;
        this.originalFrom = from;
        this.to = to;
        this.id = id;

        this.isACK = false;
    }

    public byte getRelayFrom() {
        return relayFrom;
    }

    public void setRelayFrom(byte relayFrom) {
        this.relayFrom = relayFrom;
    }

    public byte getTo() {
        return to;
    }

    public void setTo(byte to) {
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
                this.id == otherMsg.id &&
                this.isACK == otherMsg.isACK
        );

        return result;
    }
    @Override
    public int hashCode(){
        return this.id * this.relayFrom * this.originalFrom * new Boolean(this.isACK).hashCode();
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
        return "Classic[id="+id+",or="+originalFrom+",relayFrom="+ relayFrom +",to="+to+"]" + " -- " +
                "Lattice[type="+ latticeType+ ",value="+latticeValue +",prop="+latticeProposalNumber +"]"
                ;
    }

    public byte getOriginalFrom() {
        return originalFrom;
    }

    public void setOriginalFrom(byte originalFrom) {
        this.originalFrom = originalFrom;
    }

    public int compareTo(Message other) {
        return Integer.compare(id, other.id);
    }

    public LatticeType getLatticeType() {
        return latticeType;
    }

    public void setLatticeType(LatticeType latticeType) {
        this.latticeType = latticeType;
    }

    public int getLatticeProposalNumber() {
        return latticeProposalNumber;
    }

    public void setLatticeProposalNumber(int latticeProposalNumber) {
        this.latticeProposalNumber = latticeProposalNumber;
    }

    public Set<Integer> getLatticeValue() {
        return latticeValue;
    }

    public void setLatticeValue(Set<Integer> latticeValue) {
        this.latticeValue = latticeValue;
    }
}

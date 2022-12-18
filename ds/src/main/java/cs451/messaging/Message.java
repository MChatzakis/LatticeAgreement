package cs451.messaging;

import cs451.commonUtils.CommonUtils;
import cs451.lattice.LatticeType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class represents a message of the distributed system
 */
public class Message implements Serializable, Cloneable, Comparable<Message> {
    public static final String FIELDS_DELIM=",";
    public static final String SET_DELIM="%";
    public static final String MSG_DELIM=" ";
    private int id;
    private short relayFrom;
    private short originalFrom;
    private short to;
    private boolean isACK;
    private LatticeType latticeType = null;
    private int latticeProposalNumber;
    private Set<Integer> latticeValue = null;
    private int latticeRound;

    public Message(short from, short to, int id){
        this.relayFrom = from;
        this.originalFrom = from;
        this.to = to;
        this.id = id;

        this.isACK = false;
    }

    public Message(){

    }

    public String serializeString(){
        String serial = "";

        //classic
        serial += id + FIELDS_DELIM;
        serial += relayFrom + FIELDS_DELIM;
        serial += originalFrom + FIELDS_DELIM;
        serial += to + FIELDS_DELIM;
        if(isACK){
            serial += "1" + FIELDS_DELIM;
        }
        else{
            serial += "0" + FIELDS_DELIM;
        }

        //lattice
        if(latticeType != null){
            switch(latticeType){
                case PROPOSAL:
                    serial += "P" + FIELDS_DELIM;
                    break;
                case ACK:
                    serial += "A" + FIELDS_DELIM;
                    break;
                case NACK:
                    serial += "N" + FIELDS_DELIM;
                    break;
                case DECISION:
                    serial += "D" + FIELDS_DELIM;
                    break;
                default:
                    serial += "E" + FIELDS_DELIM;
                    break;
            }
        }else{
            serial += "E" + FIELDS_DELIM;
        }

        serial += latticeProposalNumber + ",";
        if(latticeValue == null || latticeValue.size() == 0){
            serial += FIELDS_DELIM;
        }else{
            //synchronized (latticeValue){
                for(Integer val : latticeValue){
                    serial += val + SET_DELIM;
                }
                serial += FIELDS_DELIM;
            //}

        }

        serial += latticeRound /*+ FIELDS_DELIM*/;

        return serial;
    }

    public static Message deserializeString(String ms){
        Message m = new Message();

        String [] contents = ms.split(FIELDS_DELIM);

        assert contents.length == 9;

        //classic
        int id = Integer.parseInt(contents[0]);
        m.setId(id);

        short relayFrom = Short.parseShort(contents[1]);
        m.setRelayFrom(relayFrom);

        short originalFrom = Short.parseShort(contents[2]);
        m.setOriginalFrom(originalFrom);

        short to = Short.parseShort(contents[3]);
        m.setTo(to);

        boolean isACK = false;
        if(contents[4].equals("1")){
            isACK = true;
        }
        m.setACK(isACK);

        //lattice
        LatticeType latticeType = null;
        switch (contents[5]){
            case "P":
                latticeType = LatticeType.PROPOSAL;
                break;
            case "A":
                latticeType = LatticeType.ACK;
                break;
            case "N":
                latticeType = LatticeType.NACK;
                break;
            case "D":
                latticeType = LatticeType.DECISION;
                break;
        }
        m.setLatticeType(latticeType);

        int latticeProposalNumber = Integer.parseInt(contents[6]);
        m.setLatticeProposalNumber(latticeProposalNumber);

        //values at contents[6]
        Set<Integer>values = null;
        String [] valueContents = contents[7].split(SET_DELIM);
        if(valueContents.length > 0 && !valueContents[0].equals("")){
            values = ConcurrentHashMap.newKeySet(); //check again!
            for(String str : valueContents){
                //System.out.println((">>" + str));
                Integer v = Integer.parseInt(str);
                values.add(v);
            }
        }
        m.setLatticeValue(values);

        int latticeRound = Integer.parseInt(contents[8]);
        m.setLatticeRound(latticeRound);

        return m;
    }

    public static String serializeBatch(ArrayList<Message> batch){
        String serial = "";

        for(Message m : batch){
            serial += m.serializeString() + MSG_DELIM;
        }

        return serial;
    }

    public static ArrayList<Message> deserializeStringBatch(String ms){
        ArrayList<Message>batch = new ArrayList<>();

        String [] contents = ms.split(MSG_DELIM);
        for(String s : contents){
            Message m = Message.deserializeString(s);
            //System.out.println("DesM " + m);
            batch.add(m);
        }

        return batch;
    }

    public short getRelayFrom() {
        return relayFrom;
    }

    public void setRelayFrom(short relayFrom) {
        this.relayFrom = relayFrom;
    }

    public short getTo() {
        return to;
    }

    public void setTo(short to) {
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
        return this.id * this.relayFrom * this.originalFrom * /*new Boolean(this.isACK).hashCode()*/CommonUtils.boolHashCode(this.isACK);
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
        return "Classic[id="+id+",or="+originalFrom+",relayFrom="+ relayFrom +",to="+to+ ",ack=" + isACK + "]" + " --- " +
                "Lattice[type="+ latticeType+ ",value="+latticeValue +",prop="+latticeProposalNumber + ",round=" + latticeRound+ "]"
                ;
    }

    public short getOriginalFrom() {
        return originalFrom;
    }

    public void setOriginalFrom(short originalFrom) {
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

    public int getLatticeRound() {
        return latticeRound;
    }

    public void setLatticeRound(int latticeRound) {
        this.latticeRound = latticeRound;
    }
}

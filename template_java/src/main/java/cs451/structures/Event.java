package cs451.structures;

public class Event {
    private String eventType;

    private int from;
    private int to;

    public Event(String eventType, int from, int to){
        this.eventType = eventType;
        this.from = from;
        this.to = to;
    }

    public String toString(){
        return eventType + " " + to;
    }


}

package cs451.structures;

public class Message {
    private String data;

    private int from;
    private int to;
    private int id;

    public Message(int from, int to, String data, int id){
        this.from = from;
        this.to = to;
        this.data = data;
        this.id = id;
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
}

package cs451;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class Host {

    private static final String IP_START_REGEX = "/";

    private short id;
    private String ip;
    private int port = -1;

    private long deliveredMessages = 0; //to see how many messages each host has sent to a process

    public boolean populate(String idString, String ipString, String portString) {
        try {
            id = (short) Integer.parseInt(idString);

            String ipTest = InetAddress.getByName(ipString).toString();
            if (ipTest.startsWith(IP_START_REGEX)) {
                ip = ipTest.substring(1);
            } else {
                ip = InetAddress.getByName(ipTest.split(IP_START_REGEX)[0]).getHostAddress();
            }

            port = Integer.parseInt(portString);
            if (port <= 0) {
                System.err.println("Port in the hosts file must be a positive number!");
                return false;
            }
        } catch (NumberFormatException e) {
            if (port == -1) {
                System.err.println("Id in the hosts file must be a number!");
            } else {
                System.err.println("Port in the hosts file must be a number!");
            }
            return false;
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        return true;
    }

    public short getId() {
        return id;
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    public String toString(){
        return "Host("+id+","+port+")";
    }

    public boolean equals(Object obj)
    {
        if (obj == null)
            return false;
        if (obj == this)
            return true;

        Host otherHost = (Host) obj;
        return (this.id == otherHost.id);

    }
    @Override
    public int hashCode(){
        return this.getId() * this.getPort() * this.getIp().hashCode();
    }

    public long getDeliveredMessages() {
        return deliveredMessages;
    }

    public void setDeliveredMessages(long deliveredMessages) {
        this.deliveredMessages = deliveredMessages;
    }

    public void increaseDeliveredMessages(){
        deliveredMessages++;
    }

}

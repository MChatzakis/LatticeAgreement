package cs451.network;

import java.net.DatagramSocket;
import java.net.SocketException;

public abstract class UDPInstance{
    protected DatagramSocket socket;

    public UDPInstance(int port) throws SocketException {
        this.socket = new DatagramSocket(port);
    }

    public UDPInstance() throws SocketException {
        this.socket = new DatagramSocket();
    }

    public void closeSocket(){
        socket.close();
    }




}

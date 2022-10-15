package cs451.network;

import java.net.DatagramSocket;
import java.net.SocketException;

public abstract class UDPInstance{
    protected DatagramSocket socket;

    public UDPInstance(int port) throws SocketException {
        this.socket = new DatagramSocket(port);
    }

    public void closeSocket(){
        socket.close();
    }




}

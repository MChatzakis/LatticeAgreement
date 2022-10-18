package cs451.commonUtils;

import cs451.Host;
import cs451.structures.Message;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class CommonUtils {

    public static Host getHost(int host_id, ArrayList<Host>hosts){
        for(Host h : hosts){
            if (h.getId() == host_id){
                return h;
            }
        }
        return null;
    }

    public static Queue<Message> generateMessageQueue(String configFile, int processID) throws IOException {
        Queue<Message>messageQueue = new LinkedList<>();

        // Creating an object of BufferedReader class
        BufferedReader br = new BufferedReader(new FileReader(new File(configFile)));

        String st;
        int id=1;
        while ((st = br.readLine()) != null) {
            String [] contents = st.split(" ");

            int repetitions = Integer.parseInt(contents[0]);
            int to = Integer.parseInt(contents[1]);

            for(int i=0; i<repetitions; i++){
                messageQueue.add(new Message(processID, to, "Bonjour", id++));
            }

        }
        return messageQueue;
    }

    public static byte[] getBytesOfObject(Object obj) throws IOException {
        ByteArrayOutputStream boas = new ByteArrayOutputStream();
        try (ObjectOutputStream ois = new ObjectOutputStream(boas)) {
            ois.writeObject(obj);
            return boas.toByteArray();
        }
    }

    public static Object getObjectFromBytes(byte[] data) throws IOException, ClassNotFoundException {
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        ObjectInputStream is = new ObjectInputStream(in);
        return is.readObject();
    }
}

package cs451.commonUtils;

import cs451.Host;
import cs451.structures.Message;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class CommonUtils {

    public static Host getHost(int host_id, ArrayList<Host>hosts){
        //not guaranteed that host_id is host[host_id]
        for(Host h : hosts){
            if (h.getId() == host_id){
                return h;
            }
        }
        return null;
    }

    public static ArrayList<Message> generateMessageQueue(String configFile, int processID) throws IOException {
        ArrayList<Message>messageQueue = new ArrayList<>();

        // Creating an object of BufferedReader class
        BufferedReader br = new BufferedReader(new FileReader(new File(configFile)));

        String st;
        int id=0;
        while ((st = br.readLine()) != null) {
            String [] contents = st.split(" ");

            int repetitions = Integer.parseInt(contents[0]);
            int to = Integer.parseInt(contents[1]);

            for(int i=0; i<repetitions; i++){
                messageQueue.add(new Message(processID, to, "data", id++));
            }

        }
        return messageQueue;
    }
}

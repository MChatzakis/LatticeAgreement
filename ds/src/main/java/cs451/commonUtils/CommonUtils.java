package cs451.commonUtils;

import cs451.Host;
import cs451.structures.Message;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

/**
 * Common utilities holder
 */
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

    /**
     * Credits to stackOverflow :)
     * @param obj
     * @return
     * @throws IOException
     */
    public static byte[] getBytesOfObject(Object obj) throws IOException {
        ByteArrayOutputStream boas = new ByteArrayOutputStream();
        try (ObjectOutputStream ois = new ObjectOutputStream(boas)) {
            ois.writeObject(obj);
            return boas.toByteArray();
        }
    }

    /**
     * Credits to stackOverflow :)
     * @param data
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static Object getObjectFromBytes(byte[] data) throws IOException, ClassNotFoundException {
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        ObjectInputStream is = new ObjectInputStream(in);
        return is.readObject();
    }


    /**
     * Credits to stackOverflow :)
     * @param bytes
     * @return
     * @throws IOException
     */
    public static byte[] compressByteArray(byte[] bytes) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DeflaterOutputStream deflaterOutputStream = new DeflaterOutputStream(byteArrayOutputStream);

        deflaterOutputStream.write(bytes);
        deflaterOutputStream.close();

        byte[] compressedBytes = byteArrayOutputStream.toByteArray();
        byteArrayOutputStream.close();

        return compressedBytes;
    }

    /**
     * Credits to stackOverflow :)
     * @param bytes
     * @return
     * @throws IOException
     */
    public static byte[] decompressByteArray(byte[] bytes) throws IOException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        InflaterInputStream inflaterInputStream = new InflaterInputStream(byteArrayInputStream);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        int read;
        while ((read = inflaterInputStream.read()) != -1) {
            byteArrayOutputStream.write(read);
        }
        inflaterInputStream.close();
        byteArrayInputStream.close();
        byteArrayOutputStream.close();
        return byteArrayOutputStream.toByteArray();
    }


}

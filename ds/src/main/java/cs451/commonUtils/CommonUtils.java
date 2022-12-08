package cs451.commonUtils;

import cs451.Host;
import cs451.messaging.Message;

import java.io.*;
import java.util.*;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

/**
 * Common utilities holder
 */
public class CommonUtils {
    private static final long MEGABYTE = 1024L * 1024L;

    public static long bytesToMegabytes(long bytes) {
        return bytes / MEGABYTE;
    }

    public static void calculateMemoryUsed(){
        Runtime runtime = Runtime.getRuntime();
        runtime.gc();
        long memory = runtime.totalMemory() - runtime.freeMemory();
        System.out.println("Used memory: " + bytesToMegabytes(memory) + "MB");
    }

    public static Host getHost(int host_id, List<Host> hosts){
        for(Host h : hosts){
            if (h.getId() == host_id){
                return h;
            }
        }
        return null;
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
    public static synchronized byte[] compressByteArray(byte[] bytes) throws IOException {
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
    public static synchronized byte[] decompressByteArray(byte[] bytes) throws IOException {
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

    public static void createEmptyFile(String filename) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
        writer.close();
    }


    public static String getSetAsString(Set<Integer> set){
        String s = "";
        for(Integer item : set){
            s += item + " ";
        }
        return s;
    }

    public static ArrayList<Message> wrapMessage2Batch(Message m){
        ArrayList<Message>batch = new ArrayList<>();
        batch.add(m);
        return batch;
    }

}

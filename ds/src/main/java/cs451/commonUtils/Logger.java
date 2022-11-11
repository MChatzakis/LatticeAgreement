package cs451.commonUtils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Logger class saves the send and delivery events of a process
 */
public class Logger {
    private static int FLUSH_LIMIT=10000;
    private String outputFilename;
    private ConcurrentLinkedQueue<String> submittedEvents; //synchronized

    public Logger(String outputFilename){
        this.outputFilename = outputFilename;

        submittedEvents = new ConcurrentLinkedQueue<>();
    }

    public String getOutputFilename() {
        return outputFilename;
    }

    public void setOutputFilename(String outputFilename) {
        this.outputFilename = outputFilename;
    }

    public /*synchronized*/ void addEvent(String e){
        submittedEvents.add(e);

        if(submittedEvents.size() >= FLUSH_LIMIT){
            try {
                flush2file();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    public void flush2file() throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilename, true));

        for(String e: submittedEvents){
            writer.write(e + "\n");
        }

        writer.close();

        submittedEvents.clear();
    }
}

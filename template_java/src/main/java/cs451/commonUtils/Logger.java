package cs451.commonUtils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Logger class saves the send and delivery events of a process
 */
public class Logger {
    private String outputFilename;
    private ArrayList<String>submittedEvents;

    public Logger(String outputFilename){
        this.outputFilename = outputFilename;
        submittedEvents = new ArrayList<>();
    }

    public String getOutputFilename() {
        return outputFilename;
    }

    public void setOutputFilename(String outputFilename) {
        this.outputFilename = outputFilename;
    }

    public void addEvent(String e){
        submittedEvents.add(e);
    }

    public void flush2file() throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilename));

        for(String e: submittedEvents){
            writer.write(e + "\n");
        }

        writer.close();
    }
}

package cs451.commonUtils;

import cs451.structures.Event;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Logger {
    private String outputFilename;

    private ArrayList<Event>submittedEvents;

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

    public void addEvent(Event e){
        submittedEvents.add(e);
    }

    void flush2file() throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilename));

        for(Event e: submittedEvents){
            writer.write(e.toString() + "\n");
        }

        writer.close();
    }
}

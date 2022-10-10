#ifndef _LOGGER_HPP_
#define _LOGGER_HPP_

#include <vector>

#include "./structures.hpp"

class Logger
{
private:
    std::string outputFilename;
    std::vector<Event> submittedEvents;

    int process_id;
public:
    Logger() = default;

    Logger(std::string _outputFilename) : outputFilename{_outputFilename}
    {
    }

    Logger(int _process_id, std::string _outputFilename) : outputFilename{_outputFilename}, process_id{_process_id}
    {
    }

    void setOutputFilename(std::string);
    std::string getOutputFilename();

    std::vector<Event> getSubmittedEvents();
    void setSubmittedEvents(std::vector<Event>);

    void flushContentToFile();
    void addSubmittedEvent(Event e);

    std::string toString();

};

#endif
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

    void flushContentToFile();
    std::string toString();

};

#endif
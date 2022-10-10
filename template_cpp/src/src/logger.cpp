#include "logger.hpp"

#include <fstream>
#include <iostream>

void Logger::flushContentToFile()
{
    std::ofstream outputFile(this->outputFilename);

    for (auto e : this->submittedEvents)
    {
        outputFile << e.toOutputForm() + "\n";
    }

    outputFile.close();
}

std::string Logger::toString()
{
    return "";
}

void Logger::addSubmittedEvent(Event e){
    this->submittedEvents.push_back(e);
}

#ifndef _UTILS_HPP_
#define _UTILS_HPP_

#include <vector>
#include <iostream>
#include <fstream>

#include "structures.hpp"

std::vector<Message> config(std::string configFilename, int process_id){
    std::vector<Message> messageQueue;

    std::string myText;

    // Read from the text file
    std::ifstream MyReadFile("filename.txt");

// Use a while loop together with the getline() function to read the file line by line
while (getline (MyReadFile, myText)) {
  // Output the text from the file
  cout << myText;
}

// Close the file
MyReadFile.close(); 

    return messageQueue;
}

#endif
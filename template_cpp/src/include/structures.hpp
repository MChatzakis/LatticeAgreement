#ifndef _STRUCTURES_H_
#define _STRUCTURES_H_

#include <iostream>
#include <string>

/**
 * @brief To be used for the current process (keeps all the data)
 * TODO: Must be a singleton
 */
class Process
{
private:
    int id;
    int port;
    int pid;

    int servSocket;

public:
    Process() = default;

    Process(int _id, int _port, int _pid) : id{_id}, port{_port}, pid{_pid}
    {
    }

    void setID(int);
    int getID();

    void setPort(int);
    int getPort();

    void setPID(int);
    int getPID();

    std::string toString();
};

/**
 * @brief Used to keep the data of other processes
 *
 */
class Node
{
private:
    int id;
    int port;

public:
    Node() = default;

    Node(int _id, int _port) : id{_id}, port{_port}
    {
    }

    void setID(int);
    int getID();

    void setPort(int);
    int getPort();

    std::string toString();
};

class Message
{
private:
    std::string content;

    int from;
    int to;
    int id;

public:
    Message() = default;

    Message(std::string _content, int _from, int _to, int _id) : content{_content}, from{_from}, to{_to}, id{_id}
    {
    }

    void setContent(std::string);
    std::string getContent();

    void setFrom(int);
    int getFrom();

    void setTo(int);
    int getTo();

    void setID(int);
    int getID();

    std::string toString();
};

enum msg_type
{
    p2p_send = 0,
    broadcast,
    deliver
};

class Event
{
private:
    msg_type type;
    int from;
    int to;

public:
    Event() = default;

    Event(msg_type _type, int _from, int _to) : type{_type}, from{_from}, to{_to}
    {
    }

    void setType(msg_type);
    msg_type getType();

    void setFrom(int);
    int getFrom();

    void setTo(int);
    int getTo();

    std::string toString();
};

#endif
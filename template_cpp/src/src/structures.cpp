#include "../include/structures.hpp"

// Process Implementation
void Process::setID(int _id)
{
    this->id = _id;
}

int Process::getID()
{
    return this->id;
}

void Process::setPort(int _port)
{
    this->port = _port;
}

int Process::getPort()
{
    return this->port;
}

void Process::setPID(int _pid)
{
    this->pid = _pid;
}

int Process::getPID()
{
    return this->pid;
}

std::string Process::toString()
{
    std::string s;
    s = "Process [id,pid,port]=[" + std::to_string(id) + "," + std::to_string(pid) + std::to_string(port) + "]";
    return s;
}

// Node implementation
void Node::setID(int _id)
{
    this->id = _id;
}

int Node::getID()
{
    return this->id;
}

void Node::setPort(int _port)
{
    this->port = _port;
}

int Node::getPort()
{
    return this->port;
}

std::string Node::toString()
{
    std::string s;
    s = "Node [id,port]=[" + std::to_string(id) + "," + std::to_string(port) + "]";
    return s;
}

// Message
void Message::setContent(std::string _content)
{
    this->content = _content;
}

std::string Message::getContent()
{
    return this->content;
}

void Message::setFrom(int _from)
{
    this->from = _from;
}

int Message::getFrom()
{
    return this->from;
}

void Message::setTo(int _to)
{
    this->to = _to;
}

int Message::getTo()
{
    return this->to;
}

void Message::setID(int _id)
{
    this->id = _id;
}

int Message::getID()
{
    return this->id;
}

std::string Message::toString()
{
    std::string s;
    s = "Message [id,from,to,content]=[" + std::to_string(id) + "," + std::to_string(from) + "," + std::to_string(to) + "," + content + "]";
    return s;
}

// Event
void Event::setType(msg_type _type)
{
    this->type = _type;
}

msg_type Event::getType()
{
    return this->type;
}

void Event::setFrom(int _from)
{
    this->from = _from;
}

int Event::getFrom()
{
    return this->from;
}

void Event::setTo(int _to)
{
    this->to = _to;
}

int Event::getTo()
{
    return this->to;
}

std::string Event::toOutputForm()
{
    return msg_type_names[type] + " " + std::to_string(to);
}

std::string Event::toString()
{
    return "Message [from,to,type]=[" + std::to_string(from) + "," + std::to_string(to) + "," + msg_type_names[type] + "]";
}

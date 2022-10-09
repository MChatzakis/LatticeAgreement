#ifndef _NETWORK_HPP_
#define _NETWORK_HPP_

#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <string.h>

#include <sys/types.h>
#include <sys/socket.h>
#include <arpa/inet.h>
#include <netinet/in.h>

int createUDPSocket()
{
    int sockfd;

    if ((sockfd = socket(AF_INET, SOCK_DGRAM, 0)) < 0)
    {
        perror("Socket creation failed");
        exit(EXIT_FAILURE);
    }

    return sockfd;
}

int bindUDPSocket(int sockfd, struct sockaddr_in *servaddr)
{
    if (bind(sockfd, (const struct sockaddr *)servaddr, sizeof(*servaddr)) < 0)
    {
        perror("Bind failed");
        exit(EXIT_FAILURE);
    }
}

void setServerAddr(struct sockaddr_in *servaddr, int port){
    memset(servaddr, 0, sizeof(*servaddr)); 
        
    servaddr->sin_family = AF_INET; 
    servaddr->sin_port = htons(port); 
    servaddr->sin_addr.s_addr = INADDR_ANY; 
}

int sendUDPData();
int receieveUDPData();

#endif
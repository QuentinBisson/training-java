FROM maven:latest

RUN mkdir -p /usr/src/training-java
RUN mkdir -p /usr/src/computer-database
WORKDIR /usr/src/training-java

COPY . /usr/src/training-java
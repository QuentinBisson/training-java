FROM maven:latest

RUN mkdir -p /usr/src/computer-database
WORKDIR /usr/src/computer-database

ONBUILD ADD . /usr/src/computer-database

ONBUILD RUN mvn clean package -DskipTests
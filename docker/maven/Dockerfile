FROM maven:latest

RUN mkdir -p /usr/src/computer-database
WORKDIR /usr/src/computer-database

RUN git clone git@github.com:omegas27/training-java.git

ONBUILD RUN mvn clean package -DskipTests
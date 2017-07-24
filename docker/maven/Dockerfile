FROM maven:latest

RUN mkdir -p /usr/src
WORKDIR /usr/src/

RUN git clone https://github.com/omegas27/training-java.git

ONBUILD RUN mvn clean package -DskipTests
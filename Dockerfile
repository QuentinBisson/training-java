FROM maven:latest

RUN mkdir -p /usr/src/training-java
RUN mkdir -p /usr/src/computer-database
WORKDIR /usr/src/training-java

COPY . /usr/src/training-java

RUN  mvn clean package -DskipTests

WORKDIR /usr/src

RUN ls -ltr training-java/target

RUN cp training-java/target/*.war computer-database/
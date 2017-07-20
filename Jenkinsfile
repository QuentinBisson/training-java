#!groovy

pipeline {
    agent any

    stages {
        //stage('mysql-test') {
        //    steps {
        //      sh 'mvn --version'
        //    }
        //}
        stage('maven-build') {
            steps {
                docker.image('maven:latest').inside({ // https://registry.hub.docker.com/_/maven/
                    //checkout scm
                    sh 'mvn --version'
                })
            }
        }
    }
}
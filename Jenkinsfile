#!groovy

pipeline {
    agent any

    stages {
        stage('mysql-test') {
            agent { docker 'mysql:latest' }
            steps {
                echo 'Pull and configure test mysql instance'
            }
        }
        stage('maven-build') {
            agent { docker 'maven:latest' }
            steps {
                echo 'Build and test projet with maven'

                script {
                    checkout scm
                    sh 'mvn clean package'
                }
            }
        }
    }
}
#!groovy

pipeline {
    agent any

    stages {
        stage('mysql-test') {
            agent any
            steps {
                echo 'Pull and configure test mysql instance'

                sh 'docker build -t mysql-test ./docker/mysql/test/'
                sh 'docker run -d -it --name mysql-test -p 3306:3306 mysql-test'
            }
        }
        stage('maven-build') {
            agent {
                docker 'maven:latest'
                args '--name maven-test'
            }
            steps {
                echo 'Build and test projet with maven'

                script {
                    checkout scm
                    sh 'mvn clean package'
                }
            }
        }
         post {
            always {
                docker stop mysql-test
                docker rm mysql-test
                docker rmi mysql-test

                docker rmi maven-test
            }
            failure {
                echo 'Failure happened'
            }
            success {
                echo 'Build success'
            }
        }
    }
}
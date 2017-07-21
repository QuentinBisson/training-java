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
                docker {
                    image 'maven:latest'
                    args '--name maven-test'
                }
            }
            steps {
                echo 'Build and test projet with maven'

                script {
                    checkout scm
                    sh 'mvn clean test'
                }
            }
        }
    }
    post {
        always {
            sh 'docker stop mysql-test'
            sh 'docker rm mysql-test'
            sh 'docker rmi mysql-test'

            sh 'docker rmi maven-test'
        }
        failure {
            echo 'Failure happened'
        }
        success {
            echo 'Build success'
        }
    }
}
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
        stage('maven-build') { // get war and do test
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
                    sh 'mvn clean package -DskipTests'
                    deleteDir()
                }
            }
        }
        stage('tomcat-production-image') {
            agent any
            steps {
                echo 'Pull and configure tomcat production instance'

                script {
                    sh 'docker build -t tomcat-run ./docker/tomcat'
                    docker.withRegistry("https://registry.hub.docker.com", "docker-hub-credentials") {
                        docker.image("tomcat-run").push()
                    }
                }
            }
        }
        stage('mysql-production-image') {
            agent any
            steps {
                echo 'Pull and configure mysql production instance'

                sh 'docker build -t mysql-run ./docker/mysql/prod/'
                docker.withRegistry("https://registry.hub.docker.com", "docker-hub-credentials") {
                    docker.image("mysql-run").push()
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

            sh 'docker rmi tomcat-run'
            sh 'docker rmi mysql-run'
        }
        failure {
            echo 'Failure happened'
        }
        success {
            echo 'Build success'
        }
    }
}
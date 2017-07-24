#!groovy

pipeline {
    agent any
    // Add network between mysql and tomcat
    stages {
        stage('mysql-test') {
            steps {
                echo 'Pull and configure test mysql instance'
                sh 'docker network create --driver bridge mysql-tomcat'

                script {
                    def image = docker.build('mysql-test', './docker/mysql/test/')
                    image.run('-itd --name mysql-test --network=mysql-tomcat -p 3306:3306 mysql-test')
                }
            }
        }
        stage('maven-build') {
            steps {
                echo 'Build and test projet with maven'

                script {
                    docker.image('maven:3.5-jdk-8').inside('--name maven-test --network=mysql-tomcat -v /opt/jenkins/volumes/computer-database/:$(pwd)/build/') {
                        checkout scm
                        sh 'mvn clean package -DskipTests'
                        sh 'cp target/ComputerDatabase.war $(pwd)/build/ComputerDatabase.war'
                        deleteDir()
                    }
                }
            }
        }
        stage('tomcat-production-image') {
            steps {
                echo 'Build production tomcat image'

                script {
                    docker.withRegistry("https://registry.hub.docker.com", "docker-hub-credentials") {
                        docker.build('omegas27/tomcat-run', './docker/tomcat').push('latest')
                    }
                }
            }
        }
        stage('mysql-production-image') {
            steps {
                echo 'Build production mysql image'

                script {
                    docker.withRegistry("https://registry.hub.docker.com", "docker-hub-credentials") {
                        docker.build('omegas27/mysql-run', './docker/mysql/prod').push('latest')
                    }
                }
            }
        }
    }
    post {
        always {
            sh 'docker stop mysql-test'

            sh 'docker network rm mysql-tomcat'
            sh 'docker rm mysql-test'
            sh 'docker rmi mysql-test'

            archive "target/**/*"
            junit 'target/surefire-reports/*.xml'

            sh 'docker rmi omegas27/tomcat-run'
            sh 'docker rmi omegas27/mysql-run'
        }
        failure {
            echo 'Failure happened'
        }
        success {
            echo 'Build success'
        }
    }
}
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
                    image.run('-itd --name mysql-container --network=mysql-tomcat -p 3306:3306 mysql-test')
                }
            }
        }
        stage('maven-build') {
            steps {
                echo 'Build and test projet with maven'

                script {
                   //def image = docker.build('maven-test', '.')
                   docker.run('-it --name maven-test --network=mysql-tomcat -v /opt/jenkins/volumes/computer-database:/usr/src/training-java -w /usr/src/training-java maven:latest mvn clean package')
                  //sh 'docker cp maven-test:/usr/src/training-java/target/ /opt/jenkins/volumes/computer-database'
                }
            }
        }
        stage('tomcat-production-image') {
            steps {
                echo 'Build production tomcat image'

                script {
                    sh 'cp /opt/jenkins/volumes/computer-database/*.war docker/tomcat'
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
            archive '/opt/jenkins/volumes/computer-database/**/*'
            junit '/opt/jenkins/volumes/computer-database/surefire-reports/*.xml'

            sh 'docker stop mysql-container'
            sh 'docker stop maven-test'

            sh 'docker rm mysql-container'
            sh 'docker rm maven-test'

            sh 'docker network rm mysql-tomcat'

            sh 'docker rmi maven-test'
            sh 'docker rmi mysql-test'

            sh 'docker rmi omegas27/tomcat-run'
            sh 'docker rmi omegas27/mysql-run'

            sh 'ls -ltr /opt/jenkins/volumes/computer-database/'
            sh 'rm -rf /opt/jenkins/volumes/computer-database/'
        }
        failure {
            echo 'Failure happened'
        }
        success {
            echo 'Build success'
        }
    }
}
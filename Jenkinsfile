#!groovy

pipeline {
    agent any
    // Add network between mysql and tomcat
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
                    args '--name maven-test -v /opt/jenkins/${env.BUILD_ID}:target/'
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
            steps {
                script {
                    docker.withRegistry("https://registry.hub.docker.com", "docker-hub-credentials") {
                        docker.build('omegas27/tomcat-run', './docker/tomcat').push('latest')
                    }
                }
            }
        }
        stage('mysql-production-image') {
            steps {
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
            archive "target/**/*"
            junit 'target/surefire-reports/*.xml'

            sh 'docker stop mysql-test'
            sh 'docker rm mysql-test'
            sh 'docker rmi mysql-test'

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
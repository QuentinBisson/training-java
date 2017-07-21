#!groovy

pipeline {
    agent any

    stages {
        stage('mysql-test') {
            agent any
            steps {
                echo 'Pull and configure test mysql instance'

                docker run -it --rm --name mysql-test
                    -p 3306:3306
                    -v ./docker/mysql/test/config:/etc/mysql/conf.d
                    -v ./docker/mysql/test/data:/docker-entrypoint-initdb.d
                    -e MYSQL_ROOT_PASSWORD=mysqladmin
                    --character-set-server=utf8mb4
                    --collation-server=utf8mb4_unicode_ci
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
                docker stop $(docker ps -q --filter ancestor=mysql-test )
                docker stop $(docker ps -q --filter ancestor=maven-test )
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
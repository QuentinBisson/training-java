#!groovy

pipeline {
    agent any

    stages {
        stage('mysql-test') {
            agent {
                docker 'mysql:latest'
                args '''-p 3306:3306
                    -v ./docker/mysql/test/config:/etc/mysql/conf.d
                    -v ./docker/mysql/test/data:/docker-entrypoint-initdb.d
                    -e MYSQL_ROOT_PASSWORD=mysqladmin
                    --character-set-server=utf8mb4
                    --collation-server=utf8mb4_unicode_ci'''
            }
            steps {
                echo 'Pull and configure test mysql instance'
            }
            post {
                always {
                    echo 'Mysql was attempted'
                }
                failure {
                    echo 'Mysql failure'
                }
                success {
                    echo 'Mysql success'
                }
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
            post {
                always {
                    echo 'Maven was attempted'
                }
                failure {
                    echo 'Maven failure'
                }
                success {
                    echo 'Maven success'
                }
            }
        }
    }
}
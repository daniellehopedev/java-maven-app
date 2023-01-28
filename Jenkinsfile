#! /usr/bin/env groovy

library identifier: 'jenkins-shared-library@main', retriever: modernSCM(
    [
        $class: 'GitSCMSource',
        remote: 'git@github.com:daniellehopedev/jenkins-shared-library.git',
        credentialsId: 'github-ssh-credentials'
    ]
)

pipeline {
    agent any

    tools {
        maven 'maven'
    }

    environment {
        IMAGE_NAME = 'danielleh/my-repo:maven-app-5.0'
    }

    stages {
        stage('build app') {
            steps {
                script {
                    echo 'building application jar...'
                    buildJar()
                }
            }
        }

        stage('build image') {
            steps {
                script {
                    echo 'building docker image...'
                    buildImage(env.IMAGE_NAME)
                    dockerLogin()
                    dockerPush(env.IMAGE_NAME)
                }
            }
        }

        stage('deploy') {
            steps {
                script {
                    echo 'deploying docker image to EC2...'
                    // using docker
                    // def dockerCmd = "docker run -d -p 8080:8080 ${IMAGE_NAME}"
                    // using docker-compose
                    def ec2Instance = "ec2-user@18.118.211.183"
                    def shellCmd = "bash ./server-cmds.sh ${IMAGE_NAME}"
                    sshagent(['ec2-ssh-credentials']) {
                        // copying shell script
                        sh "scp -o StrictHostKeyChecking=no server-cmds.sh ${ec2Instance}:/home/ec2-user"
                        // copying docker-compose file
                        sh "scp -o StrictHostKeyChecking=no docker-compose.yaml ${ec2Instance}:/home/ec2-user"
                        // -o StrictHostKeyChecking=no, suppresses popup when connecting to the ec2 server
                        // using docker
                        //sh "ssh -o StrictHostKeyChecking=no ${ec2Instance} ${dockerCmd}"
                        // using docker-compose
                        sh "ssh -o StrictHostKeyChecking=no ${ec2Instance} ${shellCmd}"
                    }
                }
            }
        }
    }   
}
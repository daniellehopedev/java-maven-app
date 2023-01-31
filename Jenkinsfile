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

// this will be replaced by the 'increment version' stage
//     environment {
//         IMAGE_NAME = 'danielleh/my-repo:maven-app-5.0'
//     }

    stages {
        stage('increment version') {
            steps {
                script {
                    echo 'incrementing app version...'
                    // maven command that increments the version and updated the pom file
                    sh 'mvn build-helper:parse-version versions:set \
                        -DnewVersion=\\\${parsedVersion.majorVersion}.\\\${parsedVersion.minorVersion}.\\\${parsedVersion.nextIncrementalVersion} versions:commit'
                    // read the version from the pom file and set it as the IMAGE_NAME
                    def matcher = readFile('pom.xml') =~ '<version>(.+)</version>'
                    def version = matcher[0][1]
                    // BUILD_NUMBER is a value from the Jenkins pipeline builds
                    env.IMAGE_NAME = "danielleh/my-repo:$version-$BUILD_NUMBER"
                }
            }
        }

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
                    def ec2Instance = "ec2-user@3.139.62.81"
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

        stage('commit version update') {
            steps {
                script {
                    sshagent(['github-ssh-credentials']) {
                        // on the jenkins server, setting the remote url for the repository to commit and push to
                        sh "git remote set-url origin git@github.com:daniellehopedev/java-maven-app.git"
                        sh 'git add .'
                        // the commit and push will be as the jenkins user
                        sh 'git commit -m "ci: version bump"'
                        sh 'git push origin HEAD:feature/jenkinsfile-ec2-docker'
                    }
                }
            }
        }
    }   
}
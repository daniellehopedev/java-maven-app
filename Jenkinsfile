#! /usr/bin/env groovy

// for single project use, the jenkins-shared-library will only be used for this project
// be sure to remove the library from the global pipeline configuration in jenkins
// library identifier: 'jenkins-shared-library@main', retriever: modernSCM(
//         [$class: 'GitSCMSource',
//          remote: 'git@github.com:daniellehopedev/jenkins-shared-library.git',
//          credentialsId: 'github-ssh-credentials'
//         ]
// )


// for global use in all projects
@Library('jenkins-shared-library')

def gv

pipeline {
    agent any

    tools {
        maven 'maven'
    }

    stages {
        stage("init") {
            steps {
                script {
                    gv = load "script.groovy"
                }
            }
        }

        stage("build jar") {
            steps {
                script {
                    buildJar()
                }
            }
        }

        stage("build image") {
            steps {
                script {
                    buildImage 'danielleh/my-repo:maven-app-3.0'
                    dockerLogin()
                    dockerPush 'danielleh/my-repo:maven-app-3.0'
                }
            }
        }
        stage("deploy") {
            steps {
                script {
                    gv.deployApp()
                }
            }
        }
    }   
}
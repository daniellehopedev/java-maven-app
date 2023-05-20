def buildJar() {
    echo "building the application..."
    sh 'mvn package'
} 

def buildImage() {
    echo "building the docker image..."
    withCredentials([usernamePassword(credentialsId: 'docker-hub-credentials', passwordVariable: 'PASS', usernameVariable: 'USER')]) {
        sh 'docker build -t danielleh/my-repo:maven-app-10.0 .'
        sh "echo $PASS | docker login -u $USER --password-stdin"
        sh 'docker push danielleh/my-repo:maven-app-10.0'
    }
} 

def deployApp() {
    echo 'deploying the application...'
} 

return this
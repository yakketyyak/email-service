pipeline {
   
   agent any
   environment {
    ARTIFACTID = readMavenPom().getArtifactId()
    VERSION = readMavenPom().getVersion()
   }

    stages {

        stage('Build') { 
          steps {
            withMaven(
              maven: 'maven-3.6.3',
              mavenLocalRepo: '.repository'){
              sh 'mvn -B -DskipTests clean package' 
              }
            }
        }
        stage('Test'){
          steps {
               withMaven(
                maven: 'maven-3.6.3',
                //image docker
                mavenLocalRepo: '.repository'){
                sh 'mvn test' 
              }
            }
        }

        stage('Deploy on tomcat-8'){
          steps {
            deploy (
                war: '**/*.war', onFailure: true,
                //contextPath: '${ARTIFACTID}-${VERSION}',
                adapters: [
                    tomcat8(
                      url: 'http://localhost:8888/',
                      credentialsId: 'tomcat-deployer',
                      path: ''
                    )
                ]
            )
          }
        }

    }
}

node {

    stage('Main Build') { 
      docker.image('maven:3.3.3-jdk-8').inside {
        
        stage('Build'){
          git 'https://github.com/yakketyyak/jenkins.git'
          writeFile file: 'settings.xml', text: "<settings><localRepository>${pwd()}/.m2repo</localRepository></settings>"
          sh '''
             mvn -v
             mvn -B -s settings.xml -DskipTests clean package
          '''
        }

        stage('Test'){
          sh 'mvn test'
        }
      }
    }

    // Clean up workspace
    step([$class: 'WsCleanup'])
}
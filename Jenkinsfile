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
                war: '**/*${ARTIFACTID}-${VERSION}.war', onFailure: true,
                //contextPath: '${ARTIFACTID}-${VERSION}',
                adapters: [
                    tomcat8(
                      url: 'http://localhost:8888/',
                      credentialsId: 'tomcat-deployer'
                    )
                ]
            )
          }
        }

    }
}
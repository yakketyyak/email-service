pipeline {
   
   agent any
   /*environment {
    //VERSION = readMavenPom().getVersion()
    //SSH_LOCAL_HOST = 'localhost'
    }*/

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
        stage('Deploy to tomcat'){
          steps{
            adapters(
                war: '**/*.war', onFailure: true,
                adapters: [
                    tomcat9(
                      url: 'http://localhost:8888/',
                      credentialsId: 'tomcat-deployer',
                      //contextPath: 'tomcat'
                    )
                ]
            )
          }
        }
    }
}
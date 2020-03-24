//OKAY
pipeline {
   
	 agent any
   environment {
    //Use Pipeline Utility Steps plugin to read information from pom.xml into env variables
    IMAGE = readMavenPom().getArtifactId()
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

        stage('Test') { 
		   
            steps {
               withMaven(maven: 'maven-3.6.3',
                mavenLocalRepo: '.repository'){
              sh 'mvn test' 
              }
            }
            
        }

        stage('Deploy') {           
           steps {                
           	withMaven(maven: 'maven-3.6.3',
              mavenLocalRepo: '.repository'){
              sh 'mvn deploy' 
              }
            }        
        }

        stage('Build docker image') {    
           
           steps {                
             sh "docker build -t spring-test:${VERSION} -f Dockerfile ."
            }        
        }
    }
}
//OKAY
pipeline {
   
    agent none

    stages {

        stage('Build') { 
          agent {
            //image docker maven
            label 'maven-3.6.3'
          }
          steps {
            sh 'mvn -B -DskipTests clean package' 
          }
        }

        stage('Test') { 
            agent {
              label 'maven-3.6.3'
            }
            steps {
               sh 'mvn test' 
            }
            
        }

        stage('Deploy') {   
           agent {
              label 'maven-3.6.3'
            }      
           steps {  
            sh 'mvn deploy' 
           }        
        }

        stage('Build image') {   
           agent any
           environment {
            //Use Pipeline Utility Steps plugin to read information from pom.xml into env variables
            IMAGE = readMavenPom().getArtifactId()
            VERSION = readMavenPom().getVersion()
          }      
           steps {  
            sh 'docker build -t spring-test:${VERSION} -f Dockerfile .' 
           }        
        }
    }
}
//ne fonctionne pas
pipeline {

   agent {
        docker { 
           image 'maven:3-alpine'            
           args '-v $HOME/.m2:/root/.m2'
         }
      }

    stages {

        stage('Build') { 
            steps {
                sh 'mvn -B -DskipTests clean package' 
            }
        }

        stage('Test') { 
       
            steps {
                sh 'mvn test' 
            }
        }

        stage('Deploy') { 
               when {              
                expression {                
                  currentBuild.result == null || currentBuild.result == 'SUCCESS' 

                }            
              }            
           steps {                
            sh 'mvn deploy'           
              }        
            }
    }
}
//OKAY
pipeline {
   
   agent any
   environment {
    //Use Pipeline Utility Steps plugin to read information from pom.xml into env variables
    IMAGE = readMavenPom().getArtifactId()
    VERSION = readMavenPom().getVersion()
    //GITHUB_CREDS = credentials('github')
    SSH_LOCAL_HOST = 'localhost'
    }

    stages {

        stage('Build') { 
          steps {
            withMaven(
              maven: 'maven-3.6.3',
              //globalMavenSettingsFilePath: '${user.home}/.m2/settings.xml',
              mavenLocalRepo: '.repository'){
              sh 'mvn -B -DskipTests clean package' 
              }
            }
        }

        stage('Test') { 
       
            steps {
               withMaven(
                maven: 'maven-3.6.3',
                //image docker
                mavenLocalRepo: '.repository'){
              sh 'mvn test' 
              }
            }
            
        }

        stage('Deploy') {           
           steps {                
            withMaven(
              maven: 'maven-3.6.3',
              mavenLocalRepo: '.repository'){
              sh '''
                mvn deploy
                echo M2_HOME ${M2_HOME}
              '''
              }
            }        
        }

        stage('Build docker image') {    
           
           steps {                
             sh "docker build -t ${IMAGE}:${VERSION} -f Dockerfile ."
            }        
        }

        stage('SSH transfer'){
          steps([$class: 'BapSshPromotionPublisherPlugin']){
            sshPublisher(
                continueOnError: false, failOnError: true,
                publishers: [
                    sshPublisherDesc(
                        configName: "${SSH_LOCAL_HOST}",
                        verbose: true,
                        transfers: [
                            sshTransfer(
                              //${WORKSPACE}/.repository/${IMAGE}-${VERSION}.jar
                              sourceFiles: "**/*${IMAGE}-${VERSION}.jar",
                              //removePrefix: "target",
                              //remoteDirectory: ".",
                              //execCommand: "java -jar **/${IMAGE}-${VERSION}.jar"
                              execCommand: "mv deployJenkins/target/${IMAGE}-${VERSION}.jar deployJenkins/target/${IMAGE}-${VERSION}-${BUILD_TIMESTAMP}.jar"
                            )
                        ],
                        useWorkspaceInPromotion: true,
                        usePromotionTimestamp: true,
                        /*sshRetry: [
                          retries: 2,
                          retryDelay: 3600
                        ]*/
                    )
                ]
            )
          }
        }
    }
}
//OKAY
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
//OKAY
node {

    stage('Main Build') { 
      docker.image('maven:3.6-jdk-8').inside ('-v $HOME/.m2:/root/.m2'){
        
        stage('Build'){
          git 'https://github.com/yakketyyak/jenkins.git'
          sh '''
             mvn -v
             mvn -B -DskipTests clean package
          '''
        }

        stage('Test'){
          sh '''
            mvn test
            echo $WORKSPACE
          '''
        }
      }
    }
//OKAY
pipeline {
agent any
stages
{
  stage('Docker Build') { 
  steps{
    script{
      docker.image('maven:3.6-jdk-8').inside ('-v $HOME/.m2:/root/.m2'){
    
      stage('Build'){
        git 'https://github.com/yakketyyak/jenkins.git'
        sh '''
           mvn -v
           mvn -B -DskipTests clean package
        '''
      }

      stage('Test'){
        sh '''
          mvn test
          echo $WORKSPACE
        '''
      }
  }
}
}
  
}
stage('SSH transfer'){
      steps([$class: 'BapSshPromotionPublisherPlugin']){
        sshPublisher(
            continueOnError: false, failOnError: true,
            publishers: [
                sshPublisherDesc(
                    configName: 'localhost',
                    verbose: true,
                    transfers: [
                        sshTransfer(
                          sourceFiles: '**/*spring-test-0.0.1-SNAPSHOT.jar',
                          execCommand: 'mv deployJenkins/target/spring-test-0.0.1-SNAPSHOT.jar deployJenkins/target/spring-test-0.0.1-SNAPSHOT.jar'
                        )
                    ],
                    useWorkspaceInPromotion: true,
                    usePromotionTimestamp: true
                )
            ]
        )
      }
}
} 
}
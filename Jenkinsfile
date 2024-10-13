pipeline {
    agent any

    environment {
        REGION = 'ap-northeast-2'
        IMAGE_NAME = '339713037008.dkr.ecr.ap-northeast-2.amazonaws.com/logi_back'
        ECR_PATH = '339713037008.dkr.ecr.ap-northeast-2.amazonaws.com'
        AWS_CREDENTIAL_NAME = 'aws-key'
    }

    stages {
        stage('Pull Codes from Github') {
            steps {
                checkout scm
            }
        }

        stage('Check PATH') {
            steps {
                script {
                    bat 'echo %PATH%'  // Windows에서 PATH 확인
                }
            }
        }

        // 프론트엔드 및 백엔드 서비스 배포
        stage('Apply Services') {
            steps {
                script {
                    withCredentials([[$class: 'AmazonWebServicesCredentialsBinding', credentialsId: 'aws-key']]) {
                        bat 'kubectl apply -f E:/docker_Logi/logi-front-service.yaml'
                        bat 'kubectl apply -f E:/docker_Logi/logi-back-service.yaml'
                    }
                }
            }
        }

        stage('Update Backend Properties with Frontend URL') {
            steps {
                script {
                    withCredentials([[$class: 'AmazonWebServicesCredentialsBinding', credentialsId: 'aws-key']]) {
                        // 1. kubectl 명령어로 프론트엔드 서비스의 호스트 이름을 가져옴
                        def frontend_service_url = powershell(script: 'kubectl get service frontend-service -o jsonpath="{.status.loadBalancer.ingress[0].hostname}"', returnStdout: true).trim()
                        echo "Frontend Service URL: ${frontend_service_url}"

                        // 2. PowerShell 스크립트로 application.properties 파일 업데이트
                        bat """
                        powershell -Command "\$frontendUrl = '${frontend_service_url}'; (Get-Content 'E:\\docker_Logi\\logi_react_back_cloud\\src\\main\\resources\\application.properties') -replace 'FRONTEND_SERVICE_URL=.*', 'FRONTEND_SERVICE_URL=http://\$frontendUrl' | Set-Content 'E:\\docker_Logi\\logi_react_back_cloud\\src\\main\\resources\\application.properties';"
                        """
                    }
                }
            }
        }

        stage('Get Kafka Bootstrap Servers') {
            steps {
                script {
                    withCredentials([[$class: 'AmazonWebServicesCredentialsBinding', credentialsId: 'aws-key']]) {
                        // 1. kubectl 명령어로 Kafka 서비스의 호스트 이름을 가져옴
                        def kafka_service_url = powershell(script: 'kubectl get service kafka -o jsonpath="{.status.loadBalancer.ingress[0].hostname}"', returnStdout: true).trim()
                        echo "Kafka Service URL: ${kafka_service_url}"

                        // 2. PowerShell 스크립트로 application.properties 파일 업데이트
                        bat """
                        powershell -Command "\$kafkaUrl = '${kafka_service_url}'; (Get-Content 'E:\\docker_Logi\\logi_react_back_cloud\\src\\main\\resources\\application.properties') -replace 'spring.kafka.bootstrap-servers=.*', 'spring.kafka.bootstrap-servers=\$kafkaUrl:9092' | Set-Content 'E:\\docker_Logi\\logi_react_back_cloud\\src\\main\\resources\\application.properties';"
                        """
                    }
                }
            }
        }

        stage('Build Codes by Gradle') {
            steps {
                script {
                    bat "./gradlew clean build"
                }
            }
        }

        stage('Dockerizing Project by Dockerfile') {
            steps {
                script {
                    bat "docker build -t ${IMAGE_NAME}:latest ."
                    bat "docker tag ${IMAGE_NAME}:latest ${IMAGE_NAME}:latest"
                }
            }
        }

        stage('Upload to AWS ECR') {
            steps {
                script {
                    docker.withRegistry("https://$ECR_PATH", "ecr:$REGION:$AWS_CREDENTIAL_NAME") {
                        docker.image("$IMAGE_NAME:latest").push()
                    }
                }
            }
        }

        // 백엔드 디플로이먼트 적용
        stage('Apply Backend Deployment') {
            steps {
                script {
                    withCredentials([[$class: 'AmazonWebServicesCredentialsBinding', credentialsId: 'aws-key']]) {
                        bat 'kubectl apply -f E:/docker_Logi/logi-back-deployment.yaml'
                    }
                }
            }
        }
    }
}

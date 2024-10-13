stage('Get Kafka Bootstrap Servers') {
            steps {
                script {
                    withCredentials([[$class: 'AmazonWebServicesCredentialsBinding', credentialsId: 'aws-key']]) {
                        // 1. kubectl 명령어로 Kafka 서비스의 호스트 이름을 가져옴
                        def kafka_service_url = powershell(script: 'kubectl get service kafka-service -o jsonpath="{.status.loadBalancer.ingress[0].hostname}"', returnStdout: true).trim()
                        echo "Kafka Service URL: ${kafka_service_url}"

                        // 2. PowerShell 스크립트로 application.properties 파일 업데이트
                        bat """
                        powershell -Command "\$kafkaUrl = '${kafka_service_url}'; (Get-Content 'E:\\docker_Logi\\logi_react_back_cloud\\src\\main\\resources\\application.properties') -replace 'spring.kafka.bootstrap-servers=.*', 'spring.kafka.bootstrap-servers=\$kafkaUrl:9092' | Set-Content 'E:\\docker_Logi\\logi_react_back_cloud\\src\\main\\resources\\application.properties';"
                        """
                    }
                }
            }
        }
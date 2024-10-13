//package kr.co.seoulit.logistics.sys.configuration;
//
//import org.apache.kafka.clients.admin.NewTopic;
//import org.apache.kafka.clients.admin.AdminClientConfig;
//import org.springframework.beans.factory.annotation.Value; // 추가된 부분
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.kafka.config.TopicBuilder;
//import org.springframework.kafka.core.KafkaAdmin;
//
//import java.util.Map;
//
//@Configuration
//public class KafkaTopicConfig {
//
//    @Value("${spring.kafka.bootstrap-servers}") // application.properties에서 값 주입
//    private String bootstrapServers;
//
//    @Bean
//    public KafkaAdmin kafkaAdmin() {
//        return new KafkaAdmin(Map.of(
//                AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers  // 하드코딩 대신 주입받은 값 사용
//        ));
//    }
//
//    @Bean
//    public NewTopic createTopic() {
//        return TopicBuilder.name("kafka-react-logi-acc1")
//                .partitions(1)
//                .replicas(1)
//                .build();
//    }
//}
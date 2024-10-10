package kr.co.seoulit.logistics.sys.configuration;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.admin.AdminClientConfig; // 올바른 클래스
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.Map;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public KafkaAdmin kafkaAdmin() {
        return new KafkaAdmin(Map.of(
                AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, "kafka:9092"  // 수정된 부분
        ));
    }

    @Bean
    public NewTopic createTopic() {
        return TopicBuilder.name("kafka-react-logi-acc1")
                .partitions(1)
                .replicas(1)
                .build();
    }
}

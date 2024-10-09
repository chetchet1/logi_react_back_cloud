//package kr.co.seoulit.logistics.sys.service;
//
//import org.apache.kafka.clients.producer.KafkaProducer;
//import org.apache.kafka.clients.producer.Producer;
//import org.apache.kafka.clients.producer.ProducerConfig;
//import org.apache.kafka.clients.producer.ProducerRecord;
//import org.apache.kafka.common.serialization.StringSerializer;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//
//import javax.annotation.PostConstruct;
//import javax.annotation.PreDestroy;
//import java.util.Properties;
//
//@Service
//public class KafkaProducerServiceImpl implements KafkaProducerService {
//
//    private Producer<String, String> producer;
//
//    @Value("${spring.kafka.bootstrap-servers}")
//    private String bootstrapServers;
//
//    @Value("kafka-accounting-home")
//    private String topic;
//
//    @PostConstruct
//    public void init() {
//        Properties props = new Properties();
//        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
//        props.put(ProducerConfig.ACKS_CONFIG, "all");
//        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
//        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
//        // UTF-8 인코딩 설정
//        props.put("serializer.encoding", "UTF8");
//        producer = new KafkaProducer<>(props);
//    }
//
//    @Override
//    public void sendMessage(String key, String value) {
//        // 파티션을 직접 지정
//        int partition = 1; // 예: 두 번째 파티션으로 설정 (해당 파티션 인덱스는 카프카 설정에 따라 달라질 수 있음)
//        producer.send(new ProducerRecord<>(topic, partition, key, value), (metadata, exception) -> {
//            if (exception != null) {
//                System.err.println("@@@@@카프카 전송 실패: " + exception.getMessage());
//            } else {
//                System.out.println("@@@@@카프카 발송 완료: " + metadata.toString());
//            }
//        });
//    }
//
//    @PreDestroy
//    public void close() {
//        if (producer != null) {
//            producer.close();
//        }
//    }
//}
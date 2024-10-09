//package kr.co.seoulit.logistics.sys.service;
//
//import com.google.gson.Gson;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import org.springframework.web.reactive.function.client.WebClient;
//
//@Service
//public class KafkaConsumerServiceImpl implements KafkaConsumerService {
//
//    private final Gson gson = new Gson();
//    private final WebClient webClient;
//
//    @Autowired
//    public KafkaConsumerServiceImpl(WebClient.Builder webClientBuilder) {
//        this.webClient = webClientBuilder.build();
//    }
//
//    @Override
//    public void consumeMessages() {
//        // 메시지를 수동으로 소비할 로직을 여기에 추가
//        // 예: 메시지 큐에서 직접 메시지를 소비하도록 구현
//    }
//
//    @Override
////    @KafkaListener(topics = "accounting-group", groupId = "accounting-group")
//    public void processMessage(String message) {
//
//    }
//}

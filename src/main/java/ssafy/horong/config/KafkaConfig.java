package ssafy.horong.config;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import ssafy.horong.common.properties.KafkaProperties;
import ssafy.horong.domain.chat.dto.ChatKafkaMessage;
import ssafy.horong.domain.community.dto.NotificationKafkaMessage;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
@EnableConfigurationProperties(KafkaProperties.class)  // KafkaProperties 클래스 사용
public class KafkaConfig {

    private final KafkaProperties kafkaProperties;

    public KafkaConfig(KafkaProperties kafkaProperties) {
        this.kafkaProperties = kafkaProperties;
    }

    // Producer 설정
    @Bean
    public ProducerFactory<String, Object> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.bootstrapServers());
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        configProps.put(ProducerConfig.ACKS_CONFIG, kafkaProperties.producerAcks());
        configProps.put(ProducerConfig.RETRIES_CONFIG, kafkaProperties.producerRetries());
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    // Consumer 설정
    @Bean
    public ConsumerFactory<String, Object> consumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.bootstrapServers());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaProperties.consumerGroupId());
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, kafkaProperties.consumerAutoOffsetReset());
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "ssafy.horong.*");
        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }

    // Admin 설정 (토픽 생성을 위한 설정)
    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.bootstrapServers());
        return new KafkaAdmin(configs);
    }

    @Bean
    public ConsumerFactory<String, NotificationKafkaMessage> notificationConsumerFactory() {
        // 1) NotificationKafkaMessage 전용 JsonDeserializer 생성
        JsonDeserializer<NotificationKafkaMessage> deserializer =
                new JsonDeserializer<>(NotificationKafkaMessage.class);
        deserializer.addTrustedPackages("ssafy.horong.*");
        // 타입 헤더 없이도 기본 타입으로 파싱하도록 설정 (필요 시)
        deserializer.ignoreTypeHeaders();

        // 2) 오직 기본 Kafka Consumer 프로퍼티만 세팅
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.bootstrapServers());
        props.put(ConsumerConfig.GROUP_ID_CONFIG,                kafkaProperties.consumerGroupId());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,       kafkaProperties.consumerAutoOffsetReset());
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,  StringDeserializer.class);

        // 3) StringDeserializer + 위에서 만든 JsonDeserializer 조합으로 ConsumerFactory 생성
        return new DefaultKafkaConsumerFactory<>(
                props,
                new StringDeserializer(),
                deserializer
        );
    }


    /**
     * NotificationKafkaMessage 전용 ListenerContainerFactory
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, NotificationKafkaMessage>
    notificationKafkaListenerContainerFactory() {

        ConcurrentKafkaListenerContainerFactory<String, NotificationKafkaMessage> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(notificationConsumerFactory());
        return factory;
    }

    /**
     * ChatKafkaMessage 전용 ConsumerFactory
     */
    @Bean
    public ConsumerFactory<String, ChatKafkaMessage> chatConsumerFactory() {
        JsonDeserializer<ChatKafkaMessage> deserializer =
                new JsonDeserializer<>(ChatKafkaMessage.class);
        deserializer.addTrustedPackages("ssafy.horong.*");
        deserializer.ignoreTypeHeaders(); // 헤더 없이도 파싱 가능

        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.bootstrapServers());
        props.put(ConsumerConfig.GROUP_ID_CONFIG,                kafkaProperties.consumerGroupId());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,       kafkaProperties.consumerAutoOffsetReset());
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,  StringDeserializer.class);
        // VALUE_DESERIALIZER_CLASS_CONFIG는 제거

        return new DefaultKafkaConsumerFactory<>(
                props,
                new StringDeserializer(),
                deserializer
        );
    }

    /**
     * ChatKafkaMessage 전용 ListenerContainerFactory
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ChatKafkaMessage>
    chatKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, ChatKafkaMessage> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(chatConsumerFactory());
        return factory;
    }

    // 토픽 정의
    @Bean
    public NewTopic notificationTopic() {
        return new NewTopic("horong-notifications", 3, (short) 1);
    }

    @Bean
    public NewTopic eventsTopic() {
        return new NewTopic("horong-events", 3, (short) 1);
    }

    @Bean
    public NewTopic chatsTopic() {
        return new NewTopic("horong-chats", 3, (short) 1);
    }
}

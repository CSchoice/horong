package ssafy.horong.common.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "kafka")
public record KafkaProperties(
        String bootstrapServers,
        String consumerGroupId,
        String consumerAutoOffsetReset,
        String consumerKeyDeserializer,
        String consumerValueDeserializer,
        String producerKeySerializer,
        String producerValueSerializer,
        String producerAcks,
        Long producerRetries,
        Long producerBatchSize,
        Long producerLingerMs,
        String producerCompressionType
) {
}

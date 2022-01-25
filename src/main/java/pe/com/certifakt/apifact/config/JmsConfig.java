package pe.com.certifakt.apifact.config;

import com.amazon.sqs.javamessaging.SQSConnectionFactory;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;
import org.springframework.jms.support.destination.DynamicDestinationResolver;

import javax.jms.Session;

@Configuration
@EnableJms
public class JmsConfig {

    @Value("${apifact.aws.s3.region}")
    private String region;

    @Value("${apifact.aws.iam.access_key_id}")
    private String awsId;

    @Value("${apifact.aws.iam.secret_access_key}")
    private String awsKey;

    SQSConnectionFactory sqsConnectionFactory() {
        BasicAWSCredentials awsCreds = new BasicAWSCredentials(awsId, awsKey);
        SQSConnectionFactory connectionFactory =
                SQSConnectionFactory.builder()
                        .withRegion(Region.getRegion(Regions.fromName(region)))
                        .withAWSCredentialsProvider(new AWSStaticCredentialsProvider(awsCreds))
                        .build();
        return connectionFactory;
    }


    @Bean
    public DefaultJmsListenerContainerFactory jmsListenerContainerFactory() {
        DefaultJmsListenerContainerFactory factory =
                new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(this.sqsConnectionFactory());
        factory.setDestinationResolver(new DynamicDestinationResolver());
        factory.setConcurrency("3-10");
        factory.setSessionAcknowledgeMode(Session.CLIENT_ACKNOWLEDGE);
        factory.setMessageConverter(messageConverter());
        return factory;
    }

    @Bean
    public JmsTemplate defaultJmsTemplate() {
        return new JmsTemplate(this.sqsConnectionFactory());
    }

    @Bean
    public MessageConverter messageConverter() {
        Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
        builder.serializationInclusion(JsonInclude.Include.NON_EMPTY);
        builder.dateFormat(new ISO8601DateFormat());

        org.springframework.jms.support.converter.MappingJackson2MessageConverter mappingJackson2MessageConverter = new MappingJackson2MessageConverter();

        mappingJackson2MessageConverter.setObjectMapper(builder.build());
        mappingJackson2MessageConverter.setTargetType(MessageType.TEXT);
        mappingJackson2MessageConverter.setTypeIdPropertyName("documentType");
        return mappingJackson2MessageConverter;
    }

}
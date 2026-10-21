package org.example.aicodehelper.ai.model;

import dev.langchain4j.http.client.jdk.JdkHttpClientBuilder;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.listener.ChatModelListener;
import dev.langchain4j.model.openai.OpenAiChatModel;
import jakarta.annotation.Resource;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.time.Duration;
import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "langchain4j.open-ai.chat-model")
@Data
public class KimiChatModelConfig {

    private String modelName;   // 对应langchain4j.open-ai.chat-model.model-name

    private String apiKey;      // 对应langchain4j.open-ai.chat-model.api-key

    private String baseUrl;     // 对应langchain4j.open-ai.chat-model.base-url

    private Duration timeout;   // 对应langchain4j.open-ai.chat-model.timeout

    @Resource
    private ChatModelListener chatModelListener;

//    @Bean
//    public ChatModel myKimiChatModel(OpenAiChatModel openAiChatModel) {
//         return OpenAiChatModel.builder()
//                .httpClientBuilder(new JdkHttpClientBuilder())
//                .modelName(modelName)
//                .apiKey(apiKey)
//                .baseUrl(baseUrl)
//                .timeout(timeout)
//                .listeners(List.of(chatModelListener))
//                .build();
//    }
}

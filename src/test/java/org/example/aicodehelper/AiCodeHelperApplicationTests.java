package org.example.aicodehelper;

import dev.langchain4j.data.message.ImageContent;
import dev.langchain4j.data.message.TextContent;
import dev.langchain4j.data.message.UserMessage;
import jakarta.annotation.Resource;
import org.apache.catalina.User;
import org.example.aicodehelper.ai.AiCodeHelper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;

@SpringBootTest
class AiCodeHelperApplicationTests {

    @Resource
    private AiCodeHelper aiCodeHelper;

    @Test
    void contextLoads() {
    }

    @Test
    void chat() {
        aiCodeHelper.chat("你是什么模型");
    }

    @Test
    void multimodalChat() throws IOException {
        //获取文件路径
        Path imagePath = Path.of("src/main/resources/OIP-C (1).webp");
        //获取文件字节码数组
        byte[] imageBytes = Files.readAllBytes(imagePath);
        //Base64编码
        String base64 = Base64.getEncoder().encodeToString(imageBytes);

        UserMessage userMessage = UserMessage.from(
                TextContent.from("描述图片"),
                ImageContent.from("data:image/jpeg;base64," + base64)  // 注意MIME是webp
        );
        aiCodeHelper.multimodalChat(userMessage);
    }
}

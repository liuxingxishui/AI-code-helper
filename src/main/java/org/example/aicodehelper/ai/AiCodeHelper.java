package org.example.aicodehelper.ai;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.response.ChatResponse;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

//业务逻辑层
@Service
@Slf4j
public class AiCodeHelper {

    // 对话模型
    @Resource
    private ChatModel kimiChatModel;

    // 系统提示词
    private static final String SYSTEM_MESSAGE = " ";

    // 简单对话
    public String chat(String message) {
        // 将系统提示词封装为SystemMessage对象
        SystemMessage systemMessage = SystemMessage.from(SYSTEM_MESSAGE);
        // 将用户发送的消息封装为UserMessage对象
        UserMessage userMessage = UserMessage.from(message);
        // 获取模型返回的完整结果
        ChatResponse chatResponse = kimiChatModel.chat(systemMessage,userMessage);
        // 从返回的结果中中获取模型回复的内容
        AiMessage aiMessage = chatResponse.aiMessage();
        // 打印日志查看AI回复结果
        log.info("AI输出："+aiMessage.toString());
        // 返回AI回复结果的文本
        return aiMessage.text();
    }

    // 简单对话-自定义用户内容
    public String multimodalChat(UserMessage userMessage) {
        // 获取模型返回的完整结果
        ChatResponse chatResponse = kimiChatModel.chat(userMessage);
        // 从返回的结果中中获取模型回复的内容
        AiMessage aiMessage = chatResponse.aiMessage();
        // 打印日志查看AI回复结果
        log.info("AI输出："+aiMessage.toString());
        // 返回AI回复结果的文本
        return aiMessage.text();
    }
}

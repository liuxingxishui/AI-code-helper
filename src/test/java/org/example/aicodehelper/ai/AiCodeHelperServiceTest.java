package org.example.aicodehelper.ai;

import dev.langchain4j.service.Result;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AiCodeHelperServiceTest {

    @Resource
    private AiCodeHelperService aiCodeHelperService;

    @Test
    void chat() {
        String result = aiCodeHelperService.chat(1,"你好，我是一个大学生");
        System.out.println(result);
    }

    // 测试会话记忆
    @Test
    void chatWithMemory() {
        String result = aiCodeHelperService.chat(1,"你好，我是一个大学生");
        System.out.println(result);
        System.out.println("-------------------------------------");
        result = aiCodeHelperService.chat(2,"我是谁");
        System.out.println(result);
    }

    // 测试结构化输出
    @Test
    void chatForReport() {
        String userMessage = "你好，我是大学生，学习编程两年半，请帮我制定学习报告";
        AiCodeHelperService.Report report = aiCodeHelperService.chatForReport(userMessage);
        System.out.println(report);
    }

    // 测试RAG检索增强生成
    @Test
    void chatWithRag() {
        Result<String> result = aiCodeHelperService.chatWithRag("怎么学习Java？有哪些常见的面试题");
        System.out.println(result.sources());
        System.out.println(result.content());
    }

    // 测试工具调用Tool Calling
    @Test
    void chatWithTools() {
        String result = aiCodeHelperService.chat(1,"有哪些常见的计算机网络面试题");
        System.out.println(result);
    }

    // 测试MCP
    @Test
    void chatWithMCP() {
        String result = aiCodeHelperService.chat(1,"什么是程序员鱼皮的编程导航");
        System.out.println(result);
    }

    // 测试Guardrail
    @Test
    void chatWithGuardrail() {
        String result = aiCodeHelperService.chat(1,"kill the game");
        System.out.println(result);
    }
}
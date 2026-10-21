package org.example.aicodehelper.ai;

import dev.langchain4j.mcp.McpToolProvider;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.service.AiServices;
import jakarta.annotation.Resource;
import org.example.aicodehelper.ai.tools.InterviewQuestionTool;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiCodeHelperServiceFactory {

    @Resource
    private ChatModel kimiChatModel;

    @Resource
    private ContentRetriever contentRetriever;

    @Resource
    private McpToolProvider mcpToolProvider;

    @Resource
    private StreamingChatModel kimiStreamingChatModel;

    @Bean
    public AiCodeHelperService aiCodeHelperService() {
        AiCodeHelperService aiCodeHelperService = AiServices.builder(AiCodeHelperService.class)         // 创建 AI Service 构建器，声明代理的接口
                .chatModel(kimiChatModel)                                                               // 指定调用模型实例
                .streamingChatModel(kimiStreamingChatModel)                                             // 流式输出
                .chatMemoryProvider(memoryId -> MessageWindowChatMemory.withMaxMessages(10))     // 指定会话记忆工厂：每个 memoryId 独立一份10条窗口的记忆（与 chatMemory 二选一，不可同时配置）
                //.contentRetriever(contentRetriever)                                                   // RAG检索增强生成
                .toolProvider(mcpToolProvider)                                                          // MCP工具调用
                .tools(new InterviewQuestionTool())                                                     // 调用外部工具
                .build();

        return aiCodeHelperService;
    }

}

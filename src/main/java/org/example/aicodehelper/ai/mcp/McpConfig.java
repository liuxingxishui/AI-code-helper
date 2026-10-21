package org.example.aicodehelper.ai.mcp;

import dev.langchain4j.mcp.McpToolProvider;
import dev.langchain4j.mcp.client.DefaultMcpClient;
import dev.langchain4j.mcp.client.McpClient;
import dev.langchain4j.mcp.client.transport.McpTransport;
import dev.langchain4j.mcp.client.transport.http.HttpMcpTransport;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class McpConfig {
     @Value("${bigmodel.apikey}")
    public String apiKey;

    @Bean
    public McpToolProvider mcpToolProvider() {
        // 1.和MCP建立连接
        McpTransport transport = new HttpMcpTransport.Builder()                                         // 创建传输层构建器
                .sseUrl("https://open.bigmodel.cn/api/mcp/web_search/sse?Authorization=" + apiKey)      // 指定 SSE 服务端地址
                .logRequests(true)                                                                      // 开启请求日志
                .logResponses(true)                                                                     // 开启响应日志
                .build();

        // 2.建立MCP客户端
        McpClient mcpClient = new DefaultMcpClient.Builder()    // 创建客户端的构建器
                .key("yupiMcpClient")                           // 给这个客户端起唯一标识名
                .transport(transport)                           // 挂载传输通道
                .build();

        // 3.从MCP客户端获取工具
        McpToolProvider toolProvider = McpToolProvider.builder()    // 创建工具提供者的构建器
                .mcpClients(mcpClient)                              // 挂载客户端
                .build();

        return toolProvider;
    }
}

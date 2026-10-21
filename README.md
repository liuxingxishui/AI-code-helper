# 🤖 AI 编程小助手 - LangChain4j 实战项目

> 基于 LangChain4j + Kimi K2.6 的 AI 智能编程学习与求职辅导机器人

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.1-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Vue.js](https://img.shields.io/badge/Vue.js-3-4FC08D.svg)](https://vuejs.org/)
[![LangChain4j](https://img.shields.io/badge/LangChain4j-1.20-blue.svg)](https://github.com/langchain4j/langchain4j)
[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/)

本项目是一个前后端分离的 AI 对话应用，围绕「编程学习 + 求职面试」两个场景，在实践中完整落地 LangChain4j 的主流用法：会话记忆、结构化输出、RAG 检索增强、工具调用、MCP 协议、输入护栏与 SSE 流式输出。

本项目中，AI 输出不是简单回显——前端实现了手写 SSE 解析器与 Markdown 安全渲染，并针对流式接口完成了专项测试（分片完整性、断连释放、并发压测）。

## ✨ 项目介绍

### 定位
- 编程学习导师：提供清晰的学习路线规划和个性化建议
- 求职面试助手：涵盖简历优化、面试技巧、高频题目解析
- 代码答疑专家：实时解答编程技术问题，提供代码示例

### 技术

#### AI 服务
- **LangChain4j 集成**：采用业界领先的 Java AI 应用开发框架（1.20 + Spring Boot 4 Starter）
- **Kimi K2.6 模型**：基于 Moonshot 大模型（OpenAI 兼容接口），支持深度思考，超时放宽至 300s
- **智谱 Embedding**：embedding-3 向量模型，支撑本地知识库语义检索
- **流式响应**：Reactor Flux → SSE 逐字推送，实时打字机效果

#### 安全机制
- **输入安全防护**：自实现 Guardrail，分词 + 大小写不敏感敏感词拦截
- **输出渲染消毒**：marked 解析 Markdown 后必经 DOMPurify 消毒，防 XSS
- **密钥管理**：API 密钥经环境变量注入，不硬编码不入库

#### 工具集成
- **RAG 检索增强**：启动时加载本地文档库，段落切割（1000字符/重叠200）并向量化，问答精准可溯源
- **MCP 协议支持**：经 SSE 传输接入智谱 web_search MCP 服务器，实现联网搜索
- **面试题搜索工具**：jsoup 实时抓取面试鸭网站高频面试题
- **全链路日志**：ChatModelListener 记录请求 / 响应 / 错误，可观测可排查

## 🚀 快速开始

### 环境要求

- **Java**: JDK 21
- **Node.js**: 16+
- **Maven**: 3.6+
- **Moonshot API**: 需申请 KIMI_KEY（对话模型）
- **Big Model API**: 需申请 GLM_KEY（向量模型 + MCP 联网搜索）

### 启动步骤

#### 1. 配置 API 密钥（二选一）

```bash
# 方式一：环境变量（推荐）
set KIMI_KEY=sk-xxx
set GLM_KEY=xxx
```

```yaml
# 方式二：编辑 src/main/resources/application.yml，将 ${KIMI_KEY} 直接替换为真实密钥
```

#### 2. 后端启动

```bash
# Maven 启动
mvn spring-boot:run

# 或打包后启动
mvn clean package
java -jar target/AI-code-helper-0.0.1-SNAPSHOT.jar
```

#### 3. 前端启动

```bash
# 进入前端目录
cd aicodehelper-frontend

# 安装依赖
npm install

# 启动开发服务器
npm run dev
```

#### 4. 访问应用
- 前端地址：`http://localhost:5173`
- 后端 API：`http://localhost:8081/api`

## 技术架构

```
┌─────────────────┐    /api 代理    ┌─────────────────┐
│   Vue.js 前端    │ ──────────────▶ │  Spring Boot    │
│  - 聊天界面      │                 │   后端服务       │
│  - 手写SSE解析   │   ◀── SSE 流 ── │  - RESTful API  │
│  - Markdown渲染  │                 │  - SSE 推送      │
└─────────────────┘                 └─────────────────┘
                                             │
                                   ┌─────────────────┐
                                   │   LangChain4j   │
                                   │  - AiServices   │
                                   │  - RAG / 工具   │
                                   │  - Guardrail    │
                                   └─────────────────┘
                                      │            │
                            ┌──────────────┐  ┌──────────────┐
                            │ Kimi K2.6    │  │ 智谱服务      │
                            │ (对话模型)    │  │ embedding-3  │
                            └──────────────┘  │ MCP web搜索  │
                                              └──────────────┘
```

## 核心模块

后端（`src/main/java/org/example/aicodehelper/`）：
- `AiCodeHelperService`: 核心对话服务接口（记忆 / 流式 / RAG / 结构化输出）
- `KimiChatModelConfig`: Kimi 对话模型配置
- `RagConfig`: RAG 检索增强配置（文档加载、切割、向量化）
- `McpConfig`: MCP 客户端配置（SSE 传输）
- `InterviewQuestionTool`: 面试题搜索工具（jsoup）
- `SafeInputGuardrail`: 输入安全防护
- `ChatModelListenerConfig`: 全链路日志监听
- `AiController`: SSE 流式接口入口

前端（`aicodehelper-frontend/src/`）：
- `api/chat.js`: 基于 fetch ReadableStream 的 SSE 解析器（分片拼接、缓冲区冲刷）
- `App.vue`: 聊天界面，marked + DOMPurify 安全渲染 Markdown

## 质量保障

- JUnit 5 七组接口级用例（对话 / 记忆 / 结构化报告 / RAG / 工具 / MCP / 护栏）
- SSE 流式接口专项测试：分片完整性、结束标志、断连后的连接释放
- JMeter 长连接并发压测（20 / 50 / 100 并发三档）
- 测试驱动修复真实缺陷：流末尾缓冲区未冲刷导致尾字丢失、Markdown 无渲染、XSS 消毒缺失

## 致谢

- [LangChain4j](https://github.com/langchain4j/langchain4j) - 强大的 Java AI 应用开发框架
- [Moonshot Kimi](https://platform.moonshot.cn/) - 优秀的大语言模型
- [智谱 AI](https://open.bigmodel.cn/) - Embedding 向量模型与 MCP 服务
- [Spring Boot](https://spring.io/projects/spring-boot) - 简化的 Java 开发框架
- [Vue.js](https://vuejs.org/) - 渐进式 JavaScript 框架

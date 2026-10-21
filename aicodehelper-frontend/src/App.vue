<template>
  <div class="chat-page">
    <!-- 顶部标题栏 -->
    <header class="chat-header">
      <div class="header-title">
        <span class="logo">🤖</span>
        <div>
          <h1>AI 编程小助手</h1>
          <p class="subtitle">编程学习 · 求职面试 · 有问必答</p>
        </div>
      </div>
      <div class="room-info">聊天室 #{{ memoryId }}</div>
    </header>

    <!-- 聊天记录区域 -->
    <main ref="messageListRef" class="message-list">
      <div
        v-for="(msg, index) in messages"
        :key="index"
        class="message-row"
        :class="msg.role === 'user' ? 'row-user' : 'row-ai'"
      >
        <!-- AI 头像在左 -->
        <div v-if="msg.role === 'ai'" class="avatar avatar-ai">AI</div>

        <div class="bubble" :class="msg.role === 'user' ? 'bubble-user' : 'bubble-ai'">
          <!-- 加载中的三点动画 -->
          <span v-if="msg.loading && !msg.content" class="typing">
            <i></i><i></i><i></i>
          </span>
          <!-- AI 消息：Markdown 渲染（消毒后 v-html），支持加粗/列表/代码块 -->
          <span
            v-else-if="msg.role === 'ai'"
            class="bubble-text markdown-body"
            v-html="renderMarkdown(msg.content)"
          ></span>
          <!-- 用户消息：纯文本展示 -->
          <span v-else class="bubble-text">{{ msg.content }}</span>
          <!-- 流式输出中的光标 -->
          <span v-if="msg.loading && msg.content" class="cursor">▍</span>
        </div>

        <!-- 用户头像在右 -->
        <div v-if="msg.role === 'user'" class="avatar avatar-user">我</div>
      </div>
    </main>

    <!-- 底部输入区域 -->
    <footer class="input-area">
      <div class="input-inner">
        <div class="input-wrap">
          <textarea
            v-model="inputMessage"
            class="input-box"
            placeholder="输入你的编程或面试问题，Enter 发送，Shift + Enter 换行"
            rows="1"
            :disabled="isSending"
            @keydown.enter.exact.prevent="sendMessage"
          ></textarea>
          <button class="send-btn" :disabled="!canSend" title="发送" @click="sendMessage">
            <svg viewBox="0 0 24 24" width="18" height="18">
              <path d="M12 2.5 L21 21 L12 16.6 L3 21 Z" fill="#ffffff" />
            </svg>
          </button>
        </div>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { ref, computed, nextTick, onMounted, onBeforeUnmount } from 'vue'
import { marked } from 'marked'
import DOMPurify from 'dompurify'
import { chatStream } from './api/chat'

// Markdown 渲染配置：启用 GFM（表格、删除线等），不换行即 <br>
marked.setOptions({ gfm: true, breaks: true })

/**
 * 把 AI 回复的 Markdown 文本转成安全 HTML。
 * marked 负责语法解析，DOMPurify 负责消毒：
 *  AI 输出不可信，直接 v-html 会执行其中的脚本/事件属性（XSS），必须过一遍消毒。
 */
function renderMarkdown(text) {
  return DOMPurify.sanitize(marked.parse(text, { async: false }))
}

// 进入页面自动生成聊天室 id（memoryId），用于区分不同会话
const memoryId = ref(Math.floor(Math.random() * 1000000000) + 1)

const messages = ref([])
const inputMessage = ref('')
const isSending = ref(false)
const messageListRef = ref(null)

let cancelStream = null

const canSend = computed(() => inputMessage.value.trim() !== '' && !isSending.value)

// 欢迎语
onMounted(() => {
  messages.value.push({
    role: 'ai',
    content:
      '你好呀～我是 AI 编程小助手 👋\n无论是编程学习中的疑惑，还是求职面试的准备，都可以问我哦！',
    loading: false
  })
})

// 组件卸载时中断未完成的流式请求
onBeforeUnmount(() => {
  if (cancelStream) cancelStream()
})

// 滚动到聊天记录底部
function scrollToBottom() {
  nextTick(() => {
    const el = messageListRef.value
    if (el) el.scrollTop = el.scrollHeight
  })
}

function sendMessage() {
  const text = inputMessage.value.trim()
  if (!text || isSending.value) return

  // 1. 用户消息上屏（右侧）
  messages.value.push({ role: 'user', content: text, loading: false })
  inputMessage.value = ''
  isSending.value = true
  scrollToBottom()

  // 2. 放置 AI 占位消息（左侧），流式追加内容
  const aiMessage = ref({ role: 'ai', content: '', loading: true })
  messages.value.push(aiMessage.value)

  // 3. 通过 SSE 调用 chat 接口
  cancelStream = chatStream({
    memoryId: memoryId.value,
    message: text,
    onChunk: (chunk) => {
      aiMessage.value.content += chunk
      scrollToBottom()
    },
    onDone: () => {
      aiMessage.value.loading = false
      isSending.value = false
      cancelStream = null
      scrollToBottom()
    },
    onError: (err) => {
      console.error(err)
      aiMessage.value.content =
        aiMessage.value.content || '抱歉，服务暂时出错了，请稍后重试 😥'
      aiMessage.value.loading = false
      isSending.value = false
      cancelStream = null
    }
  })
}
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

html,
body,
#app {
  height: 100%;
  font-family: 'PingFang SC', 'Microsoft YaHei', 'Helvetica Neue', Arial, sans-serif;
}

.chat-page {
  height: 100%;
  display: flex;
  flex-direction: column;
  background: #f2f3f7;
}

/* ===== 顶部标题栏 ===== */
.chat-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 14px 24px;
  background: #ffffff;
  border-bottom: 1px solid #e8e8ee;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.04);
}

.header-title {
  display: flex;
  align-items: center;
  gap: 12px;
}

.logo {
  font-size: 32px;
}

.chat-header h1 {
  font-size: 18px;
  color: #1f2329;
}

.subtitle {
  font-size: 12px;
  color: #8a8f99;
  margin-top: 2px;
}

.room-info {
  font-size: 12px;
  color: #8a8f99;
  background: #f2f3f7;
  border-radius: 12px;
  padding: 4px 12px;
}

/* ===== 聊天记录区域 ===== */
.message-list {
  flex: 1;
  overflow-y: auto;
  padding: 24px;
  display: flex;
  flex-direction: column;
  gap: 18px;
}

.message-row {
  display: flex;
  align-items: flex-start;
  gap: 10px;
  max-width: 900px;
  width: 100%;
  margin: 0 auto;
}

.row-user {
  justify-content: flex-end;
}

.row-ai {
  justify-content: flex-start;
}

.avatar {
  width: 38px;
  height: 38px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 13px;
  font-weight: 600;
  flex-shrink: 0;
}

.avatar-ai {
  background: linear-gradient(135deg, #6a8dff, #9b6bff);
  color: #fff;
}

.avatar-user {
  background: linear-gradient(135deg, #34c77b, #2aa5a0);
  color: #fff;
}

.bubble {
  max-width: 70%;
  padding: 12px 16px;
  border-radius: 12px;
  font-size: 14px;
  line-height: 1.7;
  word-break: break-word;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.06);
}

.bubble-ai {
  background: #ffffff;
  color: #1f2329;
  border-top-left-radius: 4px;
}

.bubble-user {
  background: #4c7dff;
  color: #ffffff;
  border-top-right-radius: 4px;
}

.bubble-text {
  white-space: pre-wrap;
}

/* ===== AI 消息 Markdown 渲染样式 ===== */
.markdown-body strong {
  font-weight: 600;
}

.markdown-body ul,
.markdown-body ol {
  padding-left: 22px;
  margin: 6px 0;
}

.markdown-body li {
  margin: 3px 0;
}

.markdown-body h1,
.markdown-body h2,
.markdown-body h3,
.markdown-body h4 {
  font-size: 15px;
  margin: 10px 0 6px;
  color: #1f2329;
}

.markdown-body p {
  margin: 6px 0;
}

.markdown-body code {
  background: #f2f3f7;
  border-radius: 4px;
  padding: 2px 5px;
  font-family: 'JetBrains Mono', Consolas, Monaco, monospace;
  font-size: 13px;
  color: #d4380d;
}

.markdown-body pre {
  background: #1f2329;
  color: #e6e8ee;
  border-radius: 8px;
  padding: 10px 14px;
  overflow-x: auto;
  margin: 8px 0;
}

.markdown-body pre code {
  background: none;
  padding: 0;
  color: inherit;
}

.markdown-body blockquote {
  border-left: 3px solid #dcdfe8;
  padding-left: 10px;
  margin: 6px 0;
  color: #6b7280;
}

.markdown-body a {
  color: #4c7dff;
}

.markdown-body table {
  border-collapse: collapse;
  margin: 8px 0;
}

.markdown-body th,
.markdown-body td {
  border: 1px solid #dcdfe8;
  padding: 5px 10px;
}

/* 流式输出光标 */
.cursor {
  display: inline-block;
  color: #4c7dff;
  animation: blink 0.8s infinite;
}

@keyframes blink {
  0%, 100% { opacity: 1; }
  50% { opacity: 0; }
}

/* 加载中的三点动画 */
.typing i {
  display: inline-block;
  width: 6px;
  height: 6px;
  margin: 0 2px;
  border-radius: 50%;
  background: #b0b6c3;
  animation: bounce 1.2s infinite;
}

.typing i:nth-child(2) { animation-delay: 0.2s; }
.typing i:nth-child(3) { animation-delay: 0.4s; }

@keyframes bounce {
  0%, 60%, 100% { transform: translateY(0); }
  30% { transform: translateY(-5px); }
}

/* ===== 底部输入区域 ===== */
.input-area {
  position: relative; /* 作为右侧装饰图片的定位参照 */
  padding: 16px 24px 20px;
  background: #ffffff;
  border-top: 1px solid #e8e8ee;
}

/* 与消息列表同宽（900px 居中） */
.input-inner {
  max-width: 900px;
  margin: 0 auto;
}

/* 输入框容器：按钮绝对定位在框内右端 */
.input-wrap {
  position: relative;
  display: flex;
}

.input-box {
  flex: 1;
  resize: none;
  border: 1px solid #dcdfe8;
  border-radius: 50px;
  padding: 12px 48px 12px 14px; /* 右侧留出发送按钮的位置 */
  font-size: 14px;
  line-height: 1.6;
  outline: none;
  font-family: inherit;
  transition: border-color 0.2s;
}

.input-box:focus {
  border-color: #4c7dff;
}

.input-box:disabled {
  background: #f5f6fa;
}

/* 圆形图标发送按钮：输入框内最右端，距右边框 5px */
.send-btn {
  position: absolute;
  right: 5px;
  top: 50%;
  transform: translateY(-50%);
  width: 34px;
  height: 34px;
  border: none;
  border-radius: 50%;
  padding: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #d5d8de;
  cursor: pointer;
  transition: background 0.2s;
}

.send-btn:hover:not(:disabled) {
  background: #c2c6ce;
}

.send-btn:disabled {
  opacity: 0.55;
  cursor: not-allowed;
}
</style>

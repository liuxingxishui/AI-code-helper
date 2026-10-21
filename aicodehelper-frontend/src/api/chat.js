import axios from 'axios'

/**
 * Axios 实例：作为项目统一的 HTTP 请求库。
 * 普通（非流式）接口直接使用 apiClient 发起请求。
 */
const apiClient = axios.create({
  baseURL: '/api',
  timeout: 30000
})

// 响应拦截器：统一处理错误
apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    console.error('[API 请求失败]', error)
    return Promise.reject(error)
  }
)

export default apiClient

/**
 * 通过 SSE 调用聊天接口（GET /api/ai/chat），实时接收 AI 回复。
 *
 * 说明：浏览器端 Axios 不支持对流式响应做增量解析，
 * 因此 SSE 流式读取这里基于 fetch 的 ReadableStream 实现，
 * 其余常规请求统一走上面的 Axios 实例。
 *
 * 后端返回格式（Spring ServerSentEvent）：
 *   data:内容片段
 *
 *   data:下一个片段
 *
 * @param {Object}   options
 * @param {number}   options.memoryId 会话 id（聊天室 id）
 * @param {string}   options.message  用户消息
 * @param {Function} options.onChunk  每收到一个数据片段时回调
 * @param {Function} options.onDone   流结束时回调
 * @param {Function} options.onError  出错时回调
 * @returns {Function} 取消函数，调用可中断本次流式请求
 */
export function chatStream({ memoryId, message, onChunk, onDone, onError }) {
  const controller = new AbortController()

  const url = `/api/ai/chat?memoryId=${encodeURIComponent(memoryId)}&message=${encodeURIComponent(message)}`

  fetch(url, {
    method: 'GET',
    headers: { Accept: 'text/event-stream' },
    signal: controller.signal
  })
    .then(async (response) => {
      if (!response.ok || !response.body) {
        throw new Error(`请求失败，状态码：${response.status}`)
      }

      const reader = response.body.getReader()
      const decoder = new TextDecoder('utf-8')
      let buffer = ''

      // 解析单个 SSE 事件：提取所有 data: 行并拼接（多行 data 以 \n 连接）
      const parseEvent = (event) =>
        event
          .split('\n')
          .filter((line) => line.startsWith('data:'))
          .map((line) => line.slice(5).replace(/^ /, ''))
          .join('\n')

      while (true) {
        const { done, value } = await reader.read()
        if (done) break

        buffer += decoder.decode(value, { stream: true })

        // SSE 事件之间以空行（\n\n）分隔
        const events = buffer.split('\n\n')
        buffer = events.pop() // 最后一段可能不完整，留到下次拼接

        for (const event of events) {
          const data = parseEvent(event)
          if (data) {
            onChunk && onChunk(data)
          }
        }
      }

      // 流结束后缓冲区可能还残留最后一段（后端收尾不带空行时），
      // 不处理会导致回复末尾几个字符丢失
      const tail = parseEvent(buffer)
      if (tail) {
        onChunk && onChunk(tail)
      }

      onDone && onDone()
    })
    .catch((err) => {
      if (err.name === 'AbortError') return // 主动取消，不视为错误
      onError && onError(err)
    })

  return () => controller.abort()
}

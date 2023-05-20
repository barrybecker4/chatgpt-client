package com.barrybecker4.chatgpt

/**
 * Example response:

{
  "id": "chatcmpl-7IIV6xWZOZv1xiYJTQYgoxlHlMLci",
  "object": "chat.completion",
  "created": 1684595376,
  "model": "gpt-3.5-turbo-0301",
  "usage": {
    "prompt_tokens": 26,
    "completion_tokens": 103,
    "total_tokens": 129
  },
  "choices": [
    {
      "message": {
        "role": "assistant",
        "content": "As an AI language model, I don't have a personal belief system or a definitive answer to the meaning of life. Philosophers, scientists, and spiritual leaders have debated this question for centuries, and there are many different perspectives on what the purpose of life might be. Some people believe that the meaning of life is to seek happiness, while others believe it is to fulfill a divine purpose or to contribute to society. Ultimately, the meaning of life is a subjective and personal question that each individual must answer for themselves."
      },
      "finish_reason": "stop",
      "index": 0
    }
  ]
}
 */
case class ChatGptResponse(id: String, `object`: String, created: Long, model: String, usage: Usage, choices: Seq[ChatGptChoice])

case class Usage(prompt_tokens: Int, completion_tokens: Int, total_tokens: Int)
case class ChatGptChoice(message: Message, finish_reason: String, index: Int)

case class Message(role: String, content: String)

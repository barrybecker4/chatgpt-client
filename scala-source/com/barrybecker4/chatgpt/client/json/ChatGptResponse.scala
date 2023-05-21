package com.barrybecker4.chatgpt.client.json

import scala.language.implicitConversions
import grapple.json.{ *, given }


/**
 * Example response:
 *
 * {
 *   "id": "chatcmpl-7IIV6xWZOZv1xiYJTQYgoxlHlMLci",
 *   "object": "chat.completion",
 *   "created": 1684595376,
 *   "model": "gpt-3.5-turbo-0301",
 *   "usage": {
 *     "prompt_tokens": 26,
 *     "completion_tokens": 103,
 *     "total_tokens": 129
 *   },
 *   "choices": [
 *     {
 *       "message": {
 *       "role": "assistant",
 *       "content": "As an AI language model, ... must answer for themselves."
 *     },
 *     "finish_reason": "stop",
 *     "index": 0
 *    }
 *  ]
 * }
 */
case class ChatGptResponse(id: String, `object`: String, created: Long, model: String, usage: Usage, choices: Seq[ChatGptChoice])
case class Usage(prompt_tokens: Int, completion_tokens: Int, total_tokens: Int)
case class ChatGptChoice(message: Message, finish_reason: String, index: Int)

/**
 * @param role one of system, user, assistant
 * @param content some text. Usually a question or answer.
 */
case class Message(role: String, content: String) {
  override def toString: String = s"""{ "role": "$role", "content": "$content" }"""
}


given JsonInput[ChatGptResponse] with
  def read(json: JsonValue) = ChatGptResponse(
    json("id"), json("object"), json("created"), json("model"), json("usage"), json("choices")
  )

given JsonInput[Message] with
  def read(json: JsonValue) = Message(json("role"), json("content"))

given JsonInput[Usage] with
  def read(json: JsonValue) = Usage(
    json("prompt_tokens"), json("completion_tokens"), json("total_tokens")
  )

given JsonInput[ChatGptChoice] with
  def read(json: JsonValue) = ChatGptChoice(json("message"), json("finish_reason"), json("index"))


object ChatGptResponse {

  def parse(jsonString: String): ChatGptResponse = {
    Json.parse (jsonString).as[ChatGptResponse]
  }
}


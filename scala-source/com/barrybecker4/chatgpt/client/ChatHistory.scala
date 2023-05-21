package com.barrybecker4.chatgpt.client

import com.barrybecker4.chatgpt.client.ChatHistory.BASE_CONTEXT
import com.barrybecker4.chatgpt.client.json.Message

class ChatHistory {

  var messageHistory = Seq(BASE_CONTEXT)

  def addUserMessage(content: String): Unit = {
    messageHistory :+= Message("user", content)
  }

  def addAssistantMessage(content: String): Unit = {
    messageHistory :+= Message("assistance", content)
  }

  override def toString: String = messageHistory.map(_.toString).mkString("[", ",", "]")
}

object ChatHistory {
  val BASE_CONTEXT = Message("system", "You are a psychologist.")
}
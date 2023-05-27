package com.barrybecker4.chatgpt.client

import com.barrybecker4.chatgpt.client.json.Message

class ChatHistory(systemMessage: String) {

  private var messageHistory = Seq(Message("system", systemMessage))

  def this() = {
    this("You are a helpful assistant.")
  }

  def addUserMessage(content: String): Unit = {
    messageHistory :+= Message("user", content)
  }

  def addAssistantMessage(content: String): Unit = {
    messageHistory :+= Message("assistant", content.replaceAll("\"", "\\\""))
  }

  override def toString: String = messageHistory.map(_.toString).mkString("[", ",", "]")
}

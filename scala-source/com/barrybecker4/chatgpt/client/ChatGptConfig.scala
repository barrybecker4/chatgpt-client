package com.barrybecker4.chatgpt.client

import akka.http.scaladsl.model.{ContentTypes, HttpEntity, RequestEntity}
import com.typesafe.config.ConfigFactory

object ChatGptConfig {

  private val config = ConfigFactory.load()

  def getApiHost: String = config.getString("api_host")
  def getApiEndpoint: String = config.getString("api_endpoint")
  def getApiKey: String = config.getString("api_key")
  def getModel: String =  config.getString("model")
  def getMaxTokens: Int = config.getInt("max_tokens")
  def getTemperature: Double = config.getDouble("temperature")
  def getNumCompletions: Int = config.getInt("num_completions")

  /**
   * possible roles are system, user, or assistant.
   * @param prompt the current text from user
   * @return parameters to send to the chat request
   */
  def getParameters(prompt: String): RequestEntity =

    HttpEntity(ContentTypes.`application/json`,
      s"""{
         |"model": "$getModel",
         |"messages": [
         |  {"role": "system", "content": "You are a helpful assistant." },
         |  {"role": "user",   "content": "$prompt" }
         |],
         |"max_tokens": $getMaxTokens,
         |"temperature": $getTemperature,
         |"n": $getNumCompletions
      }""".stripMargin)
}

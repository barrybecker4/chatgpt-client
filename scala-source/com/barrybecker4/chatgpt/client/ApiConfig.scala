package com.barrybecker4.chatgpt.client

import akka.http.scaladsl.model.{ContentTypes, HttpEntity, RequestEntity}
import com.barrybecker4.chatgpt.client.json.Personality
import com.typesafe.config.{Config, ConfigFactory}


object ApiConfig {

  private val config: Config = ConfigFactory.load("api")

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
  def getParameters(messages: String): RequestEntity =

    val content =
      s"""{
         |"model": "$getModel",
         |"messages": $messages,
         |"max_tokens": $getMaxTokens,
         |"temperature": $getTemperature,
         |"n": $getNumCompletions
    }""".stripMargin

    //println("content = \n" + content)
    HttpEntity(ContentTypes.`application/json`, content)
}

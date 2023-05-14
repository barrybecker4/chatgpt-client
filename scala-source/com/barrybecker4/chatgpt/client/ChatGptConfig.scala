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

  def getParameters(prompt: String): RequestEntity = HttpEntity(ContentTypes.`application/json`,
    s"""{
       |"prompt": "$prompt",
       |"model": "$getModel",
       |"max_tokens": $getMaxTokens,
       |"temperature": $getTemperature,
       |"n": $getNumCompletions
    }""".stripMargin)
}

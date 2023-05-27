package com.barrybecker4.chatgpt.client

import akka.http.scaladsl.model.{ContentTypes, HttpEntity, RequestEntity}
import com.barrybecker4.chatgpt.client.json.Personality
import com.typesafe.config.{Config, ConfigFactory}

object PersonalitiesConfig {

  private val config = ConfigFactory.load("personalities")

  def getPersonalities: Seq[Personality] =
    config.getConfigList("personalities").toArray
      .map(personality => {
        val p = personality.asInstanceOf[Config]
        Personality(p.getString("id"), p.getString("name"), p.getString("greeting"), p.getString("prompt"))
      })
  
}

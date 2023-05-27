package com.barrybecker4.chatgpt.client.json

import scala.language.implicitConversions
import grapple.json.{ *, given }

case class Personality(id: String, name: String, greeting: String, prompt: String)



given JsonInput[Personality] with
  def read(json: JsonValue) = Personality(json("id"), json("name"), json("greeting"), json("prompt"))


object Personality {

  def parse(jsonString: String): Personality = {
    Json.parse (jsonString).as[Personality]
  }
}

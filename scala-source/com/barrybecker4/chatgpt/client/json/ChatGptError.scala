package com.barrybecker4.chatgpt.client.json

import scala.language.implicitConversions
import grapple.json.{ *, given }


case class ChatGptError(error: ErrorBody)

case class ErrorBody(message: String, `type`: String)


given JsonInput[ChatGptError] with
  def read(json: JsonValue) = ChatGptError(json("error"))


given JsonInput[ErrorBody] with
  def read(json: JsonValue) = ErrorBody(json("message"), json("type"))


object ChatGptError {

  def parse(jsonString: String): ChatGptError = {
    Json.parse (jsonString).as[ChatGptError]
  }
}


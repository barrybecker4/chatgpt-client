package com.barrybecker4.chatgpt.client

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpRequest, HttpResponse, MediaRange, MediaTypes, Uri}
import akka.http.scaladsl.model.HttpMethods.*
import akka.http.scaladsl.model.Uri.{Path, Query}
import akka.http.scaladsl.model.headers.{Accept, Authorization, OAuth2BearerToken}
import akka.stream.ActorMaterializer
import akka.util.ByteString
import com.barrybecker4.chatgpt.{ChatGptChoice, ChatGptResponse, Usage, Message}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}
import scala.language.implicitConversions
import grapple.json.{*, given}


/**
 * This can probably all be replaced with https://index.scala-lang.org/cequence-io/openai-scala-client
 */
object Client {

  def callChatGPT(prompt: String): Future[String] = {
    implicit val system = ActorSystem()

    val request = getRequest(prompt)
    val responseFuture: Future[HttpResponse] = Http().singleRequest(request)

    responseFuture.flatMap { response =>
      response.entity.dataBytes.runFold(ByteString(""))(_ ++ _).map(_.utf8String)
    }.andThen { case _ => system.terminate() }
  }

  def getRequest(prompt: String): HttpRequest = {
    val apiKey = ChatGptConfig.getApiKey
    val uri = getUri(Query())
    val entity = ChatGptConfig.getParameters(prompt)

    HttpRequest(
      method = POST,
      uri = uri,
      entity = entity
    ).withHeaders(Accept(MediaRange(MediaTypes.`application/xml`)), Authorization(OAuth2BearerToken(apiKey)))
  }

  private def getUri(query: Query): Uri = {
    val host = ChatGptConfig.getApiHost
    val endpoint = Path(ChatGptConfig.getApiEndpoint)
    println("endpoint = " + endpoint.toString)
    Uri().withScheme("https").withHost(host).withPath(endpoint).withQuery(query)
  }
  case class User(id: Int, name: String)



  def main(args: Array[String]): Unit = {
    val prompt = "What is the meaning of life?"

    // Define how to convert JsonValue to User
    given JsonInput[User] with
      def read(json: JsonValue) = User(json("id"), json("name"))

    given JsonInput[Usage] with
      def read(json: JsonValue) = Usage(
        json("prompt_tokens"), json("completion_tokens"), json("total_tokens")
      )

    given JsonInput[Message] with
      def read(json: JsonValue) = Message(json("role"), json("content"))

    given JsonInput[ChatGptChoice] with
      def read(json: JsonValue) = ChatGptChoice(json("message"), json("finish_reason"), json("index"))

    given JsonInput[ChatGptResponse] with
      def read(json: JsonValue) = ChatGptResponse(
        json("id"), json("object"), json("created"), json("model"), json("usage"), json("choices")
      )

    val jsonStr = Json.parse("""{ "id": 1000, "name": "lupita" }""")
    println("jsonStr = " + jsonStr)

    callChatGPT(prompt).onComplete {
      case Success(response) =>
        val responseJson = Json.parse(response).as[ChatGptResponse]
        println(responseJson.choices.head.message.content)
      case Failure(ex) =>
        println(ex)
    }
  }
}
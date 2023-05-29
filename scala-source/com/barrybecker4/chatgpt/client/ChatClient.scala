package com.barrybecker4.chatgpt.client

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpRequest, HttpResponse, MediaRange, MediaTypes, Uri}
import akka.http.scaladsl.model.HttpMethods.*
import akka.http.scaladsl.model.Uri.{Path, Query}
import akka.http.scaladsl.model.headers.{Accept, Authorization, OAuth2BearerToken}
import akka.stream.ActorMaterializer
import akka.util.ByteString
import com.barrybecker4.chatgpt.client.json.{ChatGptChoice, ChatGptError, ChatGptResponse, Message, Usage}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}
import scala.language.implicitConversions
import scala.concurrent.duration.{Duration, SECONDS}
import grapple.json.{*, given}


/**
 * This can probably all be replaced with https://index.scala-lang.org/cequence-io/openai-scala-client
 */
class ChatClient(chatHistory: ChatHistory) {

  private var actorSystem: ActorSystem = _

  def getResponse(prompt: String): Future[String] = {
    callChatGPT(prompt).transform {
      case Success(response) =>
        var responseText = ""
        try {
          val responseJson = ChatGptResponse.parse(response)
          responseText = responseJson.choices.head.message.content
          chatHistory.addAssistantMessage(responseText)
        }
        catch {
          case nse: NoSuchElementException => println(ChatGptError.parse(response))
          case ex: Exception => throw new IllegalStateException(ex)
        }
        Success(responseText)
      case Failure(cause) => Failure(new IllegalStateException(cause))
    }
  }

  def terminate(): Unit =
    if (actorSystem != null) actorSystem.terminate()

  private def callChatGPT(prompt: String): Future[String] = {
    implicit val system = ActorSystem()
    actorSystem = system

    val request = getRequest(prompt)
    val responseFuture: Future[HttpResponse] = Http().singleRequest(request)

    responseFuture.flatMap { response =>
      response.entity.dataBytes.runFold(ByteString(""))(_ ++ _).map(_.utf8String)
    }
  }

  def getRequest(prompt: String): HttpRequest = {
    val apiKey = ApiConfig.getApiKey
    val uri = getUri(Query())

    chatHistory.addUserMessage(prompt)

    val entity = ApiConfig.getParameters(chatHistory.toString())

    HttpRequest(
      method = POST,
      uri = uri,
      entity = entity
    ).withHeaders(Accept(MediaRange(MediaTypes.`application/xml`)), Authorization(OAuth2BearerToken(apiKey)))
  }

  private def getUri(query: Query): Uri = {
    val host = ApiConfig.getApiHost
    val endpoint = Path(ApiConfig.getApiEndpoint)
    Uri().withScheme("https").withHost(host).withPath(endpoint).withQuery(query)
  }
}

// For simple testing
object ChatClient {
  def main(args: Array[String]): Unit = {
    val prompt = "What is the answer to life the universe and everything?"

    val chatClient = new ChatClient(new ChatHistory())

    chatClient.getResponse(prompt).onComplete {
      case Success(response) => println(response)
      case Failure(ex) => ex.printStackTrace()
    }
  }
}
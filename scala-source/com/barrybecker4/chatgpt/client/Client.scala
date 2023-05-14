package com.barrybecker4.chatgpt.client

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpRequest, HttpResponse, MediaRange, MediaTypes, Uri}
import akka.http.scaladsl.model.HttpMethods.*
import akka.http.scaladsl.model.Uri.{Path, Query}
import akka.http.scaladsl.model.headers.{Accept, Authorization, OAuth2BearerToken}
import akka.stream.ActorMaterializer
import akka.util.ByteString

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}


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

  def main(args: Array[String]): Unit = {
    val prompt = "What is the meaning of life?"

    callChatGPT(prompt).onComplete {
      case Success(response) =>
        println(response)
      case Failure(ex) =>
        println(ex)
    }
  }
}
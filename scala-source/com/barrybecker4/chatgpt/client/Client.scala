package com.barrybecker4.chatgpt.client

import com.typesafe.config.{Config, ConfigFactory}
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


object Client {



  def callChatGPT(prompt: String, config: Config): Future[String] = {
    implicit val system = ActorSystem()

    val host = config.getString("api-host")
    val endpoint = Path(config.getString("api-endpoint"))
    val apiKey = config.getString("api-key")
    val maxTokens = config.getInt("max-tokens")
    val n = config.getInt("n")

    val query = Query(
      "prompt" -> prompt,
      "max_tokens" -> maxTokens.toString,
      "n" -> n.toString,
      "stop" -> "\n",
    )

    val uri = Uri().withHost(host).withPath(endpoint).withQuery(query)

    val request = HttpRequest(
      method = POST,
      uri = uri,
      entity = HttpEntity(ContentTypes.`application/json`, s"""{"prompt": "$prompt"}""")
    ).withHeaders(Accept(MediaRange(MediaTypes.`application/xml`)), Authorization(OAuth2BearerToken(apiKey)))

    val responseFuture: Future[HttpResponse] = Http().singleRequest(request)

    responseFuture.flatMap { response =>
      response.entity.dataBytes.runFold(ByteString(""))(_ ++ _).map(_.utf8String)
    }.andThen { case _ => system.terminate() }
  }

  def main(args: Array[String]): Unit = {
    val prompt = "What is the meaning of life?"
    val config = ConfigFactory.load()

    callChatGPT(prompt, config).onComplete {
      case Success(response) =>
        println(response)
      case Failure(ex) =>
        println(ex)
    }
  }
}
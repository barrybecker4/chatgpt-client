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


/**
 * This can probably all be replaced with https://index.scala-lang.org/cequence-io/openai-scala-client
 */
object Client {

  def callChatGPT(prompt: String, config: Config): Future[String] = {
    implicit val system = ActorSystem()

    val request = getRequest(prompt, config)
    val responseFuture: Future[HttpResponse] = Http().singleRequest(request)

    responseFuture.flatMap { response =>
      response.entity.dataBytes.runFold(ByteString(""))(_ ++ _).map(_.utf8String)
    }.andThen { case _ => system.terminate() }
  }

  def getRequest(prompt: String, config: Config): HttpRequest = {
    val apiKey = config.getString("api_key")
    val query = Query()
    val uri = getUri(query, config)
    println("Query = " + query.toString)
    val model = config.getString("model")
    val max_tokens = config.getInt("max_tokens").toString
    val temperature = config.getDouble("temperature").toString
    val num_completions = config.getInt("num_completions").toString
    val entity = HttpEntity(ContentTypes.`application/json`,
      s"""{
         |"prompt": "$prompt",
         |"model": "$model",
         |"max_tokens": $max_tokens,
         |"temperature": $temperature,
         |"n": $num_completions
      }""".stripMargin)

    HttpRequest(
      method = POST,
      uri = uri,
      entity = entity
    ).withHeaders(Accept(MediaRange(MediaTypes.`application/xml`)), Authorization(OAuth2BearerToken(apiKey)))
  }

  private def getUri(query: Query, config: Config): Uri = {
    val host = config.getString("api_host")
    val endpoint = Path(config.getString("api_endpoint"))
    println("endpoint = " + endpoint.toString)
    Uri().withScheme("https").withHost(host).withPath(endpoint).withQuery(query)
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
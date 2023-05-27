package com.barrybecker4.chatgpt.client

import scala.util.{Failure, Success}
import scala.io.StdIn.readLine
import scala.concurrent.ExecutionContext.Implicits.global


object PsychologistApp {

  def main(args: Array[String]): Unit = {

    val chatClient = new ChatClient(new ChatHistory("You are a psychologist named Dr Eliza."))

    println("I am Dr. Eliza, a psychologist. What can I help you with?")

    var done = false

    while (!done) {
      val userContent = readLine()
      done = isDone(userContent)
      if (!done) {
        chatClient.getResponse(userContent).onComplete {
          case Success(response) => println(response)
          case Failure(ex) => ex.printStackTrace()
        }
      }
    }
    chatClient.terminate()
  }

  private def isDone(content: String): Boolean =
    content.isEmpty || content.contains("done") || content.contains("quit") || content.contains("exit")

}

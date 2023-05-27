package com.barrybecker4.chatgpt.client

import com.barrybecker4.chatgpt.client.json.Personality

import scala.util.{Failure, Success}
import scala.io.StdIn.{readLine, readInt}
import scala.concurrent.ExecutionContext.Implicits.global


object AiPersonalityApp {

  def determinePersonality(): Personality = {
    val personalities = PersonalitiesConfig.getPersonalities
    println("Who should I be? Pick from one of the following.")

    for (i <- personalities.indices) {
      println(s"$i) ${personalities(i).name}")
    }

    val selection = readInt()
    personalities(selection)
  }

  private def isDone(content: String): Boolean = {
    val c = content.toLowerCase()
    c.isEmpty || c == "done" || c == "quit" || c == "exit"
  }

  def main(args: Array[String]): Unit = {

    val personality = determinePersonality()
    val chatClient = new ChatClient(new ChatHistory(personality.prompt))
    println(personality.greeting)

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

}

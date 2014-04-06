package bot

import java.util.Scanner
import collection.immutable.IndexedSeq

import main.Region
import move.PlaceArmiesMove
import move.AttackTransferMove


class BotParser(bot: Bot) {

  val scan = new Scanner(System.in)

  var currentState: BotState = new BotState

  def run() {
    while (scan.hasNextLine) {
      val line = scan.nextLine.trim
      if (!line.isEmpty) {
        handleLine(line)
      }
    }
  }

  def handleLine(line: String): Unit = {
    val parts = IndexedSeq(line.split(" "):_*)
    val command = parts(0)
    command match {
      case "pick_starting_regions" =>
        currentState.setPickableStartingRegions(parts)
        val timeout = parts(1).toLong
        val output = bot
          .getPreferredStartingRegions(currentState, timeout)
          .map(_.id)
          .mkString(" ")
        println(output)
      case "go" if parts.length == 3 =>
        val timeout = parts(2).toLong
        val action = parts(1)
        val moves = action match {
          case "place_armies" =>
            bot.getPlaceArmiesMoves(currentState, timeout)
          case "attack/transfer" =>
            bot.getAttackTransferMoves(currentState, timeout)
        }
        val output = moves.map(_.getString).mkString(",")
        if (!output.isEmpty) {
          println(output)
        } else {
          println("No moves")
        }
      case "settings" if parts.length == 3 =>
        currentState.updateSettings(parts(1), parts(2))
      case "setup_map" =>
        currentState.setupMap(parts)
      case "update_map" =>
        currentState.updateMap(parts)
      case "opponent_moves" =>
        currentState.readOpponentMoves(parts)
      case _ =>
        System.err.println(s"""Unable to parse line "$line"""")
    }
  }
}


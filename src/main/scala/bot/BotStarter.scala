package bot

import collection.immutable.Seq

import main.Region
import main.SuperRegion
import move.AttackTransferMove
import move.PlaceArmiesMove


object BotStarter {
  def main(args: Array[String]) {
    val parser = new BotParser(new BotStarter)
    parser.run()
  }
}


class BotStarter extends Bot {
  override def getPreferredStartingRegions(
    state: BotState,
    timeOut: Long
  ): Seq[Region] = {
    util.Random.shuffle(state.pickableStartingRegions).take(6)
  }

  override def getPlaceArmiesMoves(
    state: BotState,
    timeOut: Long
  ): Seq[PlaceArmiesMove] = {
    val myName = state.playerName
    val armiesToPlacePerRegion = 2
    val myVisibleRegions =
      state.visibleMap.regions.filter(_.ownedByPlayer(myName))
    val deploymentSizes = {
      val completeDeploysCount =
        (state.startingArmies / armiesToPlacePerRegion).toInt
      val rest = state.startingArmies % armiesToPlacePerRegion
      val deploys = Seq.fill(completeDeploysCount)(armiesToPlacePerRegion)
      if (rest > 0)
        deploys :+ rest
      else
        deploys
    }
    val regionsInRandomOrder = util.Random.shuffle(myVisibleRegions)
    def makeMove(spec: (Region, Int)) = {
      val (region, deploySize) = spec
      new PlaceArmiesMove(myName, region, deploySize)
    }
    val moves = regionsInRandomOrder zip deploymentSizes map makeMove
    moves.toIndexedSeq
  }

  override def getAttackTransferMoves(
    state: BotState,
    timeOut: Long
  ): Seq[AttackTransferMove] = {
    val myName = state.playerName
    val armiesToMove = 5
    val myVisibleRegions = state.visibleMap.regions.filter(_.ownedByPlayer(myName))

    def pickMove(fromRegion: Region): Option[AttackTransferMove] = {
      val possibleToRegions = util.Random.shuffle(fromRegion.neighbors)
      // TODO: use find instead of flatMap, so we only create one Move instance
      possibleToRegions.flatMap { toRegion =>
        val eligibleForAttack =
          (toRegion.playerName != myName && fromRegion.armies > 6)
        val eligibleForTransfer =
          (toRegion.playerName == myName && fromRegion.armies > 1)
        if (eligibleForAttack || eligibleForTransfer)
          Some(new AttackTransferMove(myName, fromRegion, toRegion, armiesToMove))
        else
          None
      }.headOption
    }
    val moves = myVisibleRegions flatMap pickMove
    moves.toIndexedSeq
  }
}

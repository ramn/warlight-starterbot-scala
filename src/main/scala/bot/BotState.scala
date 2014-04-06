package bot

import collection.immutable.Seq
import collection.immutable.IndexedSeq
import main.WorldMap
import main.Region
import main.SuperRegion
import move.AttackTransferMove
import move.PlaceArmiesMove
import move.Move


class BotState {
  val fullMap = new WorldMap

  private var myName = ""
  private var myOpponentName = ""
  var visibleMap = new WorldMap
  var myPickableStartingRegions = Seq.empty[Region]
  var opponentMoves = Seq.empty[Move]
  var startingArmies = -1
  var roundNumber = 0

  def updateSettings(key: String, value: String) =
    key match {
      case "your_bot" => myName = value
      case "opponent_bot" => myOpponentName = value
      case "starting_armies" =>
        startingArmies = value.toInt
        roundNumber += 1
    }

  def setupMap(mapInput: Seq[String]) {
    var i = 0
    var regionId = 0
    var superRegionId = 0
    var reward = 0

    val areaType = mapInput(1)
    val areaSpec = mapInput.drop(2)
    areaType match {
      case "super_regions" =>
        val superRegions = areaSpec.grouped(2).map { pair =>
          val superRegionId :: reward :: Nil = pair.toList
          new SuperRegion(superRegionId.toInt, reward.toInt)
        }
        superRegions foreach fullMap.add
      case "regions" =>
        val regions = areaSpec.grouped(2).flatMap { pair =>
          val regionId :: superRegionId :: Nil = pair.toList
          val superRegionOpt = fullMap.getSuperRegion(superRegionId.toInt)
          superRegionOpt.map { superRegion =>
            new Region(regionId.toInt, superRegion)
          }
        }
        regions foreach fullMap.add
      case "neighbors" =>
        val neighborSpecs = areaSpec.grouped(2)
        neighborSpecs.foreach { pair =>
          val regionId = pair(0).toInt
          val neighborIds: Seq[Int] = pair(1).split(",").map(_.toInt).toIndexedSeq
          val neighbors = neighborIds.flatMap(fullMap.getRegion)
          for {
            region <- fullMap.getRegion(regionId)
            neighbor <- neighbors
          } region.addNeighbor(neighbor)
        }
      case unknown =>
        System.err.println(s"Unknown area type: $unknown")
    }
  }

  def setPickableStartingRegions(mapInput: Seq[String]) {
    for (i <- 2 until mapInput.length) {
      try {
        val regionId = mapInput(i).toInt
        val pickableRegionOpt = fullMap.getRegion(regionId)
        pickableRegionOpt foreach addRegion
      } catch {
        case e: Exception =>
          System.err.println("Unable to parse pickable regions " + e.getMessage)
      }
    }
  }

  def updateMap(mapInput: Seq[String]) {
    visibleMap = fullMap.copy
    mapInput.drop(1).grouped(3) foreach { triple =>
      try {
        val regionOpt = visibleMap.getRegion(triple(0).toInt)
        val playerName = triple(1)
        val armies = triple(2).toInt
        regionOpt foreach { region =>
          region.playerName = playerName
          region.armies = armies
        }
      } catch {
        case e: Exception =>
          System.err.println("Unable to parse Map Update " + e.getMessage)
      }
    }
    val unknownRegions = visibleMap.regions.filter(_.playerName == "unknown")
    unknownRegions foreach visibleMap.removeRegion
  }

  def readOpponentMoves(moveInput: Seq[String]) {
    opponentMoves = Seq.empty[Move]
    val input = moveInput.drop(1).iterator
    while (input.hasNext) {
      val playerName = input.next
      val command = input.next
      val move = command match {
        case "place_armies" =>
          val region = visibleMap.getRegion(input.next.toInt).get
          val armies = input.next.toInt
          new PlaceArmiesMove(playerName, region, armies)
        case "attack/transfer" =>
          val fromRegion = visibleOrFullRegion(input.next.toInt)
          val toRegion = visibleOrFullRegion(input.next.toInt)
          val armies = input.next.toInt
          new AttackTransferMove(playerName, fromRegion, toRegion, armies)
      }
      addOpponentMoves(move)
    }
  }

  def visibleOrFullRegion(regionId: Int): Region = {
    visibleMap.getRegion(regionId)
      .orElse(fullMap.getRegion(regionId))
      .get
  }

  def playerName = myName

  def opponentPlayerName = myOpponentName

  def addRegion(region: Region): Unit = {
    myPickableStartingRegions = pickableStartingRegions :+ region
  }

  def pickableStartingRegions = myPickableStartingRegions

  def addOpponentMoves(move: Move): Unit = {
    opponentMoves = opponentMoves :+ move
  }
}

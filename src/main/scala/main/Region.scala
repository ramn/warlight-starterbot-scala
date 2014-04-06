package main


class Region(
  val id: Int,
  val superRegion: SuperRegion,
  playerNameInit: String,
  armiesInit: Int
) {

  def this(id: Int, superRegion: SuperRegion) = {
    this(id, superRegion, "", 0)
  }

  private var myNeighbors = Set.empty[Region]
  var playerName: String = ""
  var armies: Int = 0

  def addNeighbor(region: Region): Unit = {
    myNeighbors += region
  }

  def neighbors = myNeighbors

  def ownedByPlayer(playerName: String): Boolean =
    playerName == this.playerName
}

package move

import main.Region


class PlaceArmiesMove(
  playerName: String,
  region: Region,
  armies: Int) extends Move {

  var illegalMove = ""

  def getString =
    if (illegalMove.isEmpty)
      s"$playerName place_armies ${region.id} $armies"
    else
      s"$playerName illegal_move $illegalMove"
}

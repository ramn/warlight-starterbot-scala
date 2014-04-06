package move

import main.Region


class AttackTransferMove(
  playerName: String,
  fromRegion: Region,
  toRegion: Region,
  armies: Int) extends Move {

  var illegalMove = ""

  def getString =
    if (illegalMove.isEmpty)
      s"$playerName attack/transfer ${fromRegion.id} ${toRegion.id} $armies"
    else
      s"$playerName illegal_move $illegalMove"
}

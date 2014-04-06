package bot

import main.Region
import move.AttackTransferMove
import move.PlaceArmiesMove


trait Bot {
  def getPreferredStartingRegions(state: BotState, timeOut: Long): Seq[Region]
  def getPlaceArmiesMoves(state: BotState, timeOut: Long): Seq[PlaceArmiesMove]
  def getAttackTransferMoves(state: BotState, timeOut: Long): Seq[AttackTransferMove]
}

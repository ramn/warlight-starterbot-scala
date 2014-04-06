package move

import main.Region


trait Move {
  def getString: String
  def illegalMove: String
}

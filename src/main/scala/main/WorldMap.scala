package main


class WorldMap {
  private var myRegions = Set.empty[Region]
  private var mySuperRegions = Set.empty[SuperRegion]

  def add(region: Region): Unit = {
    myRegions += region
  }

  def add(region: SuperRegion): Unit = {
    mySuperRegions += region
  }

  def getRegion(id: Int): Option[Region] = {
    regions find (_.id == id)
  }

  def getSuperRegion(id: Int): Option[SuperRegion] = {
    superRegions find (_.id == id)
  }

  def regions = myRegions

  def superRegions = mySuperRegions

  def removeRegion(region: Region): Unit = {
    myRegions = myRegions - region
  }

  def getMapString: String = {
    val regionsAsStrings = regions.map { r =>
      Seq(r.id, r.playerName, r.armies).mkString(";")
    }
    regionsAsStrings.mkString(" ")
  }

  def copy: WorldMap = {
    val newMap = new WorldMap

    for (sr <- superRegions) {
      newMap.add(new SuperRegion(sr.id, sr.reward))
    }
    for (r <- regions) {
      val superRegionOpt = newMap.getSuperRegion(r.superRegion.id)
      superRegionOpt foreach { superRegion =>
        val newRegion = new Region(r.id, superRegion, r.playerName, r.armies)
        newMap.add(newRegion)
      }
    }
    for (r <- regions) {
      val newRegionOpt = newMap.getRegion(r.id)
      for {
        newRegion <- newRegionOpt
        neighbor <- r.neighbors
        neighborRegion <- newMap.getRegion(neighbor.id)
      } newRegion.addNeighbor(neighborRegion)
    }
    newMap
  }
}

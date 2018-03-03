package group_by.simple;

import scala.collection.mutable.Map;

object Generator {
  def generate(numDays: Int, rowsPerDay: Int): List[Map[String, Any]] = {
    var rows: List[Map[String, Any]] = List[Map[String, Any]]();
    for (i <- 0 until numDays) {
      val day = scala.util.Random.nextInt(100000);
      for (j <- 0 until rowsPerDay) {
        val stockId: Int = scala.util.Random.nextInt(10);
        val stockPrice: Double = scala.util.Random.nextDouble() * 1000;
        rows = rows :+ Map[String, Any]("day" -> day, "stockId" -> stockId, "stockPrice" -> stockPrice);
      }
    }
    return rows;
  }
}

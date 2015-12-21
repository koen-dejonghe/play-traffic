package model

import anorm._
import anorm.SqlParser._
import play.api.db.DB
import play.api.Play.current

case class Celltower (mcc: Int, mnc: Int, cell: Int, area: Int, lat: Double, lon: Double)

object Celltower {

    val celltowerRowParser: RowParser[Celltower] = {
            int("cell_towers.mcc") ~
            int("cell_towers.net") ~
            int("cell_towers.cell") ~
            int("cell_towers.area") ~
            double("cell_towers.lat") ~
            double("cell_towers.lon") map {
                case mcc ~ mnc ~ cell ~ area ~ lat ~ lon =>
                    Celltower(mcc, mnc, cell, area, lat, lon)
            }
    }

    val celltowerParser: ResultSetParser[List[Celltower]] = {
        celltowerRowParser *
    }

    def getAll(mcc: Int, mnc: Int, limit: Int = 0): List[Celltower] = DB.withConnection {
        val limitClause = if (limit == 0) "" else s"limit $limit"
        val sql: SqlQuery = SQL(s"""
           |select *
           |from cell_towers
           |where mcc = $mcc and net = $mnc
           |$limitClause
           |""".stripMargin)

        implicit connection =>
            sql.as(celltowerParser)
    }

    def getOne(mcc: Int, mnc: Int, cell: Int, area: Int): Option[Celltower] = DB.withConnection {
        val sql: SqlQuery = SQL(s"""
            |select *
            |from cell_towers
            |where mcc = $mcc and net = $mnc
            |and cell = $cell and area = $area
            |""".stripMargin)

        implicit connection =>
            val rs: List[Celltower] = sql.as(celltowerParser)
            rs.headOption
    }

    def getRandom(mcc: Int, mnc: Int, count: Int): List[Celltower] = DB.withConnection {
        val sql: SqlQuery = SQL(s"""
                                   |select *
                                   |from cell_towers
                                   |where mcc = $mcc and net = $mnc
                                   |order by random()
                                   |limit $count
                                   |""".stripMargin)

        implicit connection =>
            sql.as(celltowerParser)
    }



}

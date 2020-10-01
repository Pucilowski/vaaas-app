package io.vaaas

import cats.implicits._
import doobie.Meta
import io.circe.Json
import io.circe.parser._
import org.postgresql.util.PGobject

trait DoobieMappers {

  implicit val jsonMeta: Meta[Json] = Meta.Advanced
    .other[PGobject]("json")
    .timap[Json](a => parse(a.getValue).leftMap[Json](e => throw e).merge)(a => {
      val o = new PGobject
      o.setType("json")
      o.setValue(a.noSpaces)
      o
    })

}

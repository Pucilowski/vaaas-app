package io.vaaas.common.protobuf

import java.time.{Duration, Instant}

import scalapb.TypeMapper
import shapeless._

import scala.util.Try

trait AnyValTypeMapper {

  implicit def anyValTypeMapper[V, U](implicit ev: V <:< AnyVal,
                                      V: Unwrapped.Aux[V, U]): TypeMapper[U, V] = {
    val _ = ev
    TypeMapper[U, V](V.wrap)(V.unwrap)
  }

}

trait CaseClassTypeMapper {

  implicit def caseClassTypeMapper[A, B, Repr <: HList](
    implicit aGen: Generic.Aux[A, Repr],
    bGen: Generic.Aux[B, Repr]
  ): TypeMapper[A, B] =
    TypeMapper { x: A =>
      bGen.from(aGen.to(x))
    } { x =>
      aGen.from(bGen.to(x))
    }

}

trait BaseTypeMapper {

  implicit val bigDecimal: TypeMapper[String, BigDecimal] =
    TypeMapper[String, BigDecimal] { x =>
      val value = if (x.isEmpty) "0" else x
      BigDecimal(value)
    }(_.toString())

  implicit val instant: TypeMapper[Long, Instant] =
    TypeMapper[Long, Instant](Instant.ofEpochMilli)(_.toEpochMilli)

  implicit val instantOpt: TypeMapper[Long, Option[Instant]] =
    instant.map2(i => if (i.toEpochMilli == 0) None else Some(i))(
      _.getOrElse(Instant.ofEpochMilli(0))
    )

  implicit val duration: TypeMapper[String, java.time.Duration] =
    TypeMapper[String, Duration] { s =>
      Try(Duration.parse(s)).getOrElse(Duration.ZERO)
    }(_.toString)

}

trait TypeMapperInstances extends BaseTypeMapper with AnyValTypeMapper with CaseClassTypeMapper {

  implicit class TypeMapperOps[A <: Any](a: A) {
    def toCustom[B](implicit tm: TypeMapper[A, B]): B = tm.toCustom(a)
    def toBase[B](implicit tm: TypeMapper[B, A]): B = tm.toBase(a)
  }

}

object TypeMappers extends TypeMapperInstances {

//  implicit val money: TypeMapper[BigDecimal, Money] =
//    bigDecimal.map2(Money(_, ???))(_.amount)
}

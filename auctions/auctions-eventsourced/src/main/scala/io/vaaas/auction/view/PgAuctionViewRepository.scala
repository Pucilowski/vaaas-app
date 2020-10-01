package io.vaaas.auction.view

import java.time.Instant

import cats.implicits._
import cats.syntax._
import cats.Monad
import cats.effect.Bracket
import cats.implicits._
import doobie._
import doobie.implicits._
import doobie.util.transactor.Transactor
import io.circe.parser._
import io.circe.{Decoder, Encoder, Json}
import io.vaaas.auction.json.AuctionJsonCodecs
import io.vaaas.auction.{AuctionKey, AuctionStatus}
import io.vaaas.user.UserId
import org.postgresql.util.PGobject

class PgAuctionViewRepository[F[_] : Monad : Bracket[?[_], Throwable]]
(
  transactor: Transactor[F],
  tableName: String = "auctions"
) extends AuctionViewRepository[F] with AuctionJsonCodecs /*with LegacyInstantMetaInstance*/ {

  implicit val logHandler = LogHandler.jdkLogHandler

  object Sql {
    val fieldsString = "auction_id, seller_id, ends_at, reserve, reserve_ccy, targets, status, bids, version"
    val fields = Fragment.const(fieldsString)

    def queryView(id: AuctionKey) =
      (fr"SELECT " ++ fields ++ fr" FROM " ++ Fragment.const(tableName) ++
        fr"WHERE auction_id = ${id.value.toString};")
        .query[AuctionView]

    val setViewQuery =
      s"""INSERT INTO $tableName
    (auction_id, seller_id, ends_at, reserve, reserve_ccy, targets, status, bids, version)
    VALUES (?,?,?,?,?,?,?,?,?)
    ON CONFLICT (auction_id)
    DO UPDATE SET
     targets = EXCLUDED.targets,
     status = EXCLUDED.status,
     bids = EXCLUDED.bids,
     version = EXCLUDED.version;"""

    def queryAll() =
      (fr"SELECT " ++ Sql.fields ++ fr" FROM " ++ Fragment.const(tableName))
        .query[AuctionView]

    def queryEnded(now: Instant) =
      (fr"SELECT auction_id FROM " ++ Fragment.const(tableName) ++
        fr"WHERE status = ${AuctionStatus.Live: AuctionStatus} AND ends_at < $now;")
        .query[AuctionKey]
  }

  implicit val jsonMeta: Meta[Json] =
    Meta.Advanced
      .other[PGobject]("json")
      .timap[Json](a => parse(a.getValue).leftMap[Json](e => throw e).merge)(a => {
        val o = new PGobject
        o.setType("json")
        o.setValue(a.noSpaces)
        o
      })

  implicit val auctionKeyMeta: Meta[AuctionKey] =
    Meta[String].timap(AuctionKey.apply)(_.value)
  //    Meta[String].timap(s => AuctionKey.apply(UUID.fromString(s)))(_.value.toString)

  //  implicit val instantMeta: Meta[Instant] =
  //    Meta[Timestamp].timap(_.toInstant)(Timestamp.from)

  implicit val auctionStatusMeta: Meta[AuctionStatus] =
    Meta[String].timap(AuctionStatus.withName)(_.entryName)

  implicit val bidTargetsMeta: Meta[List[BidTargetView]] = jsonMeta.timap(
    j => Decoder[List[BidTargetView]].decodeJson(j).right.get
  )(s => Encoder[List[BidTargetView]].apply(s))

  implicit val userBidsMeta: Meta[Map[UserId, UserBidView]] = jsonMeta.timap(
    j => Decoder[Map[UserId, UserBidView]].decodeJson(j).right.get
  )(s => {
    val encoded = Encoder[Map[UserId, UserBidView]].apply(s)

    println("ENCODED: " + encoded.toString())

    encoded
  })

  def get(auctionId: AuctionKey): F[Option[AuctionView]] =
    Sql.queryView(auctionId).option.transact(transactor)

  def set(view: AuctionView): F[Unit] =
    Update[AuctionView](Sql.setViewQuery, logHandler0 = LogHandler.jdkLogHandler)
      .run(view).transact(transactor).void

  def all(): fs2.Stream[F, AuctionView] =
    Sql.queryAll().stream.transact(transactor)

  def ended(now: Instant): fs2.Stream[F, AuctionKey] =
    Sql.queryEnded(now).stream.transact(transactor)

  def createTable: F[Unit] = createTableQuery.transact(transactor).void

  private val createTableQuery = (fr"""
    CREATE TABLE IF NOT EXISTS """ ++ Fragment.const(tableName) ++
    fr""" (
    auction_id    text      NOT NULL PRIMARY KEY,
    seller_id     text      NOT NULL,
    ends_at       timestamptz NOT NULL,
    reserve       bigint      NOT NULL,
    reserve_ccy   text      NOT NULL,
    targets       json      NOT NULL,
    status        text      NOT NULL,
    bids          json      NOT NULL,
    version       bigint    NOT NULL
    );
  """).update.run

}

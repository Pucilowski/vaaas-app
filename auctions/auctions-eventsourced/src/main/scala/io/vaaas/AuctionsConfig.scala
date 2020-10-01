package io.vaaas

import scala.concurrent.duration.FiniteDuration

case class AuctionsConfig(
  cluster: ActorSystemName,
  postgres: PostgresConfig,
  postgresJournals: PostgresJournals
)

final case class PostgresConfig(contactPoints: String, port: Int, database: String, username: String, password: String)

final case class ActorSystemName(systemName: String)

final case class PostgresJournals(auction: PostgresEventJournalSettings)

final case class PostgresEventJournalSettings(tableName: String, pollingInterval: FiniteDuration)

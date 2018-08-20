lazy val root = (project in file("."))
  .settings(name := "online-auction-scala")
  .aggregate(itemApi, itemImpl,
    biddingApi, biddingImpl,
    userApi, userImpl,
    searchApi, searchImpl,
    webGateway)
  .settings(commonSettings: _*)

organization in ThisBuild := "com.example"

// the Scala version that will be used for cross-compiled libraries
scalaVersion in ThisBuild := "2.12.4"

version in ThisBuild := "1.0.0-SNAPSHOT"

val playJsonDerivedCodecs = "org.julienrf" %% "play-json-derived-codecs" % "4.0.0"
val macwire = "com.softwaremill.macwire" %% "macros" % "2.2.5" % "provided"
val scalaTest = "org.scalatest" %% "scalatest" % "3.0.1" % "test"

lazy val cinnamonDependencies = Seq(
  // Use Coda Hale Metrics and Lagom instrumentation
  Cinnamon.library.cinnamonCHMetrics,
  Cinnamon.library.cinnamonLagom
)

lazy val security = (project in file("security"))
  .settings(commonSettings: _*)
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslApi,
      lagomScaladslServer % Optional,
      playJsonDerivedCodecs,
      scalaTest
    )
  )

lazy val itemApi = (project in file("item-api"))
  .enablePlugins(Cinnamon)
  .settings(commonSettings: _*)
  .settings(
    resolvers += Cinnamon.resolver.commercial,
    libraryDependencies ++= Seq(
      lagomScaladslApi,
      playJsonDerivedCodecs
    ) ++ cinnamonDependencies
  )
  .dependsOn(security)

lazy val itemImpl = (project in file("item-impl"))
  .settings(commonSettings: _*)
  .enablePlugins(LagomScala, SbtReactiveAppPlugin, Cinnamon)
  .settings(
    resolvers += Cinnamon.resolver.commercial,
    libraryDependencies ++= Seq(
      lagomScaladslPersistenceCassandra,
      lagomScaladslTestKit,
      lagomScaladslKafkaBroker,
      Cinnamon.library.cinnamonCHMetrics,
      Cinnamon.library.cinnamonLagom,
      "com.datastax.cassandra" % "cassandra-driver-extras" % "3.0.0",
      macwire,
      scalaTest
    )
  )
  .settings(lagomForkedTestSettings: _*)
  .dependsOn(itemApi, biddingApi)

lazy val biddingApi = (project in file("bidding-api"))
  .enablePlugins(Cinnamon)
  .settings(commonSettings: _*)
  .settings(
    resolvers += Cinnamon.resolver.commercial,
    libraryDependencies ++= Seq(
      lagomScaladslApi,
      Cinnamon.library.cinnamonPrometheusHttpServer,
      Cinnamon.library.cinnamonJvmMetricsProducer,
      playJsonDerivedCodecs
    ) ++ cinnamonDependencies
  )
  .dependsOn(security)

lazy val biddingImpl = (project in file("bidding-impl"))
  .settings(commonSettings: _*)
  .enablePlugins(LagomScala, SbtReactiveAppPlugin, Cinnamon)
  .dependsOn(biddingApi, itemApi)
  .settings(
    resolvers += Cinnamon.resolver.commercial,
    libraryDependencies ++= Seq(
      macwire,
      scalaTest
    ) ++ cinnamonDependencies,
    maxErrors := 10000
  )

lazy val searchApi = (project in file("search-api"))
  .enablePlugins(Cinnamon)
  .settings(commonSettings: _*)
  .settings(
    resolvers += Cinnamon.resolver.commercial,
    libraryDependencies ++= Seq(
      Cinnamon.library.cinnamonPrometheusHttpServer,
      Cinnamon.library.cinnamonJvmMetricsProducer,
      lagomScaladslApi,
      playJsonDerivedCodecs
    ) ++ cinnamonDependencies
  )
  .dependsOn(security)

lazy val searchImpl = (project in file("search-impl"))
  .settings(commonSettings: _*)
  .enablePlugins(LagomScala, SbtReactiveAppPlugin, Cinnamon)
  .dependsOn(searchApi, itemApi, biddingApi)
  .settings(
    resolvers += Cinnamon.resolver.commercial,
    libraryDependencies ++= Seq(
      lagomScaladslPersistenceCassandra,
      lagomScaladslKafkaClient,
      lagomScaladslTestKit,
      macwire,
      scalaTest
    ) ++ cinnamonDependencies
  )

lazy val transactionApi = (project in file("transaction-api"))
  .enablePlugins(Cinnamon)
  .settings(commonSettings: _*)
  .dependsOn(itemApi)
  .settings(
    resolvers += Cinnamon.resolver.commercial,
    libraryDependencies ++= Seq(
      lagomScaladslApi,
      Cinnamon.library.cinnamonPrometheusHttpServer,
      Cinnamon.library.cinnamonJvmMetricsProducer,
      playJsonDerivedCodecs
    ) ++ cinnamonDependencies ,
    EclipseKeys.skipProject := true
  )
  .dependsOn(security)

lazy val transactionImpl = (project in file("transaction-impl"))
  .settings(commonSettings: _*)
  // .enablePlugins(LagomScala)
  .enablePlugins(Cinnamon)
  .dependsOn(transactionApi, biddingApi)
  .settings(
    resolvers += Cinnamon.resolver.commercial,
    libraryDependencies ++= Seq(
      lagomScaladslPersistenceCassandra,
      lagomScaladslTestKit,
      macwire,
      scalaTest
    ) ++ cinnamonDependencies,
    EclipseKeys.skipProject := true
  )

lazy val userApi = (project in file("user-api"))
  .enablePlugins(Cinnamon)
  .settings(commonSettings: _*)
  .settings(
    resolvers += Cinnamon.resolver.commercial,
    libraryDependencies ++= Seq(
      Cinnamon.library.cinnamonPrometheusHttpServer,
      Cinnamon.library.cinnamonJvmMetricsProducer,
      lagomScaladslApi,
      playJsonDerivedCodecs
    ) ++ cinnamonDependencies
  )
  .dependsOn(security)

lazy val userImpl = (project in file("user-impl"))
  .settings(commonSettings: _*)
  .enablePlugins(LagomScala, SbtReactiveAppPlugin, Cinnamon)
  .dependsOn(userApi)
  .settings(
    resolvers += Cinnamon.resolver.commercial,
    libraryDependencies ++= Seq(
      lagomScaladslPersistenceCassandra,
      macwire,
      scalaTest
    ) ++ cinnamonDependencies
  )

lazy val webGateway = (project in file("web-gateway"))
  .settings(commonSettings: _*)
  .enablePlugins(PlayScala, LagomPlay, SbtReactiveAppPlugin, Cinnamon)
  .dependsOn(biddingApi, itemApi, userApi)
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslServer,
      macwire,
      scalaTest,

      "org.ocpsoft.prettytime" % "prettytime" % "3.2.7.Final",

      "org.webjars" % "foundation" % "6.2.3",
      "org.webjars" % "foundation-icon-fonts" % "d596a3cfb3"
    ) ++ cinnamonDependencies,
    EclipseKeys.preTasks := Seq(compile in Compile),
    httpIngressPaths := Seq("/")
  )

def commonSettings: Seq[Setting[_]] = Seq(
)

lagomCassandraCleanOnStart in ThisBuild := false

// ------------------------------------------------------------------------------------------------

// register 'elastic-search' as an unmanaged service on the service locator so that at 'runAll' our code
// will resolve 'elastic-search' and use it. See also com.example.com.ElasticSearch
lagomUnmanagedServices in ThisBuild += ("elastic-search" -> "http://127.0.0.1:9200")

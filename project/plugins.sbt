//
// Copyright (C) 2016 Lightbend Inc. <https://www.lightbend.com>
//

// The Lagom plugin
addSbtPlugin("com.lightbend.lagom" % "lagom-sbt-plugin" % "1.4.7")
// Needed for importing the project into Eclipse
addSbtPlugin("com.typesafe.sbteclipse" % "sbteclipse-plugin" % "5.2.4")
// Platform Tooling plugin
addSbtPlugin("com.lightbend.rp" % "sbt-reactive-app" % "1.1.0")
// The Cinnamon Telemetry plugin
addSbtPlugin("com.lightbend.cinnamon" % "sbt-cinnamon" % "2.10.0")
// Credentials and resolver to download the Cinnamon Telemetry libraries
credentials += Credentials(Path.userHome / ".lightbend" / "commercial.credentials")
resolvers += Resolver.url("lightbend-commercial", url("https://repo.lightbend.com/commercial-releases"))(Resolver.ivyStylePatterns)

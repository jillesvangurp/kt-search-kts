#!/usr/bin/env kotlin

@file:Repository("https://maven.tryformation.com/releases")
@file:Repository(" https://repo.maven.apache.org/maven2/")
@file:Repository("https://jitpack.io")
@file:DependsOn("com.github.jillesvangurp:kt-search-kts:1.0.7")

import com.jillesvangurp.ktsearch.DEFAULT_PRETTY_JSON
import com.jillesvangurp.ktsearch.kts.addClientParams
import com.jillesvangurp.ktsearch.kts.searchClient
import com.jillesvangurp.ktsearch.listSnapshots
import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.required
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.encodeToString

val parser = ArgParser("script")
val searchClientParams = parser.addClientParams()

val snapshotRegistryId by parser.option(
    type = ArgType.String,
    fullName = "snapshot-registry",
    shortName = "s",
    description = "Snapshot registry id"
).required()

parser.parse(args)

val client = searchClientParams.searchClient

runBlocking {
    println(DEFAULT_PRETTY_JSON.encodeToString(client.listSnapshots(snapshotRegistryId)))
}

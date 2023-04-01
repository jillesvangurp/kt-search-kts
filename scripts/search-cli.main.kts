#!/usr/bin/env kotlin

@file:Repository("https://maven.tryformation.com/releases")
@file:Repository(" https://repo.maven.apache.org/maven2/")
@file:Repository("https://jitpack.io")
@file:DependsOn("com.github.jillesvangurp:kt-search-kts:1.0.7")

import com.jillesvangurp.ktsearch.*
import com.jillesvangurp.ktsearch.kts.addClientParams
import com.jillesvangurp.ktsearch.kts.searchClient
import com.jillesvangurp.searchdsls.querydsl.matchAll
import com.jillesvangurp.searchdsls.querydsl.queryString
import com.jillesvangurp.searchdsls.querydsl.simpleQueryString
import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.default
import kotlinx.cli.required
import kotlinx.coroutines.runBlocking

// ArgParser is included by kt-search-kts to allow you to configure the search endpoint
val parser = ArgParser("script")
// this adds the params for configuring search end point
val searchClientParams = parser.addClientParams()

val indexName by parser.option(
    type = ArgType.String,
    fullName = "index",
    shortName = "i",
    description = "Index or alias"
).default("")

val field by parser.option(
    type = ArgType.String,
    fullName = "field",
    shortName = "f",
    description = "Field name"
).default("title")

val textQuery by parser.option(
    type = ArgType.String,
    fullName = "query",
    shortName = "q",
    description = "Query"
).default("*")

parser.parse(args)

// extension function in kt-search-kts that uses the params
val client = searchClientParams.searchClient

// now use the client as normally in a runBlocking block (creates a co-routine)
runBlocking {
    client.search(indexName) {
        trackTotalHits = "true" // can have non boolean values
        query = simpleQueryString(textQuery,field)
        resultSize = 50
    }.also {
        println("Found ${it.total} results.")
    }.searchHits.forEach {
        // note we can't deserialize using kotlinx-serialization here because
        // we need the compiler plugin to be able to add a data class with @Serializable
        // see this issue for a workaround https://youtrack.jetbrains.com/issue/KT-47384
        // alternatively, you could create a separate library and push that somewhere
        // and then pull in the -jvm jar file to get the model class on the classpath
        println(it.source?.toString())
    }
}

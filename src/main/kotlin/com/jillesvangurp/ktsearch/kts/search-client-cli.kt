package com.jillesvangurp.ktsearch.kts

import com.jillesvangurp.ktsearch.KtorRestClient
import com.jillesvangurp.ktsearch.SearchClient
import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.default

interface SearchClientParams {
    val host: String
    val port: Int
    val user: String?
    val password: String?
    val apiKey: String?
    val ssl: Boolean
    val logApiCalls: Boolean

}

val SearchClientParams.searchClient get() = SearchClient(
    // our docker test cluster runs on port 9999
    // you may want to use 9200 with your own cluster
    KtorRestClient(
        host = host,
        port = port,
        user = user,
        password = password,
        elasticApiKey = apiKey,
        https = ssl,
        logging = logApiCalls
    )
)

fun ArgParser.addClientParams(): SearchClientParams {
    val parser = this
    return object: SearchClientParams {
        override val host by parser.option(ArgType.String, shortName = "a", fullName = "host", description = "Host")
            .default("localhost")
        override val port by parser.option(ArgType.Int, shortName = "p", fullName = "port", description = "Port").default(9200)
        override val user by parser.option(
            ArgType.String,
            fullName = "user",
            description = "Basic authentication user name if using with cloud hosting"
        )
        override val password by parser.option(
            ArgType.String,
            fullName = "password",
            description = "Basic authentication user name if using with cloud hosting"
        )
        override val apiKey by parser.option(
            ArgType.String,
            fullName = "apiKey",
            description = "Elasticsearch apiKey"
        )
        override val ssl by parser.option(ArgType.Boolean, fullName = "protocol", description = "Use https if true")
            .default(false)
        override val logApiCalls by parser.option(ArgType.Boolean, fullName = "log-api-calls", description = "Turn on logging for http traffic")
            .default(false)
    }

}


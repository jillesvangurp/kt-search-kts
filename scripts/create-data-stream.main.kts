#!/usr/bin/env kotlin

@file:Repository("https://maven.tryformation.com/releases")
@file:Repository(" https://repo.maven.apache.org/maven2/")
@file:Repository("https://jitpack.io")
@file:DependsOn("com.github.jillesvangurp:kt-search-kts:1.0.9")

import com.jillesvangurp.jsondsl.withJsonDsl
import com.jillesvangurp.ktsearch.*
import com.jillesvangurp.ktsearch.kts.addClientParams
import com.jillesvangurp.ktsearch.kts.searchClient
import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.default
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Clock
import kotlin.time.Duration.Companion.days

// ArgParser is used by kt-search-kts to allow you to configure the server
val parser = ArgParser("script")
val searchClientParams = parser.addClientParams()

val prefix: String by parser.option(ArgType.String, fullName = "prefix").default("applogs")
val hotRollOverGb: Int by parser.option(ArgType.Int, fullName = "hotRollOverGb").default(1)

val numberOfReplicas: Int by parser.option(ArgType.Int, fullName = "numberOfReplicas").default(1)
val numberOfShards: Int by parser.option(ArgType.Int, fullName = "numberOfShards").default(1)
val warmMinAgeDays: Int by parser.option(ArgType.Int, fullName = "warmMinAgeDays").default(1)

// ilm settings
val configureIlm: Boolean by parser.option(
    ArgType.Boolean,
    fullName = "configureIlm",
    description = "Configures index lifecycle management. This feature is elasticsearch only. On opensearch use state management"
).default(false)
val deleteMinAgeDays: Int by parser.option(ArgType.Int, fullName = "deleteMinAgeDays").default(1)
val warmShrinkShards: Int by parser.option(ArgType.Int, fullName = "warmShrinkShards").default(1)
val warmSegments: Int by parser.option(ArgType.Int, fullName = "warmSegments").default(1)

parser.parse(args)

val client = searchClientParams.searchClient

runBlocking {
    if(client.dataStreamExists(prefix)) {
        println("Data stream $prefix already exists")
        System.exit(1)
    }
    if(configureIlm) {
        client.setIlmPolicy("$prefix-ilm-policy") {
            hot {
                actions {
                    rollOver(hotRollOverGb)
                }
            }
            warm {
                minAge(warmMinAgeDays.days)
                actions {
                    shrink(warmShrinkShards)
                    forceMerge(warmSegments)
                }
            }
            delete {
                minAge(deleteMinAgeDays.days)
                actions {
                    delete()
                }
            }
        }
    }
    // using component templates is a good idea
    client.updateComponentTemplate("$prefix-template-settings") {
        settings {
            replicas = numberOfReplicas
            shards = numberOfShards
            put("index.lifecycle.name", "$prefix-ilm-policy")
        }
    }
    client.updateComponentTemplate("$prefix-template-mappings") {
        dynamicTemplate("keywords") {
            match = "*"
            // this works on the mdc and context fields where set turn dynamic to true
            mapping("keyword")
        }
        mappings(false) {
            text("text")
            text("message") {
                fields {
                    keyword("keyword")
                }
            }
            date("@timestamp")
            keyword("thread")
            keyword("level")
            keyword("contextName")
            objField("mdc", dynamic = "true") {
            }
            objField("context", dynamic = "true") {
            }
        }
        meta {
            put("created_by","kt-search-logback-appender")
            put("created_at", Clock.System.now().toString())
        }
    }
    // now create the template
    client.createIndexTemplate("$prefix-template") {
        indexPatterns = listOf("$prefix*")
        // make sure to specify an empty object for data_stream
        dataStream = withJsonDsl {
            // the elastic docs are a bit vague on what goes here
        }
        // make sure we outrank elastics own stuff
        priority=300
        composedOf = listOf("$prefix-template-settings", "$prefix-template-mappings")
    }

    // create the data stream
    client.createDataStream(prefix)
    println("data stream $prefix created")
}

suspend fun SearchClient.dataStreamExists(name: String): Boolean {
    val response = restClient.get {
        path(name)
    }
    val e = response.exceptionOrNull()
    return if(e!=null) {
        if(e is RestException) {
            if(e.status == 404) {
                false
            } else {
                throw e
            }
        } else {
            throw e
        }
    } else {
        true
    }
}
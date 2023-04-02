[![](https://jitpack.io/v/jillesvangurp/kt-search-kts.svg)](https://jitpack.io/#jillesvangurp/kt-search-kts)

Simple helper library for [kt-search](https://github.com/jillesvangurp/kt-search) that enables 
scripting with Kotlin Scripts (kts)

## Pre-requisites

You need kotlin installed on your system. It's available from most linux package managers, the snap store, sdkman, home brew (mac), etc. It of course needs a jvm as well. 

## Adding kt-search-kts to your script

First, add the dependency and the maven repositories in your script (make sure it has the `.main.kts` ending):

```kotlin
#!/usr/bin/env kotlin

@file:Repository("https://maven.tryformation.com/releases")
@file:Repository(" https://repo.maven.apache.org/maven2/")
@file:Repository("https://jitpack.io")
@file:DependsOn("com.github.jillesvangurp:kt-search-kts:1.0.7")

import com.jillesvangurp.ktsearch.ClusterStatus
import com.jillesvangurp.ktsearch.clusterHealth
import com.jillesvangurp.ktsearch.kts.addClientParams
import com.jillesvangurp.ktsearch.kts.searchClient
import com.jillesvangurp.ktsearch.root
import kotlinx.cli.ArgParser
import kotlinx.coroutines.runBlocking
```

This will transitively pull in `kt-search` and `kotlinx-cli` and everything else needed for those.

Then use the `kotlinx-cli` parser to load the search client parameters and create a 
search client with convenient extension functions:

```kotlin
val parser = ArgParser("script")
val searchClientParams = parser.addClientParams()
parser.parse(args)

val client: SearchClient = searchClientParams.searchClient
```

After this, you can use the client as normally via a `runBlocking` block (the client functions are suspending).

```kotlin
runBlocking {
    client.root().let { rootResponse ->
        println(
            """
                Cluster name: ${rootResponse.clusterName}
                Search Engine distribution: ${rootResponse.version.distribution}
                Version: ${rootResponse.version.number}
            """.trimIndent()
        )
    }
}
```

Finally, you can invoke your kts script from the command line if you add the shebang at the top:

```bash
# addClientParams adds a few default parameters and you can add your own as well
./my-script.main.kts --help
# this configures the client to use localhost:9200 (default of course)
./my-script.main.kts --host localhost --port 9200
```

See the `scripts` directory for complete examples. These serve as examples. And you will probably want to customize your own scripts.

Happy scripting!

## Some ideas for scripts

All of these things are supported by kt-search and easy to use.

- check cluster status
- manipulate cluster settings
- take and restore snapshots
- bulk index some content
- create some index templates and a data stream
- create indices and manage index aliases
- introspect documents with some scripts
- run some queries
- use cron jobs to do any of the above
- etc. 

## Documentation

The [kt-search manual](https://jillesvangurp.github.io/kt-search/manual) has sections on scripting and setting up jupyter. It also explains the basics of using it to search and index documents, and use all of the other features.
- 

## Support and Community

Please file issues if you find any or have any reasonable suggestions for changes. 

Within reason, I can advice and help with simple issues. Beyond that, I can offer my services as a consultant as well if you need some more help with getting started or just using Elasticsearch/Opensearch in general with any tech stack. I can help with discovery projects, trainings, architecture analysis, query and mapping optimizations, or just generally help you get the most out of your search setup and your product roadmap.

You can reach me via the issue tracker and I also lurk in the amazing [Kotlin Slack](https://kotlinlang.org/community/), [Elastic Slack](https://www.elastic.co/blog/join-our-elastic-stack-workspace-on-slack), and [Search Relevancy Slack](https://opensourceconnections.com/blog/2021/07/06/building-the-search-community-with-relevance-slack/) communities. And I have a [website](https://www.jillesvangurp.com).

[![](https://jitpack.io/v/jillesvangurp/kt-search-kts.svg)](https://jitpack.io/#jillesvangurp/kt-search-kts)

Simple helper library for [kt-search](https://github.com/jillesvangurp/kt-search) that enables 
scripting with Kotlin Scripts (kts)

## Pre-requisites

You need kotlin 1.7 installed on your system. It's available from most linux package managers, the snap store, home brew (mac), etc. It of course needs a jvm as well. 

## Adding kt-search-kts to your script

First, add the dependency and the maven repositories in your script (make sure it has the `.main.kts` ending):

```kotlin
#!/usr/bin/env kotlin

@file:Repository("https://jitpack.io")
@file:Repository("https://maven.tryformation.com/releases")
// look up latest version number via jitpack and the github releases.
@file:DependsOn("com.github.jillesvangurp:kt-search-kts:0.1.x")
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
[![](https://jitpack.io/v/jillesvangurp/kt-search-kts.svg)](https://jitpack.io/#jillesvangurp/kt-search-kts)

Simple helper library for [kt-search](https://github.com/jillesvangurp/kt-search) that enables 
scripting with Kotlin Scripts (kts)

## Pre-requisites

You need kotlin 1.7 installed on your system. It's available from most linux package managers, the snap store, home brew (mac), etc. It of course needs a jvm as well. 

## Adding kt-search-kts to your script

First, add the dependency and the maven repositories in your script (make sure it has the `.main.kts` ending):

```kotlin
@file:Repository("https://jitpack.io")
@file:Repository("https://maven.tryformation.com/releases")
@file:DependsOn("com.github.jillesvangurp:kt-search-kts:0.1.2")
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

See the `scripts` directory for a complete example.

Happy scripting!

## Some ideas for scripts

- check cluster status
- manipulate cluster settings
- take and restore snapshots
- bulk index some content
- introspect documents with some scripts
- run some queries
- use cron jobs to do any of the above
- etc. 
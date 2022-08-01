Simple  library that combines kt-search and kotlinx-cli to be able
to create kts scripts and reuse the client initialization without 
a lot of boiler-plate.

## Adding kt-search-kts to your script

First, add the dependency and the maven repositories:

```kotlin
@file:Repository("https://jitpack.io")
@file:Repository("https://maven.tryformation.com/releases")
@file:DependsOn("com.github.jillesvangurp:kt-search-kts:0.1.2")
```

This will transitively pull in `kt-search` and `kotlinx-cli`.

Then use the `kotlinx-cli` to load the search client parameters:

```kotlin
val parser = ArgParser("script")
val searchClientParams = parser.addClientParams()
parser.parse(args)

val client: SearchClient = searchClientParams.searchClient
```

After this, you can use the client as normally via a `runBlocking` block (the client functions are suspending).

See the scripts directory for a complete example.

Happy scripting!
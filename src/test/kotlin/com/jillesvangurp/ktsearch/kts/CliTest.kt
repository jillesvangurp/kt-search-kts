package com.jillesvangurp.ktsearch.kts

import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlinx.cli.ArgParser
import org.junit.jupiter.api.Test

class CliTest {

    @Test
    fun `should return search client`() {
        val p = ArgParser("test")
        val searchClientParams = p.addClientParams()
        // add your other parameters as needed and parse
        p.parse(arrayOf("--port","9999"))

        searchClientParams.port shouldBe 9999

        val client = searchClientParams.searchClient
        // should initialize without fuss
        client shouldNotBe null
    }
}
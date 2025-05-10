package com.cashngun

import java.io.File
import kotlin.test.Test
import kotlin.random.Random
import kotlin.test.assertEquals

class IntegrationTest {
  @Test
  fun `should match output given input`() {
    val testInputs = List(100) { listOf(1,2) }.flatten().listIterator()
    val seed = Random(42)

    val output = captureGameOutput(seed, testInputs)
    val expectedOutput = ""//File("src/test/resources/expected_output.txt").readText()

    assertEquals(expectedOutput, output)
  }
}
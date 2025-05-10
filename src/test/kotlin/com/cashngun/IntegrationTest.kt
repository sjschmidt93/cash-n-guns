package com.cashngun

import kotlin.test.Test
import kotlin.random.Random
import kotlin.test.assertEquals
import java.io.File

class IntegrationTest {
  @Test
  fun `seeded game output should match expected output`() {
    val testInputs = List(1000) { listOf(1,2) }.flatten().listIterator()
    val seed = Random(1)

    val output = captureGameOutput(seed, testInputs)
    File("src/test/resources/actual_output.txt").writeText(output)
    val expectedOutput = File("src/test/resources/expected_output.txt").readText()

    assertEquals(expectedOutput, output)
  }
}
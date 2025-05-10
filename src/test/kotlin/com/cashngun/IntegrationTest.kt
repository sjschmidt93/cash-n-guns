package com.cashngun

import kotlin.test.Test
import kotlin.random.Random
import kotlin.test.assertEquals
import java.io.File
import kotlin.test.assertNotEquals

class IntegrationTest {
  @Test
  fun `seeded game output should match expected output`() {
    val seed = Random(42)

    val output = gameLoopTest(seed)
    File("src/test/resources/actual_output.txt").writeText(output)
    val expectedOutput = File("src/test/resources/expected_output.txt").readText()

    assertEquals(expectedOutput, output)
  }

  @Test
  fun `unseeded game runs should produce different results`() {
    val firstRun = gameLoopTest(null)
    val secondRun = gameLoopTest(null)
    
    assertNotEquals(firstRun, secondRun)
  }
}
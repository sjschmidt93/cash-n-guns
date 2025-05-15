package com.cashngun

import kotlin.test.Test
import kotlin.random.Random
import kotlin.test.assertEquals
import java.io.File
import kotlin.test.assertNotEquals

class RegressionTest {
  private val PATH = "src/test/resources/regression_tests/"

  @Test
  fun `seeded game outputs should match expected output`() {
    val seeds = (1..10).map { Random(it) }

    seeds.forEachIndexed { idx, seed ->
      val output = gameLoopTest(seed)
      File("$PATH/actual/output_$idx.txt").writeText(output)
    }
    seeds.forEachIndexed { idx, seed ->
      val rawOutput = File("$PATH/actual/output_$idx.txt").readText()
      val expectedOutput = File("$PATH/expected/output_$idx.txt").readText()
      assertEquals(expectedOutput, rawOutput, "Outputs do not match for idx=$idx")
    }
  }

  @Test
  fun `unseeded game runs should produce different results`() {
    val firstRun = gameLoopTest()
    val secondRun = gameLoopTest()
    
    assertNotEquals(firstRun, secondRun)
  }
}
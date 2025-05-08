package com.cashngun

fun main() {
  val lootDeck = INITIAL_DECK

  while(!lootDeck.isEmpty()) {
    val lootForThisRound = getLootForThisRound(lootDeck)

    println("deck size: ${lootDeck.size}")
  }
}

package com.cashngun

fun main() {
  val lootDeck = INITIAL_DECK

  while(!lootDeck.isEmpty()) {
    val lootForThisRound = getLootForThisRound(deck)

    println("deck size: ${deck.size}")
  }
}

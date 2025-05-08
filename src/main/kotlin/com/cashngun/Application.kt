package com.cashngun

fun main() {
  var round = 1
  val lootDeck = INITIAL_DECK

  while(!lootDeck.isEmpty()) {
    val lootForThisRound = getLootForThisRound(deck)

    println("deck size: ${deck.size}")

    round++
  }
}

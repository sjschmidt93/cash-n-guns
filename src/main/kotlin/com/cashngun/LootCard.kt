package com.cashngun

enum class LootCardType {
  CASH,
  PAINTING,
  CLIP,
  DIAMOND,
  FIRST_AID_KIT
}

data class LootCard(
  val type: LootCardType,
  val value: Int? = null
)

val INITIAL_DECK = listOf(
  *Array(15) { LootCard(LootCardType.CASH, 5000) },
  *Array(15) { LootCard(LootCardType.CASH, 10000) },
  *Array(10) { LootCard(LootCardType.CASH, 20000) },

  *Array(5) { LootCard(LootCardType.DIAMOND, 1000) },
  *Array(3) { LootCard(LootCardType.DIAMOND, 5000) },
  LootCard(LootCardType.DIAMOND, 10000),

  *Array(10) { LootCard(LootCardType.PAINTING) },

  *Array(3) { LootCard(LootCardType.CLIP) },

  *Array(2) { LootCard(LootCardType.FIRST_AID_KIT) }
).shuffled().toMutableList()

private val LOOT_CARDS_PER_ROUND = 8

fun getLootForThisRound(deck: MutableList<LootCard>): MutableList<LootCard> {
  println("Step 1: Loot")
  val lootForThisRound = mutableListOf<LootCard>()

  while(lootForThisRound.size < LOOT_CARDS_PER_ROUND) {
    val card = deck.removeAt(0)
    lootForThisRound.add(card)
  }

  printLootForThisRound(lootForThisRound)

  return lootForThisRound
}

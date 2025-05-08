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
  *List(15) { LootCard(LootCardType.CASH, 5000) },
  *List(15) { LootCard(LootCardType.CASH, 10000) },
  *List(10) { LootCard(LootCardType.CASH, 20000) },

  *List(5) { LootCard(LootCardType.DIAMOND, 1000) },
  *List(3) { LootCard(LootCardType.DIAMOND, 5000) },
  LootCard(LootCardType.DIAMOND, 10000),

  *List(10) { LootCard(LootCardType.PAINTING) },

  *List(3) { LootCard(LootCardType.CLIP) },

  *List(2) { LootCard(LootCardType.FIRST_AID_KIT) }
)

fun getLootForThisRound(deck: Deck): List<LootCard> {
  return deck.cards.shuffled().take(3)
}

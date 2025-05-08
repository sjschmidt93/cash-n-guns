fun main() {
  var gameRunning = true
  val deck = INITIAL_DECK;

  assert(deck.size == 64);

  while(gameRunning) {
    val lootForThisRound = getLootForThisRound(deck)
  }
}

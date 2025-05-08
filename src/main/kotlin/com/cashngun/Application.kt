package com.cashngun

fun main() {
  val lootDeck = INITIAL_DECK
  val numberOfPlayers = 3

  val players = listOf(
    Player(name = "Frank"),
    Player(name = "Dan"),
    Player(name = "Steven")
  )

  while(!lootDeck.isEmpty()) {
    val lootForThisRound = getLootForThisRound(lootDeck)
    printLootForThisRound(lootForThisRound)

    val choices = playersChooseBulletCards(players)
  }
}

fun playersChooseBulletCards(players: List<Player>): List<BulletCard> {
  println("Choose your action:")
  println("1) Bullet")
  println("2) Click\n")
  
  return players.map {
    println("${it.name} choose your card")

    val chosenCard = when {
      it.numClickCards == 0 && it.numBangCards == 0 -> {
        println("You have no cards left, this should not happen")
        BulletCard.INVALID
      }

      it.numClickCards == 0 -> {
        println("You have no click cards, you must choose a bang card")
        it.numBangCards--
        BulletCard.BANG
      }

      it.numBangCards == 0 -> {
        println("You have no bang cards, you must choose a click card")
        it.numClickCards--
        BulletCard.CLICK
      }
      
      else -> {
        when (readLine()?.trim()) {
          "1" -> {
            it.numBangCards--
            println(it.numBangCards)
            BulletCard.BANG
          }
          else -> {
            it.numClickCards--
            BulletCard.CLICK
          }
        }
      }
    }

    println("${it.name} chose ${chosenCard}")

    chosenCard
  }
}

fun printLootForThisRound(loot: List<LootCard>) {
  println("Loot for this round:")

  loot.forEach {
    when (it.type) {
      LootCardType.CASH -> println("• ${it.value} cash")
      LootCardType.PAINTING -> println("• Painting") 
      LootCardType.CLIP -> println("• Clip")
      LootCardType.DIAMOND -> println("• Diamond worth ${it.value}")
      LootCardType.FIRST_AID_KIT -> println("• First Aid Kit")
    }
  }

  println("")
}

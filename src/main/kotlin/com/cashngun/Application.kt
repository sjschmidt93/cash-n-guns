package com.cashngun

fun main() {
  val lootDeck = INITIAL_DECK
  val numberOfPlayers = 3
  var roundNumber = 1

  val players = listOf(
    Player(name = "Frank", isGodFather = true),
    Player(name = "Dan"),
    Player(name = "Steven")
  )

  while(!lootDeck.isEmpty()) {
    printRoundAndGodFather(roundNumber, players)

    val lootForThisRound = getLootForThisRound(lootDeck)

    val choices = playersChooseBulletCards(players)
    
    changeGodFather(players)
    roundNumber++
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

    println("${it.name} chose ${chosenCard}\n")

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

fun printRoundAndGodFather(roundNumber: Int, players: List<Player>) {
  println("Round ${roundNumber}")
  println("Godfather: ${players.find { it.isGodFather }?.name}\n")
}

fun changeGodFather(players: List<Player>) {
  val godFather = players.indexOfFirst { it.isGodFather }
  val newGodFatherIndex = (godFather + 1) % players.size

  players[godFather].isGodFather = false
  players[newGodFatherIndex].isGodFather = true
}

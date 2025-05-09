package com.cashngun

import kotlin.system.exitProcess

val mode = GameMode.AUTOMATIC

fun getModeAndChoice(gameMode: GameMode) = { totalChoices: Int -> 
  if (gameMode == GameMode.AUTOMATIC) {
    (1..totalChoices).random()
  } else {
    readLine()?.trim()?.toIntOrNull() ?: 1
  }
}

val getChoice = getModeAndChoice(mode)

fun main() {
  val mode = GameMode.AUTOMATIC
  val lootDeck = INITIAL_DECK
  var roundNumber = 1

  var players = mutableListOf(
    Player(name = "Frank"),
    Player(name = "Dan", isGodFather = true),
    Player(name = "Steven"),
    Player(name = "Erik")
  )

  val bulletCardDiscardPile = mutableListOf<BulletCard>()

  while(!lootDeck.isEmpty()) {
    printRoundAndGodFather(roundNumber, players)

    val lootForThisRound = getLootForThisRound(lootDeck)

    val bulletCards = playersChooseBulletCards(players)
    println("")

    val playersPointingGuns = playersPointGuns(players)

    val playerToRedirect = godFatherPrivilege(players)
    println("")

    redirectPlayer(playerToRedirect, playersPointingGuns)

    courage(players)
    println("")

    players = resolvePointing(bulletCards, playersPointingGuns, players)

    checkMoreThanOnePlayerRemaining(players)

    collectLoot(players, lootForThisRound, bulletCardDiscardPile)

    // End of round
    bulletCardDiscardPile.addAll(bulletCards)
    roundNumber++
  }

  val playersWithLootCards = players.map { (it.name to calculateEndGameTotalForPlayer(it, players)) }
  println()
  println("Results: $playersWithLootCards")
  val winner = determineWinner(players)
  println()
  winner?.let { println("The winner is: ${it.name}!") } ?: println("Nobody wins!")
}

fun checkMoreThanOnePlayerRemaining(players: List<Player>) {
  if (players.size == 1) {
    println("${players.first().name} wins, all other players are dead!")
    exitProcess(0)
  }
}

fun createAndSortPlayersEligibleForLoot(players: List<Player>): List<Player> {
  val godFatherIndex = players.indexOfFirst { it.isGodFather }
  val playersStanding = players.filter { it.playerPosition == PlayerPosition.STANDING }
  val sortedPlayers = mutableListOf<Player>()
  var i = godFatherIndex

  while (sortedPlayers.size != playersStanding.size) {
    if (players[i].playerPosition ==  PlayerPosition.STANDING) {
      sortedPlayers.add(players[i])
    }
    i = (i + 1) % players.size
  }

  return sortedPlayers
}

fun collectLoot(players: MutableList<Player>, lootForThisRound: MutableList<LootCard>, bulletCardDiscardPile: MutableList<BulletCard>) {
  createAndSortPlayersEligibleForLoot(players).forEach { player ->
    println("${player.name}: choose your loot")

    val choice = getChoice(lootForThisRound.size)
    val lootToCollect = lootForThisRound[choice - 1]
    player.lootCards.add(lootToCollect)
    lootForThisRound.remove(lootToCollect)

    if (lootToCollect.type == LootCardType.FIRST_AID_KIT) {
      player.wounds = 0
    }

    if (lootToCollect.type == LootCardType.GODFATHER) {
      players.first { it.isGodFather }.isGodFather = false
      player.isGodFather = true
    }

    println("${player.name} collects ${getLootString(lootToCollect)}")

    if (lootToCollect.type == LootCardType.CLIP) {
      when {
        player.numClickCards == 0 -> {} // swap bang for a bang
        bulletCardDiscardPile.none { it == BulletCard.BANG } -> {} // no bang cards to take
        player.numClickCards == 0 && player.numBangCards == 0 -> {}
        else -> {
          println("${player.name} gains bang card")

          player.numClickCards--
          player.numBangCards++
          bulletCardDiscardPile.remove(BulletCard.BANG)
          bulletCardDiscardPile.add(BulletCard.CLICK)
        }
      }
    }
  }
}

fun resolvePointing(
  bulletCards: List<BulletCard>,
  playersPointingGuns: MutableList<Pair<Player, Player>>,
  players: MutableList<Player>
) : MutableList<Player> {
  println("Step 6: Card effects")

  playersPointingGuns.forEachIndexed { idx, pair ->
    val playerPointing = pair.first
    val playerPointingBulletCard = bulletCards[idx]
    val playerBeingPointedAt = pair.second
    val playerBeingPointedAtPosition = playerBeingPointedAt.playerPosition

    println("${playerPointing.name} fires a ${playerPointingBulletCard} at ${playerBeingPointedAt.name} who is ${playerBeingPointedAtPosition}")

    if (playerBeingPointedAtPosition == PlayerPosition.LAYING_DOWN) {

    } else {

      if (playerPointingBulletCard == BulletCard.BANG) {
        playerBeingPointedAt.wounds++
        println("${playerBeingPointedAt.name} gains 1 wound, they now have ${playerBeingPointedAt.wounds} wounds")
        playerBeingPointedAt.playerPosition = PlayerPosition.LAYING_DOWN
      }
    }
    println("")
  }

  players.forEach { player ->
    if (player.isAlive().not()) {
      println("${player.name} is dead")
    }
  }

  return players.filter { player -> player.isAlive() }.toMutableList()
}

fun playersPointGuns(players: List<Player>): MutableList<Pair<Player, Player>> {
  println("Step 3: Hold-Up")
  val playerPairs = players.map { player ->
    println("\n${player.name}: Choose a player to point your gun at:")

    val otherPlayers = players.filterNot { p -> p.name == player.name }
    
    otherPlayers.forEachIndexed { index, it ->
      println("${index + 1}) ${it.name}")
    }

    val playerToPointAtPrompt = getChoice(otherPlayers.size)
    
    val playerToPointAt = playerToPointAtPrompt - 1
    println("${player.name} points their gun at ${otherPlayers[playerToPointAt].name}")

    Pair(player, otherPlayers[playerToPointAt])
  }

  println("")

  return playerPairs.toMutableList()
}

fun godFatherPrivilege(players: List<Player>): Player? {
  println("Step 4: Godfather's Privilege")
  val godfather = players.first{it.isGodFather}
  val otherPlayers = players.filterNot { it.isGodFather }
  println("\n${godfather.name}: Order another player to change target:")
  otherPlayers.forEachIndexed { index, it ->
    println("${index + 1}) ${it.name}")
  }
  println("${otherPlayers.size + 1}) Nobody")
  val choice = getChoice(otherPlayers.size + 1)
  return if (choice < otherPlayers.size) {
    println("Godfather chose ${otherPlayers[choice].name}")
     otherPlayers[choice]
  } else {
    println("Godfather chose nobody")
    null
  }
}

fun redirectPlayer(player: Player?, playerPairs: MutableList<Pair<Player, Player>>) {
  player?.let {
    val currentTarget = playerPairs.first {it.first == player}.second
    val newTargetChoices = playerPairs.map { it.first }.filterNot {it == currentTarget || it == player}

    newTargetChoices.forEachIndexed { i, p -> println("${i + 1}) ${p.name}") }

    val choice = getChoice(newTargetChoices.size) - 1

    val indexToRemove = playerPairs.indexOfFirst { it.first == player }
    playerPairs.removeAt(indexToRemove)
    playerPairs.add(Pair(player, newTargetChoices[choice]))
    println("${player.name} redirects and points their gun at ${newTargetChoices[choice].name}")
    println("")
  }
}

enum class PlayerPosition {
  LAYING_DOWN,
  STANDING
}

fun courage(players: MutableList<Player>) {
  println("Step 5: Courage")
  players.forEach {
    println("${it.name}: Lay down or stay standing")
    when (getChoice(2)) { 
      1 -> {
        println("${it.name} lays down")
        it.playerPosition = PlayerPosition.LAYING_DOWN
      }
      else -> {
        println("${it.name} stays standing")
        it.playerPosition = PlayerPosition.STANDING
      }
    }
  }
}

fun playersChooseBulletCards(players: List<Player>): List<BulletCard> {
  println("Step 2: Choice of the Bullet card")
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
        when (getChoice(2)) {
          1 -> {
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
    println("• ${getLootString(it)}")
  }

  println("")
}

fun getLootString(loot: LootCard): String {
  return when (loot.type) {
    LootCardType.CASH -> "cash worth ${loot.value}"
    LootCardType.PAINTING -> "painting"
    LootCardType.CLIP -> "clip"
    LootCardType.DIAMOND -> "diamond worth ${loot.value}"
    LootCardType.FIRST_AID_KIT -> "first aid kit"
    LootCardType.GODFATHER -> "godfather card"
  }
}

fun printRoundAndGodFather(roundNumber: Int, players: List<Player>) {
  println("Round ${roundNumber}")
  println("Godfather: ${players.find { it.isGodFather }?.name}\n")
}

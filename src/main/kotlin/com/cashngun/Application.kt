package com.cashngun

import kotlin.random.Random
import kotlin.system.exitProcess
import java.io.ByteArrayOutputStream
import java.io.PrintStream

val EMPTY_ITERATOR = emptyList<Int>().listIterator()

fun getChoice(totalChoices: Int, seed: Random?, gameMode: GameMode = GameMode.AUTOMATIC): Int {
  return when (gameMode) {
    GameMode.AUTOMATIC -> (1..totalChoices).random(seed ?: Random.Default)
    GameMode.REAL -> readlnOrNull()?.trim()?.toIntOrNull() ?: 1
  }
}

fun main() {
  gameLoop()
}

fun gameLoopTest(
  seed: Random = Random.Default
): String {
  val outputStream = ByteArrayOutputStream()
  val originalOut = System.out
  System.setOut(PrintStream(outputStream))
  
  try {
    gameLoop(seed)
    val output = outputStream.toString()
    return output
  } finally {
    System.setOut(originalOut)
  }
}

fun gameLoop(
  seed: Random = Random.Default
) {
  val lootDeck = getInitialDeck(seed).toMutableList()

  var roundNumber = 1

  var players = mutableListOf(
    Player(name = "Frank"),
    Player(name = "Dan", isGodFather = true),
    Player(name = "Steven"),
    Player(name = "Erik")
  )
  val specialPowers = SpecialPower.entries.toTypedArray().toList().shuffled(seed)
  players.forEachIndexed { index, player ->
    player.specialPower = specialPowers[index]
  }

  players.forEach { println("${it.name} has special power ${it.specialPower}") }

  println(players.toString())

  val bulletCardDiscardPile = mutableListOf<BulletCard>()

  while(lootDeck.isNotEmpty()) {
    printRoundAndGodFather(roundNumber, players)

    val lootForThisRound = getLootForThisRound(lootDeck)

    val bulletCards = playersChooseBulletCards(players, seed)
    println("")

    val playersPointingGuns = playersPointGuns(players, seed)

    val playerToRedirect = godFatherPrivilege(players, seed)
    println("")

    redirectPlayer(playerToRedirect, playersPointingGuns, seed)

    courage(players, seed)
    println("")

    players = resolvePointing(bulletCards, playersPointingGuns, players)

    checkPlayersRemaining(players)

    collectLoot(players, lootForThisRound, bulletCardDiscardPile, seed)

    // End of round
    bulletCardDiscardPile.addAll(bulletCards)
    resetAlivePlayersToStanding(players)
    roundNumber++
  }

  val playersWithLootCards = players.map { (it.name to calculateEndGameTotalForPlayer(it, players)) }
  println()
  println("Results: $playersWithLootCards")
  val winner = determineWinner(players)
  println()
  winner?.let { println("The winner is: ${it.name}!") } ?: println("Nobody wins!")
}

fun resetAlivePlayersToStanding(players: List<Player>) {
  players.forEach {
    it.playerPosition = PlayerPosition.STANDING
  }
}

fun checkPlayersRemaining(players: List<Player>) {
  if (players.isEmpty()) {
    println("Everyone is dead! Game over")
    exitProcess(0)
  }
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

fun collectLoot(
  players: MutableList<Player>,
  lootForThisRound: MutableList<LootCard>,
  bulletCardDiscardPile: MutableList<BulletCard>,
  seed: Random?
) {
  println("Step 7: Loot Collection")
  var idx = 0
  val playersSorted = createAndSortPlayersEligibleForLoot(players)

  if (playersSorted.isEmpty()) {
    println("No players are eligible for loot")
    return
  }

  while (lootForThisRound.isNotEmpty()) {
    val player =  playersSorted[idx % playersSorted.size]
    println("${player.name}: choose your loot")

    val choice = getChoice(lootForThisRound.size, seed)
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

    idx++
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

fun playersPointGuns(players: List<Player>, seed: Random = Random.Default): MutableList<Pair<Player, Player>> {
  println("Step 3: Hold-Up")
  val playerPairs =  players
      .sortedBy { if (it.specialPower == SpecialPower.KID) 1 else 0 }
      .map { player ->
        println("\n${player.name}: Choose a player to point your gun at:")

        val otherPlayers = players.filterNot { p -> p.name == player.name }

        otherPlayers.forEachIndexed { index, it ->
          println("${index + 1}) ${it.name}")
        }

        val playerToPointAtPrompt = getChoice(otherPlayers.size, seed)

        val playerToPointAt = playerToPointAtPrompt - 1
        println("${player.name} points their gun at ${otherPlayers[playerToPointAt].name}")

        Pair(player, otherPlayers[playerToPointAt])
      }

  println("")

  return playerPairs.toMutableList()
}

fun godFatherPrivilege(players: List<Player>, seed: Random?): Player? {
  println("Step 4: Godfather's Privilege")
  val godfather = players.first{it.isGodFather}
  val otherPlayers = players.filterNot { it.isGodFather }
  println("\n${godfather.name}: Order another player to change target:")
  otherPlayers.forEachIndexed { index, it ->
    println("${index + 1}) ${it.name}")
  }
  println("${otherPlayers.size + 1}) Nobody")
  val choice = getChoice(otherPlayers.size + 1, seed)
  return if (choice < otherPlayers.size) {
    println("Godfather chose ${otherPlayers[choice].name}")
     otherPlayers[choice]
  } else {
    println("Godfather chose nobody")
    null
  }
}

fun redirectPlayer(player: Player?, playerPairs: MutableList<Pair<Player, Player>>, seed: Random?) {
  player?.let {
    val currentTarget = playerPairs.first {it.first == player}.second
    val newTargetChoices = playerPairs.map { it.first }.filterNot {it == currentTarget || it == player}

    newTargetChoices.forEachIndexed { i, p -> println("${i + 1}) ${p.name}") }

    val choice = getChoice(newTargetChoices.size, seed) - 1

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

fun courage(players: MutableList<Player>, seed: Random?) {
  println("Step 5: Courage")
  players.forEach {
    println("${it.name}: Lay down or stay standing")
    when (getChoice(2, seed)) {
      1 -> {
        println("${it.name} lays down")
        it.playerPosition = PlayerPosition.LAYING_DOWN
        if (it.wounds > 0 && it.specialPower == SpecialPower.DOCTOR) it.wounds--
      }
      else -> {
        println("${it.name} stays standing")
        it.playerPosition = PlayerPosition.STANDING
      }
    }
  }
}

fun playersChooseBulletCards(players: List<Player>, seed: Random?): List<BulletCard> {
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
        when (getChoice(2, seed)) {
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

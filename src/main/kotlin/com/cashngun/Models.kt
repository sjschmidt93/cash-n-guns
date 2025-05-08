
data class Player(
  val health: Int = 3,
  val bulletCards: List<BulletCard> = listOf(
    *Array(5) { BulletCard.CLICK },
    *Array(3) { BulletCard.BANG }
  ),
  val name: String
)

enum class BulletCard {
  CLICK, BANG
}

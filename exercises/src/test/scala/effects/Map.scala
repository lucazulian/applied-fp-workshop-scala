package effects

class Map extends munit.FunSuite {

  case class Item(qty: Int)

  def createItem(qty: String): Option[Item] =
    if (qty.matches("^[0-9]+$")) Some(Item(qty.toInt))
    else None

  def checkIn(qty: Int, item: Item): Item =
    item.copy(qty = item.qty + qty)

  test("valid creation, can checkIn") {
    val item: Option[Item] = createItem("100")

    // TODO: chain checkIn of 10 items and write the assert
    val result: Option[Item] = item.map(item => checkIn(10, item))
    assertEquals(result, Some(Item(110)))
  }

  test("invalid creation - map short circuit") {
    val item: Option[Item] = createItem("asd")

    // TODO: chain checkIn of 10 items and write the assert
    val result: Option[Item] = item.map(item => checkIn(10, item))
    assertEquals(result, None)
  }
}

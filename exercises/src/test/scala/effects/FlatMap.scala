package effects

class FlatMap extends munit.FunSuite {

  case class Item(qty: Int)

  def createItem(qty: String): Option[Item] =
    if (qty.matches("^[0-9]+$")) Some(Item(qty.toInt))
    else None

  def checkIn(qty: Int, item: Item): Item =
    item.copy(qty = item.qty + qty)

  def checkOut(qty: Int, item: Item): Option[Item] =
    if (qty <= item.qty) Some(item.copy(qty = item.qty - qty))
    else None

  test("valid creation, can checkOut") {
    val item = createItem("100")

    // TODO: chain checkOut of 10 items and write the assert
    val result = item.flatMap(item => checkOut(10, item))
    assertEquals(result, Some(Item(90)))
  }

  test("valid creation, can't checkOut (too much) - flatMap short circuit") {
    val item = createItem("100")

    // TODO: chain checkOut of 110 items and write the assert
    val result = item.flatMap(item => checkOut(110, item))
    assertEquals(result, None)
  }

  test("invalid creation - flatMap short circuit") {
    val item = createItem("asd")

    // TODO: chain checkOut of 10 items and write the assert
    val result = item.flatMap(item => checkOut(10, item))
    assertEquals(result, None)
  }

  test("valid creation, checking, can checkOut") {
    val item = createItem("100")

    val result = item
      .map(item => checkIn(10, item))
      .flatMap(item => checkOut(10, item))
    assertEquals(result, Some(Item(100)))
  }

  test("valid creation, can checkOut, can checking") {
    val item = createItem("100")

    val result = item
      .flatMap(item => checkOut(10, item))
      .map(item => checkIn(10, item))
    assertEquals(result, Some(Item(100)))
  }

  test("invalid creation, checking - flatMap short circuit") {
    val item = createItem("asd")

    val result = item
      .map(item => checkIn(10, item))
      .flatMap(item => checkOut(10, item))
    assertEquals(result, None)
  }

  test("invalid creation - flatMap short circuit, map short circuit") {
    val item = createItem("asd")

    val result = item
      .flatMap(item => checkOut(10, item))
      .map(item => checkIn(10, item))
    assertEquals(result, None)
  }
}

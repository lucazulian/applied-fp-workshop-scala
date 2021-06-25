package effects

class Fold extends munit.FunSuite {

  case class Item(qty: Int)

  def createItem(qty: String): Option[Item] =
    if (qty.matches("^[0-9]+$")) Some(Item(qty.toInt))
    else None

  test("valid creation") {
    val item: Option[Item] = createItem("100")

    // TODO: use fold to make test green and remove ignore
    val result: String = item.fold("no value")(x => x.qty.toString)
    assertEquals(result, "100")
  }

  test("invalid creation") {
    val item: Option[Item] = createItem("asd")

    // TODO: use fold to make test green and remove ignore
    val result: String = item.fold("alternative value")(x => x.qty.toString)
    assertEquals(result, "alternative value")
  }
}

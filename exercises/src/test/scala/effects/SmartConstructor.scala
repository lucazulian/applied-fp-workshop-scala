package effects

class SmartConstructor extends munit.FunSuite {

  /*
   * TODO: remove null with a custom type for valid and invalid states
   */

  case class Item(qty: Int)

  sealed trait MaybeItem
  case class Valid(item: Item) extends MaybeItem
  case object Invalid extends MaybeItem

  def createItem(qty: String): MaybeItem =
    if (qty.matches("^[0-9]+$")) Valid(Item(qty.toInt))
    else Invalid

  test("valid") {
    assertEquals(createItem("100"), Valid(Item(100)))
  }

  test("invalid") {
    assertEquals(createItem("asd"), Invalid)
    assertEquals(createItem("1 0 0"), Invalid)
    assertEquals(createItem(""), Invalid)
  }
}

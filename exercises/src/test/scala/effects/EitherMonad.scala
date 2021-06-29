package effects

class EitherMonad extends munit.FunSuite {

  import scala.util._

  case class ItemId(value: Int)
  case class Item(id: ItemId, qty: Int)

  type Error = String

  def load(id: ItemId): Either[Error, Item] =
    Right(Item(id, 100))

  def save(item: Item): Either[Error, Unit] =
    Right(())

  def checkIn(qty: Int, item: Item): Item =
    item.copy(qty = item.qty + qty)

  test("scenario") {
    // TODO: implement follow steps
    // load an item
    // checkIn 10
    // save item
    val stepOne: Either[Error, Item]   = load(ItemId(1))
    val stepTwo: Either[Error, Item]   = stepOne.map(checkIn(10, _))
    val stepThree: Either[Error, Unit] = stepTwo.flatMap(save)

    val program: Either[Error, Unit] = stepThree

    // run the computation
    program.fold("err " + _, "value " + _)
  }
}

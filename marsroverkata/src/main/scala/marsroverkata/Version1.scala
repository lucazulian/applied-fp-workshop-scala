package marsroverkata

//V1 - Focus on the center (pure domain logic)
//
//Develop an API (types and functions) that executes single commands:
//
//  - The planet is divided into a grid with x (width) and y (height) size.
//  - The rover has a position expressed as x, y co-ordinates and an orientation (North, Est, West, South).
//  - The rover can handle four commands: turn left or right, move forward or backward.
//  - Implement wrapping from one edge of the grid to another (pacman effect).

object Version1 {

  def run(program: Program): Rover = program.command match {
    case Right =>
      program.rover.copy(orientation = turnRight(program.rover.orientation))

    case Left =>
      program.rover.copy(orientation = turnLeft(program.rover.orientation))

    case Forward =>
      move(program.rover, calcMoveStep(program.rover.orientation), program.planet)

    case Backward =>
      move(program.rover, !calcMoveStep(program.rover.orientation), program.planet)
  }

  def turnRight(orientation: Orientation): Orientation =
    orientation match {
      case North => Est
      case Est   => South
      case West  => North
      case South => West
    }

  def turnLeft(orientation: Orientation): Orientation =
    orientation match {
      case Est   => North
      case South => Est
      case North => West
      case West  => South
    }

  def move(rover: Rover, step: Step, planet: Planet): Rover =
    Rover(modulo(rover.position + step, planet), rover.orientation)

  def modulo(position: Position, planet: Planet): Position =
    Position(
      if (position.x > 0) position.x % planet.size.w else (planet.size.w + position.x) % planet.size.w,
      if (position.y > 0) position.y % planet.size.h else (planet.size.h + position.y) % planet.size.h
    )

  def calcMoveStep(orientation: Orientation): Step =
    orientation match {
      case North => Step(0, 1)
      case South => Step(0, -1)
      case Est   => Step(1, 0)
      case West  => Step(-1, 0)
    }

  case class Size(w: Int, h: Int)
  case class Position(x: Int, y: Int) {
    def +(step: Step): Position =
      Position(x + step.dx, y + step.dy)
  }

  case class Program(planet: Planet, rover: Rover, command: Command)
  case class Step(dx: Int, dy: Int) {
    def unary_! : Step = Step(dx * -1, dy * -1)
  }

  sealed trait Orientation
  case object North extends Orientation
  case object South extends Orientation
  case object Est   extends Orientation
  case object West  extends Orientation

  case class Planet(size: Size)
  case class Rover(position: Position, orientation: Orientation)

  sealed trait Command
  case object Forward  extends Command
  case object Backward extends Command
  case object Left     extends Command
  case object Right    extends Command

//  sealed trait Command
//  case class Rotate(rotation: RotateDirection) extends Command
//  case class Move(direction: MoveDirection) extends Command
//
//  sealed trait RotateDirection
//  case object Forward extends RotateDirection
//  case object Backward extends RotateDirection
//
//  sealed trait MoveDirection
//  case object Left extends MoveDirection
//  case object Right extends MoveDirection
}

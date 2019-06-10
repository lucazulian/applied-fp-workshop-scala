package marsroverkata.answers

import scala.util._

import cats._
import cats.data._
import cats.implicits._

object Version4 {

  sealed trait Error
  case class InvalidPlanet(value: String, error: String) extends Error
  case class InvalidRover(value: String, error: String)  extends Error

  def parsePlanet(raw: String): ValidatedNel[Error, Planet] =
    Try {
      val parts = raw.split("x")
      Planet(Size(parts(0).trim.toInt, parts(1).trim.toInt),
             List(
               Obstacle(Position(2, 0)),
               Obstacle(Position(0, 3)),
               Obstacle(Position(3, 2)),
             ))
    }.toEither
      .leftMap(ex => InvalidPlanet(raw, ex.getClass.getSimpleName))
      .toValidatedNel

  def parseRover(raw: String): ValidatedNel[Error, Rover] =
    Try {
      val parts    = raw.split(",")
      val subparts = parts(1).trim.split(":")
      val direction = subparts(1).trim.toLowerCase match {
        case "n" => N
        case "w" => W
        case "e" => E
        case "s" => S
      }
      Rover(Position(parts(0).trim.toInt, subparts(0).trim.toInt), direction)
    }.toEither
      .leftMap(ex => InvalidRover(raw, ex.getClass.getSimpleName))
      .toValidatedNel

  def parseCommands(raw: String): List[Command] =
    raw.map(parseCommand).toList

  def parseCommand(raw: Char): Command =
    raw.toString.toLowerCase match {
      case "f" => Move(Forward)
      case "b" => Move(Backward)
      case "r" => Turn(OnRight)
      case "l" => Turn(OnLeft)
      case _   => Unknown
    }

  def init(planet: String, rover: String): Either[NonEmptyList[Error], Mission] =
    (
      parsePlanet(planet),
      parseRover(rover)
    ).mapN(Mission.apply).toEither

  def render(rover: Rover): String =
    s"${rover.position.x}:${rover.position.y}:${rover.direction}"

  def renderHit(rover: Rover): String =
    s"O:${rover.position.x}:${rover.position.y}:${rover.direction}"

  def execute(mission: Mission, commands: List[Command]): Either[Mission, Mission] =
    commands.foldLeftM(mission)(execute)

  def execute(mission: Mission, command: Command): Either[Mission, Mission] =
    (command match {
      case Turn(tt) => turn(mission.rover, tt).some
      case Move(mt) => move(mission.rover, mission.planet, mt)
      case Unknown  => noOp(mission.rover).some
    }).map(r => mission.copy(rover = r)).toRight(mission)

  val noOp: Rover => Rover = identity _

  def turn(rover: Rover, turn: TurnType): Rover =
    rover.copy(direction = turn match {
      case OnRight => turnRight(rover.direction)
      case OnLeft  => turnLeft(rover.direction)
    })

  def turnRight(direction: Direction): Direction =
    direction match {
      case N => E
      case E => S
      case S => W
      case W => N
    }

  def turnLeft(direction: Direction): Direction =
    direction match {
      case N => W
      case W => S
      case S => E
      case E => N
    }

  def move(rover: Rover, planet: Planet, move: MoveType): Option[Rover] =
    (move match {
      case Forward  => forward(rover, planet)
      case Backward => backward(rover, planet)
    }).map(p => rover.copy(position = p))

  def forward(rover: Rover, planet: Planet): Option[Position] =
    next(rover.position, planet, delta(rover.direction))

  def backward(rover: Rover, planet: Planet): Option[Position] =
    next(rover.position, planet, delta(opposite(rover.direction)))

  def opposite(direction: Direction): Direction =
    direction match {
      case N => S
      case S => N
      case E => W
      case W => E
    }

  def delta(direction: Direction): Delta =
    direction match {
      case N => Delta(0, 1)
      case S => Delta(0, -1)
      case E => Delta(1, 0)
      case W => Delta(-1, 0)
    }

  def wrap(axis: Int, size: Int, delta: Int): Int =
    (((axis + delta) % size) + size) % size

  def next(position: Position, planet: Planet, delta: Delta): Option[Position] = {
    val candidate = Position(
      wrap(position.x, planet.size.x, delta.x),
      wrap(position.y, planet.size.y, delta.y)
    )
    if (planet.obstacles.map(_.position).contains(candidate)) None
    else candidate.some
  }

  case class Delta(x: Int, y: Int)
  case class Position(x: Int, y: Int)
  case class Size(x: Int, y: Int)
  case class Obstacle(position: Position)
  case class Planet(size: Size, obstacles: List[Obstacle])
  case class Rover(position: Position, direction: Direction)
  case class Mission(planet: Planet, rover: Rover)

  sealed trait Command
  case class Move(direction: MoveType) extends Command
  case class Turn(direction: TurnType) extends Command
  case object Unknown                  extends Command

  sealed trait MoveType
  case object Forward  extends MoveType
  case object Backward extends MoveType

  sealed trait TurnType
  case object OnRight extends TurnType
  case object OnLeft  extends TurnType

  sealed trait Direction
  case object N extends Direction
  case object E extends Direction
  case object W extends Direction
  case object S extends Direction
}

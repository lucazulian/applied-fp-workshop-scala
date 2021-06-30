package marsroverkata

object Version3 {

  import scala.util._
  import cats.implicits._

  def run(planet: (String, String), rover: (String, String), commands: String): Either[String, String] = {
    val mission: Either[String, Mission]              = (parsePlanet(planet), parseRover(rover)).mapN(Mission)
    val parsedCommands: Either[String, List[Command]] = parseCommands(commands)
    val a: Either[String, Either[Mission, Mission]]   = (mission, parsedCommands).mapN(execute)

    a.map((x: Either[Mission, Mission]) => x.fold(missionAbortedRendering, missionRendering))
  }

  def missionRendering(mission: Mission): String =
    s"${mission.rover.position.x}:${mission.rover.position.y}:${mission.rover.direction}"

  def missionAbortedRendering(mission: Mission): String = s"O:${missionRendering(mission)}"

  def parseDirection(direction: String): Either[String, Direction] =
    direction match {
      case "N" => Right(N)
      case "S" => Right(S)
      case "W" => Right(W)
      case "E" => Right(E)
      case _   => Left("Invalid rover direction")
    }

  def parseCommand(command: Char): Option[Command] =
    command match {
      case 'R' => Some(Turn(OnRight))
      case 'L' => Some(Turn(OnLeft))
      case 'F' => Some(Move(Forward))
      case 'B' => Some(Move(Backward))
      case _   => None
    }

  def parsePosition[A](value: String, separator: Char): Either[String, Position] = {
    val sizes: Array[String] = value.split(separator)

    val a: Try[Int]               = Try(sizes(0).trim().toInt)
    val b: Either[Throwable, Int] = a.toEither
    val x: Either[String, Int]    = b.leftMap(_ => "Invalid x size")
//    val x1: Either[String, Int] = a.toOption.toRight("Invalid x size")
    val y: Either[String, Int] = Try(sizes(1).trim().toInt).toEither.leftMap(_ => "Invalid y size")

    (x, y).mapN(Position)
  }

  def parseSize(size: String): Either[String, Size] =
    parsePosition(size, 'x').map(x => Size(x.x, x.y))

  def parseObstacle(obstacles: String): Either[String, Obstacle] =
    parsePosition(obstacles, ',').map(Obstacle)

  def parseObstacles(obstacles: String): Either[String, List[Obstacle]] =
    obstacles.split(' ').toList.traverse(parseObstacle)

  def parsePlanet(planet: (String, String)): Either[String, Planet] =
    (parseSize(planet._1), parseObstacles(planet._2))
      .mapN(Planet)
      .leftMap(_ => "Invalid planet size")

  def parseRover(rover: (String, String)): Either[String, Rover] =
    (parsePosition(rover._1, ','), parseDirection(rover._2)).mapN(Rover)

  def parseCommands(commands: String): Either[String, List[Command]] =
    commands.toList.map(parseCommand).filter(_.isDefined).sequence.toRight("Invalid commands")

  def execute(mission: Mission, commands: List[Command]): Either[Mission, Mission] =
    commands.foldLeftM(mission)(execute)

  def execute(mission: Mission, command: Command): Either[Mission, Mission] =
    (command match {
      case Turn(tt) => turn(mission.rover, tt).some
      case Move(mt) => move(mission.rover, mission.planet, mt)
    }).map(r => mission.copy(rover = r)).toRight(mission)

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

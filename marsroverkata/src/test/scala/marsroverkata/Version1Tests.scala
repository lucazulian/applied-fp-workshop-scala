package marsroverkata

import marsroverkata.Version1._

class Version1Tests extends munit.FunSuite {

// +-----+-----+-----+-----+-----+
// | 0,3 |     |     |     | 4,3 |
// +-----+-----+-----+-----+-----+
// |     |     |     |     |     |
// +-----+-----+-----+-----+-----+
// |     |     |     |     |     |
// +-----+-----+-----+-----+-----+
// | 0,0 |     |     |     | 4,0 |
// +-----+-----+-----+-----+-----+

  val planet: Planet = Planet(Size(5, 4))

  test("turn right command") {
    /*
        Planet: 5 4
        Rover: 0 0 N
        Command: R
        --
        Rover: 0 0 E
     */
    val rover   = Rover(Position(0, 0), North)
    val program = Program(planet, rover, Right)

    assertEquals(run(program), Rover(Position(0, 0), Est))
  }

  test("turn left command") {
    /*
        Planet: 5 4
        Rover: 0 0 N
        Command: L
        --
        Rover: 0 0 W
     */
    val rover   = Rover(Position(0, 0), North)
    val program = Program(planet, rover, Left)

    assertEquals(run(program), Rover(Position(0, 0), West))
  }

  test("move forward command") {
    /*
        Planet: 5 4
        Rover: 0 1 N
        Command: F
        --
        Rover: 0 2 N
     */
    val rover   = Rover(Position(0, 1), North)
    val program = Program(planet, rover, Forward)

    assertEquals(run(program), Rover(Position(0, 2), North))
  }

  test("move forward command, opposite orientation") {
    /*
        Planet: 5 4
        Rover: 0 1 S
        Command: F
        --
        Rover: 0 0 S
     */
    val rover   = Rover(Position(0, 1), South)
    val program = Program(planet, rover, Forward)

    assertEquals(run(program), Rover(Position(0, 0), South))
  }

  test("move backward command") {
    /*
        Planet: 5 4
        Rover: 0 1 N
        Command: B
        --
        Rover: 0 0 N
     */
    val rover   = Rover(Position(0, 1), North)
    val program = Program(planet, rover, Backward)

    assertEquals(run(program), Rover(Position(0, 0), North))
  }

  test("move backward command, opposite orientation") {
    /*
        Planet: 5 4
        Rover: 0 1 S
        Command: B
        --
        Rover: 0 2 S
     */
    val rover   = Rover(Position(0, 1), South)
    val program = Program(planet, rover, Backward)

    assertEquals(run(program), Rover(Position(0, 2), South))
  }

  test("wrap on North") {
    /*
        Planet: 5 4
        Rover: 0 3 N
        Command: F
        --
        Rover: 0 0 N
     */
    val rover   = Rover(Position(0, 3), North)
    val program = Program(planet, rover, Forward)

    assertEquals(run(program), Rover(Position(0, 0), North))
  }

  test("wrap on South") {
    /*
        Planet: 5 4
        Rover: 0 0 S
        Command: F
        --
        Rover: 0 3 S
     */
    val rover   = Rover(Position(0, 0), South)
    val program = Program(planet, rover, Forward)

    assertEquals(run(program), Rover(Position(0, 3), South))
  }

  test("wrap on Est") {
    /*
        Planet: 5 4
        Rover: 4 1 E
        Command: F
        --
        Rover: 0 1 E
     */
    val rover   = Rover(Position(4, 1), Est)
    val program = Program(planet, rover, Forward)

    assertEquals(run(program), Rover(Position(0, 1), Est))
  }

  test("wrap on West") {
    /*
        Planet: 5 4
        Rover: 0 1 W
        Command: F
        --
        Rover: 4 1 W
     */
    val rover   = Rover(Position(0, 1), West)
    val program = Program(planet, rover, Forward)

    assertEquals(run(program), Rover(Position(4, 1), West))
  }
}

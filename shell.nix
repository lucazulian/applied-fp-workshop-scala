with (import <nixpkgs> {});
mkShell {
  buildInputs = [
    openjdk
    scala
    gradle
    sbt
  ];
  shellHook = ''
  '';
}

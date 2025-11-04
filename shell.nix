# Project Shell
{
  pkgs ? import <nixpkgs> {}
}: pkgs.mkShell {
  packages = with pkgs; [
    bash
    jdk24
    maven
  ];

  shellHook = ''
    export name="jCLIx"
  '';
}


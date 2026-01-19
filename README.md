## Setup

Cavetale testing server setup utility.

### Usage, commands and flags

```
----------------------------- Help -----------------------------
Interactive: java -jar <file>.jar
Single: java -jar <file>.jar <input>
Input: [flag] <command> [flag]
----------------------------------------------------------------

--------------------------- Commands ---------------------------
Command          | Info                   | Params
----------------------------------------------------------------
connect          | Link tool to folder    | [path]:targets
exit             | Exit interactive mode  |
help             | Show usage help        |
test             | For unit testing       |
----------------------------------------------------------------
compare          | Compare selected       | -PCSZA:select
eula             | Agree to the EULA      | -y:auto
install          | Install selected       | -PCSZA:select
link             | Link files as plugins  | [path]:files
list             | List selected          | -PCSZIA:select
run              | Run server             | :unique! | -Z:select
search           | Search refs            | [string]
status           | Installation status    | = list -I
uninstall        | Uninstall selected     | -PCSZIA:select
update           | Update selected        | -PCSZIA:select
----------------------------------------------------------------

---------------------------- Flags -----------------------------
Flag             | Info                   | Params
----------------------------------------------------------------
-c --command     | Filter by commands     | [command]:select
-x --execute     | Run succeeding command |
-e --error       | Detailed error output  |
-f --flag        | Filter by flags        | [flag]:select
-h --help        | Show command help      |
-i --interactive | Enter prompt mode      |
-n --normal      | Normal verbosity       |
-q --quiet       | Reduced verbosity      |
-v --verbose     | Detailed verbosity     |
-y --yes         | Skip confirmations     |
-t --test        | For unit testing       |
----------------------------------------------------------------
-A --all         | Select all             |
-C --categories  | Select categor(y/ies)  | [category]:select
-I --installed   | Select installed       |
-P --plugins     | Select plugins(s)      | [plugin]:select
-S --servers     | Select server(s)       | [server]:select
-Z --software    | Select server software | [software]:select
----------------------------------------------------------------
```

### Dependencies

Setup depends on [cmdLib][cmdLib].

### Customization

Simply add a new entry with your code to the corresponding enum.
For console command and flag customization, please check the [cmdLib][cmdLib] documentation.

[cmdLib]: https://gitlab.com/iJustLeyxo/cmdlib

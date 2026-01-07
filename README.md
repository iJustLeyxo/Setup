## Setup

Cavetale testing server setup utility.

### Usage, commands and flags

```
------------------------------- Help --------------------------------
Interactive: java -jar <file>.jar
Single: java -jar <file>.jar <input>
Input: [flag] <command> [flag]
---------------------------------------------------------------------

----------------------------- Commands ------------------------------
Command          | Info                    | Params
---------------------------------------------------------------------
connect          | Link tool to folder     | [path]:targets
exit             | Exit interactive mode   |
help             | Show usage help         |
test             | For unit testing        |
---------------------------------------------------------------------
compare          | Compare software        | -PCSZIA
eula             | Agree to the EULA       |
find             | Find anything           | -cfPCSZIA
install          | Install software        | -PCSZIA
link             | Link jar as plugin      | [path]:files
list             | List software           | -PCSZIA
run              | Run server              | :only | -Z
status           | Installation status     |
uninstall        | Uninstall software      | -PCSZIA
update           | Update software         | -PCSZIA
---------------------------------------------------------------------

------------------------------- Flags -------------------------------
Flag             | Info                    | Params
---------------------------------------------------------------------
-c --command     | Filter by commands      | [command]:select
-x --execute     | Run succeeding command  |
-e --error       | Detailed error output   |
-f --flag        | Filter by flags         | [flag]:select
-h --help        | Show command help       |
-i --interactive | Enter prompt mode       |
-n --normal      | Normal verbosity        |
-q --quiet       | Reduced verbosity       |
-v --verbose     | Detailed verbosity      |
-y --yes         | Skip confirmations      |
-t --test        | For unit testing        |
---------------------------------------------------------------------
-A --all         | Select all              |
-C --category    | Specify categor(y/ies)  | :all | [category]:select
-I --installed   | Select installed        |
-P --plugin      | Specify plugins(s)      | :all | [plugin]:select
-S --server      | Specify server(s)       | :all | [server]:select
-Z --software    | Specify server software | :all | [software]:select
---------------------------------------------------------------------
```

### Dependencies

Setup depends on [cmdLib][cmdLib].

### Customization

Simply add a new entry with your code to the corresponding enum.
For console command and flag customization, please check the [cmdLib][cmdLib] documentation.

[cmdLib]: https://gitlab.com/iJustLeyxo/cmdlib

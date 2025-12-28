## Setup

Cavetale testing server setup utility.

### Usage, commands and flags

```
---------------------------------------- Help -----------------------------------------
Interactive: java -jar <file>.jar
Single: java -jar <file>.jar <input>
Input: [flag] <command> [flag]
---------------------------------------------------------------------------------------

-------------------------------------- Commands ---------------------------------------
Command          | Info
---------------------------------------------------------------------------------------
exit             | Exit interactive mode
help             | Show usage help
test             | Used for unit tests.
---------------------------------------------------------------------------------------
compare          | Compare installed to selected software
connect          | Link this tool to a server installation
eula             | Agree to the Minecraft EULA
find             | Find anything
install          | Install plugins and server software
link             | Link any jar archive path to the plugins directory
list             | List plugins, categories, servers and server software
run              | Run installed server software
status           | View installation status
uninstall        | Uninstall plugins, server software and files
update           | Update plugins and software
---------------------------------------------------------------------------------------

--------------------------------------- Flags -----------------------------------------
Flag             | Info                             | Usage
---------------------------------------------------------------------------------------
-c --command     | Filter by commands               | -c
-x --execute     | Run the command after the flag   | -x
-e --error       | Detailed error output            | -e
-f --flag        | Filter by flags                  | -f
-h --help        | Show command help                | -h
-i --interactive | Enter command prompt mode        | -i
-n --normal      | Normal console output            | -n
-q --quiet       | Reduced console output           | -q
-v --verbose     | Detailed console output          | -v
-y --yes         | Skip confirmation prompts        | -y
-t --test        | Used for unit tests.             | -t
---------------------------------------------------------------------------------------
-A --all         | Select all                       | -A
-C --category    | Specify categor(y/ies)           | -s []:all | [categories]
-I --installed   | Select installed                 | -I
-P --plugin      | Specify plugins(s)               | -p []:all | [plugins]
-S --server      | Specify server(s)                | -s []:all | [servers]
-Z --software    | Specify server software          | -S []:all | [software]
---------------------------------------------------------------------------------------
```

### Dependencies

Setup depends on [cmdLib][cmdLib].

### Customization

Simply add a new entry with your code to the corresponding enum.
For console command and flag customization, please check the [cmdLib][cmdLib] documentation.

[cmdLib]: https://gitlab.com/iJustLeyxo/cmdlib

### CaveMan
Cavetale installation manager for plugins and server software on test servers

##### Usage, commands and flags
```
---------------------------------------- Help -----------------------------------------
Interactive: java -jar Manager.jar
Single: java -jar Manager.jar <1+command(s)> <0+flag(s)>

-------------------------------------- Commands ---------------------------------------
   Command       | Info
---------------------------------------------------------------------------------------
exit             | Close the application
help             | Show help
install          | Install plugins and server software
link             | Install plugins by linking
list             | List plugins, categories, servers and server software
status           | View installation status
uninstall        | Uninstall plugins, server software and files
unlink           | Uninstall linked plugins

--------------------------------------- Flags -----------------------------------------
   Flag          | Info                             | Usage
---------------------------------------------------------------------------------------
-a --all         | Select all                       |
-c --category    | Specify categor(y/ies)           | -s <categor(y/ies)>
-d --default     | Normal console output            |
-f --force       | Force execution                  |
-h --help        | Show command help                |
-i --interactive | Enter command prompt mode        |
-P --path        | Specify a file path              | -P <path>
-p --plugin      | Specify plugins(s)               | -p <plugins(s)>
-q --quiet       | Reduced console output           |
-s --server      | Specify server(s)                | -s <server(s)>
-S --software    | Specify server software          |
-v --verbose     | Detailed console output          |
```

##### Adding commands
1. Create a command executor that extends the Exec class and overrides the run() method to run your custom logic
2. Add an entry to the Command enum with a description of your command and maybe some aliases that overrides the exec() method to run your command executor

##### Adding flags
1. If required, create a new container for you flag options / arguments that extends the EmptyContainer class and overrides the option() and isEmpty() methods to run your custom logic
2. Add an entry to the Flag enum with a description and maybe some usage help of your flag and a custom short ref if there already is another flag that start with the same letter that overrides the container() method to return your custom container

##### Adding plugins, categories, servers and software
- Simply add a new entry to the respective enum with the data of your new entry
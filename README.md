### Setup
Cavetale installation manager for plugins and server software on test servers

##### Usage, commands and flags
```
---------------------------------------- Help -----------------------------------------
Interactive: java -jar Manager.jar
Single: java -jar Manager.jar <command>
Command: <flag(s)> <command(s)> <flag(s)>

-------------------------------------- Commands ---------------------------------------
Command          | Info                                                                
---------------------------------------------------------------------------------------
connect          | Link this tool to a server installation                             
eula             | Agree to the Minecraft EULA                                         
exit             | Exit interactive mode                                               
find             | Find anything                                                       
help             | Show usage help                                                     
install          | Install plugins and server software                                 
link             | Link any jar archive path to the plugins directory                  
list             | List plugins, categories, servers and server software               
run              | Run installed server software                                       
status           | View installation status                                            
uninstall        | Uninstall plugins, server software and files                        
update           | Update plugins and software                                         

--------------------------------------- Flags -----------------------------------------
Flag             | Info                             | Usage                            
---------------------------------------------------------------------------------------
-a --all         | Select all                       |                                  
-c --category    | Specify categor(y/ies)           | -s []:all | [categories]         
-C --command     | Filter by commands               |                                  
-d --debug       | Debug console output             |                                  
-x --execute     | Run the command after the flag   |                                  
-e --error       | Detailed error output            |                                  
-F --flag        | Filter by flags                  |                                  
-f --force       | Force execution                  |                                  
-h --help        | Show command help                |                                  
-I --installed   | Select installed                 |                                  
-i --interactive | Enter command prompt mode        |                                  
-n --normal      | Normal console output            |                                  
-p --plugin      | Specify plugins(s)               | -p []:all | [plugins]         
-q --quiet       | Reduced console output           |                                  
-s --server      | Specify server(s)                | -s []:all | [servers]            
-S --software    | Specify server software          | -S []:all | [software]           
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

##### Compiling
- Package using `mvn clean package`

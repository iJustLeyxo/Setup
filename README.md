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
Compare          | Compare installed to selected software                              
Connect          | Link this tool to a server installation                             
Eula             | Agree to the Minecraft EULA                                         
Exit             | Exit interactive mode                                               
Find             | Find anything                                                       
Help             | Show usage help                                                     
Install          | Install plugins and server software                                 
Link             | Link any jar archive path to the plugins directory                  
List             | List plugins, categories, servers and server software               
Run              | Run installed server software                                       
Status           | View installation status                                            
Uninstall        | Uninstall plugins, server software and files                        
Update           | Update plugins and software                                         

--------------------------------------- Flags -----------------------------------------
Flag             | Info                             | Usage                            
---------------------------------------------------------------------------------------
-a --All         | Select all                       |                                  
-c --Category    | Specify categor(y/ies)           | -s []:all | [categories]         
-C --Command     | Filter by commands               |                                  
-d --Debug       | Debug console output             |                                  
-x --Execute     | Run the command after the flag   |                                  
-e --Error       | Detailed error output            |                                  
-f --Flag        | Filter by flags                  |                                  
-h --Help        | Show command help                |                                  
-I --Installed   | Select installed                 |                                  
-i --Interactive | Enter command prompt mode        |                                  
-n --Normal      | Normal console output            |                                  
-p --Plugin      | Specify plugins(s)               | -p []:all | [plugins]            
-q --Quiet       | Reduced console output           |                                  
-s --Server      | Specify server(s)                | -s []:all | [servers]            
-S --Software    | Specify server software          | -S []:all | [software]           
-v --Verbose     | Detailed console output          |                                  
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

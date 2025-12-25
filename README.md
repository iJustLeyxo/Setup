## Setup

Cavetale testing server setup utility.

### Usage, commands and flags

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

### Dependencies

Setup depends on [cmdLib][cmdLib].

### Customization

Simply add a new entry with your code to the corresponding enum.
For console command and flag customization, please check the [cmdLib][cmdLib] documentation.

[cmdLib]: https://gitlab.com/iJustLeyxo/cmdlib

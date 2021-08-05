import java.io.*;
import java.util.*;
//parse through command list
public class ParseAndInterpret {
    //counter for where on command list we are
    public int counter = 0;

    public boolean parsingCommand(ArrayList<String> commands, DBServer server) throws IOException{
        //create command calls class
        if (commands.get(counter).equals("CREATE")){
            CreateCommand createCommand = new CreateCommand();
            counter++;
            if (!createCommand.createCommand(commands,server, this)){
                return false;
            }
            //checks ends on ;
            if (commands.get(counter).equals(";")){
                return true;
            }
        }
        //for use command and calls class
        if (commands.get(counter).equals("USE")){
            counter++;
            UseCommand use = new UseCommand();
            if(!use.useDatabase(commands, server, this)){
                //sets personalised error query
                server.setErrorMessage("Invalid query");
                return false;
            }
            if (commands.get(counter).equals(";")){
                return true;
            }
        }
        //for drop command
         if (commands.get(counter).equals("DROP")){
            DropCommand drop = new DropCommand();
            counter++;
            if (!drop.dropCommands(commands, server, this)){
                server.setErrorMessage("Invalid query");
                return false;
            }
             if (commands.get(counter).equals(";")){
                 return true;
             }
        }
         //for insert command
         if (commands.get(counter).equals("INSERT")){
            counter++;
            InsertCommand insert = new InsertCommand();
            if(!insert.insertCommand(commands, server, this)){
                server.setErrorMessage("Invalid query");
                return false;
            }
             if (commands.get(counter).equals(";")){
                 return true;
             }
        }
         //for alter command
         if (commands.get(counter).equals("ALTER")){
             counter++;
             AlterCommand alter = new AlterCommand();
             if (!alter.alterCommand(commands, server, this)){
                 server.setErrorMessage("Invalid query");
                 return false;
             }
             //writes to file after altering
             server.writeDBToFile();
             if (commands.get(counter).equals(";")){
                 return true;
             }
         }
         //update command
        if (commands.get(counter).equals("UPDATE")){
            counter++;
            UpdateCommand update = new UpdateCommand();
            if(!update.updateCommand(commands, server, this)){
                server.setErrorMessage("Invalid query");
                return false;
            }
            //writes to file
            server.writeDBToFile();
            if (commands.get(counter).equals(";")){
                return true;
            }
        }
        //delete command
        if (commands.get(counter).equals("DELETE")){
            counter++;
            DeleteCommand delete = new DeleteCommand();
            if(!delete.deleteCommand(commands, server, this)){
                server.setErrorMessage("Invalid query");
                return false;
            }
            server.writeDBToFile();
            if (commands.get(counter).equals(";")){
                return true;
            }
        }
        //select command
        if (commands.get(counter).equals("SELECT")){
            counter++;
            SelectCommand select = new SelectCommand();
            if(!select.selectCommand(commands, server, this)) {
                return false;
            }
            if (commands.get(counter).equals(";")){
                return true;
            }
        }
        //join command
        if (commands.get(counter).equals("JOIN")){
            counter++;
            JoinCommand join = new JoinCommand();
            if(!join.joinCommand(commands, server, this)){
                server.setErrorMessage("Invalid query");
                return false;
            }
            if (commands.get(counter).equals(";")){
                return true;
            }

        }
        return false;
    }

}

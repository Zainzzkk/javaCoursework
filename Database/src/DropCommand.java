import java.util.ArrayList;
//class to deal with drop command
public class DropCommand {

    public boolean dropCommands(ArrayList<String> commands, DBServer server, ParseAndInterpret parser){
        //if database selected to drop
        if (commands.get(parser.counter).equals("DATABASE")) {
            parser.counter++;
            //ensures name of database is given
            if (commands.get(parser.counter).equals(";")) {
                return false;
            }
            if (!dropDatabaseCommand(commands, server, parser)){
                return false;
            }
            return true;
        }
        //if table selected to be dropped
        if (commands.get(parser.counter).equals("TABLE")){
            //ensures DB is selected
            if(server.returnCurrentDBName().equals("DEFAULT")){
                return false;
            }
            parser.counter++;
            if(!dropTableCommand(commands, server, parser)){
                return false;
            }
            return true;
        }
        return false;

    }
    //function to deal with database being deleted
    private boolean dropDatabaseCommand(ArrayList<String> commands, DBServer server, ParseAndInterpret parser){
            //calls database class
            Database toDelete = new Database();
            //adds name from command list
            if (!toDelete.dropDatabase(server, commands.get(parser.counter))){
                return false;
            }
            parser.counter++;

            return true;
    }
    //for deleting table
    private boolean dropTableCommand(ArrayList<String> commands, DBServer server, ParseAndInterpret parser) {
        //ensures table name is given
        if (commands.get(parser.counter).equals(";")){
            return false;
        }
        //calls table class
        TableModify toDelete = new TableModify();
        //calls delete function in table class
        if (!toDelete.dropTable(commands.get(parser.counter), server)){
            return false;
        }
        parser.counter++;
        //ensures last is ;
        if (!commands.get(parser.counter).equals(";")){
            return false;
        }
        return true;
    }

}

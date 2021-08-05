import java.io.*;
import java.net.*;
import java.util.*;

class StagServer {

    public ArrayList<String> narration;


    public static void main(String args[]) {
        if (args.length != 2) System.out.println("Usage: java StagServer <entity-file> <action-file>");
        else new StagServer(args[0], args[1], 8888);
    }

    public StagServer(String entityFilename, String actionFilename, int portNumber) {

        StagMap map = new StagMap(entityFilename, actionFilename, this);
        StagController controller = new StagController(map, this);
        narration = new ArrayList<>();

        try {
            ServerSocket ss = new ServerSocket(portNumber);
            System.out.println("Server Listening");
            while (true) acceptNextConnection(ss, controller);
        } catch (IOException ioe) {
            System.err.println(ioe);
        }
    }

    private void acceptNextConnection(ServerSocket ss, StagController controller) {
        try {
            // Next line will block until a connection is received
            Socket socket = ss.accept();
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
            processNextCommand(in, out, controller);
            out.close();
            in.close();
            socket.close();
        } catch (IOException ioe) {
            System.err.println(ioe);
        }
    }

    private void processNextCommand(BufferedReader in, PrintWriter out, StagController controller) throws IOException {
        String line = in.readLine();
        controller.nextCommand(line);
        if (!narration.isEmpty()) {
            for (String narr : narration) {
                out.println(narr);
            }
            narration.removeAll(narration);
        }
    }
}



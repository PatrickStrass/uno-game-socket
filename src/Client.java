import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {
    private static final String ADDRESS = "localhost";
    private static final int PORT = 4000;

    public static void main(String[] args) throws UnknownHostException, IOException {
        Socket socket = new Socket(ADDRESS, PORT);
        BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader console = new BufferedReader(new InputStreamReader(System.in));

        String serverMessage; 

        while((serverMessage = input.readLine()) != null) {
            System.out.println("Server: " + serverMessage);

            if(serverMessage.contains("Your turn:")) {
                String playerResponse = console.readLine();
                output.println(playerResponse);
            }
        }

        socket.close();
        input.close();
        output.close();
        console.close();
    }
}

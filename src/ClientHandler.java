import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import classes.Card;
import classes.enums.Color;

public class ClientHandler extends Thread {
    private Socket socket;
    private BufferedReader input;
    private PrintWriter output;
    private List<Card> hand = new ArrayList<>();

    public ClientHandler(Socket socket) throws IOException {
        this.socket = socket;
        this.input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.output = new PrintWriter(socket.getOutputStream(), true);
    }

    @Override
    public void run() {
        try {
            output.println("Welcome to Uno");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getInitialCards(List<Card> cards) {
        hand.addAll(cards);

        // sendMessage("Your initial hand: " + hand);
        sendMessage("Your initial hand:");
        
        for (Card card : hand) {
            sendMessage(card.toString());
        }
    }

    public Card playCard() throws IOException {
        sendMessage("Enter a card to play or type 'draw' to draw a card: ");
        String response = input.readLine();

        if(response.equalsIgnoreCase("draw")) {
            return null;
        }

        for(Card card : hand) {
            if(card.toString().equalsIgnoreCase(response)) { //revisar
                hand.remove(card);
                
                return card;
            }
        }

        sendMessage("Invalid card");
        return playCard();
    }

    public Color chooseColor() throws IOException {
        sendMessage("Type a color: ");
        String response = input.readLine().toUpperCase();
        List<Color> colors = new ArrayList<>(Arrays.asList(Color.values()));

        if(colors.contains(Color.valueOf(response))) {
            return Color.valueOf(response);
        }
    
        sendMessage("Invalid color");
        
        return chooseColor();
    }

    public void addCard(Card card) {
        hand.add(card);
    }

    public boolean hasWon() {
        return hand.isEmpty();
    }

    public void sendMessage(String message) {
        output.println(message);
    }

    public void closeConnection() throws IOException {
        socket.close();
        input.close();
        output.close();
    }
}

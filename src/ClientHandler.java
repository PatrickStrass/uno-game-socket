import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import classes.Card;
import classes.enums.Color;

public class ClientHandler extends Thread {
    private Socket socket;
    private BufferedReader input;
    private PrintWriter output;
    private String username;
    private List<ClientHandler> players;
    private List<Card> hand = new ArrayList<>();

    public ClientHandler(Socket socket, List<ClientHandler> players) throws IOException {
        this.socket = socket;
        this.players = players;
        this.input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.output = new PrintWriter(socket.getOutputStream(), true);
    }

    @Override
    public void run() {
        try {
            output.println("Welcome to Uno\n");

            do {
                this.username = enterUsername();   
            } while (usernameAlreadyExists(this.username, players));

            synchronized(players) {
                players.add(this);
            }

            Server.checkAndStart();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String enterUsername() throws IOException {
        sendMessage("Enter your username: ");
        username = input.readLine().trim();

        if(username == null || username.isEmpty()) {
            sendMessage("Invalid username\n");
            return enterUsername();
        }

        return username;
    }

    private boolean usernameAlreadyExists(String username, List<ClientHandler> players) {
        for(ClientHandler player : players) {
            if(player.getUsername().equals(username)) {
                sendMessage("It already exists a player with this username\n");
                return true;
            }
        }
        
        return false;
    }

    public String getUsername() {
        return username;
    }

    public List<Card> getHand() {
        return hand;
    }

    public void getInitialCards(List<Card> cards) {
        hand.addAll(cards);

        // Uncomment this section if you want to see your initial cards when the game starts
        // sendMessage("Your initial hand:");
        // showHand();
    }

    public void showHand() {
        for (Card card : hand) {
            sendMessage(card.coloredString());
        }

        sendMessage("");
    }  

    public Card playCard() throws IOException {
        sendMessage("Your hand:");
        showHand();
        sendMessage("Enter a card to play or type 'draw' to draw a card: ");
        String response = input.readLine().trim();

        if(response.equalsIgnoreCase("draw")) {
            return null;
        }

        for(Card card : hand) {
            if(card.toString().equalsIgnoreCase(response)) {
                hand.remove(card);
                
                return card;
            }
        }

        sendMessage("Invalid card\n");
        return playCard();
    }

    public Color chooseColor() throws IOException {
        sendMessage("Choose a color: ");
        String response = input.readLine().toUpperCase().trim();

        try {
            return Color.valueOf(response);
        } catch (Exception e) {
            sendMessage("Invalid color\n");
            return chooseColor();
        }
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

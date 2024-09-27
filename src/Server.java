import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import classes.ActionCard;
import classes.Card;
import classes.Deck;
import classes.NumberCard;
import classes.WildCard;
import classes.enums.Action;
import classes.enums.Color;
import classes.enums.Type;
import classes.enums.Wild;

public class Server {
    private static final int PORT = 4000;
    private static final int MIN_NUMBER_PLAYERS = 2;
    private static ArrayList<ClientHandler> players = new ArrayList<>();
    private static Deck deck = new Deck();

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(PORT);
        System.out.println("Server is listening at PORT " + PORT);

        while(players.size() < MIN_NUMBER_PLAYERS) {
            Socket clientSocket = serverSocket.accept();
            System.out.println(clientSocket.getInetAddress() + " connected");
            ClientHandler player = new ClientHandler(clientSocket);
            players.add(player);
            new Thread(player).start();
        }

        if(players.size() == MIN_NUMBER_PLAYERS) {
            startGame();
        }

        serverSocket.close();
    }

    private static void startGame() throws IOException {
        // Deal cards to players
        for (ClientHandler player : players) {
            player.getInitialCards(deck.drawInitialCards());
        }

        // Main game loop
        int currentPlayer = 0;
        Card currentCard = deck.drawCard(); // Start with a random card from the deck

        while(currentCard.getType().equals(Type.ACTION) || currentCard.getType().equals(Type.WILD)) {
            deck.getCards().add(deck.getCards().size() - 1, currentCard);
            currentCard = deck.drawCard();
        }

        broadcast("Game started!\nFirst card is " + currentCard + "\n");

        while(true) {
            ClientHandler player = players.get(currentPlayer);
            // player.sendMessage("Your turn\nCurrent card is " + currentCard);
            Card playedCard = player.playCard();
            //player.sendMessage("Your turn");

            if (playedCard != null && playedCard.matches(currentCard)) {
                currentCard = playedCard;

                broadcast("Player " + (currentPlayer + 1) + " played " + currentCard + "\n");

                if(player.hasWon()) {
                    broadcast("Player " + (currentPlayer + 1) + " has won!");
                    break;
                } 

                if(playedCard instanceof ActionCard) {
                    ActionCard actionCard = (ActionCard) playedCard;

                    if(actionCard.getAction().equals(Action.DRAW_2)) {
                        ClientHandler nextPlayer = players.get((currentPlayer + 1) % 2);

                        broadcast("Player " + currentPlayer + " drew 2 cards\n");
                        nextPlayer.addCard(deck.drawCard());
                        nextPlayer.addCard(deck.drawCard());
                    } else if(actionCard.getAction().equals(Action.REVERSE)) {
                        //TODO when there is more than two players
                    } else if(actionCard.getAction().equals(Action.SKIP)) {
                        currentPlayer = (currentPlayer + 1) % 2; 
                    }
                } else if(playedCard instanceof WildCard) {
                    WildCard wildCard = (WildCard) playedCard;

                    if(wildCard.getWild().equals(Wild.WILD)) {
                        player.sendMessage("Your turn:\nChoose a color");
                        Color selectedColor = player.chooseColor();
                        currentCard = new NumberCard(Type.NUMBER, selectedColor, -1);
                    } else {
                        player.sendMessage("Your turn:\nChoose a color");
                        Color selectedColor = player.chooseColor();
                        currentCard = new NumberCard(Type.NUMBER, selectedColor, -1);

                        int nextPlayer = (currentPlayer + 1) % 2;

                        players.get(nextPlayer).addCard(deck.drawCard());
                        players.get(nextPlayer).addCard(deck.drawCard());
                        players.get(nextPlayer).addCard(deck.drawCard());
                        players.get(nextPlayer).addCard(deck.drawCard());
                    }
                }

                currentPlayer = (currentPlayer + 1) % 2;

            } else if(playedCard == null) {
                Card drawnCard = deck.drawCard();
                player.addCard(drawnCard); 
                player.sendMessage("You drew a card " + drawnCard);
                broadcast("Player " + (currentPlayer + 1) + " draw a card\n");

                currentPlayer = (currentPlayer + 1) % 2;
            } else {
                player.addCard(playedCard);
                player.sendMessage("The card you played does not match");
            }

            // currentPlayer = (currentPlayer + 1) % 2;
        }

        closeConnections();
    }

    private static void broadcast(String message) throws IOException {
        for (ClientHandler player : players) {
            player.sendMessage(message);
        }
    }

    private static void closeConnections() throws IOException {
        for (ClientHandler player : players) {
            player.closeConnection();
        }
    }
}
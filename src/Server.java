import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
    private static final int NUMBER_PLAYERS = 3;
    private static int rotationWay = 1;
    private static List<ClientHandler> players = Collections.synchronizedList(new ArrayList<>());
    private static Deck deck = new Deck();
    private static Deck auxiliaryDeck = new Deck();

    public static void main(String[] args) throws IOException, InterruptedException {
        ServerSocket serverSocket = new ServerSocket(PORT);
        System.out.println("Server is listening at PORT " + PORT);

        while(players.size() < NUMBER_PLAYERS) {
            Socket clientSocket = serverSocket.accept();
            System.out.println(clientSocket.getInetAddress() + " connected");
            ClientHandler player = new ClientHandler(clientSocket, players);
            new Thread(player).start();
        }

        serverSocket.close();
    }

    private static void startGame() throws IOException {
        deck.createCards();

        for (ClientHandler player : players) {
            player.getInitialCards(deck.drawInitialCards());
        }

        int currentPlayer = 0;
        Card currentCard = deck.drawCard();

        while(currentCard.getType().equals(Type.ACTION) || currentCard.getType().equals(Type.WILD)) {
            deck.getCards().add(deck.getCards().size() - 1, currentCard);
            currentCard = deck.drawCard();
        }

        broadcast("Game started!\nFirst card is " + currentCard.coloredString() + "\n");

        while(true) {
            ClientHandler player = players.get(currentPlayer);
            Card playedCard = player.playCard();

            if(player.getHand().size() == 1) {
                broadcast(players.get(currentPlayer).getUsername() + " said Uno!\n");
            }

            if (playedCard != null && playedCard.matches(currentCard)) {
                auxiliaryDeck.getCards().add(currentCard);
                currentCard = playedCard;
                broadcast(players.get(currentPlayer).getUsername() + " played " + currentCard.coloredString() + "\n");

                if(player.hasWon()) {
                    broadcast(players.get(currentPlayer).getUsername() + " has won!");
                    break;
                } 

                if(playedCard instanceof ActionCard) {
                    ActionCard actionCard = (ActionCard) playedCard;

                    if(actionCard.getAction().equals(Action.DRAW_2)) {
                        ClientHandler nextPlayer = players.get((currentPlayer + rotationWay) % NUMBER_PLAYERS);
                        broadcast(players.get((currentPlayer + rotationWay) % NUMBER_PLAYERS).getUsername() + " drew 2 cards\n");

                        nextPlayer.addCard(deck.drawCard());
                        nextPlayer.addCard(deck.drawCard());
                    } else if(actionCard.getAction().equals(Action.REVERSE)) {
                        rotationWay *= -1;
                    } else if(actionCard.getAction().equals(Action.SKIP)) {
                        currentPlayer = (currentPlayer + rotationWay) % NUMBER_PLAYERS; 
                    }
                } else if(playedCard instanceof WildCard) {
                    WildCard wildCard = (WildCard) playedCard;

                    if(wildCard.getWild().equals(Wild.WILD)) {
                        Color selectedColor = player.chooseColor();
                        broadcast(players.get(currentPlayer).getUsername() + " selected the color " + selectedColor.getColorCode() + selectedColor + selectedColor.resetCode() + "\n");
                        currentCard = new NumberCard(Type.NUMBER, selectedColor, -1);
                    } else {
                        Color selectedColor = player.chooseColor();
                        broadcast(players.get((currentPlayer + rotationWay) % NUMBER_PLAYERS).getUsername() + " drew 4 cards\n");
                        broadcast(players.get(currentPlayer).getUsername() + " selected the color " + selectedColor.getColorCode() + selectedColor + selectedColor.resetCode() + "\n");
                        currentCard = new NumberCard(Type.NUMBER, selectedColor, -1);

                        int nextPlayer = (currentPlayer + rotationWay) % NUMBER_PLAYERS;

                        players.get(nextPlayer).addCard(deck.drawCard());
                        players.get(nextPlayer).addCard(deck.drawCard());
                        players.get(nextPlayer).addCard(deck.drawCard());
                        players.get(nextPlayer).addCard(deck.drawCard());
                    }
                }
                currentPlayer = ((currentPlayer + rotationWay) % NUMBER_PLAYERS) < 0 ? players.size() - 1 : (currentPlayer + rotationWay) % NUMBER_PLAYERS;

            } else if(playedCard == null) {
                if(deck.getCards().isEmpty()) {
                    deck.setCards(auxiliaryDeck.getCards());
                }

                Card drawnCard = deck.drawCard();
                player.addCard(drawnCard); 
                player.sendMessage("You drew a card " + drawnCard.coloredString());
                broadcast(players.get(currentPlayer).getUsername() + " drew a card\n");

                currentPlayer = (currentPlayer + 1) % NUMBER_PLAYERS;
            } else {
                player.addCard(playedCard);
                player.sendMessage("The card you played does not match\n");
            }
        }
        closeConnections();
    }

    public static void checkAndStart() throws IOException {
        if(players.size() == NUMBER_PLAYERS) {
            startGame();
        }
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
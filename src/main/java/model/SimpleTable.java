package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import libs.CircularList;
import model.card.Card;
import model.deck.IDeck;

public class SimpleTable implements Table{
    private static final List<Role> totalRoles = List.of(
        Role.SHERIFF,Role.RENEGADE,Role.OUTLAW,Role.OUTLAW,Role.DEPUTY,Role.OUTLAW,Role.OUTLAW);
    
    private IDeck deck;
    private List<Card> discardPile;
    private CircularList<Player> players;
    private Player currentPlayer;
    private List<String> usedCards = new ArrayList<>();
    
    private TurnObservable<List<Player>> choosePlayersObservable = new TurnObservable<>();
    private TurnObservable<Map<Card, Player>> chooseCardsObservable = new TurnObservable<>();
	private int howMany;
	private Message message = null;
	private int distance;
	private List<Card> cardsToChoose;
	private List<Player> choosers;
	private int howManyPerPlayer;
    
    public SimpleTable(final IDeck deck, final int numberOfPlayers) {
        this.deck = deck;
        this.players = getPlayersFromNumber(numberOfPlayers);
        this.getFirstCards();
        this.currentPlayer = this.players.getCurrentElement();
    }

    private CircularList<Player> getPlayersFromNumber(final int playerNumber){
        List<Role> roles = new ArrayList<>(totalRoles.subList(0, playerNumber));
        Collections.shuffle(roles);
        CircularList<Player> c = new CircularList<>(roles.stream().map(r -> new SimplePlayer(r, "player " + Integer.toString(roles.indexOf(r))))
        .collect(Collectors.toList()));
        
        int pos = c.indexOf(c.stream().filter(p -> p.getRole() == Role.SHERIFF).findFirst().get());
        c.setCurrentIndex(pos);
        return c;
    }

    private void getFirstCards() {
        this.players.forEach(p -> {
            this.deck.nextCards(p.getLifePoints()).forEach(c -> p.addCard(c));
        });
    }
    
    @Override
    public IDeck getDeck() {
        if(this.deck.remainigCards() == 0) {
            this.deck.getCards().addAll(this.discardPile);
            this.deck.shuffleDeck();
            this.discardPile.clear();
        }
        return this.deck;
    }
    
    @Override
    public List<Card> getDiscardPile() {
        return this.discardPile;
    }
    
    @Override
    public void discardCard(final Card card) {
        this.discardPile.add(card);
    };

    @Override
    public CircularList<Player> getPlayers() {
        return this.players;
    }

    @Override
    public void removePlayer(Player player) {
        players.remove(player);
    }

    @Override
    public Player getCurrentPlayer() {
        return this.currentPlayer;
    }
    
    @Override
    public void setCurrentPlayer(Player player) {
        this.currentPlayer = player;
    }

    @Override
    public void nextPlayer() {
        this.usedCards.clear();
        this.setCurrentPlayer(players.getNext());
    }

    @Override
    public TurnObservable<List<Player>> getChoosePlayersObservable() {
        return this.choosePlayersObservable;
    }

    @Override
    public TurnObservable<Map<Card, Player>> getChooseCardsObservable() {
        return this.chooseCardsObservable;
    }

    @Override
    public void choosePlayers(int howMany) {
    	this.howMany = howMany;
    	this.message = Message.CHOOSE_PLAYER;
    }

    @Override
    public void choosePlayers(int howMany, int distance) {
    	this.howMany = howMany;
    	this.distance = distance;
    	this.message = Message.CHOOSE_PLAYER_WITH_DISTANCE;
    }

    @Override
    public void chooseCards(List<Card> cardsToChoose, List<Player> choosers, int howManyPerPlayer) {
    	this.cardsToChoose = cardsToChoose;
    	this.choosers = choosers;
    	this.howManyPerPlayer = howManyPerPlayer;
    	this.message = Message.CHOOSE_CARD;
    }

    @Override
    public void playerUsedCard(String cardName) {
        this.usedCards.add(cardName);
    }

    @Override
    public List<String> getPlayerUsedCards() {
        return this.usedCards;
    }
    
    @Override
    public Message getMessage() {
        return this.message;
    }

    public int getDistance() {
        return distance;
    }

    public int getHowMany() {
        return howMany;
    }

    public List<Card> getCardsToChoose() {
        return cardsToChoose;
    }

    public List<Player> getChoosers() {
        return choosers;
    }

    public int getHowManyPerPlayer() {
        return howManyPerPlayer;
    }
}

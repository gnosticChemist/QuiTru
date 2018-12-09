package quitru.gnosticchemist.com.github.Control;

import java.util.ArrayList;
import java.util.Random;

import quitru.gnosticchemist.com.github.Model.Element;
import quitru.gnosticchemist.com.github.Model.Player;

public class GameController {
    ArrayList<Element> cardDeck;
    Player playerA, playerB;
    Player chooser;
    Element.Attributes turnAttribute;
    Random random = new Random();
    private int turns;

    public GameController(ArrayList<Element> cardDeck, Player a, Player b){
        playerA = a;
        playerB = b;
        this.cardDeck = cardDeck;
    }

    public void startGame(){
        for(int c=0;c<5;c++){
            int i = random.nextInt(cardDeck.size());
            playerA.giveCard(cardDeck.get(i));
            cardDeck.remove(i);
            i = random.nextInt(cardDeck.size());
            playerB.giveCard(cardDeck.get(i));
            cardDeck.remove(i);
        }
        turns = 5 + cardDeck.size()/2;
        attributeChoose();
    }

    private void attributeChoose(){
        if(chooser!=null){
            chooser.requestAttributeChoose(this);
            if(chooser == playerA)playerB.updateEnemyChoosing();
            else playerA.updateEnemyChoosing();
        }else switch (random.nextInt(6)){
            case 0: setTurnAttribute(Element.Attributes.Número_atômico); break;
            case 1: setTurnAttribute(Element.Attributes.Eletronegatividade); break;
            case 2: setTurnAttribute(Element.Attributes.x_Energia_de_ionização); break;
            case 3: setTurnAttribute(Element.Attributes.Temperatura_de_fusão); break;
            case 4: setTurnAttribute(Element.Attributes.Massa_atômica); break;
            case 5: setTurnAttribute(Element.Attributes.Raio_atômico); break;
        }
    }

    public void setTurnAttribute(Element.Attributes attribute){
        turnAttribute = attribute;
        choosedPlayerA = false;
        choosedPlayerB = false;
        playerA.requestCardChoose(this, attribute);
        playerB.requestCardChoose(this, attribute);
    }

    public float compareElemnts(Element pA, Element pB, Element.Attributes turnAttribute){
        float x = 0;
        switch (turnAttribute){
            case Massa_atômica:
                x = pA.getMass() - pB.getMass();
                x = -x;
                break;
            case Número_atômico:
                x = pA.getNumber() - pB.getNumber();
                break;
            case Raio_atômico:
                x = pA.getRadium() - pB.getRadium();
                break;
            case Eletronegatividade:
                x = pA.getElectronegativity() - pB.getElectronegativity();
                break;
            case x_Energia_de_ionização:
                x = pA.getIonEnergy() - pB.getIonEnergy();
                break;
            case Temperatura_de_fusão:
                x = pA.getFusion() - pB.getFusion();
                break;
        }
        return x;
    }

    boolean choosedPlayerA = false, choosedPlayerB = false;
    Element pA, pB;
    public void setTurnCard(Element element, Player player){
        if(player.equals(playerA)){
            choosedPlayerA = true;
            pA = element;
            playerB.updateEnemyChoosed();
        }else if(player.equals(playerB)){
            choosedPlayerB = true;
            pB = element;
            playerA.updateEnemyChoosed();
        }
        //new Thread(() -> {
            if (choosedPlayerA && choosedPlayerB) {
                Player winner = null;
                float x = compareElemnts(pA, pB, turnAttribute);
                if (x > 0) winner = playerA;
                else if (x < 0) winner = playerB;
                if (winner != null) winner.points++;
                chooser = winner;
                playerA.updateResult(new Element[]{pA, pB}, winner);
                playerB.updateResult(new Element[]{pB, pA}, winner);

                playerA.removeCard(pA);
                playerB.removeCard(pB);
                turns--;

                if (turns >= 5) {
                    int i = random.nextInt(cardDeck.size());
                    playerA.giveCard(cardDeck.get(i));
                    cardDeck.remove(i);
                    i = random.nextInt(cardDeck.size());
                    playerB.giveCard(cardDeck.get(i));
                    cardDeck.remove(i);
                }
                if (turns > 0) attributeChoose();
                else {
                    Player gameWinner = null;
                    if (playerA.points > playerB.points) gameWinner = playerA;
                    else if (playerB.points > playerA.points) gameWinner = playerB;
                    playerA.updateGameEnd(gameWinner);
                    playerB.updateGameEnd(gameWinner);
                }
            }
        //}).run();

    }

    public String getPlacar(){
        return playerA.points + " x " + playerB.points;
    }

    public int getCards(){
        return cardDeck.size();
    }

    public int getTurns(){ return turns; }

    public Element.Attributes getTurnAttribute() {
        return turnAttribute;
    }
}

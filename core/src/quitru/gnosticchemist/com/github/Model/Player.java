package quitru.gnosticchemist.com.github.Model;

import java.util.ArrayList;

import quitru.gnosticchemist.com.github.Control.GameController;

public abstract class Player {
    private static int index = 0;
    private int ID;

    public Player(){
        ID = index++;
    }

    ArrayList<Element> handCards = new ArrayList<Element>();
    public int points = 0;


    public void giveCard(Element element){
        handCards.add(element);
    }

    public void removeCard(Element element){
        handCards.remove(element);
    }

    public abstract void requestAttributeChoose(GameController gc);
    public abstract void requestCardChoose(GameController gc, Element.Attributes choosedAttribute);
    public abstract void updateEnemyChoosed();
    public abstract void updateResult(Element element[], Player Winner);
    public abstract void updateGameEnd(Player Winner);
    public abstract void updateEnemyChoosing();

    @Override
    public boolean equals(Object o) {
        if(o instanceof Player)return(((Player)o).ID == ID);
        else return false;
    }
}

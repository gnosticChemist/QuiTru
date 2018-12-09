package quitru.gnosticchemist.com.github.Model;

import java.util.Random;

import quitru.gnosticchemist.com.github.Control.GameController;

public class PlayerBot extends Player {
    Random random = new Random();
    @Override
    public void requestAttributeChoose(GameController gc) {
        new Thread(()->{
            try {
                Thread.sleep(6000);
            } catch (InterruptedException e) {}
            switch (random.nextInt(6)){
                case 0: gc.setTurnAttribute(Element.Attributes.Número_atômico); break;
                case 1: gc.setTurnAttribute(Element.Attributes.Eletronegatividade); break;
                case 2: gc.setTurnAttribute(Element.Attributes.x_Energia_de_ionização); break;
                case 3: gc.setTurnAttribute(Element.Attributes.Temperatura_de_fusão); break;
                case 4: gc.setTurnAttribute(Element.Attributes.Massa_atômica); break;
                case 5: gc.setTurnAttribute(Element.Attributes.Raio_atômico); break;
            }
        }).start();
    }


    @Override
    public void requestCardChoose(GameController gc, Element.Attributes choosedAttribute) {
        new Thread(()->{
            try {
                Thread.sleep(random.nextInt(3000));
                Element choose = handCards.get(0);
                for(Element e : handCards)if(gc.compareElemnts(choose,e,choosedAttribute) < 0)choose = e;
                gc.setTurnCard(choose,this);
            } catch (InterruptedException e) {
                System.out.println("INTERRUPTED");
            }
        }).start();
    }

    @Override
    public void updateEnemyChoosed() {

    }

    @Override
    public void updateResult(Element element[], Player Winner) {
    }

    @Override
    public void updateGameEnd(Player Winner) {

    }

    @Override
    public void updateEnemyChoosing() {

    }
}

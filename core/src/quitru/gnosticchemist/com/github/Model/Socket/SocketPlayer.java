package quitru.gnosticchemist.com.github.Model.Socket;


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import quitru.gnosticchemist.com.github.Control.GameController;
import quitru.gnosticchemist.com.github.Model.Element;
import quitru.gnosticchemist.com.github.Model.Player;

public class SocketPlayer extends Player implements Runnable{
    private Socket socket;
    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;
    GameController gc;

    public SocketPlayer(Socket socket){
        try {
            this.socket = socket;
            outputStream = new ObjectOutputStream(socket.getOutputStream());
            inputStream = new ObjectInputStream(socket.getInputStream());
            new Thread(this).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void send(SocketPackage socketPackage){
        try {
            System.out.println("ENVIADO PORRA");
            outputStream.writeObject(socketPackage);
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void giveCard(Element element){
        send(new SocketPackage(SocketPackage.GIVE_CARD,element));
    }

    @Override
    public void removeCard(Element element){
        send(new SocketPackage(SocketPackage.REMOVE_CARD,element));
    }

    @Override
    public void requestAttributeChoose(GameController gc) {
        this.gc = gc;
        send(new SocketPackage(SocketPackage.CHOOSE_ATTRIBUTE,null));
    }

    @Override
    public void requestCardChoose(GameController gc, Element.Attributes choosedAttribute) {
        this.gc = gc;
        send(new SocketPackage(SocketPackage.CHOOSE_CARD,choosedAttribute));
    }

    @Override
    public void updateEnemyChoosed() {
        send(new SocketPackage(SocketPackage.ENEMY_CHOOSED,null));
    }

    @Override
    public void updateResult(Element[] element, Player winner) {
        int winnerCode = 0;
        if(winner!= null){
            if(winner.equals(this))winnerCode = 1;
            else winnerCode = 2;
        }
        send(new SocketPackage(SocketPackage.TURN_RESULT,element,winnerCode));

        send(new SocketPackage(SocketPackage.STATS, gc.getPlacar(),gc.getCards(),gc.getTurns()));
    }

    @Override
    public void updateGameEnd(Player winner) {
        int winnerCode = 0;
        if(winner!= null){
            if(winner.equals(this))winnerCode = 1;
            else winnerCode = 2;
        }
        send(new SocketPackage(SocketPackage.GAME_END,winnerCode));
    }

    @Override
    public void updateEnemyChoosing() {
        send(new SocketPackage(SocketPackage.ENEMY_CHOOSING,null));
    }

    @Override
    public void run() {
        while(socket.isConnected()){
            try {
                if(true){
                    SocketPackage recived = (SocketPackage) inputStream.readObject();
                    switch (recived.calledMethod){
                        case SocketPackage.SET_ATTRIBUTE:
                            gc.setTurnAttribute((Element.Attributes) recived.parameters[0]);
                        break;
                        case SocketPackage.SET_CARD:
                            gc.setTurnCard((Element)recived.parameters[0],this);
                        break;
                    }
                }
                Thread.sleep(250);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

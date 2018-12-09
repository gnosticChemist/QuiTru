package quitru.gnosticchemist.com.github.Model.Socket;

import com.esotericsoftware.kryonet.Client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.management.PlatformLoggingMXBean;
import java.net.Socket;
import java.util.ArrayList;

import quitru.gnosticchemist.com.github.Control.GameController;
import quitru.gnosticchemist.com.github.Model.Element;
import quitru.gnosticchemist.com.github.Model.Player;
import quitru.gnosticchemist.com.github.Model.PlayerBot;

public class SocketPlayerReciever extends GameController implements Runnable {
    private Player player, bot;
    private String points = "0 x 0";
    private int cards = 30;
    private int turns = 20;
    private Element.Attributes turnAttribute;
    private Socket socket;
    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;

    public SocketPlayerReciever(Socket socket, Player player) {
        super(null,null,null);
        this.player = player;
        this.bot = new PlayerBot();
        try {
            this.socket = socket;
            outputStream = new ObjectOutputStream(socket.getOutputStream());
            inputStream = new ObjectInputStream(socket.getInputStream());
            new Thread(this).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Player getWinner(int winnerCode){
        Player winner = null;
        if(winnerCode==1)winner = player;
        else if(winnerCode==2)winner = bot;
        return winner;
    }

    private void send(byte method, Object parameter){
        try {
            outputStream.writeObject(new SocketPackage(method,parameter));
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setTurnAttribute(Element.Attributes attribute){
        send(SocketPackage.SET_ATTRIBUTE,attribute);
    }

    @Override
    public void setTurnCard(Element element, Player player){
        send(SocketPackage.SET_CARD,element);
    }

    @Override
    public void run() {
        while(socket.isConnected()){
            try {
                System.out.println("hmm");
                if(true){
                    System.out.println("aff");
                    SocketPackage recived = (SocketPackage) inputStream.readObject();
                    switch (recived.calledMethod){
                        case SocketPackage.CHOOSE_ATTRIBUTE:
                            player.requestAttributeChoose(this);
                        break;
                        case SocketPackage.CHOOSE_CARD:
                            turnAttribute = (Element.Attributes)recived.parameters[0];
                            player.requestCardChoose(this,turnAttribute);
                        break;
                        case SocketPackage.ENEMY_CHOOSED :
                            player.updateEnemyChoosed();
                        break;
                        case SocketPackage.ENEMY_CHOOSING :
                            player.updateEnemyChoosing();
                        break;
                        case SocketPackage.GAME_END :
                            player.updateGameEnd(getWinner((int) recived.parameters[0]));
                        break;
                        case SocketPackage.GIVE_CARD :
                            player.giveCard((Element)recived.parameters[0]);
                        break;
                        case SocketPackage.REMOVE_CARD :
                            player.removeCard((Element)recived.parameters[0]);
                        break;
                        case SocketPackage.TURN_RESULT :
                            player.updateResult((Element[])recived.parameters[0],getWinner((int) recived.parameters[1]));
                        break;
                        case SocketPackage.STATS:
                            points = (String) recived.parameters[0];
                            cards = (int) recived.parameters[1];
                            turns = (int) recived.parameters[2];
                            turnAttribute = null;
                        break;
                    }
                }
                Thread.sleep(250);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public String getPlacar(){
        return  points;
    }

    @Override
    public int getCards(){ return cards; }

    @Override
    public int getTurns(){ return turns; }

    @Override
    public Element.Attributes getTurnAttribute() {
        return turnAttribute;
    }
}

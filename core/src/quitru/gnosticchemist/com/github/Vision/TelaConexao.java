package quitru.gnosticchemist.com.github.Vision;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;

import quitru.gnosticchemist.com.github.Control.CustomUtils;
import quitru.gnosticchemist.com.github.Control.GameController;
import quitru.gnosticchemist.com.github.Model.ColorDrawable;
import quitru.gnosticchemist.com.github.Model.Element;
import quitru.gnosticchemist.com.github.Model.Socket.SocketPlayer;
import quitru.gnosticchemist.com.github.Model.Socket.SocketPlayerReciever;

/**
 * Manage the network connection between players
 */
public class TelaConexao extends MyScreen {
    private Game game;
    private Label label, labelIP;
    private ScrollPane panel;
    private Server server;
    private Client client;
    private List<InetAddress> list;
    private boolean verify = true;
    private final int tcpPort = 54555,udpPort = 54777, tcpPort2 = 54999;
    private boolean listLock = false;
    private TelaGame gameScreen; //The screen that will be used when the connection begin

    public TelaConexao(Game game, TelaInicial telaInicial){
        super();
        this.game = game;
        gameScreen = new TelaGame(game,telaInicial); //Load the game screen

        //Define sizes to visual element positioning
        float oneCmY = CustomUtils.cmToPixel(1,false);
        float oneCmX = CustomUtils.cmToPixel(1,true);
        float fiveQuarters = oneCmY;//CustomUtils.cmToPixel(1.25f);

        //Generate the font of the User Interface
        BitmapFont font = CustomUtils.generateFont(CustomUtils.FontName.UbuntuBold, (int) (fiveQuarters/2));

        //Create the label and it's style
        Label.LabelStyle labelStyle = new Label.LabelStyle(font,Color.WHITE);
        labelStyle.background = new ColorDrawable(0,0,0,0.75f);
        label = new Label(" Jogadores disponíveis:", labelStyle);
        label.setBounds(0,height -fiveQuarters - oneCmY,width,fiveQuarters);
        stage.addActor(label);

        labelIP = new Label(" Seu IP:", labelStyle);
        labelIP.setBounds(0,height - oneCmY,width,fiveQuarters);
        stage.addActor(labelIP);

        //Create the list and it's style
        List.ListStyle styleList = new List.ListStyle(font,new Color(0.301f,0.609f,0.691f,1),Color.WHITE,new ColorDrawable(new Color(0,0,0,0.25f)));
        list = new List<InetAddress>(styleList);
        list.setSelectedIndex(-1);
        list.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if(listLock)return;
                InetAddress selected = list.getSelected();
                if(selected == null) return;
                try {
                    //If the user select an IP it try to connect and start the game
                    Socket socket = new Socket(selected,tcpPort2);
                    verify = false;
                    if(server!=null)server.stop();
                    new SocketPlayerReciever(socket,gameScreen);
                    game.setScreen(gameScreen);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        //Create a scrollPane to support the list
        ScrollPane.ScrollPaneStyle styleScrollPane = new ScrollPane.ScrollPaneStyle(new ColorDrawable(new Color(0,0,0,0.5f)),null,null,null,null);
        panel = new ScrollPane(list,styleScrollPane);
        panel.setBounds(oneCmX/2,oneCmY,width-oneCmX,height - 2*oneCmY - fiveQuarters);
        stage.addActor(panel);

        //Make a server and start so the device can be found in the network
        Server server = new Server();
        server.start();
        try {
            server.bind(tcpPort,udpPort);

            //This server is to connect the players
            ServerSocket serverSocket = new ServerSocket(tcpPort2);
            new Thread(()->{
                while (verify){try {
                    Socket socket = serverSocket.accept(); //Wait until some connection
                    if(socket != null){
                        //Setup and start the game
                        SocketPlayer player = new SocketPlayer(socket);
                        GameController gameController = new GameController(new ArrayList<Element>(
                                Arrays.asList(CustomUtils.getElements())),
                                gameScreen,player);
                        gameController.startGame();
                        game.setScreen(gameScreen);
                    }
                    Thread.sleep(250);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                }
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Creates a Client to search Servers
        client = new Client();
        client.start();
        new Thread(()->{ //Loop for search the servers
            while (verify){try {
                refreshList(client.discoverHosts(udpPort,1000));
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }}
        }).start();
    }

    private InetAddress myIP = null;

    /**
     * Refresh the UI list of available players
     * @param listIPs
     */
    public void refreshList(java.util.List<InetAddress> listIPs){
        listLock = true;
        if(myIP==null){ //Here it's get the machine IP on WLAN interface
            try {
                NetworkInterface wifiInterface = null;
                Enumeration<NetworkInterface> interfaces = null;
                interfaces = NetworkInterface.getNetworkInterfaces();

                while(interfaces.hasMoreElements()){
                    NetworkInterface networkInterface = interfaces.nextElement();
                    System.out.println(networkInterface.getName());
                    if (networkInterface.getDisplayName().contains("wl"))
                        wifiInterface = networkInterface;
                }
                if(wifiInterface!=null){
                    Enumeration<InetAddress> address = wifiInterface.getInetAddresses();
                    while(address.hasMoreElements())myIP = address.nextElement();
                }
                labelIP.setText(" Seu IP: " + myIP.getHostAddress());
            } catch (SocketException e) {
                e.printStackTrace();
            }
        }

        for(int c = listIPs.size()-1;c>=0;c--){ //Remove all localhost address
            InetAddress ip = listIPs.get(c);
            if(ip.isLoopbackAddress())listIPs.remove(c);
            else if(ip.getHostAddress().equals("127.0.0.1"))listIPs.remove(c);
            else if(myIP!=null && ip.getHostAddress().equals(myIP.getHostAddress()))listIPs.remove(c);
        }

        //Update the list
        list.setItems(listIPs.toArray(new InetAddress[listIPs.size()]));
        list.setSelectedIndex(-1);
        listLock = false;
    }
}

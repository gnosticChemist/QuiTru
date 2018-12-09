package quitru.gnosticchemist.com.github.Control;

import com.badlogic.gdx.Gdx;

import quitru.gnosticchemist.com.github.Model.User;
import quitru.gnosticchemist.com.github.Vision.TelaConexao;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

public class MulticastCommunication implements Runnable{
    private MulticastSocket socketInput;
    private InetAddress endereco;
    private int porta = 12345;
    private User user;
    private TelaConexao telaConexao;
    NetworkInterface net;
    ArrayList<User> listaUsers = new ArrayList<>();
    
//Saidas de dados
    ByteArrayOutputStream byteArrayOutputStream;
    ObjectOutputStream objectOutputStream;
    private DatagramSocket socketOutput;
    public static MulticastCommunication siht;

    public MulticastCommunication(int porta, User user, TelaConexao telaConexao) throws IOException{
        this.user = user;
        user.setEndereco(InetAddress.getLocalHost());
        this.porta = porta;

        socketInput = new MulticastSocket(porta);
//        endereco = InetAddress.getByName("225.0.0.1");
        byte[] endr = {(byte) 225,(byte) 0,(byte) 0,(byte) 1};
        endereco = InetAddress.getByAddress(endr);
        siht = this;
        socketOutput = new DatagramSocket();
        this.telaConexao = telaConexao;

        Enumeration<NetworkInterface> enumeration = NetworkInterface.getNetworkInterfaces();
        while (enumeration.hasMoreElements()){
            NetworkInterface test = enumeration.nextElement();
            System.out.println(test.getName() + ": ");
            for(InetAddress address : Collections.list(test.getInetAddresses())){
                System.out.println("    " + address.getHostAddress());
            }
            if(test.getName().contains("wl"))net = test;
        }
        System.out.println(net.getName());
    }

    public void recarregarLista(){
        listaUsers = new ArrayList<>();
        //telaConexao.refreshList(listaUsers);
        enviar(PacoteEnvio.OP_SEND_USERS);
    }
    
    public void close() throws IOException{
       receberMensagens = false;
       enviar(PacoteEnvio.OP_USER_EXITED);
       socketInput.leaveGroup(endereco);
       socketInput.disconnect();
       socketInput.close();
       socketOutput.disconnect();
       socketOutput.close();
    }
    
    public void start() throws IOException{
        if(net == null){
            socketInput.joinGroup(endereco);
            socketOutput.connect(endereco,porta);
        }
        else{
            InetSocketAddress add = new InetSocketAddress(endereco,porta);
            socketInput.joinGroup(add,net);
            socketOutput.connect(endereco,porta);
            //socketOutput.bind(add);
        }
        System.out.println(socketInput.getInterface().getHostAddress());
        enviar(PacoteEnvio.OP_SEND_USERS);
        new Thread(this).start();
    }

    public User getUserByID(User userID){
        for(User usuarios : listaUsers){
            //if(usuarios.getUserID().equals(userID))return usuarios;
        }
        return null;
    }
    
    private void enviar(byte opcao){
        PacoteEnvio pacoteEnvio = new PacoteEnvio(user, opcao);
        try {
            byteArrayOutputStream = new ByteArrayOutputStream();
            objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(pacoteEnvio);
            byte[] bytesMensagem = byteArrayOutputStream.toByteArray();
            socketOutput.send(new DatagramPacket(bytesMensagem,bytesMensagem.length,endereco, porta));
        } catch (IOException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        }
    }
        
    protected User getUser(InetAddress inetAddress){
        for(User user : listaUsers)if(user.getEndereco().equals(inetAddress))return user;
        return new User(inetAddress.getHostName());
    }
    
    private boolean running = false, receberMensagens = true;
    @Override
    public void run() {
        if(running == true)return;
        else running = true;
        ByteArrayInputStream byteArrayInputStream;
        ObjectInputStream objectInputStream;
        PacoteEnvio pacoteEnvio;
        while(receberMensagens){
            try {

                DatagramPacket pacoteInput = new DatagramPacket(new byte[1023],1023);
                socketInput.receive(pacoteInput);
                System.out.println("aaaaaaaa");
                pacoteEnvio = (PacoteEnvio) new ObjectInputStream(new BufferedInputStream(new ByteArrayInputStream(pacoteInput.getData()))).readObject();
                switch (pacoteEnvio.opcao){
                    case PacoteEnvio.OP_NEW_USER:
                        User user = pacoteEnvio.user;
                        user.setEndereco(pacoteInput.getAddress());
                        listaUsers.add(user);
                        //telaConexao.refreshList(listaUsers);
                        //for(UserListener userL : userListeners)userL.onNewUser(user);
                    break;
                    case PacoteEnvio.OP_REQUESTED_USER:
                        user = (User) pacoteEnvio.user;
                        user.setEndereco(pacoteInput.getAddress());
                        boolean bool = true;
                        for(User userL : listaUsers){
                            if(bool && userL.equals(user))bool = false;
                        }
                        if(bool){
                            listaUsers.add(user);
                            //telaConexao.refreshList(listaUsers);
                            //for(UserListener userL : userListeners)userL.onNewUser(user);
                        }
                    break;
                    case PacoteEnvio.OP_SEND_USERS:
                        enviar(PacoteEnvio.OP_REQUESTED_USER);
                    break;
                    case PacoteEnvio.OP_USER_EXITED:
                        listaUsers.remove(pacoteEnvio.user);
                        //telaConexao.refreshList(listaUsers);
                        //for(UserListener userL : userListeners)userL.onUserExited(saiu);
                    break;
                    }
                
            } catch(Exception ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public InetAddress getEndereco() {
        return endereco;
    }
    
}

class PacoteEnvio implements Serializable {
    public byte opcao;
    public final static byte
            OP_MENSAGEM = 1,
            OP_NEW_USER = 2,
            OP_SEND_USERS = 3,
            OP_REQUESTED_USER =4,
            OP_USER_EXITED = 5;
    public User user;

    public PacoteEnvio(byte opcao) {
        this.opcao = opcao;
    }

    public PacoteEnvio(User user, byte opcao) {
        this.opcao = opcao;
        this.user = user;
    }

    static final long serialVersionUID = 4L;
}
package quitru.gnosticchemist.com.github.Model;

import java.io.Serializable;
import java.net.InetAddress;

public class User implements Serializable {
    private InetAddress endereco;
    private String username;
    final private UserID userID;

    public User(String username) {
        this.username = username;
        userID = new UserID(username);
    }

    public InetAddress getEndereco() {
        return endereco;
    }

    public void setEndereco(InetAddress endereco) {
        if(endereco != null)this.endereco = endereco;
    }

    public String getUsername() {
        return username;
    }
    
    public UserID getUserID(){
        return userID;
    }
    
    @Override
    public String toString(){
        return username;
    }
    
    @Override
    public boolean equals(Object object){
        if(!(object instanceof User)) return false;
        User user = (User)object;
        return user.userID.equals(userID);
    }
    static final long serialVersionUID = 5L;
}

class UserID implements Serializable{
    private final long time;
    private final char first;

    public UserID(String username){
        first=username.charAt(0);
        time = System.currentTimeMillis();
    }

    @Override
    public boolean equals(Object object){
        if(object instanceof UserID){
            UserID usr = (UserID)object;
            return (usr.time == time) && (first == usr.first);
        }else return false;
    }
    static final long serialVersionUID = 6L;
}

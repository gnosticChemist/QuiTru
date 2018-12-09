package quitru.gnosticchemist.com.github.Model.Socket;

import java.io.Serializable;

public class SocketPackage implements Serializable {
    public static final byte    CHOOSE_ATTRIBUTE = 1,
                                CHOOSE_CARD = 2,
                                ENEMY_CHOOSED = 3,
                                ENEMY_CHOOSING = 4,
                                TURN_RESULT = 5,
                                GAME_END = 6,
                                GIVE_CARD = 7,
                                REMOVE_CARD = 8,
                                SET_ATTRIBUTE = 9,
                                SET_CARD = 10,
                                STATS = 11;
    public byte calledMethod;
    public Object[] parameters;

    public SocketPackage(byte calledMethod, Object ... parameters){
        this.calledMethod = calledMethod;
        this.parameters = parameters;
    }
}

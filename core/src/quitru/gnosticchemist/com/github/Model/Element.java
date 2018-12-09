package quitru.gnosticchemist.com.github.Model;

import java.io.Serializable;

public class Element implements Serializable {
    public enum Attributes {Número_atômico, Raio_atômico, Massa_atômica, Temperatura_de_fusão, x_Energia_de_ionização, Eletronegatividade};
    int n, r; //number, radium
    float m, f, i, e; //mass, fusion, firstIonEnergy, electronegativity
    String s; //symbol
    int x,y;

    public Element(String s){
        n = 10;
        r = 38;
        m = 20.18f;
        f = -248.59f;
        i = 2080.7f;
        e = 0;
        x = 2;
        y = 7;
        this.s = s;
    }

    public static String attributeName(Attributes attribute){
        if(attribute!=Attributes.x_Energia_de_ionização)return attribute.name().replace('_',' ');
        else return "1ª Energia de ionização";
    }

    public Element(){
    }

    public String getSymbol(){return s;}

    public float getMass() {
        return m;
    }

    public float getElectronegativity() {
        return e;
    }

    public float getFusion(){
        return f;
    }

    public float getIonEnergy() {
        return i;
    }

    public int getNumber() {
        return n;
    }

    public int getRadium() {
        return r;
    }

    public int getColumn(){return y;}

    public int getRow(){return x;}

    @Override
    public boolean equals(Object o) {
        if(o instanceof Element)return n == ((Element)o).n;
        else return false;
    }
}


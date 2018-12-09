package quitru.gnosticchemist.com.github.Control;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.utils.Json;

import java.util.HashMap;

import quitru.gnosticchemist.com.github.Model.Element;
import quitru.gnosticchemist.com.github.Model.ElementInfo;

public class CustomUtils {
    public enum FontName { UbuntuBold};
    private static Json json = new Json();
    public static Texture background = new Texture("Fundo.jpg");
    private static Element[] elements, elementDeck, elementsSelected = null;
    private static HashMap<Integer,Element> eHashMap = new HashMap<Integer, Element>();
    private static int[] eDeckNumbers,eSelectedNumbers;
    private static HashMap<Integer,ElementInfo> infoHashMap = new HashMap<Integer, ElementInfo>();

    static FreeTypeFontGenerator generatorUbuntuBold = new FreeTypeFontGenerator(Gdx.files.internal("ubuntu-bold.ttf"));
    static FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
    public static BitmapFont generateFont(FontName fontName, int size){
        parameter.size = size;
        if(fontName == FontName.UbuntuBold)return generatorUbuntuBold.generateFont(parameter);
        return null;
    }

    static float density = 0;
    public static float cmToPixel(float cm, boolean xAxis){
        float value = cm;
        if(xAxis)value*=Gdx.graphics.getPpcX();
        else value*=Gdx.graphics.getPpcY();
        return value;
    }

    public static Element[] getElements(){
        if(elements == null){
            elements = json.fromJson(Element[].class,Gdx.files.internal("elements_info/elements.json"));
            for(Element e : elements)eHashMap.put(e.getNumber(),e);
        }
        return elements;
    }

    public static int[] getDeckNumbers(){
        if(eDeckNumbers == null){
            FileHandle file = Gdx.files.local("elements_info/elementDeck.json");
            if(!file.exists()){
                Gdx.files.local("elements_info/").mkdirs();
                Gdx.files.internal("elements_info/elementDeck.json").copyTo(file);
            }
            file = Gdx.files.local("elements_info/elementDeck.json");
            eDeckNumbers = json.fromJson(int[].class,file);
        }
        return eDeckNumbers;
    }

    public static int[] getSelectedNumbers(){
        if(eSelectedNumbers == null){
            FileHandle file = Gdx.files.local("elements_info/elementsSelected.json");
            if(!file.exists()){
                Gdx.files.local("elements_info/").mkdirs();
                Gdx.files.internal("elements_info/elementsSelected.json").copyTo(file);
            }
            file = Gdx.files.local("elements_info/elementsSelected.json");
            eSelectedNumbers = json.fromJson(int[].class,file);
        }
        return eSelectedNumbers;
    }

    public static Element[] getSelectedDeck(){
        if(elementsSelected == null){
            if(eSelectedNumbers == null)getSelectedNumbers();
            elementsSelected = new Element[eSelectedNumbers.length];
            if(eHashMap.isEmpty())getElements();
            for(int c=0;c<eSelectedNumbers.length;c++)elementsSelected[c] = eHashMap.get(eSelectedNumbers[c]);
        }
        return elementsSelected;
    }

    public static Element[] getDeck(){
        if(elementDeck == null){
            if(eSelectedNumbers==null)getSelectedNumbers();
            elementDeck = new Element[eDeckNumbers.length];
            for(int c=0;c<eDeckNumbers.length;c++)elementDeck[c] = eHashMap.get(eSelectedNumbers[c]);
        }
        return elementDeck;
    }

    public static void setElementsSelected(int selecteds[]){
        eSelectedNumbers = selecteds;
        elementsSelected = new Element[selecteds.length];
        for(int c=0;c<eSelectedNumbers.length;c++)elementsSelected[c] = eHashMap.get(eSelectedNumbers[c]);
        FileHandle file = Gdx.files.local("elements_info/elementsSelected.json");
        file.writeString(json.toJson(selecteds),false);
    }

    public static ElementInfo getInfo(int number){
        if(infoHashMap.isEmpty()){
            ElementInfo info[] = json.fromJson(ElementInfo[].class,Gdx.files.internal("elements_info/elementInfo.json"));
            for(ElementInfo elementInfo : info)infoHashMap.put(elementInfo.getNumber(),elementInfo);
        }
        return infoHashMap.get(number);
    }
}

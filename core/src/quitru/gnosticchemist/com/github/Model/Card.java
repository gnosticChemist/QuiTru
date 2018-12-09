package quitru.gnosticchemist.com.github.Model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;

public class Card extends Group {
    static Label.LabelStyle fontSymbol, fontAttributes, fontNumber;
    static String attributes[]= {"Massa: ","\nRaio : ","\nFusão: ","\n1ª EI: ","\nEleng: "};
    static TextureRegionDrawable back,alcalino,metal,nobres,padrao;
    static Image backImage;

    public Element element;
    Image card,flipImage;
    Label attributesLabel, symbol, number;
    boolean fliped = true;

    public Card(){
        Texture cardTexture = new Texture("carta.jpg");
        if(back == null){
            nobres = new TextureRegionDrawable(new TextureRegion(new Texture("carta.jpg")));
            metal = new TextureRegionDrawable(new TextureRegion(new Texture("cardMetal.jpg")));
            alcalino = new TextureRegionDrawable(new TextureRegion(new Texture("cardAlcalino.jpg")));
            back = new TextureRegionDrawable(new TextureRegion(new Texture("cardBack.jpg")));
            padrao = new TextureRegionDrawable(new TextureRegion(new Texture("cardPadrao.jpg")));
        }
        card = new Image(cardTexture);
        if(backImage == null)backImage = new Image(back);
        flipImage = new Image(back);
        addActor(card);

        setBounds(0,0,cardTexture.getWidth(),cardTexture.getHeight());

        float x = 0;
        float aurea = cardTexture.getHeight()-cardTexture.getWidth();
        float y = aurea;
        float hAtributes = aurea*10/13;
        float wAtributes = cardTexture.getWidth()*3/4;

        if(fontSymbol == null){
            FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("ubuntu-bold.ttf"));
            FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
            //parameter.size = cardTexture.getWidth()/2;
            parameter.borderColor = Color.BLACK;
            parameter.borderWidth = 2;
            parameter.size = 96;
            BitmapFont fontBit = generator.generateFont(parameter);
            parameter.borderWidth = 0;
            float scale = ((float)cardTexture.getWidth())/192;
            fontBit.getData().setScale(scale);
            fontSymbol = new Label.LabelStyle(fontBit,Color.WHITE);
            parameter.size =(int) hAtributes/6;
            if(parameter.size%2 == 1)parameter.size++;
            generator = new FreeTypeFontGenerator(Gdx.files.internal("ubuntuMono.ttf"));
            fontAttributes = new Label.LabelStyle(generator.generateFont(parameter),Color.WHITE);
            parameter.size*=1.25;
            fontNumber = new Label.LabelStyle(generator.generateFont(parameter),Color.WHITE);
        }


        symbol = new Label("",fontSymbol);
        symbol.setAlignment(Align.center);
        symbol.setSize(cardTexture.getWidth(),cardTexture.getWidth());

        symbol.setPosition(x,y);
        addActor(symbol);

        String texto = attributes[0] + attributes[1] + attributes[2] + attributes[3] + attributes[4];

        attributesLabel = new Label(texto,fontAttributes);
        attributesLabel.setAlignment(Align.left);
        float gap = (cardTexture.getWidth() -wAtributes)/2;
        attributesLabel.setPosition(gap,gap);
        attributesLabel.setSize(wAtributes,hAtributes);
        addActor(attributesLabel);

        number = new Label("   ",fontNumber);
        number.setAlignment(Align.right);
        number.setPosition(cardTexture.getWidth() - gap -number.getWidth()*1.1f,gap + hAtributes - number.getHeight()*1.1f);
        addActor(number);

        setScale(0.4f);
        addActor(flipImage);
    }

    public void setElement(Element e){
        element = e;

        switch (element.n){
            case 3:case 11:case 12:case 19:case 20:case 37:case 38:case 55:case 87:case 88:
                card.setDrawable(alcalino);
            break;
            case 13:case 22:case 25:case 26:case 28:case 29:case 30:case 47:case 56:case 74:case 78:case 79:case 80:case 82:
                card.setDrawable(metal);
            break;
            case 2:case 10:case 18:case 36:case 54:case 86:
                card.setDrawable(nobres);
            break;
            default:
                card.setDrawable(padrao);
            break;
        }

        String texto = attributes[0] + element.m;
        texto+= attributes[1] + element.r;
        texto+= attributes[2] + element.f;
        texto+= attributes[3] + element.i;
        texto+= attributes[4] + element.e;

        attributesLabel.setText(texto);
        number.setText(""+element.n);
        symbol.setText(element.s);
        //addActor(flipImage);
    }

    public void flip(){
        fliped = !fliped;
        if(fliped)addActor(flipImage);
        else removeActor(flipImage);
    }

    public void setFliped(boolean fliped) {
        this.fliped = fliped;
        if(fliped)addActor(flipImage);
        else removeActor(flipImage);
    }
/*
    @Override
    public void draw(Batch batch, float parentAlpha) {
        if(fliped)batch.draw(back,getX(),getY(),getWidth()*getScaleX(),getHeight()*getScaleY());
        else super.draw(batch, parentAlpha);
    }*/
}

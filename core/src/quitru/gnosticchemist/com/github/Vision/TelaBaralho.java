package quitru.gnosticchemist.com.github.Vision;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.util.HashMap;
import java.util.Set;

import quitru.gnosticchemist.com.github.Control.CustomUtils;
import quitru.gnosticchemist.com.github.Model.ColorDrawable;
import quitru.gnosticchemist.com.github.Model.Element;
import quitru.gnosticchemist.com.github.Model.ElementInfo;

public class TelaBaralho extends MyScreen implements InputProcessor {
    private Game game;
    static TelaBaralho telaBaralho;
    private TelaInicial telaInicial;
    private InputMultiplexer multiplexer; //Manage events for both stage and general input
    private OrthographicCamera camera;    //Camera to move view
    private int xCenter, yCenter;
    private float cameraMaxX, cameraMaxY;
    //UI
    private Group descAttributes;
    private SelectBox<String> selectBox;
    private HashMap<Integer, ElementFrame> elementFrames;
    HashMap<Integer, Element> selectedFrames;
    //Max an min values for each element attribute (Used to colour elements)
    private float values[][] = {{Float.MAX_VALUE,Float.MIN_VALUE},{Float.MAX_VALUE,Float.MIN_VALUE},{Float.MAX_VALUE,Float.MIN_VALUE},{Float.MAX_VALUE,Float.MIN_VALUE},{Float.MAX_VALUE,Float.MIN_VALUE},{Float.MAX_VALUE,Float.MIN_VALUE}};
    //Elemente view
    private Group showElement;
    private Label seTitle, seText;
    private float seGapX,seGapY;

    public TelaBaralho(Game game, TelaInicial telaInicial){
        super();
        //Graphics and Outside things
        this.game = game;
        this.telaInicial = telaInicial;
        telaBaralho = this;

        //Screen Settings
        xCenter = width/2;
        yCenter = height/2;

        //Stage and camera
        camera = new OrthographicCamera(width,height);
        camera.update();
        stage = new Stage(new ScreenViewport(camera));

        //Event listener
        multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(stage);
        multiplexer.addProcessor(this);


        //Element Buttons position calculations
        float sizeFrameX = CustomUtils.cmToPixel(1.25f,true);
        float sizeFrameY = CustomUtils.cmToPixel(1.25f,false);
        float gapX = CustomUtils.cmToPixel(0.1f,true);
        float gapY = CustomUtils.cmToPixel(0.1f,false);
        float positionsX[] = new float[20];
        float start = sizeFrameX/3;
            // X axis
        positionsX[1] = start;
        start+=sizeFrameX + gapX;
        positionsX[2] = start;
        start+=sizeFrameX + gapX;
        for(int c=9;c<19;c++){
            positionsX[c]=start;
            start+=sizeFrameX + gapX;
        }
        for(int c=3;c<9;c++){
            positionsX[c]=start;
            start+=sizeFrameX + gapX;
        }

        cameraMaxX = start + sizeFrameX/3 - xCenter - gapX; //Just and camera calculation that shorts code

            // Y axis
        start = height - 4*sizeFrameY/3;
        float positionsY[] = new float[11];
        for(int c=1;c<positionsY.length;c++){
            positionsY[c]=start;
            start -= sizeFrameY + gapY;
        }
        cameraMaxY = start +yCenter +2*sizeFrameY/3;
        System.out.println(start + " " + cameraMaxY + " " + height);

            //Element buttons creation
        elementFrames = new HashMap<Integer, ElementFrame>();
        selectedFrames = new HashMap<Integer, Element>();
        Element[] elementos = CustomUtils.getElements();
        ElementFrame.initialize(sizeFrameX,sizeFrameY);
        for(Element e : elementos){
            //Max and Min values of attributes calculation
            if(e.getNumber() < values[0][0])values[0][0] = e.getNumber();
            if(e.getNumber() > values[0][1])values[0][1] = e.getNumber();
            if(e.getRadium() < values[1][0])values[1][0] = e.getMass();
            if(e.getRadium() > values[1][1])values[1][1] = e.getRadium();
            if(e.getMass() < values[2][0])values[2][0] = e.getMass();
            if(e.getMass() > values[2][1])values[2][1] = e.getMass();
            if(e.getFusion() < values[3][0])values[3][0] = e.getFusion();
            if(e.getFusion() > values[3][1])values[3][1] = e.getFusion();
            if(e.getIonEnergy() < values[4][0])values[4][0] = e.getIonEnergy();
            if(e.getIonEnergy() > values[4][1])values[4][1] = e.getIonEnergy();
            if(e.getElectronegativity() < values[5][0])values[5][0] = e.getElectronegativity();
            if(e.getElectronegativity() > values[5][1])values[5][1] = e.getElectronegativity();

            //Frame creation
            ElementFrame frame = new ElementFrame(e);
            frame.setPosition(positionsX[e.getRow()],positionsY[e.getColumn()]);
            stage.addActor(frame);
            elementFrames.put(frame.element.getNumber(),frame);
        }
        for(int i : CustomUtils.getDeckNumbers())elementFrames.get(i).setBlocked(false);
        for(int i : CustomUtils.getSelectedNumbers())elementFrames.get(i).setSelected(true);


        //User Interface
        int sizeFont = (int) sizeFrameY/2;
        if(sizeFont%2 == 1)sizeFont++;
        BitmapFont font = CustomUtils.generateFont(CustomUtils.FontName.UbuntuBold,sizeFont);

        List.ListStyle styleList = new List.ListStyle(font,Color.WHITE,new Color(0.301f,0.609f,0.691f,1),new ColorDrawable(new Color(0,0,0,0.75f)));
        ScrollPane.ScrollPaneStyle styleScrollPane = new ScrollPane.ScrollPaneStyle(new ColorDrawable(new Color(0,0,0,0.5f)),null,null,null,null);
        SelectBox.SelectBoxStyle styleBox = new SelectBox.SelectBoxStyle(font,Color.WHITE,new ColorDrawable(new Color(0,0,0,0.75f)),styleScrollPane,styleList);
        selectBox = new SelectBox<String>(styleBox);
        selectBox.setPosition(positionsX[9],positionsY[1]);
        selectBox.setWidth(6*sizeFrameX);

        Array<String> opcoes = new Array<String>(7);
        opcoes.add(" --x--");
        for(Element.Attributes attribute : Element.Attributes.values())opcoes.add(" " + Element.attributeName(attribute));
        selectBox.setItems(opcoes);
        selectBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {colorElements();}
        });
        stage.addActor(selectBox);
        colorElements();

        TextButton.TextButtonStyle infoStyle = new TextButton.TextButtonStyle();
        infoStyle.up = new ColorDrawable(0,0,0,0.5f);
        infoStyle.font = font;
        infoStyle.fontColor = Color.WHITE;
        TextButton infoButton = new TextButton("Informações", infoStyle);
        infoButton.setPosition(positionsX[15],positionsY[1]);
        infoButton.setWidth(4*sizeFrameX);
        infoButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                descAttributes.setPosition(camera.position.x -width/2,camera.position.y-height/2);
                descAttributes.setVisible(true);
            }
        });
        stage.addActor(infoButton);

        TextButton warning = new TextButton("",infoStyle);
        warning.getStyle().up = new ColorDrawable(0,0,0,0.75f);
        warning.setSize(width,height);
        warning.getLabel().setWrap(true);
        warning.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                warning.setVisible(false);
            }
        });
        stage.addActor(warning);
        warning.setVisible(false);


        TextButton selectButton = new TextButton("Selecionar",infoStyle);
        selectButton.setPosition(positionsX[15],positionsY[2]+sizeFrameY/2);
        selectButton.setWidth(4*sizeFrameX);
        selectButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if(!ElementFrame.selectionMode){
                    selectButton.setText("Salvar");
                    ElementFrame.selectionMode = true;
                }else{
                    int selecteds = selectedFrames.values().size();
                    if(selecteds == 40){
                        ElementFrame.selectionMode = false;
                        Set<Integer> values = selectedFrames.keySet();
                        int intValues[] = new int[values.size()];
                        int c=0;
                        for(Integer i : values)intValues[c++] = i;

                        CustomUtils.setElementsSelected(intValues);
                        selectButton.setText("Selecionar");
                        warning.setPosition(camera.position.x -width/2,camera.position.y-height/2);
                        warning.setText("Deck de cartas salvo !");
                        warning.setVisible(true);
                    }else{
                        String warningText;
                        if(selecteds<40)warningText = "Selecione mais " + (40-selecteds);
                        else warningText = "Deselecione " + (selecteds - 40);
                        warningText += " elementos para completar o seu deque de 40 caartas";
                        warning.setText(warningText);
                        warning.setPosition(camera.position.x -width/2,camera.position.y-height/2);
                        warning.setVisible(true);
                    }
                }
            }
        });
        stage.addActor(selectButton);
        warning.toFront();

        //Element Viewer
        float cm1x = CustomUtils.cmToPixel(1,true),
              cm1y = CustomUtils.cmToPixel(1,false);
        float cm075x = cm1x*3/4,
              cm075y = cm1y*3/4;
        float cm05x = cm1x/2,
              cm05y = cm1y/2;

        Label.LabelStyle style = new Label.LabelStyle();
        style.font = CustomUtils.generateFont(CustomUtils.FontName.UbuntuBold, (int) cm075y);
        seTitle = new Label("--",style);
        seTitle.setBounds(cm075x,height-cm075y-cm1y,width-2*cm075x,cm1y);
        seTitle.setAlignment(Align.center);

        style = new Label.LabelStyle();
        style.font = CustomUtils.generateFont(CustomUtils.FontName.UbuntuBold, (int) cm05y);
        seText = new Label("",style);
        seText.setBounds(cm075x,0,width-2*cm075x,height-cm075y-cm1y);
        seText.setAlignment(Align.topLeft);
        seText.setWrap(true);
        seText.getStyle().font.getData().markupEnabled = true;

        ScrollPane.ScrollPaneStyle seScrollStyle = new ScrollPane.ScrollPaneStyle(null,null,null,null,null);
        ScrollPane seScroll = new ScrollPane(seText,seScrollStyle);
        seScroll.setScrollingDisabled(true,false);
        seScroll.setBounds(cm075x,0,width-2*cm075x,height-cm075y-cm1y);

        showElement = new Group();
        Image back = new Image(new ColorDrawable(0,0,0,0.75f));
        //back.setBounds(cm05x,cm05y,width-cm1x,height-cm1y);
        back.setBounds(0,0,width,height);
        seGapX = back.getWidth()/2;
        seGapY = back.getHeight()/2;
        showElement.addActor(back);
        showElement.addActor(seScroll);
        showElement.addActor(seTitle);
        stage.addActor(showElement);
        showElement.setVisible(false);

        TextButton seClose = new TextButton("X", new TextButton.TextButtonStyle(new ColorDrawable(0.302f,0.612f,0.614f,0.5f),null,null,style.font));
        seClose.setBounds(width-cm1x,height-cm1y,cm075x,cm075y);
        seClose.getLabel().setAlignment(Align.center);
        seClose.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                showElement.setVisible(false);
            }
        });
        showElement.addActor(seClose);


        //Attributes description
        Group attributesDescription = new Group();

        Label.LabelStyle adLabelStyle = new Label.LabelStyle(CustomUtils.generateFont(CustomUtils.FontName.UbuntuBold, (int) cm05y),Color.WHITE);
        adLabelStyle.font.getData().markupEnabled = true;
        adLabelStyle.font.getData().flipped = true;

        Label adLabel = new Label("",adLabelStyle);
        adLabel.setBounds(cm05x,cm05y,width -cm1x,height - cm1y);
        adLabel.setWrap(true);
        adLabel.setAlignment(Align.topLeft);
        attributesDescription.addActor(adLabel);

        Json json = new Json();
        String adDesc[] = json.fromJson(String[].class,Gdx.files.internal("elements_info/attribute_desc.json"));
        String adText = "";
        int c = 0;
        for(Element.Attributes attribute : Element.Attributes.values()){
            adText += "[#4D9C9DFF]" + Element.attributeName(attribute);
            adText += "\n[]" + adDesc[c] + "\n\n";
            c++;
        }
        adLabel.setText(adText);

        float prefHeight = adLabel.getPrefHeight();
        adLabel.setHeight(prefHeight);
        adLabel.setY(cm05y);
        attributesDescription.setBounds(cm05x,height-prefHeight-cm1y,width,prefHeight+cm1y);

        ScrollPane.ScrollPaneStyle adScrollStyle = new ScrollPane.ScrollPaneStyle(new ColorDrawable(0,0,0,0.75f),null,null,null,null);
        ScrollPane adScroll = new ScrollPane(attributesDescription,adScrollStyle);
        adScroll.setScrollingDisabled(true,false);
        adScroll.setBounds(0,0,width,height);

        TextButton adClose = new TextButton("X", new TextButton.TextButtonStyle(new ColorDrawable(0.302f,0.612f,0.614f,0.5f),null,null,adLabelStyle.font));
        adClose.setBounds(width-cm1x,height-cm1y,cm075x,cm075y);
        adClose.getLabel().setAlignment(Align.center);
        adClose.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                descAttributes.setVisible(false);
            }
        });

        descAttributes = new Group();
        descAttributes.addActor(adScroll);
        descAttributes.addActor(adClose);
        stage.addActor(descAttributes);
        descAttributes.setVisible(false);
    }

    /**
     * Color the elements according to they attributes
     */
    void colorElements(){
        int index = selectBox.getSelectedIndex() -1;
        if(index>=0){
            float delta = values[index][1] - values[index][0];
            delta = 125/delta;
            Color color = new Color(1,1,1,0.5f);
            for(ElementFrame eFrame : elementFrames.values()){
                if(eFrame.blocked)continue;
                float value = -values[index][0];
                switch (index){
                    case 0:value+=eFrame.element.getNumber();
                    break;
                    case 1:value+=eFrame.element.getRadium();
                    break;
                    case 2:value+=eFrame.element.getMass();
                    break;
                    case 3:value+=eFrame.element.getFusion();
                    break;
                    case 4: value+=eFrame.element.getIonEnergy();
                    break;
                    case 5: value+=eFrame.element.getElectronegativity();
                    break;
                }
                eFrame.setBackgroundColor(color.fromHsv(value*delta,1,1));
            }
        }else{
            Color color = new Color(1,1,1,0.5f);
            for(ElementFrame eFrame : elementFrames.values())if(!eFrame.blocked)eFrame.setBackgroundColor(color);
        }
    }

    boolean viewElement = false;
    /**
     * Load and show all information about an element, use null to close te element view
     */
    public void setViewElement(Element element){
        if(element == null){
            showElement.setVisible(false);
            viewElement = false;
        }else{
            ElementInfo elementInfo = CustomUtils.getInfo(element.getNumber());
            seTitle.setText(element.getSymbol() + " - " + elementInfo.getName());
            String desc = "";
            desc += "[#4D9C9DFF]" + Element.attributeName(Element.Attributes.Número_atômico) + ":[]\n     " + element.getNumber() + '\n';
            desc += "[#4D9C9DFF]" + Element.attributeName(Element.Attributes.Massa_atômica) + ":[]\n     " + element.getMass() + " u\n";
            desc += "[#4D9C9DFF]" + Element.attributeName(Element.Attributes.Raio_atômico) + ":[]\n     " + element.getRadium() + " pm\n";
            desc += "[#4D9C9DFF]" + Element.attributeName(Element.Attributes.Temperatura_de_fusão) + ":[]\n     " + element.getFusion() + " °C\n";
            desc += "[#4D9C9DFF]" + Element.attributeName(Element.Attributes.x_Energia_de_ionização) + ":[]\n     " + element.getIonEnergy() + " kJ/mol\n";
            desc += "[#4D9C9DFF]" + Element.attributeName(Element.Attributes.Eletronegatividade) + ":[]\n     " + element.getElectronegativity();
            desc += "\n[#4D9C9DFF]Exemplos de usos:[]" + elementInfo.getDescription();
            seText.setText(desc);
            showElement.setVisible(true);
            showElement.setPosition(camera.position.x-seGapX,camera.position.y-seGapY); //Center of the view
            viewElement = true;
        }
    }

    /**
     * Move the screen when is touched
     */
    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        float deltaX = -Gdx.input.getDeltaX();
        float deltaY = Gdx.input.getDeltaY();
        if(camera.position.x + deltaX < xCenter) deltaX = xCenter -camera.position.x;
        else if(camera.position.x+deltaX > cameraMaxX) deltaX = cameraMaxX -camera.position.x;
        if(camera.position.y + deltaY > yCenter) deltaY = yCenter -camera.position.y;
        else if(camera.position.y+deltaY < cameraMaxY) deltaY = cameraMaxY -camera.position.y;
        camera.translate(deltaX,deltaY);
        return true;
    }

    /**
     *  Close the element view
     */
    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        //if(viewElement)setViewElement(null);
        return false;
    }

    @Override
    public void show(){
        Gdx.input.setInputProcessor(multiplexer);
    }

    /**
     *  Used to go back to the main screen
     */
    @Override
    public boolean keyDown(int keycode) {
        if(keycode == Input.Keys.BACK || keycode == Input.Keys.BACKSPACE || keycode == Input.Keys.ESCAPE){
            game.setScreen(telaInicial);
            telaInicial.regainFocus();
        }
        return false;
    }

    @Override
    public boolean keyTyped(char character) { return false; }

    @Override
    public boolean keyUp(int keycode) { return false; }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) { return false; }

    @Override
    public boolean mouseMoved(int screenX, int screenY) { return false; }

    @Override
    public boolean scrolled(int amount) { return false; }
}

/**
 * Class that's used to show the elements as buttons
 */
class ElementFrame extends TextButton{
    private static TextButtonStyle styleButton = new TextButtonStyle();
    private static float sizeButtonX, sizeButtonY;
    private static SpriteDrawable spriteDrawable; //Used as background
    //Colors to use in the buttons
    private static Color selectedBackground = new Color(1,1,1,0.5f),
                         unblockedBackground = new Color(0.5f,0.5f,0.5f,0.25f),
                         blockedBackground = new Color(0.25f,0.25f,0.25f,0.5f),
                         blockedFont = new Color(0.5f,0.5f,0.5f,0.5f),
                         unblockedFont = new Color(1,1,1,0.5f);
    static boolean selectionMode = false;
    boolean selected = false, blocked = true;
    Element element;

    /**
     * Pre-load things that will be used to construct the buttons
     * @param sizeButtonX The width of the button
     * @param sizeButtonY The height of the button
     */
    static void initialize(float sizeButtonX,float sizeButtonY){
        spriteDrawable = new ColorDrawable(new Color(1,1,1,0));
        styleButton.fontColor = blockedFont;
        styleButton.up = spriteDrawable.tint(blockedBackground);
        int size = (int) (sizeButtonY/2);
        if(size%2 == 1)size++;
        styleButton.font = CustomUtils.generateFont(CustomUtils.FontName.UbuntuBold,size);
        ElementFrame.sizeButtonX = sizeButtonX;
        ElementFrame.sizeButtonY = sizeButtonY;
    }

    public ElementFrame(Element element){
        super(element.getSymbol(),new TextButtonStyle(styleButton));
        this.element = element;
        setSize(sizeButtonX,sizeButtonY);
        getLabel().setAlignment(Align.center);
        addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if(!blocked){
                    if(selectionMode) setSelected(!selected);
                    else TelaBaralho.telaBaralho.setViewElement(element);
                }
            }
        });
    }

    public void setBlocked(boolean blocked){
        this.blocked = blocked;
        if(blocked){
            getStyle().up = spriteDrawable.tint(blockedBackground);
            getStyle().fontColor = blockedFont;
        }else{
            getStyle().up =  spriteDrawable.tint(unblockedBackground);
            getStyle().fontColor = unblockedFont;
        }
    }

    public void setSelected(boolean selected){
        this.selected = selected;
        if(selected){
            TelaBaralho.telaBaralho.selectedFrames.put(element.getNumber(),element);
            getStyle().fontColor = Color.WHITE;
            getStyle().up =  spriteDrawable.tint(selectedBackground);
        }else{
            TelaBaralho.telaBaralho.selectedFrames.remove(element.getNumber());
            getStyle().fontColor = unblockedFont;
            getStyle().up =  spriteDrawable.tint(unblockedBackground);
        }
    }

    public void setBackgroundColor(Color color){
        if(selected)color.a = 0.5f;
        else color.a = 0.25f;
        getStyle().up = spriteDrawable.tint(color);
    }
}


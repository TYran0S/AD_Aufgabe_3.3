package GUI;

import java.awt.image.IndexColorModel;
import java.util.ArrayList;
import java.util.List;
import aufgabe3_1.*;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.*;
import javafx.stage.Stage;

public class Karte extends Application implements View {

    Path[] pathlist;
    List<Node> cities;
    List<Connection> connections;
    List<Connection> customconnections;
    Controller con;
    BorderPane root;
    BorderPane startscr;
    GridPane button_pane;
    Pane karten_pane;
    Pane benutzer_pane;
    // ScrollPane scrpane;
    TextArea ausgabe_area;
    TextField eingabe_text_feld;
    TextField ausgabe_text_feld;
    Scene scene;
    Stage primaryStage;
    List <Integer> xCord;
    List <Integer> yCord;
    Label[] city = new Label[10];
    Label lastlabel = null;

    int x1 = 0 ;
    int y1 = 0 ;
    int x2 = 0 ;
    int y2 = 0 ;
    int cityId1, citiId2;


    final public static int BEST6 = 1365;
    // final public static int BEST10 = 2255;
    final public static int BEST10 = 15;
    final public static int BEST20 = 4205;

    final int DEFAULT_CYCLES = 1000;

    Image city_image = new Image(getClass().getResourceAsStream("/resource/haus_symbol_small.jpg"));

    public static void main(String[] args) {
        List<List<Integer>> t = new ArrayList<List<Integer>>();
        t.add(new ArrayList<Integer>());
        t.add(new ArrayList<Integer>());
        t.get(0).add(5);
        t.get(0).add(2);
        t.get(0).add(3);
        t.get(0).add(4);
        t.get(0).add(6);
        t.get(1).add(1);
        t.get(1).add(1);
        t.get(1).add(1);
        t.get(1).add(1);
        t.get(1).add(1);

        ACOImpl.ACOImplInit(t);
        launch();
    }

    @Override
    public void start(Stage primaryStage) {
        // Controller initializieren
        this.primaryStage = primaryStage;
        primaryStage.setTitle("Strassenplan");

        root = new BorderPane();
        button_pane = new GridPane();
        karten_pane = new Pane();
        benutzer_pane = new Pane();
        // scrpane = new ScrollPane();
        root.setCenter(button_pane);
        //root.setCenter(scrpane);
        // scrpane.setContent(karten_pane);
        //Buttons erstellen
        makeChoiceButtons();


        scene = new Scene(root, 1024, 600);
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);

        //Hintergrundbild
        //scene.getStylesheets()
        //.add(Karte.class.getResource("/resource/Login.css")
        //.toExternalForm());

        primaryStage.show();
    }

    //Buttons und Text fï¿½r die Bedienung
    private void makeButtons() {
        root.setCenter(karten_pane);
        button_pane = new GridPane();
        primaryStage.setResizable(true);
        root.setTop(button_pane);
        Button default_button = new Button(DEFAULT_CYCLES + " Cycles!");
        default_button.setPrefSize(150, 20);
        default_button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                con.doSteps(DEFAULT_CYCLES);
            };
        }
        );

        Button start_button = new Button("Benutzerdefinierte Cycles durchlaufen!");
        start_button.setPrefSize(250, 20);
        start_button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    System.out.println(Integer.parseInt(eingabe_text_feld.getText()));
                    con.doSteps(Integer.parseInt(eingabe_text_feld.getText()));
                } catch (NumberFormatException e) {
                    eingabe_text_feld.setText("Int-Werte <0 eingeben");
                }
                ;
            }
        });
        int count = 0;

        Button benutzer_Stadt = new Button("Benutzer definierter Straßenplan");
        benutzer_Stadt.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event)
        {
            root.setCenter(benutzer_pane);  
            getCoordinatesPerClick();

        }


        });

        ausgabe_area = new TextArea("Beste Route");
        ausgabe_area.setEditable(false);
        ausgabe_area.setPrefRowCount(4);
        root.setBottom(ausgabe_area);
        //ausgabe_text_feld = new TextField("Beste Route");
        //ausgabe_text_feld.setMinHeight(10);
        //ausgabe_text_feld.setEditable(false);
        //root.setBottom(ausgabe_text_feld);

        eingabe_text_feld = new TextField("Anzahl Cycles");
        eingabe_text_feld.setOnMouseClicked(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent event) {
                eingabe_text_feld.setText("");
            }
        });

        eingabe_text_feld.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    System.out.println(Integer.parseInt(eingabe_text_feld.getText()));
                    con.doSteps(Integer.parseInt(eingabe_text_feld.getText()));
                } catch (NumberFormatException e) {
                    eingabe_text_feld.setText("Int-Werte <0 eingeben");
                }
                ;
            }
        });
        // Element, Spalte,Zeile
        button_pane.add(default_button, 1, 2);
        button_pane.add(start_button, 2, 2);
        button_pane.add(eingabe_text_feld, 3, 2);
        button_pane.add(benutzer_Stadt, 4, 2);
    }


    private void getCoordinatesPerClick() {

        if (xCord == null) {
            xCord = new ArrayList<Integer>();
            yCord = new ArrayList<Integer>();
        }
        benutzer_pane.setOnMouseClicked(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent event) {
                if (event.getButton() == MouseButton.PRIMARY){
                    int x,y;
                    //System.out.println(event.getButton().toString());
                    event.getButton().toString();
                    x = (int) event.getX();
                    y = (int) event.getY();
                    if (xCord.contains(x) && yCord.contains(y)) {
                        System.out.println("Stadt existiert bereits");
                    }
                    xCord.add(x);
                    yCord.add(y);
                    //System.out.println("click");
                    //int index = yCord.indexOf(yCord.get(yCord.size()-1));
                    int index = yCord.indexOf(y);
                    int length = index + 1;
                    if (length >= 10) {
                        System.out.println("max 10 cities");
                        return;
                    }
                    city[index] = new Label("" + (index), new ImageView( city_image));
                    city[index].setLayoutX(x);
                    city[index].setLayoutY(y);
                    city[index].setId(String.valueOf(index));
                    //city[yCord.indexOf(yCord.get(yCord.size()-1))].setText(String.valueOf(yCord.indexOf(yCord.size()-1)));
                    city[index].setText(String.valueOf(index));
                    city[index].setOnMouseClicked(new EventHandler<MouseEvent>(){
                        public void handle(MouseEvent event){
                            if (event.getButton() == MouseButton.SECONDARY){
                                Label label;
                                label= ((Label)event.getSource());
                                if ( x1 != 0 && y1 != 0){
                                    y2 = y1;
                                    x2 = x1;
                                }
                                x1 = (int)label.getLayoutX();
                                y1 = (int)label.getLayoutY();
                                //if ()
                                //LineTo lineto = new LineTo(
                                Path path = new Path();
                                MoveTo moveTo = new MoveTo();
                                /* wenn 2 staedte angeclickt wurden */
                                if ( x2 != 0) {

                                    //customconnections.add(new Connection(
                                    moveTo.setX(x1);
                                    moveTo.setY(y1);

                                    LineTo lineTo = new LineTo();
                                    lineTo.setX(x2);
                                    lineTo.setY(y2);
                                    int distance = (int)(Math.sqrt( ((x2 -x1)*(x2 - x1 )) + ( (y2 - y1) * (y2 - y1) ) ));
                                    System.out.println("distance = " + distance );
                                    path.getElements().add(moveTo);
                                    path.getElements().add(lineTo);

                                    path.setStrokeWidth(2);
                                    path.setStroke(Color.BLACK);
                                    benutzer_pane.getChildren().add(path);
                                    label.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/resource/haus_symbol_small_blue.png"))));
                                    System.out.println("labelId: " + label.getId());
                                    if (label != null )
                                        lastlabel.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/resource/haus_symbol_small.jpg"))));
                                }
                                lastlabel = label;

                            }
                        }});
                    benutzer_pane.getChildren().add(city[index]);
                    ausgabe_area.setText("Position X = " + x + "Position Y = " + y + "\n");

                }
            }
        });
    }

    //Buttons fï¿½r die Auswahl der Anzahl
    private void makeChoiceButtons(){
        Button button_6 = new Button("6 Staedte");
        //button_pane = new GridPane();
        button_6.setPrefSize(1024, 200);
        button_6.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/resource/6staedte.png"))));
        button_6.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                scene.getStylesheets().clear();
                //root.setTop(button_pane);
                makeButtons();
                letsGO(6, "GR6.csv");
            };
        }
        );

        Button button_10 = new Button("Pentagram");
        button_10.setPrefSize(1024, 200);
        button_10.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/resource/Pentagram.png"))));
        button_10.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                scene.getStylesheets().clear();
                makeButtons();
                //letsGO(10, "GR10v2.csv");
                letsGO(10, "GRpenta.csv");
            };
        }
        );

        Button button_20 = new Button("20 Staedte");
        button_20.setPrefSize(1024, 200);
        button_20.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/resource/20staedte.png"))));
        button_20.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                scene.getStylesheets().clear();
                makeButtons();
                letsGO(20, "GR20v3.csv");
            };
        }
        );

        button_pane.add(button_6, 1, 1);
        button_pane.add(button_10, 1, 2);
        button_pane.add(button_20, 1, 3);

    }

    //Controller erst starten wenn Anzahl der Stï¿½dte ausgewï¿½hlt wurde.
    private void letsGO(int anzahl, String datei){
        this.con = new ControlUnit(this, anzahl, datei);
        cities = con.giveNodelist(true);
        connections = con.giveConnections(true);
        // Array mit "Strich" Elementen
        pathlist = new Path[con.giveConnections(true).size()];
        drawCities();
    }

    //Controller ruft diese Methode auf um die neue beste Route zu makieren
    @Override
    public boolean newPath(final List<Integer> path, final int laenge, final List<Integer> visited, final int color) {

        // Restaurieren (alle Straï¿½en schwarz)
        for (int j = 0; j < pathlist.length; j++) {
            pathlist[j].setStroke(Color.BLACK);
        }
        // Nur bestimmte Straï¿½en rot
        for (int i = 0; i < visited.size(); i++) {
            pathlist[visited.get(i)-1].setStroke(getColor(3));
        }

        //Ausgabe des Path als String
        String ausgabe = new String();
        for (int j = 0; j < path.size(); j++) {
            ausgabe = ausgabe + "=>" +path.get(j);
        }
        ausgabe = ausgabe + "\nLaenge: " + laenge + "m\n";
        if (cities.size()==6) {
            ausgabe = ausgabe + "Bestsolution: " + BEST6 + "\n\n\n\n\n";
        }
        if(cities.size()==10){
            ausgabe = ausgabe + "Bestsolution: " + BEST10 + "\n";
        }
        if(cities.size()==20){
            ausgabe = ausgabe + "Bestsolution: " + BEST20 + "\n";
        }

        ausgabe_area.setText(ausgabe);

        return false;
    }

    private Paint getColor(int color) {
        switch(color)
        {
            case 1: return Color.DARKGOLDENROD;
            case 2: return Color.AQUA;
            case 3: return Color.BISQUE;
            case 4: return Color.CHOCOLATE;
            case 5: return Color.CYAN;
            case 6: return Color.GREEN;
            case 7: return Color.YELLOW;
            case 8: return Color.RED;
            case 9: return Color.ROSYBROWN;
            case 10: return Color.HOTPINK;
        }
        return Color.TOMATO;
    }

    // BOOLEAN AUSGABE NOCH ANPASSEN!!
    public boolean drawCities() {
        boolean ausgabe = false;

        if (cities.size()==6) {
            draw6();
            ausgabe = true;
        }
        if(cities.size()==10){
            //draw10();
            drawpenta();
            ausgabe=true;
        }
        if(cities.size()==20){
            draw20();
            ausgabe=true;
        }

        return ausgabe;
    }

    //Methode um 6 Stï¿½dte zu zeichen
    public void draw6() {


        // City-Symbole auf der Karte anzeigen
        Label[] city_label = new Label[cities.size()];
        int[] x_koordinaten = { 100, 400, 100, 320, 560, 620 };
        int[] y_koordinaten = { 100, 100, 290, 270, 100, 295 };

        drawLines(city_label, x_koordinaten, y_koordinaten);
    }

    //Methode um 10 Stï¿½dte zu zeichen
    public void draw10() {

        // City-Symbole auf der Karte anzeigen
        Label[] city_label = new Label[cities.size()];
        int[] x_koordinaten = { 103, 333, 119, 518, 486, 730, 783, 934, 1128, 1143};
        int[] y_koordinaten = { 103, 103, 384, 103, 382, 305, 100, 500, 92, 492};

        drawLines(city_label, x_koordinaten, y_koordinaten);
    }
    //Methode um 20 Stï¿½dte zu zeichen
    public void draw20() {


        // City-Symbole auf der Karte anzeigen
        Label[] city_label = new Label[cities.size()];
        int[] x_koordinaten = { 192, 143, 172, 101,48, 310, 318, 608, 396, 328,
            465, 648, 816, 853, 597, 900, 901, 747, 669, 416 };
        int[] y_koordinaten = { 18, 112, 289, 434, 578, 570, 405, 561, 311, 195,
            150, 236, 429, 553, 408, 274, 63, 145, 47, 74};

        drawLines(city_label, x_koordinaten, y_koordinaten);
    }

    public void drawpenta() {

        // City-Symbole auf der Karte anzeigen
        Label[] city_label = new Label[cities.size()];
        int[] x_koordinaten = { 420, 420, 314, 240, 346, 313, 500, 541, 525, 600};
        int[] y_koordinaten = { 30, 82, 185, 170, 320, 380, 320, 380, 185, 170};

        drawLines(city_label, x_koordinaten, y_koordinaten);
    }
    //Methode zum Platzieren der Hï¿½user und der Straï¿½en
    private void drawLines(Label[] city_label, int[] x_koordinaten,
            int[] y_koordinaten) {
        for (int i = 0; i < city_label.length; i++) {
            city_label[i] = new Label("" + (i + 1), new ImageView(
                        city_image));
            city_label[i].setLayoutX(x_koordinaten[i]);
            city_label[i].setLayoutY(y_koordinaten[i]);
            karten_pane.getChildren().add(city_label[i]);
        }

        for (int j = 0; j < pathlist.length; j++) {
            pathlist[j] = new Path();
            MoveTo moveTo = new MoveTo();
            // Gehe alle Connections durch und hole dir vo der
            // jeweiligen Connection die Stï¿½dte
            // Dann holt er sich fuer MoveTo von der Stadt 0 die X und Y
            // koordinte und dann von Stadt 1 fuer LineTo
            moveTo.setX(x_koordinaten[connections.get(j).cities.get(0) - 1]);
            moveTo.setY(y_koordinaten[connections.get(j).cities.get(0) - 1]);

            LineTo lineTo = new LineTo();
            lineTo.setX(x_koordinaten[connections.get(j).cities.get(1) - 1]);
            lineTo.setY(y_koordinaten[connections.get(j).cities.get(1) - 1]);
            pathlist[j].getElements().add(moveTo);
            pathlist[j].getElements().add(lineTo);

            pathlist[j].setStrokeWidth(2);
            pathlist[j].setStroke(Color.BLACK);

            karten_pane.getChildren().add(pathlist[j]);
        }
    }
}
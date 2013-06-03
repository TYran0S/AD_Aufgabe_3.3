package GUI;

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
    List<Connection> customconnections = new ArrayList<Connection>();
    Controller con;
    BorderPane root;
    BorderPane startscr;
    GridPane button_pane;
    BorderPane bottom;
    GridPane bottomRigth;
    Pane karten_pane;
    Pane benutzer_pane;
    TextArea ausgabe_area;
    TextField eingabe_text_feld;
    TextField ausgabe_text_feld;
    TextField pakete;
    TextField capaCity;
    Scene scene;
    Stage primaryStage;
    List<Integer> xCord;
    List<Integer> yCord;
    List<Integer> cityIds = new ArrayList<Integer>();
    List<Node> nodes = new ArrayList<Node>();
    // Label[] city = new Label[10];
    Label label;
    List<Label> cityLabel = new ArrayList<Label>(); /*
                                                     * labels fuer die
                                                     * selbstgebaute Karte
                                                     */
    List<Integer> selectedLabels = new ArrayList<Integer>(); /*
                                                              * labelIds fuer
                                                              * die zu
                                                              * markierten
                                                              * Labels
                                                              */
    List<Path> pathArrList = new ArrayList<Path>();
    Label lastlabel = null;
    boolean free = true;
    String paketAnzeige = "";
    List<List<Integer>> nodePackage = new ArrayList<List<Integer>>();
    int connectionID = 0;
    int capa = 0;


    int x1 = 0;
    int y1 = 0;
    int x2 = 0;
    int y2 = 0;
    int cityId1, cityId2, anzahl;

    final public static int BEST6 = 1365;
    final public static int BEST10 = 15;
    final public static int BEST20 = 4205;

    final int DEFAULT_CYCLES = 10000;

    Image city_image = new Image(getClass().getResourceAsStream("/resource/haus_symbol_small.jpg"));
    Image sel_city_image = new Image(getClass().getResourceAsStream("/resource/haus_symbol_small_blue.png"));

    public static void main(String[] args) {
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
        bottom = new BorderPane();
        bottomRigth = new GridPane();
        root.setCenter(button_pane);
        makeChoiceButtons();

        scene = new Scene(root, 1024, 800);
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    // Buttons und Text fï¿½r die Bedienung
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
        });

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

        ausgabe_area = new TextArea("Beste Route");
        ausgabe_area.setEditable(false);
        ausgabe_area.setPrefRowCount(8);
        ausgabe_area.setMinHeight(20);
        root.setBottom(bottom);
        bottom.setLeft(ausgabe_area);
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
    }

    private void getCoordinatesPerClick() {
        bottom.setRight(bottomRigth);
        pathlist = new Path[30];
        // liste Nodes
        nodePackage.add(new ArrayList<Integer>());
        // liste Pakete
        nodePackage.add(new ArrayList<Integer>());
        if (xCord == null) {
            xCord = new ArrayList<Integer>();
            yCord = new ArrayList<Integer>();
        }
        benutzer_pane.setOnMouseClicked(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent event) {

                if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                    int x, y;
                    x = (int) event.getX();
                    y = (int) event.getY();
                    if (xCord.contains(x) && yCord.contains(y)) {
                        System.out.println("Stadt existiert bereits");
                        return;
                    }
                    xCord.add(x);
                    yCord.add(y);
                    handleDoubleClick(x, y);
                }
            }
        });
    }

    // Buttons fuer die Auswahl der Anzahl
    private void makeChoiceButtons() {
        Button button_6 = new Button("6 Staedte");
        button_6.setPrefSize(1024, 200);
        button_6.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/resource/6staedte.png"))));
        button_6.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                scene.getStylesheets().clear();
                makeButtons();
                letsGO(6, "GR6.csv");
            };
        });

        Button button_10 = new Button("Pentagram");
        button_10.setPrefSize(1024, 200);
        button_10.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/resource/Pentagram.png"))));
        button_10.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                scene.getStylesheets().clear();
                makeButtons();
                // letsGO(10, "GR10v2.csv");
                letsGO(10, "GRpenta.csv");
            };
        });

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
        });

        Button button_User = new Button("Benutzer Stadt");
        button_User.setPrefSize(1024, 200);
        button_User.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                scene.getStylesheets().clear();
                root.setCenter(benutzer_pane);
                TextArea info = new TextArea("Info: Folgende Eingaben sind möglich \n" + "doppel Klick : erstelle Stadt \n"
                    + "Klick auf Stadt: Stadt makieren -> dann Auswahl der 2. Stadt um eine Verbindung zu erzeugen \n"
                    + "rechtsKlick auf Stadt: Paketmenge bestimmen ");
                info.setEditable(false);
                info.setPrefRowCount(4);
                info.setStyle("-fx-text-fill: white;" + "-fx-background-color: black;");
                root.setTop(info);
                ausgabe_area = new TextArea("Beste Route");
                ausgabe_area.setEditable(false);
                ausgabe_area.setPrefRowCount(6);
                ausgabe_area.setMinHeight(20);
                root.setBottom(bottom);
                bottom.setLeft(ausgabe_area);
                getCoordinatesPerClick();

            };
        });

        button_pane.add(button_6, 1, 1);
        button_pane.add(button_10, 1, 2);
        button_pane.add(button_20, 1, 3);
        button_pane.add(button_User, 1, 4);

    }

    /* TODO: nur zum finden */
    private void startUserCity(List<Connection> connection, List<Node> nod) {

        this.con = new ControlUnit(this, nod.size(), connection, nod, nodePackage, capa);
        System.out.println("Anzahl = " + anzahl);
        System.out.println("Connections = " + connection.size());
        System.out.println("Nodes = " + nod.size());
        pathlist = new Path[pathArrList.size()];
        for (Path path : pathArrList) {
            pathlist[pathArrList.indexOf(path)] = path;
        }
        con.doSteps(DEFAULT_CYCLES);

    }

    // Controller erst starten wenn Anzahl der Stï¿½dte ausgewï¿½hlt wurde.
    private void letsGO(int anzahl, String datei) {
        this.con = new ControlUnit(this, anzahl, datei);
        cities = con.giveNodelist(true);
        connections = con.giveConnections(true);
        // Array mit "Strich" Elementen
        pathlist = new Path[con.giveConnections(true).size()];
        drawCities();
    }

    // Controller ruft diese Methode auf um die neue beste Route zu makieren
    @Override
    public boolean newPath(final List<List<Integer>> path, List<Connection> c) {
        String ausgabe = new String();
        if (path == null || path.isEmpty()) {
            ausgabe = ausgabe + "No Way found\n";
            ausgabe_area.setText(ausgabe);
            return false;
        }
        // Restaurieren (alle Strassen schwarz)
        for (int j = 0; j < pathlist.length; j++) {
            pathlist[j].setStroke(Color.BLACK);
        }

        for (int j = 0; j < path.size(); j++) {
            if (path.get(j).size() > 1) {
                List<Integer> visited = new ACOImpl().visitedStreets(path.get(j), c);
                for (int i = 0; i < visited.size(); i++) {
                    pathlist[visited.get(i) - 1].setStroke(getColor(j));
                }
            }
        }
        int gesamtLaenge = 0;
        // Ausgabe des Path als String
        for (int i = 0; i < path.size(); i++) {
            if (path.get(i).size() > 1) {
                for (int j = 0; j < path.get(i).size(); j++) {
                    ausgabe = ausgabe + "=>" + path.get(i).get(j);
                }

                ausgabe = ausgabe + "\nLaenge: " + new ACOImpl().length(path.get(i), c) + "m\n";
                gesamtLaenge += new ACOImpl().length(path.get(i), c);
            }
        }
        ausgabe += Ant.output;
        bottom.setLeft(null);
        bottom.setCenter(ausgabe_area);
        ausgabe_area.setText(paketAnzeige + ausgabe + "\nGesamt Laenge: " + gesamtLaenge );
        bottom.setRight(null);
        return false;
    }

    private Paint getColor(int color) {
        switch (color) {
            case 1:
                return Color.DARKGOLDENROD;
            case 2:
                return Color.AQUA;
            case 3:
                return Color.BISQUE;
            case 4:
                return Color.CHOCOLATE;
            case 5:
                return Color.CYAN;
            case 6:
                return Color.GREEN;
            case 7:
                return Color.YELLOW;
            case 8:
                return Color.RED;
            case 9:
                return Color.ROSYBROWN;
            case 10:
                return Color.HOTPINK;
        }
        return Color.TOMATO;
    }

    // BOOLEAN AUSGABE NOCH ANPASSEN!!
    public boolean drawCities() {
        boolean ausgabe = false;

        if (cities.size() == 6) {
            draw6();
            ausgabe = true;
        }
        if (cities.size() == 10) {
            // draw10();
            drawpenta();
            ausgabe = true;
        }
        if (cities.size() == 20) {
            draw20();
            ausgabe = true;
        }

        return ausgabe;
    }

    // Methode um 6 Stï¿½dte zu zeichen
    public void draw6() {

        // City-Symbole auf der Karte anzeigen
        Label[] city_label = new Label[cities.size()];
        int[] x_koordinaten = { 100, 400, 100, 320, 560, 620 };
        int[] y_koordinaten = { 100, 100, 290, 270, 100, 295 };

        drawLines(city_label, x_koordinaten, y_koordinaten);
    }

    // Methode um 10 Stï¿½dte zu zeichen
    public void draw10() {

        // City-Symbole auf der Karte anzeigen
        Label[] city_label = new Label[cities.size()];
        int[] x_koordinaten = { 103, 333, 119, 518, 486, 730, 783, 934, 1128, 1143 };
        int[] y_koordinaten = { 103, 103, 384, 103, 382, 305, 100, 500, 92, 492 };

        drawLines(city_label, x_koordinaten, y_koordinaten);
    }

    // Methode um 20 Stï¿½dte zu zeichen
    public void draw20() {

        // City-Symbole auf der Karte anzeigen
        Label[] city_label = new Label[cities.size()];
        int[] x_koordinaten = { 192, 143, 172, 101, 48, 310, 318, 608, 396, 328, 465, 648, 816, 853, 597, 900, 901, 747, 669, 416 };
        int[] y_koordinaten = { 18, 112, 289, 434, 578, 570, 405, 561, 311, 195, 150, 236, 429, 553, 408, 274, 63, 145, 47, 74 };

        drawLines(city_label, x_koordinaten, y_koordinaten);
    }

    public void drawpenta() {

        // City-Symbole auf der Karte anzeigen
        Label[] city_label = new Label[cities.size()];
        int[] x_koordinaten = { 420, 420, 314, 240, 346, 313, 500, 541, 525, 600 };
        int[] y_koordinaten = { 30, 82, 185, 170, 320, 380, 320, 380, 185, 170 };

        drawLines(city_label, x_koordinaten, y_koordinaten);
    }

    // Methode zum Platzieren der Hï¿½user und der Straï¿½en
    private void drawLines(Label[] city_label, int[] x_koordinaten, int[] y_koordinaten) {
        for (int i = 0; i < city_label.length; i++) {
            city_label[i] = new Label("" + (i + 1), new ImageView(city_image));
            city_label[i].setLayoutX(x_koordinaten[i]);
            city_label[i].setLayoutY(y_koordinaten[i]);
            karten_pane.getChildren().add(city_label[i]);
        }

        for (int j = 0; j < pathlist.length; j++) {
            pathlist[j] = new Path();
            MoveTo moveTo = new MoveTo();
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

    private void drawSingleLine(int x1, int y1, int x2, int y2, int conIndex) {

        Path path = new Path();
        MoveTo moveTo = new MoveTo();
        moveTo.setX(x1);
        moveTo.setY(y1);
        LineTo lineTo = new LineTo();
        lineTo.setX(x2);
        lineTo.setY(y2);
        /* calculate distance */
        path.getElements().add(moveTo);
        path.getElements().add(lineTo);
        path.setStrokeWidth(2);
        path.setStroke(Color.BLACK);
        benutzer_pane.getChildren().add(path);
        // pathlist[conIndex] = path;
        pathArrList.add(path);

    }

    boolean conExists(int cityId1, int cityId2) {
        for (Connection connection : customconnections) {
            if (connection.cities.contains(cityId1) && connection.cities.contains(cityId2)) {
                return true;
            }
        }
        return false;
    }

    public void removeConnection(int index) {
    }
    public void removeCity() {

    }
    public void handleDoubleClick( int x, int y){

        int index = yCord.indexOf(y);
        int length = index + 1;
        if (length >= 10) {
            System.out.println("max 10 cities");
            return;
        }
        cityIds.add(index);
        Label tmp = new Label("" + (index), new ImageView(city_image));
        tmp.setLayoutX(x);
        tmp.setLayoutY(y);
        tmp.setId(String.valueOf(index));
        cityLabel.add(tmp);
        tmp.setText(String.valueOf(index + 1));
        tmp.setOnMouseClicked(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent event) {
                /* das momentan angeclickte label */
                label = ((Label) event.getSource());
                int labelId = Integer.valueOf(label.getId());
                if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 1) {// if
                    handleSingleClick(selectedLabels, labelId);
                } else if (event.getButton() == MouseButton.SECONDARY && event.getClickCount() == 1
                    && Integer.parseInt(((Label) event.getSource()).getId()) != 0 
                    && nodes.get(Integer.parseInt((label.getId()))).amountOfPackets == 0) {
                    handleRightClick();
                    }
                lastlabel = label;

                if (nodes.size() >= 2 && customconnections.size() >= 1) {
                    final Button fineshed = new Button("Starte Algo");
                    bottomRigth.add(fineshed, 1, 3);
                    fineshed.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event) {
                            capa = Integer.parseInt(capaCity.getText());
                            if(capa != 0)
                            {
                            	scene.getStylesheets().clear();
                            	startUserCity(customconnections, nodes);
                            }
                    };
                    
                    });
                }
                Label capaText = new Label("Max Kapazität:");
                capaCity = new TextField("0");
                bottomRigth.add(capaText, 1, 1);
                bottomRigth.add(capaCity, 1, 2);


            }
        });
        // if (free) {
        benutzer_pane.getChildren().add(tmp);
        ArrayList<Connection> trails = new ArrayList<Connection>();
        Node node = new Node(Integer.valueOf(tmp.getId()) + 1, trails, 0);
        nodes.add(node);
        // }
        ausgabe_area.setText("Position X = " + x + "Position Y = " + y + "\n");

    }

    public void handleSingleClick( List<Integer> selectedLabels, int labelId){

        switch (selectedLabels.size()) {
            case 0:
                selectedLabels.add(labelId);
                label.setGraphic(new ImageView(sel_city_image));
                break;
            case 1:
                /* toggle already selected */
                if ((cityLabel.get(selectedLabels.get(0)) == label )) {
                    label.setGraphic(new ImageView(city_image));
                    selectedLabels.clear();
                }
                else{
                    selectedLabels.add(labelId);
                    cityId1 = selectedLabels.get(0);
                    cityId2 = selectedLabels.get(1);
                    /*draw lines*/
                    x1 = (int)label.getLayoutX();
                    y1 = (int)label.getLayoutY();
                    y2 = (int)cityLabel.get(cityId1).getLayoutY();
                    x2 = (int)cityLabel.get(cityId1).getLayoutX();
                    int distance = (int)(Math.sqrt( ((x2 -x1)*(x2 - x1 )) + ( (y2 - y1) * (y2 - y1) ) ));
                    /* draw line */
                    /* extract ^^ to drawPath(x1, y1, x2, y2) */
                    List<Integer> tmpCitylist = new ArrayList<Integer>();
                    //System.out.println(selectedLabels.get(0));
                    /* Check ob eine Verbindung vorhanden ist */
                    if (!conExists(cityId1+1, cityId2+1)) {
                        connectionID++;
                        tmpCitylist.add(cityId1+1);
                        tmpCitylist.add(cityId2+1);
                        Connection connection = new Connection(connectionID, distance, 1, tmpCitylist);//TODO:
                        nodes.get(cityId1).trails.add(connection);
                        nodes.get(cityId2).trails.add(connection);
                        connection = new Connection(connectionID, distance, 0, tmpCitylist);
                        customconnections.add(connection);
                        drawSingleLine(x1, y1, x2, y2,customconnections.size() -1);

                    }
                    label.setGraphic(new ImageView(sel_city_image));
                    lastlabel.setGraphic(new ImageView(city_image));
                    //selectedLabels.clear(); /*switch too expressions to change selection behaviour */
                    selectedLabels.remove(0);
                    //System.out.println(customconnections.toString());
                    System.out.println(nodes.toString());
                    System.out.println("--------------------------------------------------nodes--------------------------------------------");
                }
                break;
            default :
                System.out.println("switch fails");
                break;
        }

    }
    public void handleRightClick(){
        final GridPane testPane = new GridPane();
        pakete = new TextField("0");
        Label text_info1 = new Label("Paketanzahl:");
        Button eingabe_anzahl = new Button("weiter");
        /*
         * die Reihenfolge hier ist wichtig für TAB
         * durch die elemente
         */
        testPane.add(pakete, 2, 1);
        testPane.add(eingabe_anzahl, 2, 2);
        testPane.add(text_info1, 1, 1);
        root.setRight(testPane);
        // root.setTop(testPane);
        pakete.setFocusTraversable(true);
        pakete.requestFocus();
        eingabe_anzahl.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                try {
                    nodes.get(Integer.parseInt((label.getId()))).setAmountOfPackets(Integer.parseInt(pakete.getText()));
                } catch (NumberFormatException e) {
                    pakete.setText("Int-Werte > 0 eingeben");
                }
                nodePackage.get(0).add(Integer.parseInt((label.getId()))+1); //TODO:
                nodePackage.get(1).add(Integer.parseInt(pakete.getText()));
                root.setRight(null);

                paketAnzeige += ("Node: " + (Integer.parseInt((label.getId()))+1) + "Pakete: "
                    + Integer.parseInt(pakete.getText()) + "\n");
                ausgabe_area.setText(paketAnzeige);
            }
        });

        System.out.println(nodes.toString());
        System.out
            .println("--------------------------------------------------nodes--------------------------------------------");

    }
}

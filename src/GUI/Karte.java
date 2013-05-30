package GUI;

import java.util.List;
import aufgabe3_1.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.stage.Stage;

public class Karte extends Application implements View {

	Path[] pathlist;
	List<Node> cities;
	List<Connection> connections;
	Controller con;
	BorderPane root;
	GridPane button_pane;
	Pane karten_pane;
	TextField eingabe_text_feld;
	TextField ausgabe_text_feld;
	Scene scene;
	final public static int BEST6 = 1365;
//	final public static int BEST10 = 2255;
	final public static int BEST10 = 15;
	final public static int BEST20 = 4205;
	
	int DEFAULT_CYCLES = 1000;

	Image city_image = new Image(getClass().getResourceAsStream("/resource/haus_symbol_small.jpg"));

	public static void main(String[] args) {
		launch();
	}

	@Override
	public void start(Stage primaryStage) {
		// Controller initializieren
		
		primaryStage.setTitle("Strassenplan");

		root = new BorderPane();
		button_pane = new GridPane();
		karten_pane = new Pane();
		
		root.setTop(button_pane);
		root.setCenter(karten_pane);
		
		//Buttons erstellen
		makeChoiceButtons();
		

		scene = new Scene(root, 1300, 724);
		primaryStage.setScene(scene);
		primaryStage.setResizable(false);
		
		//Hintergrundbild
		scene.getStylesheets()
				.add(Karte.class.getResource("/resource/Login.css")
						.toExternalForm());

		primaryStage.show();
	}
		
		//Buttons und Text für die Bedienung
		private void makeButtons() {
			
		Platform.runLater(new Runnable() {
				@Override
				public void run() {
					button_pane = new GridPane();
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

					ausgabe_text_feld = new TextField("Beste Route");
					ausgabe_text_feld.setEditable(false);
					root.setBottom(ausgabe_text_feld);
					
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
								//	Element, Spalte,Zeile 
					button_pane.add(default_button, 1, 2);
					button_pane.add(start_button, 2, 2);
					button_pane.add(eingabe_text_feld, 3, 2);

				}
			}
		);		
	}
	
	//Buttons für die Auswahl der Anzahl
	private void makeChoiceButtons(){
		Button button_6 = new Button("6 Städte");
		button_6.setPrefSize(100, 20);		
		button_6.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				scene.getStylesheets().clear();
				makeButtons();
				letsGO(6, "GR6.csv");
				};
			}
		);
		
		Button button_10 = new Button("Pentagram");
		button_10.setPrefSize(100, 20);		
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
		
		Button button_20 = new Button("20 Städte");
		button_20.setPrefSize(100, 20);		
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
		button_pane.add(button_10, 2, 1);
		button_pane.add(button_20, 3, 1);
		
	}
	
	//Controller erst starten wenn Anzahl der Städte ausgewählt wurde.
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
	public boolean newPath(final List<Integer> path, final int laenge, final List<Integer> visited) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				// Restaurieren (alle Straßen schwarz)
				for (int j = 0; j < pathlist.length; j++) {
					pathlist[j].setStroke(Color.BLACK);
				}
				// Nur bestimmte Straßen rot
				for (int i = 0; i < visited.size(); i++) {
					pathlist[visited.get(i)-1].setStroke(Color.RED);
				}
				
				//Ausgabe des Path als String
				String ausgabe = new String();
				for (int j = 0; j < path.size(); j++) {
					ausgabe = ausgabe +	"=>" +path.get(j);		
				}
				ausgabe = ausgabe + "          Länge: " + laenge + "m";
				if (cities.size()==6) {
					ausgabe = ausgabe + "          Bestsolution: " + BEST6;
				}
				if(cities.size()==10){
					ausgabe = ausgabe + "          Bestsolution: " + BEST10;
				}
				if(cities.size()==20){
					ausgabe = ausgabe + "          Bestsolution: " + BEST20;
				}
				
				ausgabe_text_feld.setText(ausgabe);
				
			}
		});
		return false;
	}

	@Override
	public boolean positionsOfAnts(List<Ant> antList) {
		// TODO Auto-generated method stub
		return false;
	}

	@SuppressWarnings("unused")
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
	
	//Methode um 6 Städte zu zeichen
	public void draw6() {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {

				// City-Symbole auf der Karte anzeigen
				Label[] city_label = new Label[cities.size()];
				int[] x_koordinaten = { 100, 400, 100, 320, 560, 620 };
				int[] y_koordinaten = { 100, 100, 290, 270, 100, 295 };

				drawLines(city_label, x_koordinaten, y_koordinaten);
			}
		});
	}
	
	//Methode um 10 Städte zu zeichen
		public void draw10() {
			Platform.runLater(new Runnable() {
				@Override
				public void run() {

					// City-Symbole auf der Karte anzeigen
					Label[] city_label = new Label[cities.size()];
					int[] x_koordinaten = { 103, 333, 119, 518, 486, 730, 783, 934, 1128, 1143};
					int[] y_koordinaten = { 103, 103, 384, 103, 382, 305, 100, 500, 92, 492};

					drawLines(city_label, x_koordinaten, y_koordinaten);
				}
			});
		}
	

	//Methode um 20 Städte zu zeichen
	public void draw20() {

		Platform.runLater(new Runnable() {
			@Override
			public void run() {

				// City-Symbole auf der Karte anzeigen
				Label[] city_label = new Label[cities.size()];
				int[] x_koordinaten = { 192, 143, 172, 101,48, 310, 318, 608, 396, 328,
						465, 648, 816, 853, 597, 900, 901, 747, 669, 416 };
				int[] y_koordinaten = { 18, 112, 289, 434, 578, 570, 405, 561, 311, 195,
						150, 236, 429, 553, 408, 274, 63, 145, 47, 74};

				drawLines(city_label, x_koordinaten, y_koordinaten);
			}
		});
	}
	
	public void drawpenta() {

		Platform.runLater(new Runnable() {
			@Override
			public void run() {

				// City-Symbole auf der Karte anzeigen
				Label[] city_label = new Label[cities.size()];
				int[] x_koordinaten = { 420, 420, 314, 240, 346, 313, 500, 541, 525, 600};
				int[] y_koordinaten = { 30, 82, 185, 170, 320, 380, 320, 380, 185, 170};

				drawLines(city_label, x_koordinaten, y_koordinaten);
			}
		});
	}
	
	//Methode zum Platzieren der Häuser und der Straßen
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
			// jeweiligen Connection die Städte
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
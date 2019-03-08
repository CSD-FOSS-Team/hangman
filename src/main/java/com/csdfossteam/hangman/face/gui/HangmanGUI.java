/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.csdfossteam.hangman.face.gui;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import com.csdfossteam.hangman.HangMan;
import com.csdfossteam.hangman.net.HangmanLANClient;
import com.csdfossteam.hangman.net.HangmanLANServer;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.event.Event;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

import com.csdfossteam.hangman.core.*;

/**
 * <h1>Implements a Hangman GUI Class.</h1>
 * Note that in order to use this class you don't call its constructor.
 * Instead call static method "startGUIThread" and casting the return to be HangmanGUI.
 *
 * example syntax: HangmanGUI new_gui = (HangmanGUI) HangmanGUI.startGUIThread"
 *
 * @author  nasioutz
 * @version 1.0
 * @since   2018-17-12
 */
public class HangmanGUI extends Application implements EventHandler<ActionEvent>
{

    /*-----------------------------------
    MULTITHREAD RELATED PART OF THE CLASS
    ------------------------------------*/


    private static final CountDownLatch latch = new CountDownLatch(1);
    private static HangmanGUI gui = null;
    private static Thread gameWindowThread;

    public static HangmanGUI getGUIinstance() {
         try
        {
            latch.await();
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        return gui;
    }

    public static void setGUI(HangmanGUI gui0) {
        gui = gui0;
        latch.countDown();
    }

    /**
     * Launch the JavaFX Application and Return the Current GUI Object
     * @return HangmanGUI
     */
    public static HangmanGUI startGUIThread()
    {
        gameWindowThread = new Thread(() -> Application.launch(HangmanGUI.class));
        gameWindowThread.start();
        HangmanGUI gui = HangmanGUI.getGUIinstance();

        return gui;
    }


    /*----------------------------------
            --- CONSTRUCTOR ---
    ------------------------------------*/

    public HangmanGUI() throws IOException {
        setGUI(this);
        sceneTable = new Hashtable<String,Scene>();
        createSplashStage();
    }


    /*----------------------------------
    --- NON STATIC PART OF THE CLASS ---
    ------------------------------------*/

    private boolean gameTerminated;
    private TextField input;
    private Label text;
    private ImageView hangman_img;
    private Stage gameStage;
    private Stage configStage;
    private Stage splashStage;
    private Hashtable<String,Scene> sceneTable;
    private TableView<Player> player_table;
    private VBox[] playerBoxList;
    private String dirPath = new java.io.File( "." ).getCanonicalPath();
    private String dirPathToData = Paths.get(dirPath,"data").toString();//"src","main","resources","com","csdfossteam","hangman","face","gui"
    private inputString handlersInput = new inputString("");
    private Hashtable<String,Object> gameConfig,gameState;
    private double xOffset = 0, yOffset = 0;

    private IntegerProperty splash_width = new SimpleIntegerProperty(this,"scene_width",550);
    private IntegerProperty splash_height = new SimpleIntegerProperty(this,"scene_height",350);
    private IntegerProperty game_width = new SimpleIntegerProperty(this,"scene_width",850);
    private IntegerProperty game_height = new SimpleIntegerProperty(this,"scene_height",400);
    private IntegerProperty config_width = new SimpleIntegerProperty(this,"scene_width",600);
    private IntegerProperty config_height = new SimpleIntegerProperty(this,"scene_height",400);

    @Override
    public void start(Stage primaryStage) throws Exception
    {

        Platform.setImplicitExit(false);

        Font.loadFont(
        new URL("file:///"+Paths.get(dirPathToData,"fonts","AC-DiaryGirl_Unicode.ttf").toString()).toExternalForm(), 60);

        gameStage = primaryStage;

    }


    /**
     * Pass game configuration parameters and open the window.
     * @param config
     * @param state
     */
    public void initGame(Hashtable<String,Object> config,Hashtable<String,Object> state) throws IOException
    {
        Platform.runLater(() -> createGameStage());
        gameConfig = config;
        gameState = state;
        gameTerminated = false;
        update(state);
        Platform.runLater(() ->
            gameStage.show());

    }

    /**
     * Pass game configuration parameters along with winodw size and open the window
     * @param config
     * @param state
     * @param width
     * @param height
     * @throws IOException
     */
    public void initGame(Hashtable<String,Object> config,Hashtable<String,Object> state,int width,int height) throws IOException
    {
        initGame(config,state);
        game_width.set(width);
        game_height.set(height);
    }


    /**
     * Method to update what is displayed in the game window.
     *
     * <p>
     * <b>Note:</b> Notice use of Pathform.runLater to be usable from a different thread.
     *
     * @param gameStatus
     */
    public void update(Hashtable<String, Object> gameStatus)
    {
        Platform.runLater(() -> {

        gameState = gameStatus;

        // Switch player box appearance based on active player
        int index = 0;
        for (VBox playerBox : playerBoxList) {

            if (index == (int) gameStatus.get("playerIndex"))
            {
                playerBox.getStyleClass().remove("player-vbox-inactive");
                for (Node boxlabel : playerBox.getChildren())
                    boxlabel.getStyleClass().remove("player-label-inactive");
            }
            else
              if (playerBox.getStyleClass().size() == 1)
              {
                    playerBox.getStyleClass().add("player-vbox-inactive");
                    for (Node boxlabel : playerBox.getChildren())
                        boxlabel.getStyleClass().add("player-label-inactive");
              }

            ((Label) playerBox.getChildren().get(1)).setText(
                    "Letters Used: " + ((ArrayList<Player>) gameStatus.get("playerList")).get(index).getLetters());
            index++;
        }


        //Update main word text
            try {
                text.setText(
                (new String(((WordDictionary) gameStatus.get("hiddenWord")).getCurrentHiddenString().getBytes(),"UTF-8")));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            //Update "hangman" image based on current players lifes
        hangman_img.setImage(
        getHangmanImages(((ArrayList<Player>)gameStatus.get("playerList")).get((int)gameStatus.get("playerIndex")).getLifes().getCurrent()));

        //Attempt to get window in focus
        input.requestFocus();
        gameStage.toFront();

    });
        //Check for game termination and end-of-play
        if (!(boolean)gameStatus.get("play"))
        {
            endGameMessage(gameStatus);
            endGame(gameStage,750);
        }
        if (gameTerminated) gameState.computeIfPresent("play",(k,v)->false);
    }

    /**
     * Pauses the main Thread to wait for user input through handling KeyEvent.
     * @param input
     * @throws InterruptedException
     */
    public boolean input(inputString input) throws InterruptedException
    {
        handlersInput = input;
        synchronized(HangMan.hangman) {HangMan.hangman.wait();}
        return gameTerminated;
    }

    /**
     * Creates and starts the main menu window
     * @return Hashtable<String, Object>
     * @throws Exception
     */
    public Hashtable<String, Object> config() throws Exception
    {
        //Copy the default settings as a base
        gameConfig = GameEngine.defaultConfig();

        //Create Server/Client Objects
        HangmanLANServer localServer = null;
        HangmanLANClient localClient = null;

        //Run Creation and Display of main window on JavaFX Thread
        Platform.runLater(() -> {
            createConfigStage();
            configStage.show();
        });

        //Make main thread for configuration to finish
        synchronized(HangMan.hangman) {HangMan.hangman.wait();}

        return gameConfig;
    }


    /**
     * Implements a basic end-game window
     */
    public void endGameMessage(Hashtable<String,Object> gameStatus)
    {
        if ((int)gameStatus.get("winnerIndex") == -1)
        {
            runAlert(Alert.AlertType.INFORMATION,
                    "Game Over",
                    "The Game is Over",
                    "...but no one won! Good luck next time",
                    "exit-white.png");
        }
        else
        {
            runAlert(Alert.AlertType.INFORMATION,
                    "Game Over",
                    "The Game is Over",
                    "The Winner is, "+
                                ((ArrayList<Player>)gameStatus.get("playerList")).get((int)gameState.get("winnerIndex")).getName()+
                                "!\nEveryone else try your best next time",
                    "exit-white.png");
        }
    }

    /**
     * Completely Terminate the javaFX platform and thus it's thread.
     * <p>
     * <b>Note:</b> It's necessary since Platform.setImplicitExit() is set to FALSE.
     */
    public void terminate()
    {
        Platform.runLater(() -> {Platform.exit();});
    }

    /**
     * Request the UI to behave as if the game has normaly ended
     */
    public void endOnMainThread()
    {
        Platform.runLater(() -> {gameTerminated = true; gameStage.hide();});
    }

    /**
     * Toggle splash screen on/off
     * @param boolean
     */
    public void waitSplashScreen(boolean toggle)
    {
        if (toggle)
            Platform.runLater(() -> splashStage.show());
        else
            Platform.runLater(() -> splashStage.hide());
    }

    /**
     * Create and Show an Alert Window on the JavaFX Thread
     * @param Alert.AlertType type
     * @param String error_title
     * @param String header_message
     * @param String content_text
     * @param String icon_name
     */
    public void runAlert(Alert.AlertType type, String error_title, String header_message, String content_text,String icon_name)
    {
        Platform.runLater(() -> makeAlert(type, error_title, header_message, content_text,icon_name).showAndWait());
    }

    /*----------------------------------
      --- PRIVATE INTERNAL METHODS ---
    ------------------------------------*/


    /**
     * Fire an Event to emulate an internal "window closure" event.
     * @param
     */
    private void endGame(Stage stage,int wait) {try{

        if (stage == gameStage) TimeUnit.MILLISECONDS.sleep(wait);

        Platform.runLater(() ->
                stage.fireEvent (new WindowEvent(stage, WindowEvent.WINDOW_CLOSE_REQUEST)));

    }catch (InterruptedException e) {e.printStackTrace();}}


    /**
     * Prompt for confirmation to end the game, close all windows and kill the application
     * @param stage
     */
    private void exit(Stage stage)
    {
        Optional<ButtonType> result = makeAlert(Alert.AlertType.CONFIRMATION,"End Game and Exit","End Game and Exit Application","Are you sure?","exit-white.png").showAndWait();
        if (((Optional) result).get()==ButtonType.OK)
        {
            gameConfig.computeIfPresent("exit", (k, v) -> true);
            endGame(stage,750);
        }
    }

    public boolean terminationRequested() {return gameTerminated;}

    /**
     * Create and alert window given the type, title, header, text and icon
     * @param Alert.AlertType type
     * @param String error_title
     * @param String header_message
     * @param String content_text
     * @param String icon_name
     * @return
     */
    private Alert makeAlert(Alert.AlertType type, String error_title, String header_message, String content_text,String icon_name)
    {
        //--- SETTINGS UP DIALOG PANES ---

        Alert alert = new Alert(type);
        alert.setTitle(error_title);
        alert.setHeaderText(header_message);
        alert.setContentText(content_text);
        alert.initStyle(StageStyle.UNDECORATED);

        //--- SETTING UP EXIT PANE SELECTION PANEL ---

        DialogPane alertPane = alert.getDialogPane();
        alertPane.getStylesheets().add(getClass().getResource("HangmanStylez.css").toExternalForm());
        alertPane.getStyleClass().add("exit-pane");
        if (icon_name.equals("exit-white.png"))
        alertPane.setGraphic(new ImageView(
                findImage(icon_name,20,20,true,true)));

        return alert;
    }


    /**
     * Innitiate the client socket and attempt to connect to a server
     * @param ip
     * @param port
     * @param name
     */
    private void connectToHost(String ip, int port, String name) {

        HangmanLANServer localServer = null;
        HangmanLANClient localClient = null;
        boolean error = false;
        String confirm = "";

        try
        {
            localClient = new HangmanLANClient(ip, port);
            confirm = localClient.receiveFromServer();
            if (confirm.equals("confirm"))
            {
                localClient.sendToServer(name);
                gameConfig.put("isClient",true);
                ((HangmanLANServer)gameConfig.get("localNetwork")).freeClients();
                gameConfig.put("localNetwork",localClient);

                gameConfig.computeIfPresent("exit", (k, v) -> false);
                Platform.runLater(() ->
                        configStage.fireEvent (new WindowEvent(configStage, WindowEvent.WINDOW_CLOSE_REQUEST)));
            }
            else
            {
                throw new IOException("No Connection");
            }

        }
        catch (IOException e1)
        {runAlert(Alert.AlertType.WARNING,"Connection Error","Problem Connecting to the Host","Double check the IP and Port","exit-white.png");}
        catch (Exception e2)
        {System.out.println("ERROR: "+e2);
         runAlert(Alert.AlertType.WARNING,"Input Error","Something was Typed Wrong!","Check the input boxes for invalid characters","exit-white.png");}


    }

    /**
     * Attempt to switch to input scene on input stage
     * @param scene
     */
    private void goToScene(Stage stage, Scene scene)
    {
        stage.setScene(scene);
    }

    /**
     * Create the Scene for the Splash windows shown as the client waits for the game to start
     */
    private void createClientWaitScene()
    {

        ImageView game_icon = new ImageView(findImage("hangman-icon.png"));
        game_icon.setStyle("-fx-fit-to-height: 200; -fx-fit-to-width: 200; -fx-padding: 20 30 20 30");
        VBox.setVgrow(game_icon,Priority.ALWAYS);

        Label wait_label = new Label("Keep Calm and wait for Your Host");
        wait_label.getStyleClass().add("menu-text");
        wait_label.setStyle("-fx-text-fill: rgba(255,255,255,1.0); -fx-font-size: 25; -fx-padding: 20 20 20 20;");
        wait_label.setMaxSize(Double.MAX_VALUE,Double.MAX_VALUE);
        VBox.setVgrow(wait_label,Priority.ALWAYS);

        VBox wait_box = new VBox(game_icon, wait_label);
        wait_box.getStyleClass().add("menu-box");
        wait_box.setStyle("-fx-padding: 20 20 20 20");
        wait_box.setAlignment(Pos.CENTER);

        BorderPane layout = new BorderPane();
        layout.setCenter(wait_box);

        layout.setBackground(new Background(
                new BackgroundImage(
                        findImage("background.png"),null,null,null,null)));

        //--- SETTING UP SCENE ---

        sceneTable.put("client_connecting_scene", new Scene(layout,splash_width.get(),splash_height.get()));
        splash_width.bind(sceneTable.get("client_connecting_scene").widthProperty());
        splash_height.bind(sceneTable.get("client_connecting_scene").heightProperty());
        sceneTable.get("client_connecting_scene").getStylesheets().add("/com/csdfossteam/hangman/face/gui/HangmanStylez.css");
        sceneTable.get("client_connecting_scene").setOnMousePressed(e-> getOffset(e));
        sceneTable.get("client_connecting_scene").setOnMouseDragged(e-> moveWindow(e,splashStage));

    }

    /**
     * Create the header bar with title and the exit and back buttons
     * @param previous_scene
     * @return
     */
    private HBox createHeaderBar(Scene previous_scene)
    {


        Region backRegion = new Region();
        Region middleRegion = new Region();
        HBox.setHgrow(backRegion, Priority.ALWAYS);
        HBox.setHgrow(middleRegion, Priority.ALWAYS);


        Button exitButton = new Button();
        exitButton.setGraphic(new ImageView(
                findImage("exit-white.png",20,20,true,true)));
        exitButton.getStyleClass().add("exit-button");
        exitButton.setOnAction(e -> {
            exit(configStage);});

        Button backButton = new Button();

        if (previous_scene != null){
            backButton.setGraphic(new ImageView(
                    findImage("back-white.png",20,20,true,true)));
            backButton.getStyleClass().add("exit-button");
            backButton.setOnAction(e -> goToScene(configStage,previous_scene));}
        else
            backButton.getStyleClass().add("dead-button");

        ImageView game_icon = new ImageView(findImage("hangman-icon.png"));
        game_icon.setFitHeight(30);
        game_icon.setFitWidth(30);
        game_icon.setStyle("-fx-padding: 0 0 0 0");
        Label menu_title = new Label("Hangman: Main Menu");
        menu_title.getStyleClass().add("menu-title");


        HBox Title = new HBox();
        Title.getChildren().addAll(game_icon,menu_title);
        Title.setAlignment(Pos.CENTER);

        HBox headerBox = new HBox(backButton,backRegion,Title,middleRegion,exitButton);
        headerBox.getStyleClass().add("playersbox-hbox");

        return headerBox;
    }

    /**
     * Create the scene for setting up a players and dictionaries
     */
    public void createConfigScene() {


        BorderPane layout = new BorderPane();

        Label host_label = new Label();
        host_label.setMaxSize(Double.MAX_VALUE,30);
        host_label.getStyleClass().add("host-label");

        TableView<Player> player_table = new TableView();

        TableColumn player_name_col = new TableColumn("Player Name");
        player_name_col.setCellValueFactory(
                new PropertyValueFactory<Player, String>("name"));
        TableColumn player_network_col = new TableColumn("Network Location");
        player_network_col.setCellValueFactory(
                new PropertyValueFactory<Player, String>("remoteTag"));

        player_name_col.prefWidthProperty().bind(player_table.widthProperty().divide(2.00));
        player_network_col.prefWidthProperty().bind(player_table.widthProperty().divide(2.05));

        player_table.setItems(FXCollections.observableList(((ArrayList<Player>)gameConfig.get("playerList"))));
        player_table.getColumns().addAll(player_name_col, player_network_col);
        player_table.getStyleClass().add("table-view");
        player_table.setEditable(true);



        Label add__local_player_label = new Label("Player Name");
        add__local_player_label.getStyleClass().add("menu-text");
        add__local_player_label.setStyle("-fx-font-size: 13;-fx-padding: 5 5 5 5;-fx-background-color: rgba(35,35,35,1.0);");

        TextField add_local_player_name = new TextField();
        add_local_player_name.getStyleClass().add("input-textfield");
        add_local_player_name.setOnAction(e ->
        {
            ((ArrayList<Player>)gameConfig.get("playerList")).add(new Player(add_local_player_name.getText()));
            player_table.setItems(FXCollections.observableList(((ArrayList<Player>)gameConfig.get("playerList"))));
            add_local_player_name.clear();
        });

        Button add_local_player_button = new Button("Add Local Player");
        add_local_player_button.getStyleClass().add("menu-button");
        add_local_player_button.setStyle("-fx-font-size: 13; -fx-background-insets: 1 1 1 1;");

        add__local_player_label.setMaxSize(Double.MAX_VALUE,Double.MAX_VALUE);
        add_local_player_name.setMaxSize(Double.MAX_VALUE,Double.MAX_VALUE);
        add_local_player_button.setMaxSize(Double.MAX_VALUE,Double.MAX_VALUE);
        add_local_player_button.setOnAction(e ->
        {
            ((ArrayList<Player>)gameConfig.get("playerList")).add(new Player(add_local_player_name.getText()));
            player_table.setItems(FXCollections.observableList(((ArrayList<Player>)gameConfig.get("playerList"))));
            add_local_player_name.clear();
        });

        VBox.setVgrow(add__local_player_label,Priority.ALWAYS);
        VBox.setVgrow(add_local_player_name,Priority.ALWAYS);
        VBox.setVgrow(add_local_player_button,Priority.ALWAYS);
        VBox add_local_player_box = new VBox(add__local_player_label,add_local_player_name,add_local_player_button);
        add_local_player_box.getStyleClass().add("menu-box");
        add_local_player_box.setStyle("-fx-background-insets: 1 1 1 1; -fx-padding: 0 0 0 0");
        add_local_player_name.maxWidthProperty().bind(add_local_player_box.widthProperty());

        Button add_network_player_button = new Button("Add\nNetwork\nPlayer");
        add_network_player_button.getStyleClass().add("menu-button");
        add_network_player_button.setStyle("-fx-font-size: 13; -fx-background-insets: 0 1 1 0; -fx-border-width: 0 0 0 1");
        add_network_player_button.setMaxSize(Double.MAX_VALUE,Double.MAX_VALUE);
        add_network_player_button.setOnAction(e->
        {
            if (!gameConfig.containsKey("localNetwork"))
            {
                try {
                    gameConfig.put("localNetwork", new HangmanLANServer(Handler.defaultPort));
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }

            String host_creds  = null;
            try {
                host_creds = "Host IP: " + ((HangmanLANServer)gameConfig.get("localNetwork")).getServerIP()+" | Host Port: " + ((HangmanLANServer)gameConfig.get("localNetwork")).getServerPort();
            } catch (UnknownHostException e1) {
                e1.printStackTrace();
            }


            add_network_player_button.setText("");
            add_network_player_button.setGraphic(new ImageView(findImage("client-connecting.gif",60,60,true,true)));
            add_network_player_button.setStyle("-fx-padding: 0 0 0 0; -fx-background-insets: 0 1 1 0; -fx-border-width: 0 0 0 1");

            host_label.setText(host_creds);
            layout.setBottom(host_label);

            HangmanLANServer finalLocalServer1 = ((HangmanLANServer)gameConfig.get("localNetwork"));
            final String[] name = new String[1];
            Task connect_task = new Task<Void>()
            {
                @Override
                protected Void call() throws Exception
                {
                    finalLocalServer1.findClient();
                    finalLocalServer1.sendToClient(finalLocalServer1.getClientNumber() - 1,"confirm");
                    name[0] = finalLocalServer1.receiveFromClient(finalLocalServer1.getClientNumber() - 1);
                    return null;
                }

            };

            connect_task.setOnSucceeded(s ->
            {
                ((ArrayList<Player>)gameConfig.get("playerList")).add(new Player(name[0], ((HangmanLANServer)gameConfig.get("localNetwork")).getClientNumber() - 1));
                player_table.setItems(FXCollections.observableList(((ArrayList<Player>)gameConfig.get("playerList"))));
                add_network_player_button.setText("Add\nNetwork Player");
                add_network_player_button.setGraphic(null);
                add_network_player_button.setStyle("-fx-font-size: 13; -fx-background-insets: 0 1 1 0; -fx-border-width: 0 0 0 1");
                layout.setBottom(null);
                Thread.currentThread().interrupt();
            });

            connect_task.setOnFailed(f ->
            {
                add_network_player_button.setText("Add\nNetwork Player");
                add_network_player_button.setGraphic(null);
                add_network_player_button.setStyle("-fx-font-size: 13; -fx-background-insets: 0 1 1 0; -fx-border-width: 0 0 0 1");
                layout.setBottom(null);
                Thread.currentThread().interrupt();
            });

            new Thread(connect_task).start();
        });
        HBox.setHgrow(add_network_player_button,Priority.ALWAYS);

        Button remove_player_button = new Button ("Remove\nPlayer");
        remove_player_button.getStyleClass().add("menu-button");
        remove_player_button.setStyle("-fx-font-size: 13; -fx-background-insets: 0 1 1 0;");
        remove_player_button.setMaxSize(Double.MAX_VALUE,Double.MAX_VALUE);
        HBox.setHgrow(remove_player_button,Priority.ALWAYS);
        remove_player_button.setOnAction(e ->
        {
            try
            {
                int network_player =  ((ArrayList<Player>)gameConfig.get("playerList")).get(player_table.getSelectionModel().getSelectedIndex()).getRemoteIndex();
                ((ArrayList<Player>)gameConfig.get("playerList")).remove(player_table.getSelectionModel().getSelectedIndex());
                if (network_player >= 0)
                {
                    gameConfig.computeIfPresent("playerList",(k,v) -> Player.refreshNetworkIndexes((ArrayList<Player>)gameConfig.get("playerList")));
                    ((HangmanLANServer)gameConfig.get("localNetwork")).removeClient(network_player);
                }
                player_table.setItems(FXCollections.observableList(((ArrayList<Player>)gameConfig.get("playerList"))));
            }
            catch (Exception remove_button) {runAlert(Alert.AlertType.WARNING,"Input Error","Couldn't Remove Player","Check whether you have a player selected!","exit-white.png");}
        });

        HBox add_player_box = new HBox(add_local_player_box,add_network_player_button,remove_player_button);

        add_network_player_button.prefWidthProperty().bind(add_player_box.widthProperty().subtract(add_local_player_box.widthProperty()).divide(3.2));
        remove_player_button.prefWidthProperty().bind(add_player_box.widthProperty().subtract(add_local_player_box.widthProperty()).divide(3.2));

        VBox.setVgrow(player_table,Priority.ALWAYS);
        VBox player_list_box = new VBox(player_table,add_player_box);

        Label dictionaryListLabel = new Label("Availiable Dictionaries");
        dictionaryListLabel.getStyleClass().add("menu-text");
        dictionaryListLabel.setStyle("-fx-background-color: rgba(85,85,85,1.0); -fx-border-color: rgba(100,100,100,1.0); -fx-border-width: 0 0 0 0;");
        dictionaryListLabel.setMaxSize(Double.MAX_VALUE,Double.MAX_VALUE);
        ArrayList<String> dictionaryList = new ArrayList<>();
        try {for (File file : WordDictionary.getDictionaries()) dictionaryList.add(file.getName().split("\\.")[0]);}
        catch (Exception e) {runAlert(Alert.AlertType.WARNING,"Data Error","Couldn't inport dictionary files successfully","Check the files provided in the data folder","exit-white.png");}

        ListView<String> dictionaryListView = new ListView(FXCollections.<String>observableArrayList(dictionaryList));
        dictionaryListView.setFixedCellSize(50);

        dictionaryListView.getSelectionModel().selectedItemProperty()
                .addListener(new ChangeListener<String>() {

                    public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue)
                    {
                        // change the label text value to the newly selected
                        // item.
                        try {
                            gameConfig.put("dict_path", WordDictionary.getDictionaries()[dictionaryListView.getSelectionModel().getSelectedIndex()].toString());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }});

        VBox dictionary_list_box = new VBox(dictionaryListLabel,dictionaryListView);

        layout.setBottom(null);
        layout.setCenter(player_list_box);
        layout.setRight(dictionary_list_box);
        layout.setTop(createHeaderBar(sceneTable.get("main_scene")));

        layout.setBackground(new Background(
                new BackgroundImage(
                        findImage("background.png"),null,null,null,null)));

        //--- SETTING UP SCENE ---

        sceneTable.put("config_scene", new Scene(layout,config_width.get(),config_height.get()));
        config_width.bind(sceneTable.get("config_scene").widthProperty());
        config_height.bind(sceneTable.get("config_scene").heightProperty());
        sceneTable.get("config_scene").getStylesheets().add("/com/csdfossteam/hangman/face/gui/HangmanStylez.css");
        sceneTable.get("config_scene").setOnMousePressed(e-> getOffset(e));
        sceneTable.get("config_scene").setOnMouseDragged(e-> moveWindow(e,configStage));
    }

    /**
     * Create the scene for joining a hosted game
     */
    public void createJoinScene()
    {

        Label player_name_label = new Label("Player Name");
        player_name_label.getStyleClass().add("menu-text");
        TextField name_input = new TextField();
        name_input.getStyleClass().add("input-textfield");
        VBox player_box = new VBox(player_name_label,name_input);
        player_box.getStyleClass().add("menu-box");

        Label host_ip_label = new Label ("Host IP");
        host_ip_label.getStyleClass().add("menu-text");
        TextField host_ip_input = new TextField();
        host_ip_input.getStyleClass().add("input-textfield");
        VBox host_ip_box = new VBox(host_ip_label,host_ip_input);
        host_ip_box.getStyleClass().add("menu-box");

        Label host_port_label = new Label ("Host Port");
        host_port_label.getStyleClass().add("menu-text");
        TextField host_port_input = new TextField();
        host_port_input.getStyleClass().add("input-textfield");
        host_port_input.setOnAction(e -> {
        try {connectToHost(host_ip_input.getText(),Integer.parseInt(host_port_input.getText()),name_input.getText()); }
        catch (Exception e1) {runAlert(Alert.AlertType.WARNING,"Input Error","Something was Typed Wrong!","Check the input boxes for invalid characters!\n\nDetails: "+e1,"exit-white.png");}
        });
        VBox host_port_box = new VBox(host_port_label,host_port_input);
        host_port_box.getStyleClass().add("menu-box");

        Button join_game_button = new Button("Connect to Host");
        join_game_button.getStyleClass().add("menu-button");
        join_game_button.setStyle("-fx-background-insets: 1 1 1 1;");

        join_game_button.setOnAction(e -> {
            try {connectToHost(host_ip_input.getText(),Integer.parseInt(host_port_input.getText()),name_input.getText()); }
            catch (Exception e1) {runAlert(Alert.AlertType.WARNING,"Input Error","Something was Typed Wrong!","Check the input boxes for invalid characters!\n\nDetails: "+e1,"exit-white.png");}
        });

        VBox credentials_box = new VBox(player_box,host_ip_box,host_port_box);
        credentials_box.getStyleClass().add("menu-box");
        credentials_box.setStyle("-fx-padding: 0 0 33 0;");

        VBox.setVgrow(join_game_button, Priority.ALWAYS);
        join_game_button.setMaxSize(Double.MAX_VALUE,Double.MAX_VALUE);
        VBox join_menu = new VBox(credentials_box,join_game_button);

        BorderPane layout = new BorderPane();
        layout.setCenter(join_menu);
        layout.setTop(createHeaderBar(sceneTable.get("main_scene")));

        layout.setBackground(new Background(
                new BackgroundImage(
                        findImage("background.png"),null,null,null,null)));

        //--- SETTING UP SCENE ---

        sceneTable.put("join_scene", new Scene(layout,config_width.get(),config_height.get()));
        config_width.bind(sceneTable.get("join_scene").widthProperty());
        config_height.bind(sceneTable.get("join_scene").heightProperty());
        sceneTable.get("join_scene").getStylesheets().add("/com/csdfossteam/hangman/face/gui/HangmanStylez.css");
        sceneTable.get("join_scene").setOnMousePressed(e-> getOffset(e));
        sceneTable.get("join_scene").setOnMouseDragged(e-> moveWindow(e,configStage));

    }


    /**
     * Create the main menu scene
     */
    private void createMainMenuScene()
    {

        Button play_button = new Button("Play Game");
        // play_button.getStyleClass().add("input-textfield");
        play_button.setOnAction(e -> {
            try {handleCloseRequest(e,configStage,true);}
            catch (Exception e1) {runAlert(Alert.AlertType.WARNING,"Configuration Error","Something went wrong with closing the config window","Details: "+e,"exit-white.png");}
        });
        play_button.getStyleClass().add("menu-button");


        Button join_button = new Button("Join Game");
        join_button.setOnAction(e -> { goToScene(configStage,sceneTable.get("join_scene"));});
        join_button.getStyleClass().add("menu-button");

        Button conf_button = new Button("Configuration");
        conf_button.setOnAction(e -> { goToScene(configStage,sceneTable.get("config_scene"));});
        conf_button.getStyleClass().add("menu-button");

        VBox.setVgrow(play_button, Priority.ALWAYS);
        VBox.setVgrow(join_button, Priority.ALWAYS);
        VBox.setVgrow(conf_button, Priority.ALWAYS);
        play_button.setMaxSize(Double.MAX_VALUE,Double.MAX_VALUE);
        join_button.setMaxSize(Double.MAX_VALUE,Double.MAX_VALUE);
        conf_button.setMaxSize(Double.MAX_VALUE,Double.MAX_VALUE);

        VBox main_menu = new VBox(play_button,join_button,conf_button);

        BorderPane layout = new BorderPane();
        layout.setCenter(main_menu);
        layout.setTop(createHeaderBar(null));

        layout.setBackground(new Background(
                new BackgroundImage(
                        findImage("background.png"),null,null,null,null)));

        //--- SETTING UP SCENE ---

        sceneTable.put("main_scene", new Scene(layout,config_width.get(),config_height.get()));
        config_width.bind(sceneTable.get("main_scene").widthProperty());
        config_height.bind(sceneTable.get("main_scene").heightProperty());
        sceneTable.get("main_scene").getStylesheets().add("/com/csdfossteam/hangman/face/gui/HangmanStylez.css");
        sceneTable.get("main_scene").setOnMousePressed(e-> getOffset(e));
        sceneTable.get("main_scene").setOnMouseDragged(e-> moveWindow(e,configStage));
    }

    /**
     * Create the main game window scene
     */
    private void createGameScene()
    {
        //--- SETTING UP INPUT PANEL ---

        input = new TextField();
        input.getStyleClass().add("input-textfield");
        input.setOnKeyPressed(e -> {
            try {handleInput(e);}
            catch (UnsupportedEncodingException e1) {runAlert(Alert.AlertType.WARNING,"Input Error","Something was Typed Wrong!","Details: "+e1,"exit-white.png");}
        });

        BorderPane layoutBottom = new BorderPane();

        layoutBottom.setCenter(input);

        //--- SETTING UP CENTRAL GAME PANEL ---

        hangman_img  = new ImageView();
        hangman_img.setImage(getHangmanImages(-1));
        hangman_img.setCache(true);
        hangman_img.fitHeightProperty().bind((game_height.multiply(0.825)));
        hangman_img.setPreserveRatio(true);

        text = new Label();
        text.getStyleClass().add("hiddenword-label");
        BorderPane.setAlignment(text,Pos.BOTTOM_LEFT);
        BorderPane.setMargin(text,new Insets(0,0,20,50));

        BorderPane layoutCenter = new BorderPane();

        layoutCenter.setRight(hangman_img);
        layoutCenter.setCenter(text);

        BorderPane.setAlignment(layoutCenter, Pos.BOTTOM_CENTER);

        //--- SETTING UP PLAYERS PANEL---

        playerBoxList = createPlayersList();

        Region backRegion = new Region();
        Region playerRegion = new Region();
        HBox.setHgrow(backRegion, Priority.ALWAYS);
        HBox.setHgrow(playerRegion, Priority.ALWAYS);

        Button exitButton = new Button();
        exitButton.setGraphic(new ImageView(
                findImage("exit-white.png",20,20,true,true)));
        exitButton.getStyleClass().add("exit-button");
        exitButton.setOnAction(e -> exit(gameStage));

        Button backButton = new Button();
        backButton.setGraphic(new ImageView(
                findImage("back-white.png",20,20,true,true)));
        backButton.getStyleClass().add("exit-button");
        backButton.setOnAction(e -> endGame(gameStage,750));

        HBox playersBox = new HBox();
        for (VBox playerVBox : playerBoxList)
        {
            playersBox.getChildren().add(playerVBox);
        }
        playersBox.getStyleClass().add("playersbox-hbox");

        HBox headerBox = new HBox(backButton,backRegion,playersBox,playerRegion,exitButton);
        headerBox.getStyleClass().add("playersbox-hbox");

        //--- SETTING UP GAME WINDOW LAYOUT ---

        BorderPane layout = new BorderPane();

        layout.setCenter(layoutCenter);
        layout.setBottom(layoutBottom);
        layout.setTop(headerBox);

        layout.setBackground(new Background(
                new BackgroundImage(
                        findImage("background.png"),null,null,null,null)));

        //--- SETTING UP SCENE ---

        sceneTable.put("game_scene", new Scene(layout,game_width.get(),game_height.get()));
        game_width.bind(sceneTable.get("game_scene").widthProperty());
        game_height.bind(sceneTable.get("game_scene").heightProperty());
        sceneTable.get("game_scene").getStylesheets().add("/com/csdfossteam/hangman/face/gui/HangmanStylez.css");
        sceneTable.get("game_scene").setOnMousePressed(e-> getOffset(e));
        sceneTable.get("game_scene").setOnMouseDragged(e-> moveWindow(e,gameStage));
    }

    /**
     * Create the stage window to display when the client waits for the host to start the game
     */
    private void createSplashStage()
    {

        //--- CREATING CONNECTING SCENE --

        createClientWaitScene();

        //--- SETTING UP STAGE ---

        splashStage = new Stage();
        splashStage.setScene(sceneTable.get("client_connecting_scene"));
        splashStage.setOnCloseRequest(e -> handleCloseRequest(e, splashStage,true));
        splashStage.initStyle(StageStyle.TRANSPARENT);
        splashStage.setTitle("Hangman: Connecting...");
        splashStage.getIcons().add(
                findImage("hangman-icon.png"));;
    }

    /**
     * Setup the Stage/Layout for the main game window.
     */
    private void createConfigStage() {

        //--- CREATING SUBMENU SCENES ---

        createMainMenuScene();
        createJoinScene();
        createConfigScene();

        //--- SETTING UP STAGE ---

        configStage = new Stage();
        configStage.setScene(sceneTable.get("main_scene"));
        configStage.setOnCloseRequest(e -> handleCloseRequest(e, configStage,true));
        configStage.initStyle(StageStyle.TRANSPARENT);
        configStage.setTitle("Handman: Menu");
        configStage.getIcons().add(
                findImage("hangman-icon.png"));
    }

    /**
     * Setup the Stage/Layout for the main game window.
     */
    private void createGameStage() {

        createGameScene();

        gameStage = new Stage();
        gameStage.setScene(sceneTable.get("game_scene"));
        gameStage.widthProperty().addListener((e) -> resizeText(e));
        gameStage.setOnCloseRequest(e -> handleCloseRequest(e, gameStage,true));
        gameStage.initStyle(StageStyle.TRANSPARENT);
        gameStage.setTitle("Hangman: Game");
        gameStage.getIcons().add(
                findImage("hangman-icon.png"));;

    }

    /**
     * Get the image corresponding to "idx" amount of lifes from data folder.
     * @param idx
     * @return Image
     */
    private Image getHangmanImages(int idx)
    {
        if (idx<0) idx = getHangmanImages().length-1;
        return getHangmanImages()[idx];
    }

    /**
     * Get the list of images corresponding the amount of lifes from data folder.
     * @return Image[]
     */
    private Image[] getHangmanImages()
    {
        File dir = new File(Paths.get(dirPathToData,"images","hangman_images").toString());

        File[] file_list = dir.listFiles (new FilenameFilter() {
            public boolean accept(File dir, String filename)
            { return filename.endsWith(".png"); }
        } );

        Image[] img_list = new Image[file_list.length];

        for(int i = 0;i<file_list.length;i++)
        {
            try {  img_list[i] = new Image ("file:///"+file_list[i].getCanonicalPath());  }
            catch (IOException e)  {e.printStackTrace();}
        }

        return img_list;
    }

    /**
     * Create a VBox list for each player in the games configuration
     * @return
     */
    private VBox[] createPlayersList()
    {
        VBox[] pList = new VBox[((ArrayList<Player>)gameConfig.get("playerList")).size()];

        int index = 0;
        for (Player player : ((ArrayList<Player>)gameConfig.get("playerList")))
        {
            pList[index] = makePlayer(player);
            index++;
        }

        return pList;
    }

    /**
     * Method for making a player box. To be updated when player class is implemented.
     * @return VBox
     */
    private VBox makePlayer(Player p)
    {
        Label label1 = new Label(p.getName());
        Label label2 = new Label("Letters Used: "+p.getLetters().toString());
        label1.getStyleClass().add("player-label-active");
        label2.getStyleClass().add("player-label-active");

        VBox player = new VBox(label1,label2);
        player.getStyleClass().add("player-vbox-active");

        return player;
    }

    /**
     * Get an image from the designated data folder.
     * @param imageName
     * @return
     */
    private Image findImage(String imageName)
    {
        return new Image ("file:///"+Paths.get(dirPathToData,"images",imageName).toString());
    }

    /**
     * Get an image from the designated data folder with the specified parameters.
     * @param imageName
     * @param w
     * @param h
     * @param preserveRatio
     * @param smooth
     * @return
     */
    private Image findImage(String imageName, int w, int h, boolean preserveRatio, boolean smooth)
    {
        return (new Image ("file:///"+Paths.get(dirPathToData,"images",imageName).toString(),w,h,preserveRatio,smooth));
    }


    /*------------------------------------
    --- EVENT HANDLERS FOR GAME WINDOW ---
    --------------------------------------*/

    /**
     * Handles the event of entering a letter by returning the letter and unpausing the main thread
     * @param event
     */
    private void handleInput(KeyEvent event) throws UnsupportedEncodingException {
        if (event.getCode().equals(KeyCode.ENTER))
        {
            handlersInput.set(new String(input.getCharacters().toString().getBytes(),"UTF-8"));
            input.clear();
            synchronized(HangMan.hangman) {HangMan.hangman.notify();}
        }

    }

    /**
     * Handles the event of a closing a window or the emulation of such event.
     * @param event
     */
    private void handleCloseRequest (Event event, Stage stage,boolean resume_main_thread)
    {
        gameTerminated = true;
        if (resume_main_thread) synchronized(HangMan.hangman) {HangMan.hangman.notify();}
        Platform.runLater(()->stage.hide());
    }


    /**
     * Moves the Window Around
     * @param event
     */
    private void moveWindow(MouseEvent event, Stage stage)
    {
        stage.setX(event.getScreenX() - xOffset);
        stage.setY(event.getScreenY() - yOffset);
    }

    /**
     * Get the Current Window Position
     * @param event
     */
    private void getOffset(MouseEvent event)
    {
        xOffset = event.getSceneX();
        yOffset = event.getSceneY();
    }

    /**
     * Resize the hidden word label font size to fit in the window size
     * @param e
     */
    private void resizeText(Observable e)
    {
        Platform.runLater(() -> {
        Double fontSize = text.getFont().getSize();
        String clippedText = Utils.computeClippedText( text.getFont(), text.getText(), text.getWidth(), text.getTextOverrun(), text.getEllipsisString() );
        Font newFont;
        while ( !text.getText().equals( clippedText ) && fontSize > 0.5 )
        {
            fontSize = fontSize - 0.05;
            newFont = Font.font( text.getFont().getFamily(), fontSize);
            clippedText = Utils.computeClippedText( newFont, text.getText(), text.getWidth(), text.getTextOverrun(), text.getEllipsisString() );
        }
        text.setStyle("-fx-font-size:"+(fontSize-4)+"px");
     });}
    @Override
    public void handle(ActionEvent event)
    {

    }

    /*----------------------------------
      ---  STATIC PART OF THE CLASS ---
    ------------------------------------*/




}


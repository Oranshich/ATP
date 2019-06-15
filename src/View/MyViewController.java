package View;

import Server.Server;
import ViewModel.MyViewModel;
import ViewModel.MyViewModel;
import algorithms.mazeGenerators.Maze;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioButton;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.input.ScrollEvent;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.*;
import java.net.URL;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;

public class MyViewController implements IView, Observer {

    private static int rows = 0;
    private static int columns = 0;
    public MenuItem btn_Save;
    private MyViewModel viewModel;
    public MazeDisplayer mazeDisplayer;
    public SolutionDisplayer solutionDisplayer;
    public CharacterDisplayer characterDisplayer;
    private static Stage primaryStage;
    private Stage mazeStage;
    private static Scene scene;
    public BorderPane startPane;
    public BorderPane borderPane;
    private boolean isControlDown = false;
    private static String level;
    private static String solveAlgo;
    private boolean isPlayed = false;
    private double minX;
    private double minY;


    @FXML
    public javafx.scene.layout.Pane pane;
    public javafx.scene.layout.AnchorPane anchorPane;
    public javafx.scene.control.Label lbl_rowsNum;
    public javafx.scene.control.Label lbl_columnsNum;
    public javafx.scene.control.Label lbl_level;
    public javafx.scene.control.Label lbl_solveAlgo;
    public javafx.scene.control.Button btn_generateMaze;
    public javafx.scene.control.Button btn_solveMaze;
    public javafx.scene.control.RadioButton btn_sound;
    public javafx.scene.control.Button btn_small;
    public javafx.scene.control.Button btn_medium;
    public javafx.scene.control.Button btn_large;
    public javafx.scene.control.Button btn_start;
    public javafx.scene.control.Button btn_playAgain;
    public javafx.scene.control.Button btn_exit;
    public javafx.scene.control.Button btn_OK;
    public javafx.scene.control.MenuButton levelList;
    public javafx.scene.control.MenuButton solveList;

    //region String Property for Binding
    public StringProperty characterPositionRow = new SimpleStringProperty();
    public StringProperty characterPositionColumn = new SimpleStringProperty();

    public void initialize(MyViewModel viewModel, Stage primaryStage, Scene scene) {
        this.viewModel = viewModel;
        this.scene = scene;
        this.primaryStage = primaryStage;
        btn_solveMaze.setDisable(true);
        btn_Save.setDisable(true);
        bindProperties(viewModel);
        switchScene();
    }

    private void bindProperties(MyViewModel viewModel) {
        //position
        lbl_rowsNum.textProperty().bind(viewModel.characterPositionRow);
        lbl_columnsNum.textProperty().bind(viewModel.characterPositionColumn);
        //border pane
        pane.prefHeightProperty().bind(borderPane.heightProperty());
        pane.prefWidthProperty().bind(borderPane.widthProperty());
        //maze displayer
        mazeDisplayer.heightProperty().bind(pane.heightProperty());
        mazeDisplayer.widthProperty().bind(pane.widthProperty());
        //character displayer
        characterDisplayer.heightProperty().bind(pane.heightProperty());
        characterDisplayer.widthProperty().bind(pane.widthProperty());
        //solution displayer
        solutionDisplayer.heightProperty().bind(pane.heightProperty());
        solutionDisplayer.widthProperty().bind(pane.widthProperty());
        //listeners
        mazeDisplayer.heightProperty().addListener(((observable, oldValue, newValue) -> mazeDisplayer.redraw()));
        mazeDisplayer.widthProperty().addListener((observable, oldValue, newValue) -> mazeDisplayer.redraw());

        characterDisplayer.heightProperty().addListener(((observable, oldValue, newValue) -> characterDisplayer.drawCharacter()));
        characterDisplayer.widthProperty().addListener((observable, oldValue, newValue) -> characterDisplayer.drawCharacter());

        solutionDisplayer.heightProperty().addListener(((observable, oldValue, newValue) -> solutionDisplayer.drawSolution()));
        solutionDisplayer.widthProperty().addListener((observable, oldValue, newValue) -> solutionDisplayer.drawSolution());
    }

    /**
     * switch scenes to the start scene
     */
    public void switchScene() {
        try {
            FXMLLoader fxmlLoader1 = new FXMLLoader();
            Parent root = fxmlLoader1.load(getClass().getResource("startView.fxml").openStream());
            //new scene
            Scene scene1 = new Scene(root, 780, 585);
            scene1.getStylesheets().add(getClass().getResource("ViewStyle2.css").toExternalForm());
            primaryStage.setScene(scene1);
            mazeDisplayer.ControlSong("start");
            bindProperties(viewModel);
            primaryStage.show();

        } catch (Exception e) {

        }
    }

    public void setScroll(ScrollEvent e){
        double deltaY = e.getDeltaY();
        if(isControlDown){
            if(deltaY > 0){
                mazeDisplayer.setZoom(mazeDisplayer.getZoom()*1.1);
                characterDisplayer.setZoom(characterDisplayer.getZoom()*1.1);
                solutionDisplayer.setZoom(solutionDisplayer.getZoom()*1.1);
            }
            else{
                mazeDisplayer.setZoom(mazeDisplayer.getZoom()/1.1);
                characterDisplayer.setZoom(characterDisplayer.getZoom()/1.1);
                solutionDisplayer.setZoom(solutionDisplayer.getZoom()/1.1);
            }

            displayMaze(viewModel.getMaze());
            displayCharacter(viewModel.getMaze());
            if(viewModel.getSolution() != null){
                displaySolution(viewModel.getMaze(),viewModel.getSolution());
            }
        }
    }

    public void generateMaze() {
        btn_generateMaze.setDisable(true);
        solutionDisplayer.clearSol();
        isPlayed = true;
        bindProperties(viewModel);
        mazeDisplayer.ControlSong("play");
        btn_sound.setSelected(true);
        viewModel.generateMaze(rows, columns);
        btn_solveMaze.setDisable(false);
        btn_Save.setDisable(false);
        viewModel.setSolutionNull();
    }

    public void displayMaze(int[][] maze) {
        this.characterPositionRow.set(characterPositionRow + "");
        this.characterPositionColumn.set(characterPositionColumn + "");
        int goalRow=viewModel.getGoalRow();
        int goalCol=viewModel.getGoalColumn();
        mazeDisplayer.setMaze(maze, goalRow, goalCol);
        btn_generateMaze.setDisable(false);
    }

    public void displayCharacter(int[][] maze) {
        int characterPositionRow = viewModel.getCharacterPositionRow();
        int characterPositionColumn = viewModel.getCharacterPositionColumn();
        characterDisplayer.setCharacterPosition(characterPositionRow, characterPositionColumn);
        characterDisplayer.setMaze(maze);
        int goalRow=viewModel.getGoalRow();
        int goalCol=viewModel.getGoalColumn();
        if (characterPositionRow==goalRow && characterPositionColumn==goalCol){
            viewModel.setToZero();
            btn_generateMaze.setDisable(false);
            solutionDisplayer.clearSol();
            btn_solveMaze.setDisable(true);
            viewModel.setSolutionNull();

            wonGame();
        }
    }

    public void displaySolution(int[][] maze, int[][] sol) {
        solutionDisplayer.setSolution(maze, sol);
        btn_generateMaze.setDisable(false);
    }


    @Override
    public void update(Observable o, Object arg) {
        if (o == viewModel) {
            int num = (int) arg;
            switch (num) {
                case 1:
                    displayMaze(viewModel.getMaze());
                    displayCharacter(viewModel.getMaze());
                    minX = characterDisplayer.getMinX();
                    minY = characterDisplayer.getMinY();
                    break;
                case 2:
                    displaySolution(viewModel.getMaze(), viewModel.getSolution());
                    btn_solveMaze.setDisable(true);
                    break;
                case 3:
                    displayCharacter(viewModel.getMaze());
                    minX = characterDisplayer.getMinX();
                    minY = characterDisplayer.getMinY();
                    break;
                //Load
                case 4:

                    if(btn_solveMaze.isDisable()){
                        bindProperties(viewModel);
                        btn_solveMaze.setDisable(false);
                        btn_Save.setDisable(false);
                    }
                    solutionDisplayer.clearSol();
                    displayMaze(viewModel.getMaze());
                    displayCharacter(viewModel.getMaze());
            }
        }
    }

    public void solveMaze(ActionEvent actionEvent) {
        //isDisplayedSolution = true;
        viewModel.solveMaze();
    }

    private void showAlert(String alertMessage) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(alertMessage);
        alert.show();
    }

    public void wonGame() {
        try {
            mazeDisplayer.clear();
            characterDisplayer.clear();
            Stage stage = new Stage();
            stage.setTitle("You Won The Game!!!");
            FXMLLoader fxmlLoader = new FXMLLoader();
            Parent root = fxmlLoader.load(getClass().getResource("winView.fxml").openStream());
            Scene scene = new Scene(root, 480, 268);
            scene.getStylesheets().add(getClass().getResource("ViewStyle3.css").toExternalForm());
            stage.setScene(scene);
            mazeDisplayer.ControlSong("win");
            stage.initModality(Modality.APPLICATION_MODAL); //Lock the window until it closes
            stage.show();
        } catch (Exception e) {
        }
    }

    public void PlayAgain(){
        Stage stage = (Stage) btn_playAgain.getScene().getWindow();
        stage.close();
        switchScene();
    }

    public void closeWin(){
        Stage stage = (Stage) btn_playAgain.getScene().getWindow();
        stage.close();
        mazeDisplayer.ControlSong("stop");
    }


    //Load maze from file
    public void load() throws IOException, ClassNotFoundException {
        viewModel.load();
    }

    //Save maze to file
    public void save() throws IOException {
        viewModel.save();
    }

    public void muteOrResumeSound(ActionEvent actionEvent)
    {
        if(btn_sound.isSelected()==false) {
            mazeDisplayer.ControlSong("mute");
        }
        else   mazeDisplayer.ControlSong("resume");
    }

    public void mouseClicked(MouseEvent mouseEvent) {
        this.mazeDisplayer.requestFocus();
    }

    //Exit the game
    public void exit() {
        viewModel.shutDown();
        Platform.exit();
    }

    public void KeyPressed(KeyEvent keyEvent) {
        if(isPlayed){
            viewModel.moveCharacter(keyEvent.getCode());
        }
        isControlDown = keyEvent.isControlDown();
        keyEvent.consume();
    }

    public void KeyReleased(Event keyEvent){
        isControlDown = false;
    }

    public void properties(ActionEvent actionEvent) {
        try {
            Stage stage = new Stage();
            stage.setTitle("Properties");
            FXMLLoader fxmlLoader = new FXMLLoader();
            Parent root = fxmlLoader.load(getClass().getResource("properties.fxml").openStream());
            Scene scene = new Scene(root, 350, 250);
            MenuButton menuButtonLabel=(MenuButton)scene.lookup("#levelList");
            level= Server.Configurations.prop.getProperty("generateMaze");
            menuButtonLabel.setText(level);
            MenuButton menuButtonSolve=(MenuButton)scene.lookup("#solveList");
            solveAlgo= Server.Configurations.prop.getProperty("solveMaze");
            menuButtonSolve.setText(solveAlgo);
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL); //Lock the window until it closes
            stage.show();
        } catch (Exception e) {

        }
    }

    public String getCharacterPositionRow() {
        return characterPositionRow.get();
    }

    public StringProperty characterPositionRowProperty() {
        return characterPositionRow;
    }

    public String getCharacterPositionColumn() {
        return characterPositionColumn.get();
    }

    public StringProperty characterPositionColumnProperty() {
        return characterPositionColumn;
    }

    public void About() {
        try {
            Stage stage = new Stage();
            stage.setTitle("About");
            FXMLLoader fxmlLoader = new FXMLLoader();
            Parent root = fxmlLoader.load(getClass().getResource("about.fxml").openStream());
            Scene scene = new Scene(root, 450, 200);
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL); //Lock the window until it closes
            stage.show();
        } catch (Exception e) {

        }
    }

    public void help(ActionEvent actionEvent) {
        try {
            Stage stage = new Stage();
            stage.setTitle("help");
            FXMLLoader fxmlLoader = new FXMLLoader();
            Parent root = fxmlLoader.load(getClass().getResource("help.fxml").openStream());
            Scene scene = new Scene(root, 440, 280);
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL); //Lock the window until it closes
            stage.show();
        } catch (Exception e) {

        }
    }

    //functions for the start view
    public void smallMaze() {
        rows = 20;
        columns = 20;
        btn_start.setDisable(false);
        btn_small.setDisable(true);
        //enable other buttons
        if (btn_medium.isDisable())
            btn_medium.setDisable(false);
        if (btn_large.isDisable())
            btn_large.setDisable(false);
    }

    public void mediumMaze() {
        rows = 40;
        columns = 40;
        btn_start.setDisable(false);
        btn_medium.setDisable(true);
        //enable other buttons
        if (btn_large.isDisable())
            btn_large.setDisable(false);
        if (btn_small.isDisable())
            btn_small.setDisable(false);
    }

    public void largeMaze() {
        rows = 60;
        columns = 60;
        btn_start.setDisable(false);
        btn_large.setDisable(true);
        //enable other buttons
        if (btn_medium.isDisable())
            btn_medium.setDisable(false);
        if (btn_small.isDisable())
            btn_small.setDisable(false);
    }

    public void startGame() {
        try {
            primaryStage.setScene(scene);

            MazeDisplayer maze_displayer=(MazeDisplayer)scene.lookup("#mazeDisplayer");
            maze_displayer.ControlSong("stop");
            RadioButton button= (RadioButton)scene.lookup("#btn_sound");
            button.setSelected(false);

        } catch (Exception e) {
        }
    }

    public MyViewModel getViewModel() {
        return viewModel;
    }

    public void selectMenuLevel(ActionEvent actionEvent){
        level=((MenuItem)(actionEvent.getSource())).getText();
        levelList.setText(level);
    }

    public void selectMenuSolve(ActionEvent actionEvent){
        solveAlgo=((MenuItem)(actionEvent.getSource())).getText();
        solveList.setText(solveAlgo);
    }

    public void setSettings(ActionEvent actionEvent) {
        Stage stage = (Stage) btn_OK.getScene().getWindow();
        stage.close();
        if (level.equals("Empty")){
            Server.Configurations.prop.setProperty("generateMaze","EmptyMazeGenerator");
        }
        else if (level.equals("Simple")){
            Server.Configurations.prop.setProperty("generateMaze","SimpleMazeGenerator");
        }
        else  if (level.equals("Complicated")){
            Server.Configurations.prop.setProperty("generateMaze","MyMazeGenerator");
        }
        if (solveAlgo.equals("Depth First Search")){
            Server.Configurations.prop.setProperty("solveMaze","DepthFirstSearch");
        }
        else if (solveAlgo.equals("Breadth First Search")){
            Server.Configurations.prop.setProperty("solveMaze","BreadthFirstSearch");
        }
        else  if (solveAlgo.equals("Best First Search")){
            Server.Configurations.prop.setProperty("solveMaze","BestFirstSearch");
        }
    }

    public void onMouseDrag(MouseEvent event){
        double cellHeight = mazeDisplayer.getCellHeight();
        double cellWidth = mazeDisplayer.getCellWidth();

        //Right
        if(event.getX() > cellWidth + minX)
            viewModel.moveCharacter(KeyCode.NUMPAD6);
            //Down
        else if(event.getY() > cellHeight + minY)
            viewModel.moveCharacter(KeyCode.NUMPAD2);
            //Left
        else if(event.getX() < minX)
            viewModel.moveCharacter(KeyCode.NUMPAD4);
            //Up
        else if(event.getY() < minY)
            viewModel.moveCharacter(KeyCode.NUMPAD8);
        //Up right
        else if(event.getX() > cellWidth + minX && event.getY() < minY)
            viewModel.moveCharacter(KeyCode.NUMPAD9);
        //RightDown
        else if(event.getX() > cellWidth + minX && event.getY() > minY + cellHeight)
            viewModel.moveCharacter(KeyCode.NUMPAD3);
        //LeftDown
        else if(event.getX() < minX && event.getY() > minY + cellHeight)
            viewModel.moveCharacter(KeyCode.NUMPAD1);
        //UpLeft
        else if(event.getX() < minX && event.getY() > minY)
            viewModel.moveCharacter(KeyCode.NUMPAD7);

    }
}

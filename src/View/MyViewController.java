package View;

import ViewModel.MyViewModel;
import ViewModel.MyViewModel;
import algorithms.mazeGenerators.Maze;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
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
    private Parent root;
    private static Scene scene;
    public BorderPane startPane;
    public BorderPane borderPane;
    private boolean isDisplayedMaze;
    private boolean isDisplayedSolution = false;

    @FXML
    public javafx.scene.layout.Pane pane;
    public javafx.scene.layout.AnchorPane anchorPane;
    public javafx.scene.control.Label lbl_rowsNum;
    public javafx.scene.control.Label lbl_columnsNum;
    public javafx.scene.control.Button btn_generateMaze;
    public javafx.scene.control.Button btn_solveMaze;
   // public javafx.scene.control.Button btn_winMaze;

    public javafx.scene.control.Button btn_small;
    public javafx.scene.control.Button btn_medium;
    public javafx.scene.control.Button btn_large;
    public javafx.scene.control.Button btn_start;

    //region String Property for Binding
    public StringProperty characterPositionRow = new SimpleStringProperty();
    public StringProperty characterPositionColumn = new SimpleStringProperty();
    public StringProperty mazeRowSize = new SimpleStringProperty();
    public StringProperty mazeColSize = new SimpleStringProperty();

    public void initialize(MyViewModel viewModel, Stage primaryStage, Scene scene) {
        this.viewModel = viewModel;
        this.scene = scene;
        this.primaryStage = primaryStage;
        //setResizeEvent(scene);
        btn_solveMaze.setDisable(true);
        btn_Save.setDisable(true);
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
//            BackgroundImage myBI= new BackgroundImage(new Image("/images/back.jpg",32,32,false,true),
//                    BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
//                    BackgroundSize.DEFAULT);

            FXMLLoader fxmlLoader1 = new FXMLLoader();
            Parent root = fxmlLoader1.load(getClass().getResource("startView.fxml").openStream());
            //new scene
            Scene scene1 = new Scene(root, 780, 585);
            scene1.getStylesheets().add(getClass().getResource("ViewStyle2.css").toExternalForm());
            primaryStage.setScene(scene1);
            mazeDisplayer.ControlSong("start");
           // startPane.setBackground(new Background(myBI));
            primaryStage.show();

        } catch (Exception e) {

        }
    }

    public void setScroll(ScrollEvent e){

            double deltaY = e.getDeltaY();

            if(deltaY > 0){
                mazeDisplayer.setZoom(mazeDisplayer.getZoom()*1.1);
                characterDisplayer.setZoom(characterDisplayer.getZoom()*1.1);
                solutionDisplayer.setZoom(solutionDisplayer.getZoom()*1.1);
                //mazeDisplayer.setScaleY(mazeDisplayer.getScaleY()*1.1);
                //mazeDisplayer.setHeight(mazeDisplayer.getCanvasHeight()*1.1);
                //mazeDisplayer.setResize(mazeDisplayer.getHeight()*1.1,mazeDisplayer.getWidth()*1.1);
            }
            else{
//                mazeDisplayer.setScaleX(mazeDisplayer.getScaleX()/1.1);
                mazeDisplayer.setZoom(mazeDisplayer.getZoom()/1.1);
                characterDisplayer.setZoom(characterDisplayer.getZoom()/1.1);
                solutionDisplayer.setZoom(solutionDisplayer.getZoom()/1.1);
//                mazeDisplayer.setScaleY(mazeDisplayer.getScaleY()/1.1);
                //mazeDisplayer.setResize(mazeDisplayer.getHeight()/1.1,mazeDisplayer.getWidth()/1.1);
                //mazeDisplayer.setHeight(mazeDisplayer.getCanvasHeight()/1.1);
                //mazeDisplayer.setWidth(mazeDisplayer.getCanvasWidth()/1.1);
            }

        displayMaze(viewModel.getMaze());
        displayCharacter(viewModel.getMaze());
        if(viewModel.getSolution() != null){
            displaySolution(viewModel.getMaze(),viewModel.getSolution());
        }

    }

    public void generateMaze() {
        btn_generateMaze.setDisable(true);
        solutionDisplayer.clearSol();
        isDisplayedMaze = true;

        //Maybe move it to initialize
        bindProperties(viewModel);
        viewModel.generateMaze(rows, columns);
        btn_solveMaze.setDisable(false);
        btn_Save.setDisable(false);
        viewModel.setSolutionNull();
    }

    public void displayMaze(int[][] maze) {
        this.characterPositionRow.set(characterPositionRow + "");
        this.characterPositionColumn.set(characterPositionColumn + "");
        mazeDisplayer.setMaze(maze);
    }

    public void displayCharacter(int[][] maze) {
        int characterPositionRow = viewModel.getCharacterPositionRow();
        int characterPositionColumn = viewModel.getCharacterPositionColumn();
        characterDisplayer.setCharacterPosition(characterPositionRow, characterPositionColumn);
        characterDisplayer.setMaze(maze);
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
                    break;
                case 2:
                    displaySolution(viewModel.getMaze(), viewModel.getSolution());
                    btn_solveMaze.setDisable(true);
                    break;
                case 3:
                    displayCharacter(viewModel.getMaze());
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
            /*
            if(isDisplayedMaze){
                isDisplayedMaze = false;
            }
            if(isDisplayedSolution){
                isDisplayedSolution= false;
                displaySolution(viewModel.getMaze(),viewModel.getSolution());
            }
            displayCharacter(viewModel.getMaze());*/
            // btn_generateMaze.setDisable(false);
        }
    }

    public void solveMaze(ActionEvent actionEvent) {
        isDisplayedSolution = true;
        viewModel.solveMaze();
    }

    private void showAlert(String alertMessage) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(alertMessage);
        alert.show();
    }

    public void wonGame(ActionEvent actionEvent) {
        try {
            Stage stage = new Stage();
            stage.setTitle("You Won The Game!!!");
            FXMLLoader fxmlLoader = new FXMLLoader();
            Parent root = fxmlLoader.load(getClass().getResource("winView.fxml").openStream());
            Scene scene = new Scene(root, 400, 350);
            stage.setScene(scene);
            mazeDisplayer.ControlSong("win");
            stage.initModality(Modality.APPLICATION_MODAL); //Lock the window until it closes
            stage.show();
        } catch (Exception e) {

        }
    }

//    public void displayMaze(int[][] maze){
//        mazeDisplayer.setMaze(maze);
//        int characterPositionRow = viewModel.getCharacterPositionRow();
//        int characterPositionColumn = viewModel.getCharacterPositionColumn();
//        mazeDisplayer.setCharacterPosition(characterPositionRow, characterPositionColumn);
//        this.characterPositionRow.set(characterPositionRow + "");
//        this.characterPositionColumn.set(characterPositionColumn + "");
//    }

    //Load maze from file
    public void load() throws IOException, ClassNotFoundException {
        viewModel.load();
    }

    //Save maze to file
    public void save() throws IOException {
        viewModel.save();
    }

    //Exit the game
    public void exit() {
        // viewModel.shutdown();
        Platform.exit();
    }

    public void KeyPressed(KeyEvent keyEvent) {
        viewModel.moveCharacter(keyEvent.getCode());
        keyEvent.consume();
    }

    public void properties(ActionEvent actionEvent) {

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

//    public void setResizeEvent(Scene scene) {
//        long width = 0;
//        long height = 0;
//        scene.widthProperty().addListener(new ChangeListener<Number>() {
//            @Override
//            public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneWidth, Number newSceneWidth) {
//                System.out.println("Width: " + newSceneWidth);
//            }
//        });
//        scene.heightProperty().addListener(new ChangeListener<Number>() {
//            @Override
//            public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneHeight, Number newSceneHeight) {
//                System.out.println("Height: " + newSceneHeight);
//            }
//        });
//    }

    public void About() {
        try {
            Stage stage = new Stage();
            stage.setTitle("About");
            FXMLLoader fxmlLoader = new FXMLLoader();
            Parent root = fxmlLoader.load(getClass().getResource("about.fxml").openStream());
            Scene scene = new Scene(root, 400, 350);
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
            Scene scene = new Scene(root, 400, 350);
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
            mazeDisplayer.ControlSong("play");
            // primaryStage.initModality(Modality.APPLICATION_MODAL); //Lock the window until it closes
            primaryStage.show();
        } catch (Exception e) {
        }
    }

    public MyViewModel getViewModel() {
        return viewModel;
    }
}
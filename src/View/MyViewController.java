package View;

import ViewModel.MyViewModel;
import ViewModel.MyViewModel;
import algorithms.mazeGenerators.Maze;
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
import javafx.scene.input.KeyEvent;
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

    @FXML
    private MyViewModel viewModel;
    public MazeDisplayer mazeDisplayer;
    public SolutionDisplayer solutionDisplayer;
    private Stage primaryStage;
    private Stage mazeStage;
    private Parent root;
    private Scene scene;
    public javafx.scene.control.Label lbl_rowsNum;
    public javafx.scene.control.Label lbl_columnsNum;
    public javafx.scene.control.Button btn_generateMaze;
    public javafx.scene.control.Button btn_solveMaze;
    public javafx.scene.control.Button btn_winMaze;

    private int rows=0;
    private int columns=0;


    public void initialize(MyViewModel viewModel, Stage primaryStage, Scene scene) {
        this.viewModel = viewModel;
        this.scene = scene;
        this.primaryStage = primaryStage;
        bindProperties(viewModel);
        setResizeEvent(scene);
        //btn_start.setDisable(true);
    }

    private void bindProperties(MyViewModel viewModel) {
        lbl_rowsNum.textProperty().bind(viewModel.characterPositionRow);
        lbl_columnsNum.textProperty().bind(viewModel.characterPositionColumn);
    }

    public void start(){

        try {
            primaryStage.setScene(scene);
            primaryStage.initModality(Modality.APPLICATION_MODAL); //Lock the window until it closes
            primaryStage.show();
        } catch (Exception e) {

        }
        viewModel.generateMaze(rows, columns);
        bindProperties(viewModel);
        btn_solveMaze.setDisable(false);
        btn_winMaze.setDisable(false);
    }

    public void generateMaze() {
        btn_generateMaze.setDisable(true);
        viewModel.generateMaze(rows, columns);
        btn_solveMaze.setDisable(false);
        btn_winMaze.setDisable(false);
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o == viewModel) {
            displayMaze(viewModel.getMaze());
            btn_generateMaze.setDisable(false);
        }
    }


    public void solveMaze(ActionEvent actionEvent) {
        showAlert("Solving maze..");
    }

    private void showAlert(String alertMessage) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(alertMessage);
        alert.show();
    }

    public void wonGame(ActionEvent actionEvent){
        try {
            Stage stage = new Stage();
            stage.setTitle("You Won The Game!!!");
            FXMLLoader fxmlLoader = new FXMLLoader();
            Parent root = fxmlLoader.load(getClass().getResource("winView.fxml").openStream());
            Scene scene = new Scene(root, 400, 350);
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL); //Lock the window until it closes
            stage.show();
        } catch (Exception e) {

        }
    }

    public void displayMaze(int[][] maze){
        mazeDisplayer.setMaze(maze);
        int characterPositionRow = viewModel.getCharacterPositionRow();
        int characterPositionColumn = viewModel.getCharacterPositionColumn();
        mazeDisplayer.setCharacterPosition(characterPositionRow, characterPositionColumn);
        this.characterPositionRow.set(characterPositionRow + "");
        this.characterPositionColumn.set(characterPositionColumn + "");
    }



    //Load mze from file
    public void load() throws IOException, ClassNotFoundException {
        FileChooser fc = new FileChooser();
        fc.setTitle("Open Maze from File");
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Text files", "*.txt");
        fc.getExtensionFilters().add(extFilter);
        Window primaryStage = null;

        File f = fc.showOpenDialog(primaryStage);
        if (f!=null){
            ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(f));
            Object readFromFile= inputStream.readObject();
            viewModel.setSavedMaze(readFromFile) ;
            inputStream.close();

        }

    }

    //Save maze to file
    public void save() throws IOException {
        //file chooser
        FileChooser fc = new FileChooser();
        fc.setTitle("Save Maze to File");
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Text files", "*.txt");
        fc.getExtensionFilters().add(extFilter);
        Window primaryStage = null;
        File f = fc.showSaveDialog(primaryStage);
        if(f != null) {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(f));
            //write the maze to file
            oos.writeObject(viewModel.getObject());
            oos.close();
        }
    }

    //Exit the game
    public void exit(){

        viewModel.shutdown();
    }

    public void KeyPressed(KeyEvent keyEvent) {
        viewModel.moveCharacter(keyEvent.getCode());
        keyEvent.consume();
    }

    public void properties(ActionEvent actionEvent){

    }

    //region String Property for Binding
    public StringProperty characterPositionRow = new SimpleStringProperty();

    public StringProperty characterPositionColumn = new SimpleStringProperty();

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

    public void setResizeEvent(Scene scene) {
        long width = 0;
        long height = 0;
        scene.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneWidth, Number newSceneWidth) {
                System.out.println("Width: " + newSceneWidth);
            }
        });
        scene.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneHeight, Number newSceneHeight) {
                System.out.println("Height: " + newSceneHeight);
            }
        });
    }

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

    public void help(ActionEvent actionEvent){
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


    public void setRows(int rows){
        this.rows=rows;
    }

    public void setColumns(int columns){
        this.columns=columns;
    }


}

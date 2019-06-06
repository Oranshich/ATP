package View;

import ViewModel.MyViewModel;
import ViewModel.MyViewModel;
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
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;

public class MyViewController implements IView, Observer {

    public BorderPane borderPane;
    private boolean isDisplayedMaze;
    private boolean isDisplayedSolution = false;
    @FXML
    private MyViewModel viewModel;
    @FXML
    public MazeDisplayer mazeDisplayer;
    @FXML
    public CharacterDisplayer characterDisplayer;
    public SolutionDisplayer solutionDisplayer;
    private Stage winStage;
    private Stage loseStage;
    public javafx.scene.control.TextField txtfld_rowsNum;
    public javafx.scene.control.TextField txtfld_columnsNum;
    public javafx.scene.control.Label lbl_rowsNum=null;
    public javafx.scene.control.Label lbl_columnsNum=null;
    public javafx.scene.control.Button btn_generateMaze;
    public javafx.scene.control.Button btn_solveMaze;
    public javafx.scene.control.Button btn_winMaze;
    @FXML
    public javafx.scene.layout.Pane pane;
    public javafx.scene.layout.AnchorPane anchorPane;

    public void displayMaze(int[][] maze){
        this.characterPositionRow.set(characterPositionRow + "");
        this.characterPositionColumn.set(characterPositionColumn + "");
        mazeDisplayer.setMaze(maze);

    }

    public void displayCharacter(int[][] maze){
        int characterPositionRow = viewModel.getCharacterPositionRow();
        int characterPositionColumn = viewModel.getCharacterPositionColumn();
        characterDisplayer.setCharacterPosition(characterPositionRow, characterPositionColumn);
        characterDisplayer.setMaze(maze);
    }

    public void displaySolution(int[][] maze, int [][] sol){
        solutionDisplayer.setSolution(maze, sol);
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o == viewModel) {
            int num = (int)arg;
            switch (num){
                case 1:
                    displayMaze(viewModel.getMaze());
                    displayCharacter(viewModel.getMaze());
                    break;
                case 2:
                    displaySolution(viewModel.getMaze(),viewModel.getSolution());
                    btn_solveMaze.setDisable(true);
                    break;
                case 3:
                    displayCharacter(viewModel.getMaze());
                    break;
            }
            /*
            if(isDisplayedMaze){
                isDisplayedMaze = false;

            }

            if(isDisplayedSolution){
                isDisplayedSolution = false;
                displaySolution(viewModel.getMaze(),viewModel.getSolution());
            }


            displayCharacter(viewModel.getMaze());*/
           // btn_generateMaze.setDisable(false);
        }
    }

    public void setViewModel(MyViewModel viewModel) {
        this.viewModel = viewModel;
        bindProperties(viewModel);

    }

    private void bindProperties(MyViewModel viewModel) {
        btn_solveMaze.setDisable(true);
        lbl_rowsNum.textProperty().bind(viewModel.characterPositionRow);
        lbl_columnsNum.textProperty().bind(viewModel.characterPositionColumn);

        pane.prefHeightProperty().bind(borderPane.heightProperty());
        pane.prefWidthProperty().bind(borderPane.widthProperty());
        mazeDisplayer.heightProperty().bind(pane.heightProperty());
        mazeDisplayer.widthProperty().bind(pane.widthProperty());

        characterDisplayer.heightProperty().bind(pane.heightProperty());
        characterDisplayer.widthProperty().bind(pane.widthProperty());

        solutionDisplayer.heightProperty().bind(pane.heightProperty());
        solutionDisplayer.widthProperty().bind(pane.widthProperty());

        mazeDisplayer.heightProperty().addListener(((observable, oldValue, newValue) -> mazeDisplayer.redraw()));
        mazeDisplayer.widthProperty().addListener((observable, oldValue, newValue) -> mazeDisplayer.redraw());

        characterDisplayer.heightProperty().addListener(((observable, oldValue, newValue) -> characterDisplayer.drawCharacter()));
        characterDisplayer.widthProperty().addListener((observable, oldValue, newValue) -> characterDisplayer.drawCharacter());

        solutionDisplayer.heightProperty().addListener(((observable, oldValue, newValue) -> solutionDisplayer.drawSolution()));
        solutionDisplayer.widthProperty().addListener((observable, oldValue, newValue) -> solutionDisplayer.drawSolution());
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

    public void generateMaze() {
        solutionDisplayer.clearSol();
        isDisplayedMaze = true;
        int rows = 20;//Integer.valueOf(txtfld_rowsNum.getText());
        int columns = 20;//Integer.valueOf(txtfld_columnsNum.getText());
        //btn_generateMaze.setDisable(true);
        btn_solveMaze.setDisable(false);
        viewModel.generateMaze(rows, columns);
    }

    //Load maze from file
    public void load(){

    }

    //Save maze to file
    public void save(){

    }

    //Exit the game
    public void exit(){

        viewModel.shutdown();
        Platform.exit();
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

    public StringProperty mazeRowSize = new SimpleStringProperty();
    public StringProperty mazeColSize = new SimpleStringProperty();

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
}

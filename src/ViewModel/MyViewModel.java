package ViewModel;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

import Model.IModel;
import algorithms.mazeGenerators.Maze;
import algorithms.search.Solution;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Alert;
import javafx.scene.input.KeyCode;
public class MyViewModel extends Observable implements Observer{

    private IModel model;
    private int characterPositionRowIndex;
    private int characterPositionColumnIndex;
    //string properties
    public StringProperty characterPositionRow = new SimpleStringProperty("0"); //For Binding
    public StringProperty characterPositionColumn = new SimpleStringProperty("0"); //For Binding

    public MyViewModel(IModel model)
    {
        this.model = model;
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o==model){
            characterPositionRowIndex = model.getCharacterPositionRow();
            characterPositionRow.set(characterPositionRowIndex + "");
            characterPositionColumnIndex = model.getCharacterPositionColumn();
            characterPositionColumn.set(characterPositionColumnIndex + "");
            setChanged();
            notifyObservers(arg);
        }
    }

    public void setSolutionNull(){ model.setSolutionNull();}

    public void solveMaze(){
        model.solveMaze();
    }

    public void generateMaze(int rows, int columns){
        model.generateMaze(rows, columns);
    }

    public void moveCharacter(KeyCode movement){
        model.moveCharacter(movement);
    }

    public int[][] getMaze() {
        return model.getMaze();
    }

    public int getCharacterPositionRow() {
        return model.getCharacterPositionRow();
    }

    public int getCharacterPositionColumn() {
        return model.getCharacterPositionColumn();
    }

    public int [][] getSolution(){return model.getSolution();}

    public int getLastCharRow(){return model.getCharacterPositionRow();}

    public int getLastCharCol(){return model.getCharacterPositionColumn();}

    public int getGoalRow(){ return model.getGoalRow();}

    public int getGoalColumn(){ return model.getGoalColumn();}

    //save maze
    public void save() throws IOException {
        model.save();
    }

    //load maze
    public void load() throws IOException, ClassNotFoundException{
        model.load();
    }
     public void shutDown(){ model.shutdown();}

    public void setToZero() {
        model.setToZero();
    }
}

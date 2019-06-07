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

    public StringProperty characterPositionRow = new SimpleStringProperty("1"); //For Binding
    public StringProperty characterPositionColumn = new SimpleStringProperty("1"); //For Binding

    public MyViewModel(IModel model){
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

    public void generateMaze(int rows, int columns){
        model.generateMaze(rows, columns);
    }

    public void moveCharacter(KeyCode movement){
        model.moveCharacter(movement);
    }

    public int[][] getMaze() {
        return model.getMaze();
    }
    public int getLastCharRow(){return model.getCharacterPositionRow();}

    public int getLastCharCol(){return model.getCharacterPositionColumn();}

    public Maze getObject(){ return model.getObject();}

    public int getCharacterPositionRow() {
        return characterPositionRowIndex;
    }

    public int getCharacterPositionColumn() {
        return characterPositionColumnIndex;
    }

    public void solveMaze(){
        model.solveMaze();
    }

    public int [][] getSolution(){return model.getSolution();}


    //save maze
    public void save() throws IOException {
        model.save();
    }

    //load maze
    public void load() throws IOException, ClassNotFoundException{
        model.load();
    }
}

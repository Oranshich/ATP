package Model;

import algorithms.mazeGenerators.Maze;
import algorithms.search.Solution;
import javafx.scene.input.KeyCode;

import java.io.IOException;

public interface IModel {
    void generateMaze(int width, int height);
    void moveCharacter(KeyCode movement);
    int[][] getMaze();
    int getCharacterPositionRow();
    int getCharacterPositionColumn();
    void solveMaze();
    int [][] getSolution();
    void shutdown();
    void save() throws IOException;
    void load() throws IOException, ClassNotFoundException;
    void setSolutionNull();
    int getGoalRow();
    int getGoalColumn();

    void setToZero();
}

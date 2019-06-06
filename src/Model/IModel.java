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
    Solution getSolution();
    void shutdown();
    Maze getObject();
    void save() throws IOException;
    void load() throws IOException, ClassNotFoundException;
}

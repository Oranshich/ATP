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


public class MyStartController{
        private MyViewController viewContoller;
        private int rows;
        private int columns;
        private Scene scene;
         @FXML
        public javafx.scene.control.Button btn_small;
        public javafx.scene.control.Button btn_medium;
        public javafx.scene.control.Button btn_large;
        public javafx.scene.control.Button btn_start;

    public void setMyStartController(MyViewController viewContoller){
        this. viewContoller=viewContoller;
        btn_start.setDisable(true);
    }
        public void smallMaze()
        {
            rows=20;
            columns=20;
            btn_start.setDisable(false);
        }

        public void mediumMaze()
        {
            rows=40;
            columns=40;
            btn_start.setDisable(false);
        }

        public void largeMaze()
        {
            rows=60;
            columns=60;
            btn_start.setDisable(false);
        }

        public void startGame(){
            btn_start.setDisable(true);
            viewContoller.setRows(rows);
            viewContoller.setColumns(columns);
            viewContoller.start();
        }

        public void setMyScene(Scene scene) { this.scene=scene;
        }
    }

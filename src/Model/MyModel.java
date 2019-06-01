package Model;
import Server.*;
import algorithms.mazeGenerators.Maze;
import algorithms.search.Solution;
import com.sun.org.apache.xpath.internal.operations.String;
import javafx.scene.input.KeyCode;
import java.util.Observable;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import Client.Client;
import Client.IClientStrategy ;
import IO.MyDecompressorInputStream;
import algorithms.mazeGenerators.Position;
import algorithms.search.AState;
import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

import static com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type.String;

public class MyModel extends Observable implements IModel  {
    public Server mazeGeneratingServer;
    public Server solveMazeServer;
    private int characterRow;
    private int characterColoumn;
    private Maze maze = null;
    private Solution solution = null;
    //private ExecutorService threadPool = Executors.newCachedThreadPool();

    public MyModel() {
        mazeGeneratingServer = new Server(5400,1000,new ServerStrategyGenerateMaze());
        solveMazeServer = new Server(5401,1000,new ServerStrategySolveSearchProblem());
    }

    public void startServers() {
        mazeGeneratingServer.start();
        solveMazeServer.start();
    }

    public void stopServers() {
        mazeGeneratingServer.stop();
        solveMazeServer.stop();
    }

    @Override
    public void generateMaze(int width, int height) {

        try{
            CommunicateWithServer_MazeGenerating(width,height);
            Thread.sleep(1000);
            characterRow = maze.getStartPosition().getRowIndex();
            characterColoumn = maze.getStartPosition().getColumnIndex();

        } catch (InterruptedException e){
        }
        setChanged();
        notifyObservers();
    }

    @Override
    public void moveCharacter(KeyCode movement) {

    }

    @Override
    public int[][] getMaze() {
        //return  maze.getMaze();
        return null;
    }

    @Override
    public int getCharacterPositionRow() {
        return characterRow;
    }

    @Override
    public int getCharacterPositionColumn() {
        return characterColoumn;
    }

    @Override
    public void solveMaze() {

        CommunicateWithServer_SolveSearchProblem(maze);
        setChanged();
        notifyObservers();
    }

    @Override
    public Solution getSolution() {
        return solution;
    }

    @Override
    public void setSavedMaze(Maze maze) {
        this.maze = maze;
        //characterRow = maze.getLastPlayerPosition().getRowIndex();
        //characterColoumn = maze.getLastPlayerPosition().getColoumnIndex();
        setChanged();
        notifyObservers();
    }

    @Override
    public void shutdown() {
        stopServers();
        setChanged();
        notifyObservers("Shutdown servers");
    }

    private void CommunicateWithServer_MazeGenerating(int height, int width) {
        try {
            Client client = new Client(InetAddress.getLocalHost(), 5400, new IClientStrategy() {
                @Override
                public void clientStrategy(InputStream inFromServer, OutputStream outToServer) {
                    try {
                        ObjectOutputStream toServer = new ObjectOutputStream(outToServer);
                        ObjectInputStream fromServer = new ObjectInputStream(inFromServer);
                        toServer.flush();
                        int[] mazeDimensions = new int[]{height, width};
                        toServer.writeObject(mazeDimensions); //send maze dimensions to server
                        toServer.flush();
                        byte[] compressedMaze = (byte[]) fromServer.readObject(); //read generated maze (compressed with MyCompressor) from server
                        InputStream is = new MyDecompressorInputStream(new ByteArrayInputStream(compressedMaze));
                        byte[] decompressedMaze = new byte[10000]; //allocating byte[] for the decompressed maze -
                        is.read(decompressedMaze); //Fill decompressedMaze with bytes
                        maze = new Maze(decompressedMaze);
                        //maze.print();
                    } catch (Exception e) {
                        //e.printStackTrace();
                    }
                }
            });
            client.communicateWithServer();
        } catch (UnknownHostException e) {
           // e.printStackTrace();
        }
    }

    private void CommunicateWithServer_SolveSearchProblem(Maze MyMaze) {
        try {
            Client client = new Client(InetAddress.getLocalHost(), 5401, new IClientStrategy() {
                @Override
                public void clientStrategy(InputStream inFromServer, OutputStream outToServer) {
                    try {
                        ObjectOutputStream toServer = new ObjectOutputStream(outToServer);
                        ObjectInputStream fromServer = new ObjectInputStream(inFromServer);
                        toServer.flush();
                        Maze maze = MyMaze;
                        maze.print();
                        toServer.writeObject(maze); //send maze to server
                        toServer.flush();
                        Solution mazeSolution = (Solution) fromServer.readObject(); //read generated maze (compressed with MyCompressor) from server

                        //Print Maze Solution retrieved from the server
                       // System.out.println(String.format("Solution steps: %s", mazeSolution));
                        ArrayList<AState> mazeSolutionSteps = mazeSolution.getSolutionPath();
                        for (int i = 0; i < mazeSolutionSteps.size(); i++) {
                         //   System.out.println(String.format("%s. %s", i, mazeSolutionSteps.get(i).toString()));
                        }
                        solution=mazeSolution;
                    } catch (Exception e) {
                        //e.printStackTrace();
                    }
                }
            });
            client.communicateWithServer();
        } catch (UnknownHostException e) {
           // e.printStackTrace();
        }
    }
}

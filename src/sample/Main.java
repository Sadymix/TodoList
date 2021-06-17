package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import sample.datamodel.TodoData;

import java.io.IOException;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("mainWindow.fxml"));
        primaryStage.setTitle("TodoList");
        primaryStage.setScene(new Scene(root, 900, 500));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }

    //event that happens when we stop the program
    @Override
    public void stop() throws Exception {
        try{
            //storing the todoItems that we created
            TodoData.getInstance().storeTodoItems();
        }catch(IOException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void init() throws Exception {
        try{
            //loading the data of created items
            TodoData.getInstance().loadTodoItems();
        }catch(IOException e) {
            System.out.println(e.getMessage());
        }
    }
}

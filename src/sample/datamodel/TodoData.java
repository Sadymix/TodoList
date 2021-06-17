package sample.datamodel;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;

//singleton project patter, which guarantees that only one object will be created
public class TodoData {
    private static TodoData instance = new TodoData();
    private static String filename = "TodoListItems.txt";

    private ObservableList<TodoItem> todoItems;
    private DateTimeFormatter formatter;

    //returning the instance of our singleton class
    public static TodoData getInstance() {
        return instance;
    }

    private TodoData() {
        formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    }

    //to ensure that we dont get any other instance of the singleton class (need that for controller to store the hard coded items)
//    private TodoData() {
//        formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
//    }

    public ObservableList<TodoItem> getTodoItems() {
        return todoItems;
    }
    //adding item that is passed to method to the todoItems list
    public void addTodoItem(TodoItem item) {
        todoItems.add(item);
    }

    //setting is needed for saving the embed items
//    public void setTodoItems(List<TodoItem> todoItems) {
//        this.todoItems = todoItems;
//    }

    public void loadTodoItems() throws IOException {
        //loading todoItems from the file (it is used because in controller we use setAll())
        todoItems = FXCollections.observableArrayList();
        Path path = Paths.get(filename);
        BufferedReader br = Files.newBufferedReader(path);

        String input;
        try{
            while((input = br.readLine()) != null) {
                // to separate each value in line by tab (in real aplication XML or database)
                String[] itemPieces = input.split("\t");

                // Strings that are in file
                String shortDescription = itemPieces[0];
                String details = itemPieces[1];
                String dateString = itemPieces[2];

                LocalDate date = LocalDate.parse(dateString, formatter);
                TodoItem todoItem = new TodoItem(shortDescription, details, date);
                todoItems.add(todoItem);
            }

        }finally {
            //trying if we have a valid object (object is not null)
            if(br != null){
                br.close();
            }
        }
    }

    public void storeTodoItems() throws IOException {
        Path path = Paths.get(filename);
        BufferedWriter bw = Files.newBufferedWriter(path);

        try{
            //Iterating threw the list of todoItems (creating an iterator that we can check entry by entry from our List)
            Iterator<TodoItem> iterator = todoItems.iterator();
            while(iterator.hasNext()) {
                TodoItem item = iterator.next();
                bw.write(String.format("%s\t%s\t%s", item.getShortDescription(), item.getDetails(),
                        item.getDeadline().format(formatter)));
                bw.newLine();
            }
        }finally {
            if(bw != null) {
                bw.close();
            }
        }
    }

    public void deleteTodoItem(TodoItem itemToDelete) {
        todoItems.remove(itemToDelete);
    }


}

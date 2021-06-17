package sample;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.util.Callback;
import sample.datamodel.TodoData;
import sample.datamodel.TodoItem;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class Controller {

    private List<TodoItem> todoItems;
    @FXML
    private ListView<TodoItem> todoListView;

    @FXML
    private TextArea itemDetailsTextArea;

    @FXML
    private Label deadlineLabel;

    @FXML
    private BorderPane mainBoarderPane;
    //method that will initialize our FX application

    @FXML
    private ContextMenu listContextMenu;

    @FXML
    private ToggleButton filterToggleButton;

    //creating a filteredList for handleFilterButton()
    FilteredList<TodoItem> filteredList;

    private Predicate<TodoItem> wantAllItems;
    private Predicate<TodoItem> wantTodaysItems;

    public void initialize() {

//        TodoItem item1 = new TodoItem("Mail birthday card", "Buy a 30th birthday card for John",
//                LocalDate.of(2021, Month.JUNE, 2));
//        TodoItem item2 = new TodoItem("Doctors appointment", "See Dr. Smith at 123 Main Street. Bring paperwork",
//                LocalDate.of(2021, Month.JULY, 5));
//        TodoItem item3 = new TodoItem("Finish design proposal", "I promise Mike I'd email website mockups by Friday 22nd June",
//                LocalDate.of(2021, Month.JUNE, 22));
//        TodoItem item4 = new TodoItem("Pickup Doug at the train station", "Doug is arriving at Jun 1 on the 5:00 pm train",
//                LocalDate.of(2021, Month.JUNE, 1));
//        TodoItem item5 = new TodoItem("Pickup dry cleaning", "The clothes should be ready by Wednesday",
//                LocalDate.of(2021, Month.JUNE, 20));
//
//        //Creating a list of things that you need to do
//        todoItems = new ArrayList<TodoItem>();
//        // adding previously created items to the todoItems
//        todoItems.add(item1);
//        todoItems.add(item2);
//        todoItems.add(item3);
//        todoItems.add(item4);
//        todoItems.add(item5);
//
//        // calling the singleton class to create a file after closing the program for the first times with five records
//        //we previously wrote
//        TodoData.getInstance().setTodoItems(todoItems);

        listContextMenu = new ContextMenu();
        MenuItem deleteMenuItem = new MenuItem("Delete");
        deleteMenuItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                TodoItem item = todoListView.getSelectionModel().getSelectedItem();
                deleteItem(item);
            }
        });

        listContextMenu.getItems().addAll(deleteMenuItem);
        //event listeners that listen if event (changes when we click on the other todoItem in the aplication)
        todoListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TodoItem>() {
            @Override
            public void changed(ObservableValue<? extends TodoItem> observable, TodoItem oldValue, TodoItem newValue) {
                if(newValue != null) {
                    TodoItem item = todoListView.getSelectionModel().getSelectedItem();
                    itemDetailsTextArea.setText(item.getDetails());
                    DateTimeFormatter df = DateTimeFormatter.ofPattern("MMMM d, yyyy");
                    deadlineLabel.setText(df.format(item.getDeadline()));
                }
            }
        });

        wantAllItems = new Predicate<TodoItem>() {
            @Override
            public boolean test(TodoItem todoItem) {
                return true;
            }
        };

        wantTodaysItems = new Predicate<TodoItem>() {
            @Override
            public boolean test(TodoItem todoItem) {
                return (todoItem.getDeadline().equals(LocalDate.now()));
            }
        };
        //assigning the values to filtered list
        filteredList = new FilteredList<TodoItem>(TodoData.getInstance().getTodoItems(),wantAllItems);

         //Creating a sorted list to sort the todoItems by the date to move items by the earlier date
        SortedList<TodoItem> sortedList = new SortedList<TodoItem>(filteredList, new Comparator<TodoItem>() {
            @Override
            public int compare(TodoItem o1, TodoItem o2) {
                return o1.getDeadline().compareTo(o2.getDeadline());
            }
        });

        //setting all items in todoListView with items from the singleton class TodoData
//        todoListView.getItems().setAll(TodoData.getInstance().getTodoItems());
        //using the ObservableList to listen to changes that the user can make
//        todoListView.setItems(TodoData.getInstance().getTodoItems());
        todoListView.setItems(sortedList);
//        //coping todoItems to todoListView
//        todoListView.getItems().setAll(todoItems);
        //only select one item at a time
        todoListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        //select first item in the list
        todoListView.getSelectionModel().selectFirst();

        //this colors the items that is
        todoListView.setCellFactory(new Callback<ListView<TodoItem>, ListCell<TodoItem>>() {
            @Override
            public ListCell<TodoItem> call(ListView<TodoItem> todoItemListView) {
                ListCell<TodoItem> cell = new ListCell<TodoItem>() {
                    @Override
                    protected void updateItem(TodoItem item, boolean empty) {
                        super.updateItem(item, empty);
                        if(empty) {
                             setText(null);
                        }else {
                            setText(item.getShortDescription());
                            if(item.getDeadline().isBefore(LocalDate.now().plusDays(1))) {
                                setTextFill(Color.RED);
                            }else if(item.getDeadline().equals(LocalDate.now().plusDays(1))){
                                setTextFill(Color.BROWN);
                            }

                        }
                    }
                };
                cell.emptyProperty().addListener(
                        (obs, wasEmpty, isNowEmpty) -> {
                            if(isNowEmpty) {
                                cell.setContextMenu(null);
                            }else {
                                cell.setContextMenu(listContextMenu);
                            }
                        }
                );

                return cell;
            }
        });
    }

    // This method is for enter to new DialogPane to create new entry in List
    @FXML
    public void showNewItemDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        //assigning the showNewItemDialog to the mainBoarderPane(id)
         dialog.initOwner(mainBoarderPane.getScene().getWindow());
         dialog.setTitle("Add New TodoItem");
         dialog.setHeaderText("Use this dialog to create a new todoItem");
         FXMLLoader fxmlLoader = new FXMLLoader();
         fxmlLoader.setLocation(getClass().getResource("todoItemDialog.fxml"));
         //loading todoItemDialog
         try {
//            Parent root = FXMLLoader.load(getClass().getResource("todoItemDialog.fxml"));
//            dialog.getDialogPane().setContent(root);
             dialog.getDialogPane().setContent(fxmlLoader.load());
         } catch (IOException e) {
             System.out.println("Couldn't load the dialog");
             e.printStackTrace();
             return;
         }

         // Creating OK and CANCEL buttons in our DialogPane
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

         //checking if the result from user input in todoItemDialog is present if it is we process it with processResult()
        Optional<ButtonType> result = dialog.showAndWait();
        if(result.isPresent() && result.get() == ButtonType.OK) {
            DialogController controller = fxmlLoader.getController();
            //creating the new TodoItem from controller by the processResults()
            TodoItem newItem = controller.processResults();
            //replacing the content in the list
//            todoListView.getItems().setAll(TodoData.getInstance().getTodoItems()); now it's done automatically with the Observable List
            //Selecting the newly created item
            todoListView.getSelectionModel().select(newItem);
//            System.out.println("OK pressed");
        } /*else {
            System.out.println("Cancel pressed");
        }*/
    }

    @FXML
    public void handleKeyPressed(KeyEvent keyEvent) {
        TodoItem selectedItem = todoListView.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            if(keyEvent.getCode().equals(KeyCode.DELETE)) {
                deleteItem(selectedItem);
            }
        }
    }
    //Identifies which item is clicked (tracking, which task is selected)
    @FXML
    public void handleClickListView() {
        TodoItem item = todoListView.getSelectionModel().getSelectedItem();
        //populating itemDetailsTextArea with item details
        itemDetailsTextArea.setText(item.getDetails());
        deadlineLabel.setText(item.getDeadline().toString());
        //populating deadlineLabel with deadline date
//        deadlineLabel.setText(item.getDeadline().toString());
//        System.out.println("The selected item is " + item);
        //creating StringBuilder with details of the item (task)
//        StringBuilder sb = new StringBuilder(item.getDetails());
        //adding space to sb (TextArea)
//        sb.append("\n\n\n\n");
//        sb.append("Due: ");
//        sb.append(item.getDeadline());
//        itemDetailsTextArea.setText(sb.toString());
        //populating TextArea with details from selected todoItem(task)


    }

    //using confirmation window
    public void deleteItem(TodoItem item) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Todo Item");
        alert.setHeaderText("Delete item: " + item.getShortDescription());
        alert.setContentText("Are you sure?  Press OK to confirm, or cancel to Back out.");
        Optional<ButtonType> result = alert.showAndWait();

        if(result.isPresent() && (result.get() == ButtonType.OK)) {
            TodoData.getInstance().deleteTodoItem(item);
        }

    }

    @FXML
    public void handleFilterButton() {
        // fixing the selection problem after clicking the toggleButton
        TodoItem selectedItem = todoListView.getSelectionModel().getSelectedItem();

        if(filterToggleButton.isSelected() ) {
            filteredList.setPredicate(wantTodaysItems);
            if(filteredList.isEmpty()) {
                itemDetailsTextArea.clear();
                deadlineLabel.setText("");
            }else if (filteredList.contains(selectedItem)) {
                todoListView.getSelectionModel().select(selectedItem);
            }else {
                todoListView.getSelectionModel().selectFirst();
            }

        }else {
            filteredList.setPredicate(wantAllItems);
            todoListView.getSelectionModel().select(selectedItem);
        }
    }

    @FXML
    public void handleExit() {
        Platform.exit();
    }
}

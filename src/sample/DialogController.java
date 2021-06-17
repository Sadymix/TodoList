package sample;

import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import sample.datamodel.TodoData;
import sample.datamodel.TodoItem;

import java.time.LocalDate;

public class DialogController {
    @FXML
    private TextField shortDescriptionField;

    @FXML
    private TextArea detailsArea;

    @FXML
    private DatePicker deadlinePicker;

    //This method gather the user input, creates todoItem, and add created todoItem to the list(in todoData instance)
    public TodoItem processResults(){
        //passing the values from the user input in todoItemDialog to the variables
        String shortDescription = shortDescriptionField.getText().trim();
        String details = detailsArea.getText().trim();
        LocalDate deadlineValue = deadlinePicker.getValue();

        TodoItem newItem = new TodoItem(shortDescription, details, deadlineValue);
        //creating an instance of singleton class TodoData and adding the newly created item
        TodoData.getInstance().addTodoItem(newItem);
        return newItem;


    }
}

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class HelloWorldFX extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Hello World!");
        Button btn1 = new Button();
        btn1.setText("Say 'Hello World'");

        /** Example of an Anonymous class
         *
         *  Since the EventHandler<ActionEvent> interface only contains
         *  one method you can use a lambda expression.  See the second button.
         *
         *  Anonymous classes are ideal for implementing an interface that contains
         *  two or more methods.
         */
        btn1.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                System.out.println("Hello World!");
            }
        });

        Button btn2 = new Button();
        btn2.setText("Say 'Hello Lambda'");

        /** Example of a lambda expression.
         */
        btn2.setOnAction(e -> System.out.println("Hello Lambda"));


        HBox root = new HBox();
        root.getChildren().addAll(btn1, btn2);
 //       root.getChildren().add(btn2);
        primaryStage.setScene(new Scene(root, 300, 250));
        primaryStage.show();
    }
}

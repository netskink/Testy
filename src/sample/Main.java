package sample;

import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.*;
import javafx.concurrent.*;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.stage.*;
import javafx.util.Duration;

public class Main extends Application {

    // Both of these png files can be downloaded using the http:// syntax.
    // Remove the comments to use them instead of the local file system paths.
    public static final String APPLICATION_ICON =
            "/res/people.png";
//            "http://cdn1.iconfinder.com/data/icons/Copenhagen/PNG/32/people.png";
    public static final String SPLASH_IMAGE =
            "/res/logo.png";

//            "http://fxexperience.com/wp-content/uploads/2010/06/logo.png";

    private Pane splashLayout;
    private ProgressBar loadProgress;
    private Label progressText;
    private Stage mainStage;
    private static final int SPLASH_WIDTH = 676;
    private static final int SPLASH_HEIGHT = 227;


    // The sample has main declared as throwing an exception, but the intellij
    // starter code does not have "throws exception".  The sample does not
    // have a try/catch block
    public static void main(String[] args) throws Exception {

        launch(args);
    }


    // init is called first
    @Override
    public void init() {
        ImageView splash = new ImageView(new Image(
//orig                SPLASH_IMAGE
// alternate method
                Main.class.getResource("/res/logo.png").toExternalForm()
        ));


        // Progress Bar
        loadProgress = new ProgressBar();
        loadProgress.setPrefWidth(SPLASH_WIDTH - 20);
        // Progress Text
        progressText = new Label("Will find friends for peanuts . . .");
        // Container and skin
        splashLayout = new VBox();
        splashLayout.getChildren().addAll(splash, loadProgress, progressText);
        progressText.setAlignment(Pos.CENTER);
        splashLayout.setStyle(
                "-fx-padding: 5; " +
                        "-fx-background-color: cornsilk; " +
                        "-fx-border-width:5; " +
                        "-fx-border-color: " +
                        "linear-gradient(" +
                        "to bottom, " +
                        "chocolate, " +
                        "derive(chocolate, 50%)" +
                        ");"
        );
        splashLayout.setEffect(new DropShadow());
    }


    /**
     * The Android Application Thread is the UI thread.
     *
     * start is called later.
    */
    @Override
    public void start(final Stage initStage) throws Exception{



        //The Worker's progress can be monitored via three different properties, totalWork, workDone, and progress.
        // These properties are set by the actual implementation of the Worker interface, but can be observed by anybody.
        // The workDone is a number between -1 (meaning indeterminate progress) and totalWork, inclusive.
        // When workDone == totalWork the progress will be 100% (or 1). totalWork will be a number between
        // -1 and Long.MAX_VALUE, inclusive. The progress will be either -1 (meaning indeterminate),
        // or a value between 0 and 1, inclusive, representing 0% through 100%.

        // Create a Task named friendTask which returns an ObservableList of Strings using the
        // generics capability of parameterized types.  In C++ its called a template.
        // This friendTask is an anonymous task.  Why is it marked final?  Supposedly nested classes (local and anonymous)
        // can only access variables in the enclosing class which are final.

        /** Step 0 Create the Task to do work in call() method */
        final Task<ObservableList<String>> friendTask = new Task<ObservableList<String>>() {

            @Override
            protected ObservableList<String> call() throws InterruptedException {

                // Task Result List
                ObservableList<String> foundFriends = FXCollections.<String>observableArrayList();

                // Task Src List
                ObservableList<String> availableFriends = FXCollections.observableArrayList("Fili", "Kili", "Oin", "Gloin", "Thorin","Dwalin", "Balin", "Bifur", "Bofur","Bombur", "Dori", "Nori", "Ori");

                // Updates Status Text
                updateMessage("Finding friends . . .");

                /** I added this per the tutorial
                 */
                if (isCancelled()) {
                    updateMessage("Cancelled");
                    return foundFriends;
                }


                for (int i = 0; i < availableFriends.size(); i++) {

                    /** Per the tutorial I wrapped this thread as well.
                     */
                    try {
                        Thread.sleep(400);
                    } catch (InterruptedException interrupted) {
                        if (isCancelled()) {
                            updateMessage("Cancelled");
                            return foundFriends;
                        }
                    }

                    // Update the progress bar
                    updateProgress(i + 1, availableFriends.size());
                    // Update the result list
                    String nextFriend = availableFriends.get(i);
                    foundFriends.add(nextFriend);
                    // Update the status text
                    updateMessage("Finding friends . . . found " + nextFriend);
                }


                /** If a task thread sleeps or otherwise blocks, it needs double check
                 * to see if its cancelled.  I wrapped the sleep() call per the
                 * tutorial.
                 */
                try {
                    Thread.sleep(400);
                } catch (InterruptedException interrupted) {
                    if (isCancelled()) {
                        updateMessage("Cancelled");
                        return foundFriends;
                    }
                }
                updateMessage("All friends found.");


                // When this happens, the thread transitions from RUNNING to SUCCEEDED
                return foundFriends;
            }
        };


        // This sets up the two windows and the task
        // Lambda expression 1
        //  This is the second call in the call stack, but oddly its
        // where we setup the connection between the splashscreen & task with what is
        // called when the thread is complete.
        showSplash(initStage, friendTask, () -> showMainStage(friendTask.valueProperty()) );

        new Thread(friendTask).start();
    }


    // This displays the stage for the friends list.
    // This is the last call in the JavaFX application thread where it
    // shows the friends list.
    private void showMainStage(ReadOnlyObjectProperty<ObservableList<String>> friends) {

        mainStage = new Stage(StageStyle.DECORATED);
        mainStage.setTitle("My Friends");
        mainStage.getIcons().add(new Image(APPLICATION_ICON));

        final ListView<String> peopleView = new ListView<>();
        peopleView.itemsProperty().bind(friends);

        mainStage.setScene(new Scene(peopleView));
        mainStage.show();
    }


    // show the splash screen and update the progress bar with the task.  After the task returns
    // call the completion handler to display the second stage screen.
    // This function implements the Interface InitCompletionHandler with one api, complete().
    private void showSplash(final Stage initStage, Task<?> task, InitCompletionHandler initCompletionHandler) {

        /** Step 1 - bind gui components to task threads */

        // Status text below status bar
        progressText.textProperty().bind(task.messageProperty());

        // The status bar. It will cylon without this.
        loadProgress.progressProperty().bind(task.progressProperty());

        /** Step 2 - add the listener for the task states */
        // Lambda expression 2
        // The interface is ObservableValue<T> with api's addListener, removeListener and getValue.
        // Why three? Is it saying that three possible events (addListener, removeListener, getValue) call
        // the lambda function?
        // The debugger shows the following calls and values for each of these parameters.
        //  call    observableValue      oldState        newState
        //  1.      bean                ready           scheduled
        //  2.      bean                scheduled       running
        //  3.      bean                running         succeeded
        task.stateProperty().addListener((observableValue, oldState, newState) -> {

            // check task state and close the splash screen when done
            // then call the completion handler
            /** Step 4 - Handle the State of the task when completed */
            if (newState == Worker.State.SUCCEEDED) {

                // loadProgress is the progress bar.
                // Decouple from the task and mark manually 100% complete.
                loadProgress.progressProperty().unbind();
                loadProgress.setProgress(1);

                // Move the first stage to front and fade?
                // This code does nothing that I can tell.
                //initStage.toFront();
                FadeTransition fadeSplash = new FadeTransition(Duration.seconds(1.2), splashLayout);
                fadeSplash.setFromValue(1.0);
                fadeSplash.setToValue(0.0);
                // does hide() "close" the window?
                // Lambda Expression 3
                fadeSplash.setOnFinished(actionEvent -> initStage.hide());
                fadeSplash.play();

                // Call the interface api this routine implements.
                // This is the first call on the way to showMainStage().
                initCompletionHandler.complete();

                System.out.println("x");
            } // todo add code to gracefully handle other task states.
        });

        // Create the scene
        Scene splashScene = new Scene(splashLayout);

        // Removes the min, max, window close buttons  and title bar.
        initStage.initStyle(StageStyle.UNDECORATED);

        // Does not seem to change anything
        final Rectangle2D bounds = Screen.getPrimary().getBounds();

        // Add the scene to the stage.
        initStage.setScene(splashScene);

        // Does not seem to change anything
        initStage.setX(bounds.getMinX() + bounds.getWidth() / 2 - SPLASH_WIDTH / 2);
        initStage.setY(bounds.getMinY() + bounds.getHeight() / 2 - SPLASH_HEIGHT / 2);

        initStage.show();
    }

    /**
     *  A functional interface is an interface that contains only one abstract method.
     *     Interface shared between initstage and the task

     */
    public interface InitCompletionHandler {
        public void complete();
    }

}

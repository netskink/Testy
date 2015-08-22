import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.stage.Stage;


/**
 * Notes on JavaFX concurrency
 *
 * The UI thread is also known as the JavaFX Application Thread.
 *
 * The javafx.concurrent package consists of the Worker Interface
 * and the two concrete implementations, Task and Service Classes.
 *
 * The Worker Interface provides APIs that are useful for a background
 * worker to communicate with the UI  The Task class enables developers to
 * implement async tasks.  The Service class executes Tasks.
 *
 * The WorkerStateEvent class specifies an event that occurs whenever the
 * Worker implementation changes state.
 *
 * Both the Task and Service classes implement the EventType interface that
 * supports listening to state events.
 *
 * The Worker Interface
 *
 * The worker interface defines an object that performs some work on one or more
 * background threads.  The state is observable and usable from the JavaFX
 * application thread.
 *
 * Lifecycle of the Worker Object
 * When created is in READY state.
 * When scheduled for work is in SCHEDULED state.
 * When running it is RUNNING state.
 * When completed successfully it is in SUCCEEDED state and value is set to result of Worker object.
 * When completed unsuccessfully (throws an exception) it is FAILED state and exception property is set
 * to the type of exception that happened.
 * If the cancel method is invoked, it is in the CANCELLED state.
 *
 *
 * Progress of work done can be monitored:
 * 1. totalWork
 * 2. workDone
 * 3. progress
 *
 *
 * The Task Class
 *
 * Tasks are used to implement the logic of work that needs to be done on a background thread.
 * You need to extend the Task class by overriding the call() method.
 *
 * A task can be started in one of two ways.
 * 1. Starting a thread with given task as a parameter.
 *   Thread th = new Thread(task);
 *   th.setDaemon(true);
 *   th.start()
 * 2. Using the ExecutorService API
 *   ExecutorService.submit(task);
 *
 * The Task class defines a one-time object that cannot be reused.  If you need a reusable Worker object
 * use the Service class.
 *
 * During the body of the call() method, it must check for cancellation.
 *
 */

public class FirstLineServiceApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FirstLineService service = new FirstLineService();
        service.setUrl("http://google.com");
        service.setOnSucceeded(new EventHandler<WorkerStateEvent>() {

            @Override
            public void handle(WorkerStateEvent t) {
                System.out.println("done:" + t.getSource().getValue());
            }
        });
        service.start();
    }

    public static void main(String[] args) {
        launch();
    }

    private static class FirstLineService extends Service<String> {
        private StringProperty url = new SimpleStringProperty();

        public final void setUrl(String value) {
            url.set(value);
        }

        public final String getUrl() {
            return url.get();
        }

        public final StringProperty urlProperty() {
            return url;
        }


        @Override
        protected Task<String> createTask() {
            return new Task<String>() {
                @Override
                protected String call()
                        throws IOException, MalformedURLException {
                    try ( BufferedReader in = new BufferedReader(
                            new InputStreamReader(
                                    new URL(getUrl()).openStream;
                          in = new BufferedReader(
                                  new InputStreamReader(u.openStream()))) {
                        return in.readLine();
                    }
                }
            };
        }
    }
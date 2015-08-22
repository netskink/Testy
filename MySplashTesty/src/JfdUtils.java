import javafx.application.Platform;

import java.util.Date;

/**
 * Created by davis on 8/20/15.
 */
public class JfdUtils {


    public void blockForSeconds(int seconds) {
        long startTime = System.currentTimeMillis();
        long elapsedTime = 0L;
        final long nSeconds = seconds*1000;

        while (elapsedTime < nSeconds) {
            //perform db poll/check
            elapsedTime = (new Date()).getTime() - startTime;
        }

    }

    public void exit() {
        Platform.exit();
        System.exit(0);

    }
}

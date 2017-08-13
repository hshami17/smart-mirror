package module.clock;

import java.net.URL;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Locale;
import java.util.ResourceBundle;
import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.RotateTransition;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import models.ModelManager;
import module.ModuleControl;

/**
 * FXML Controller class
 *
 * @author hasan
 */
public class ClockController implements Initializable, ModuleControl {
    
    @FXML
    private StackPane analogClock;
    @FXML
    private Label digitalTime;
    @FXML
    private Label date;

    private RotateTransition secondsTransition;
    private RotateTransition minuteTransition;
    private RotateTransition hourTransition;

    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
//        buildAnalogClock();
        
        // Digital time and date 
        Timeline dateTime = new Timeline(
            new KeyFrame(Duration.seconds(0), (ActionEvent actionEvent) -> {
                Calendar time = Calendar.getInstance();
                // Build date string
                String day = time.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.ENGLISH);
                String month = time.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.ENGLISH);
                String dayNum = String.valueOf(time.get(Calendar.DAY_OF_MONTH));
                String year = String.valueOf(time.get(Calendar.YEAR));
                if (dayNum.endsWith("1") && !dayNum.equals("11")){
                    dayNum = dayNum + "st";
                }
                else if (dayNum.endsWith("2") && !dayNum.equals("12")){
                    dayNum = dayNum + "nd";
                }
                else if (dayNum.endsWith("3") && !dayNum.equals("13")){
                    dayNum = dayNum + "rd";
                }
                else{
                    dayNum = dayNum + "th";
                }
                date.setText(day + ", " + month + " " + dayNum + " " + year);
                
                // Build time string
                String hour   = time.get(Calendar.HOUR) == 0 ? "12" : time.get(Calendar.HOUR) + "";
                String minute = (time.get(Calendar.MINUTE) < 10 ? "0" + 
                        time.get(Calendar.MINUTE) : time.get(Calendar.MINUTE))  + " ";
                String amPm   = time.get(Calendar.AM_PM) == Calendar.AM ? "AM" : "PM";
                digitalTime.setText(hour + ":" + minute + amPm);
            }),
            new KeyFrame(Duration.seconds(1))
        );
        dateTime.setCycleCount(Animation.INDEFINITE);
        dateTime.play();
    }    
    
    private void buildAnalogClock(){
        
        ImageView clockDial = new ImageView(new Image("/images/clock/clockDial.png"));
        clockDial.setFitWidth(300);
        clockDial.setFitHeight(300);
        
        ImageView hourHand = new ImageView(new Image("/images/clock/clockHourHand.png"));
        hourHand.setFitWidth(300);
        hourHand.setFitHeight(300);
        
        ImageView minuteHand = new ImageView(new Image("/images/clock/clockMinuteHand.png"));
        minuteHand.setFitWidth(300);
        minuteHand.setFitHeight(300);
        
        ImageView secondsHand = new ImageView(new Image("/images/clock/clockSecondsHand.png"));
        secondsHand.setFitWidth(300);
        secondsHand.setFitHeight(300);
        
        ImageView centerPoint = new ImageView(new Image("/images/clock/clockCenterPoint.png"));
        centerPoint.setFitHeight(300);
        centerPoint.setFitWidth(300);

        secondsTransition = createRotateTransition(Duration.seconds(60),
                secondsHand, getSecondAngle(LocalTime.now()));
        secondsTransition.play();

        minuteTransition = createRotateTransition(Duration.minutes(60), minuteHand,
                getMinuteAngle(LocalTime.now()));
        minuteTransition.play();

        hourTransition = createRotateTransition(Duration.hours(12), hourHand,
                getHourAngle(LocalTime.now()));
        hourTransition.play();

        analogClock.getChildren().addAll(
                clockDial, 
                hourHand, 
                minuteHand, 
                secondsHand, 
                centerPoint
        );
    }
    
    /*
    * @author TAKAHASHI,Toru
    */
    private RotateTransition createRotateTransition(Duration duration, Node node, double startAngle) {
        RotateTransition rt = new RotateTransition(duration, node);
        rt.setFromAngle(startAngle);
        rt.setByAngle(360);
        rt.setCycleCount(Animation.INDEFINITE);
        rt.setInterpolator(Interpolator.LINEAR);
        return rt;
    }
    
    /*
    * @author TAKAHASHI,Toru
    */
    private double getHourAngle(LocalTime time) {
        return (time.getHour() % 12 + time.getMinute() / 60d + time.getSecond() / (60d * 60d)) * 360 / 12;
    }

    /*
    * @author TAKAHASHI,Toru
    */
    private double getMinuteAngle(LocalTime time) {
        return (time.getMinute() + time.getSecond() / 60d) * 360 / 60;
    }
    
    /*
    * @author TAKAHASHI,Toru
    */
    private double getSecondAngle(LocalTime time) {
        return time.getSecond() * 360 / 60;
    }

    @Override
    public void setModel(ModelManager modelManager) {}

    @Override
    public void startModule() {
        buildAnalogClock();
    }

    @Override
    public void stopModule() {
        analogClock.getChildren().clear();
        secondsTransition.stop();
        minuteTransition.stop();
        hourTransition.stop();
    }
}

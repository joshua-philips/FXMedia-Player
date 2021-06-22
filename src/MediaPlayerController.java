import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.FileChooser;
import javafx.util.Duration;

public class MediaPlayerController implements Initializable {

    private String path;
    private MediaPlayer mediaPlayer;

    @FXML
    private MediaView mediaView;
    @FXML
    private Slider progressBar;
    @FXML
    private Slider volumeSlider;

    @FXML
    void chooseFileMethod(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(null);
        path = file.toURI().toString();

        if (path != null) {
            Media media = new Media(path);
            mediaPlayer = new MediaPlayer(media);

            mediaView.setMediaPlayer(mediaPlayer);
            DoubleProperty widthProp = mediaView.fitWidthProperty();
            DoubleProperty heightProp = mediaView.fitHeightProperty();

            widthProp.bind(Bindings.selectDouble(mediaView.sceneProperty(), "width"));
            heightProp.bind(Bindings.selectDouble(mediaView.sceneProperty(), "height"));

            attachProgressSlider(mediaPlayer, media, progressBar);
            attachVolumeSlider(mediaPlayer, volumeSlider);

            mediaPlayer.play();
        }
    }

    public static void attachVolumeSlider(MediaPlayer mediaPlayer, Slider volumeSlider) {
        volumeSlider.setValue(mediaPlayer.getVolume() * 100);
        volumeSlider.valueProperty().addListener(new InvalidationListener() {

            @Override
            public void invalidated(Observable observable) {
                mediaPlayer.setVolume(volumeSlider.getValue() / 100);
            }
        });
    }

    public static void attachProgressSlider(MediaPlayer mediaPlayer, Media media, Slider progressBar) {

        mediaPlayer.currentTimeProperty().addListener(new ChangeListener<Duration>() {
            @Override
            public void changed(ObservableValue<? extends Duration> observableValue, Duration oldValue,
                    Duration newValue) {
                progressBar.setValue(newValue.toSeconds());
            }
        });

        // Progress Bar slider to actual end of media
        mediaPlayer.setOnReady(new Runnable() {
            @Override
            public void run() {
                Duration totalDuration = media.getDuration();
                progressBar.setMax(totalDuration.toSeconds());
            }
        });

        progressBar.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                mediaPlayer.seek(Duration.seconds(progressBar.getValue()));
            }
        });

        progressBar.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                mediaPlayer.seek(Duration.seconds(progressBar.getValue()));
            }
        });
    }

    @FXML
    void play(ActionEvent event) {
        mediaPlayer.play();
        mediaPlayer.setRate(1);
    }

    @FXML
    void pause(ActionEvent event) {
        mediaPlayer.pause();
    }

    @FXML
    void stop(ActionEvent event) {
        mediaPlayer.stop();
    }

    @FXML
    void slowRate(ActionEvent event) {
        mediaPlayer.setRate(0.5);
    }

    @FXML
    void fastForward(ActionEvent event) {
        mediaPlayer.setRate(2);
    }

    @FXML
    void backTen(ActionEvent event) {
        mediaPlayer.seek(mediaPlayer.getCurrentTime().subtract(Duration.seconds(10)));
    }

    @FXML
    void skipTen(ActionEvent event) {
        mediaPlayer.seek(mediaPlayer.getCurrentTime().add(Duration.seconds(10)));
    }

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
    }

}

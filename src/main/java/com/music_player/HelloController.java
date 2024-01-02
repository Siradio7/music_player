package com.music_player;

import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;

import java.io.File;
import java.net.URL;
import java.util.*;

public class HelloController implements Initializable {
    public Label label_music_title;
    public Button button_back, button_pause, button_next;
    public ProgressBar music_progress;
    public ImageView image_view_song;
    public Slider volume_slider;
    public Label current_time;
    public Label end_time;
    private Media media;
    private MediaPlayer mediaPlayer;
    private ArrayList<File> songs;
    private int songNumber = (int) (Math.random() * 10);
    private boolean paused;
    RotateTransition rotateTransition;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        songs = new ArrayList<>();
        File directory = new File("music");
        File[] files = directory.listFiles();

        if (files == null)
            return;

        songs.addAll(Arrays.asList(files));

        media = new Media(songs.get(songNumber).toURI().toString());
        mediaPlayer = create_media_player(media);
        mediaPlayer.setAutoPlay(true);
        begin_timer();

        // Ecouteur d'évènement pour la modification du volume
        volume_slider.valueProperty().addListener((observableValue, number, t1) -> mediaPlayer.setVolume(volume_slider.getValue() * 0.01));
    }

    private MediaPlayer create_media_player(Media media) {
        MediaPlayer mediaPlayer = new MediaPlayer(media);

        mediaPlayer.setVolume(volume_slider.getValue() * 0.01);
        label_music_title.setText(songs.get(songNumber).getName());

        // Pour exécuter automatiquement le prochain fichier à la fin de la lecture
        mediaPlayer.setOnEndOfMedia(this::next_music);

        return mediaPlayer;
    }

    public void previous_music() {
        mediaPlayer.stop();
        songNumber = songNumber > 0 ? songNumber-1 : songs.size()-1;
        media = new Media(songs.get(songNumber).toURI().toString());
        mediaPlayer = create_media_player(media);
        mediaPlayer.setAutoPlay(true);
        begin_timer();
    }

    public void pause_music() {
        if (paused) {
            paused = false;
            mediaPlayer.play();
            rotateTransition.play();
        } else {
            paused = true;
            mediaPlayer.pause();
            rotateTransition.pause();
        }
    }

    public void next_music() {
        mediaPlayer.stop();
        songNumber = songNumber < songs.size()-1 ? songNumber+1 : 0;
        media = new Media(songs.get(songNumber).toURI().toString());
        mediaPlayer = create_media_player(media);
        mediaPlayer.setAutoPlay(true);
        begin_timer();
    }

    public void begin_timer() {
        rotateTransition = new RotateTransition();
        rotateTransition.setNode(image_view_song);
        rotateTransition.setFromAngle(0);
        rotateTransition.setDuration(Duration.millis(3000));
        rotateTransition.setCycleCount(TranslateTransition.INDEFINITE);
        rotateTransition.setInterpolator(Interpolator.LINEAR);
        rotateTransition.setAxis(Rotate.Z_AXIS);
        rotateTransition.setByAngle(360);
        rotateTransition.play();

        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                double current = mediaPlayer.getCurrentTime().toSeconds();
                double end = mediaPlayer.getTotalDuration().toSeconds();
                music_progress.setProgress(current / end);

                if (current / end == 1)
                    cancel();
            }
        };

        timer.scheduleAtFixedRate(task, 1000, 1000);
    }

    public void volume_mouse_clicked() {
        volume_slider.setVisible(true);
        volume_slider.setOnMouseExited(mouseEvent -> volume_slider.setVisible(false));
    }
}
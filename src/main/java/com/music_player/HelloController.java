package com.music_player;

import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;

import java.io.File;
import java.net.URL;
import java.util.*;

public class HelloController implements Initializable {
    public Label label_music_title;
    public Button button_back, button_pause, button_next, button_playlist, button_volume, button_fermer_playlist;
    public ProgressBar music_progress;
    public ImageView image_view_song;
    public Slider volume_slider;
    public Label current_time;
    public Label end_time;
    public ListView<File> playlist;
    public AnchorPane anchor_pane_playlist;
    private Media media;
    private MediaPlayer mediaPlayer;
    private ArrayList<File> songs;
    private int songNumber = (int) (Math.random() * 10);
    private boolean paused;
    RotateTransition rotateTransition;
    private ImageView icon_pause;
    private ImageView icon_play;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        init_icon();
        songs = new ArrayList<>();
        File directory = new File("music");
        File[] files = directory.listFiles();

        if (files == null)
            return;

        songs.addAll(Arrays.asList(files));
        playlist.getItems().addAll(Arrays.asList(files));
        playlist.getSelectionModel().selectedIndexProperty().addListener(((observableValue, number, t1) -> {
            songNumber = playlist.getSelectionModel().getSelectedIndex();
            next_music(songNumber);
        }));

        media = new Media(songs.get(songNumber).toURI().toString());
        mediaPlayer = create_media_player(media);
        mediaPlayer.setAutoPlay(true);
        begin_timer();

        // Ecouteur d'évènement pour la modification du volume
        volume_slider.valueProperty().addListener((observableValue, number, t1) -> mediaPlayer.setVolume(volume_slider.getValue() * 0.01));
    }

    private void init_icon() {
        Image icon_image_next = new Image(String.valueOf(HelloController.class.getResource("prochain.png")));
        Image icon_image_back = new Image(String.valueOf(HelloController.class.getResource("rewind.png")));
        Image icon_image_pause = new Image(String.valueOf(HelloController.class.getResource("pause.png")));
        Image icon_image_play = new Image(String.valueOf(HelloController.class.getResource("jouer.png")));
        Image icon_image_playlist = new Image(String.valueOf(HelloController.class.getResource("playlist.png")));
        Image icon_image_volume = new Image(String.valueOf(HelloController.class.getResource("volume.png")));
        Image icon_image_fermer_playlist = new Image(String.valueOf(HelloController.class.getResource("bouton-retour.png")));
        ImageView icon_next = new ImageView(icon_image_next);
        ImageView icon_back = new ImageView(icon_image_back);
        icon_pause = new ImageView(icon_image_pause);
        icon_play = new ImageView(icon_image_play);
        ImageView icon_playlist = new ImageView(icon_image_playlist);
        ImageView icon_volume = new ImageView(icon_image_volume);
        ImageView icon_fermer_playlist = new ImageView(icon_image_fermer_playlist);
        button_next.setGraphic(icon_next);
        button_back.setGraphic(icon_back);
        button_pause.setGraphic(icon_pause);
        button_playlist.setGraphic(icon_playlist);
        button_volume.setGraphic(icon_volume);
        button_fermer_playlist.setGraphic(icon_fermer_playlist);
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
            button_pause.setGraphic(icon_pause);
        } else {
            paused = true;
            mediaPlayer.pause();
            rotateTransition.pause();
            button_pause.setGraphic(icon_play);
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

    public void next_music(int songNumber) {
        mediaPlayer.stop();
        media = new Media(songs.get(songNumber).toURI().toString());
        mediaPlayer = create_media_player(media);
        mediaPlayer.setAutoPlay(true);
        begin_timer();
    }

    public void begin_timer() {
        rotateTransition = new RotateTransition();
        rotateTransition.setNode(image_view_song);
        rotateTransition.setFromAngle(0);
        rotateTransition.setDuration(Duration.millis(4500));
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

    public void show_playlist() {
        anchor_pane_playlist.setVisible(true);
    }

    public void hide_playlist() {
        anchor_pane_playlist.setVisible(false);
    }
}
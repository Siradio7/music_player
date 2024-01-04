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
    public Label label_music_title, current_time, end_time;
    public Button button_back, button_pause, button_next, button_playlist, button_volume, button_fermer_playlist;
    public ProgressBar music_progress;
    public ImageView image_view_song;
    public Slider volume_slider;
    public ListView<File> playlist;
    public AnchorPane anchor_pane_playlist;
    private Media media;
    private MediaPlayer mediaPlayer;
    private ArrayList<File> songs;
    private File[] files;
    private int songNumber = (int) (Math.random() * 10);
    private boolean paused;
    RotateTransition rotateTransition;
    TranslateTransition translateTransition;
    private ImageView icon_pause, icon_play;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Chargement des icônes
        init_icon();

        // Chargement des musiques
        load_songs_files();

        // Chargement des musiques sur la liste view
        playlist.getItems().addAll(Arrays.asList(files));

        // Initialisation des écouteurs d'événements
        init_listener();

        // Initialisation aléatoire d'une musique au lancement
        start_music(songNumber);
    }

    private void load_songs_files() {
        songs = new ArrayList<>();

        File directory = new File("music");
        files = directory.listFiles();

        if (files == null)
            return;

        songs.addAll(Arrays.asList(files));
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

    private void init_listener() {
        // Écouteur pour faire disparaitre le volume slider une fois que la souris n'est plus sur le composant
        volume_slider.setOnMouseExited(mouseEvent -> {
            translateTransition.setNode(volume_slider);
            translateTransition.setByX(40);
            translateTransition.setToX(40);
            translateTransition.setFromX(0);
            translateTransition.setDuration(Duration.millis(300));
            translateTransition.play();
            translateTransition.setOnFinished(actionEvent -> volume_slider.setVisible(false));
        });

        // Écouteur pour que chaque musique de la liste view sélectionné se lance automatiquement
        playlist.getSelectionModel().selectedIndexProperty().addListener(((observableValue, number, t1) -> {
            songNumber = playlist.getSelectionModel().getSelectedIndex();
            next_music(songNumber);
        }));

        // Écouteur d'évènement pour la modification du volume
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
        start_music(songNumber);
    }

    public void next_music(int songNumber) {
        mediaPlayer.stop();
        start_music(songNumber);
    }

    public void start_music(int songNumber) {
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
        translateTransition = new TranslateTransition();
        translateTransition.setNode(volume_slider);
        translateTransition.setByX(-40);
        translateTransition.setFromX(40);
        translateTransition.setDuration(Duration.millis(300));
        translateTransition.play();
    }

    public void show_playlist() {
        anchor_pane_playlist.setVisible(true);
        translateTransition = new TranslateTransition();
        translateTransition.setNode(anchor_pane_playlist);
        translateTransition.setByX(250);
        translateTransition.setFromX(-250);
        translateTransition.setDuration(Duration.millis(200));
        translateTransition.play();
    }

    public void hide_playlist() {
        translateTransition = new TranslateTransition();
        translateTransition.setNode(anchor_pane_playlist);
        translateTransition.setByX(250);
        translateTransition.setToX(-250);
        translateTransition.setDuration(Duration.millis(200));
        translateTransition.play();
        translateTransition.setOnFinished(actionEvent -> anchor_pane_playlist.setVisible(false));
    }
}
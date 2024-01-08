module com.music_player {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;
    requires org.kordamp.ikonli.javafx;
    requires org.jetbrains.annotations;

    opens com.music_player to javafx.fxml;
    exports com.music_player;
}
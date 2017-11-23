package player;

import java.awt.Color;
import java.io.File;
import java.text.DecimalFormat;
import java.util.List;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

import player.Botones;
import player.MenuPersonalizado;
import player.Interprete;
import player.Lista;

public class Main extends Application
{
    public List<File> lista, ventana;
    public int actualizarArchivoNo;
    public File archivoB;
    String extension;

    Media currentMedia;
    MediaPlayer mediaPlayer;

    Botones botones;
    MenuPersonalizado menuBar;
    boolean valorCambio = false;

    Lista cancionActual;
    Stage ActualizadorEstado;
    
    @Override
    public void start(final Stage primaryStage)
    {
        BorderPane raiz = new BorderPane();
        Scene scene = new Scene(raiz, 400, 400);
        scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setTitle("Consola");
        primaryStage.getIcons().add(new Image("file:WMP.png"));
        primaryStage.show();
        ActualizadorEstado=primaryStage;
        botones = new Botones();
        raiz.setBottom(botones);

        menuBar = new MenuPersonalizado(primaryStage);
        raiz.setTop(menuBar);

        menuClicks(menuBar, primaryStage, raiz);
        buttonClicks(botones, raiz);

        primaryStageProperties(primaryStage);
        
        cancionActual = new Lista();
    }

    private void menuClicks(final MenuPersonalizado menuBar, final Stage primaryStage, final BorderPane root)
    {
        menuBar.open.setOnAction(new EventHandler<ActionEvent>()
        {

            @Override
            public void handle(ActionEvent event)
            {
                ventana = menuBar.openMenuClicked(primaryStage,lista);
                
                if (ventana != null)
                {
                    lista=ventana;
                    actualizarArchivoNo = 0;
                    if (botones.play.getText().equals("Pause"))
                        mediaPlayer.stop();
                    if (cancionActual.isShowing())
                    {
                        cancionActual.showPlaylist(lista, primaryStage);
                        onPlaylistClick(cancionActual, root);
                    }
                    createMediaPlayer(root);
                    menuBar.viewPlaylist.setDisable(false);
                    mediaPlayer.play();
                }
            }
        });

        menuBar.exit.setOnAction(new EventHandler<ActionEvent>()
        {

            @Override
            public void handle(ActionEvent arg0)
            {
                System.exit(0);
            }
        });

        menuBar.viewPlaylist.setOnAction(new EventHandler<ActionEvent>()
        {

            @Override
            public void handle(ActionEvent event)
            {
                if (menuBar.viewPlaylist.isSelected())
                {
                    cancionActual.showPlaylist(lista, primaryStage);
                    cancionActual.show();
                }
                else
                {
                    cancionActual.close();
                }
                onPlaylistClick(cancionActual, root);
                onPlaylistClosed(cancionActual);

            }
        });
        
        
    }

    private void onPlaylistClick(final Lista nowPlaying, final BorderPane root)
    {
        nowPlaying.playlistPane.setOnMouseClicked(new EventHandler<MouseEvent>()
        {

            @Override
            public void handle(MouseEvent event)
            {
                if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2)
                {
                    actualizarArchivoNo = nowPlaying.playlistPane.getSelectionModel().getSelectedIndex();
                    mediaPlayer.stop();
                    createMediaPlayer(root);
                    mediaPlayer.play();

                }

            }
        });
    }

    private void onPlaylistClosed(Lista nowPlaying)
    {
        nowPlaying.setOnCloseRequest(new EventHandler<WindowEvent>()
        {

            @Override
            public void handle(WindowEvent event)
            {
                if (menuBar.viewPlaylist.isSelected())
                    menuBar.viewPlaylist.setSelected(false);
            }
        });

    }

    private void buttonClicks(final Botones buttonsPane, final BorderPane root)
    {
        final Button playButton = buttonsPane.play;
        final Button nextButton = buttonsPane.next;
        final Button stopButton = buttonsPane.stop;
        final Button previousButton = buttonsPane.previous;

        playButton.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent event)
            {
                if (playButton.getText().equals("Pause"))
                {
                    mediaPlayer.pause();
                    playButton.setText("Play");
                }
                else
                {
                    mediaPlayer.play();
                    playButton.setText("Pause");
                }
            }
        });

        stopButton.setOnAction(new EventHandler<ActionEvent>()
        {

            @Override
            public void handle(ActionEvent arg0)
            {
                if (playButton.getText().equals("Pause"))
                {
                    mediaPlayer.stop();
                    playButton.setText("Play");
                }
            }
        });

        nextButton.setOnAction(new EventHandler<ActionEvent>()
        {

            @Override
            public void handle(ActionEvent arg0)
            {
                mediaPlayer.stop();
                actualizarArchivoNo++;
                createMediaPlayer(root);
                mediaPlayer.play();
            }
        });

        previousButton.setOnAction(new EventHandler<ActionEvent>()
        {

            @Override
            public void handle(ActionEvent arg0)
            {
                mediaPlayer.stop();
                actualizarArchivoNo--;
                createMediaPlayer(root);
                mediaPlayer.play();
            }
        });

    }

    private void createMediaPlayer(final BorderPane root)
    {
        archivoB = lista.get(actualizarArchivoNo);
        extension = getExtension(archivoB.getAbsoluteFile().toString());
        if(extension.equals(".mp4")||extension.equals(".flv"))
        {
            menuBar.viewFullScreen.setDisable(false);
        }
        else
        {
            menuBar.viewFullScreen.setDisable(true);
        }
        currentMedia = new Media(archivoB.toURI().toString());
        mediaPlayer = new MediaPlayer(currentMedia);
        Interprete player = new Interprete(extension, mediaPlayer,ActualizadorEstado);
        setUpMediaPlayer(player, root);
        root.setCenter(player);
    }

    private void setUpMediaPlayer(final Interprete player, final BorderPane root)
    {
        mediaPlayer.setOnReady(new Runnable()
        {
            String titleValue, artistValue, albumValue;
            Image albumArt;
            Duration duration;

            @Override
            public void run()
            {
                albumArt = (Image) mediaPlayer.getMedia().getMetadata().get("image");
                titleValue = (String) mediaPlayer.getMedia().getMetadata().get("title");
                artistValue = (String) mediaPlayer.getMedia().getMetadata().get("artist");
                albumValue = (String) mediaPlayer.getMedia().getMetadata().get("album");
                duration = mediaPlayer.getMedia().getDuration();
                player.setMetadata(albumArt, titleValue, artistValue, albumValue);
                seekBarValue(duration);
            }
        });

        mediaPlayer.setOnPlaying(new Runnable()
        {
            @Override
            public void run()
            {
                ActualizadorEstado.setTitle(archivoB.getName());
                botones.play.setText("Pause");
                botones.next.setDisable(false);
                botones.previous.setDisable(false);
                if (actualizarArchivoNo == 0)
                    botones.previous.setDisable(true);
                if (actualizarArchivoNo == lista.size() - 1)
                    botones.next.setDisable(true);
            }
        });

        mediaPlayer.setOnStopped(new Runnable()
        {
            String timeText;
            Duration duration;

            @Override
            public void run()
            {
                // TODO Auto-generated method stub
                botones.seekBar.setValue(0);
                duration = mediaPlayer.getMedia().getDuration();
                timeText = new DecimalFormat("00").format((int) duration.toMinutes()) + ":"
                        + new DecimalFormat("00").format((int) ((duration.toMinutes() - (int) duration.toMinutes()) * 60));
                botones.time.setText("00:00/" + timeText);
            }
        });

        mediaPlayer.setOnEndOfMedia(new Runnable()
        {
            String timeText;
            Duration duration;

            @Override
            public void run()
            {
                // TODO Auto-generated method stub
                botones.seekBar.setValue(0);
                duration = mediaPlayer.getMedia().getDuration();
                timeText = new DecimalFormat("00").format((int) duration.toMinutes()) + ":"
                        + new DecimalFormat("00").format((int) ((duration.toMinutes() - (int) duration.toMinutes()) * 60));
                botones.time.setText("00:00/" + timeText);
                botones.play.setText("Play");
                if (actualizarArchivoNo < lista.size() - 1)
                {
                    actualizarArchivoNo++;
                    createMediaPlayer(root);
                    mediaPlayer.play();
                }
            }
        });
    }

    private void primaryStageProperties(final Stage primaryStage)
    {
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>()
        {

            @Override
            public void handle(WindowEvent arg0)
            {
                if (cancionActual.isShowing())
                    cancionActual.close();
            }
        });

        primaryStage.iconifiedProperty().addListener(new InvalidationListener()
        {

            @Override
            public void invalidated(Observable observable)
            {
                if (primaryStage.isIconified())
                {
                    if (cancionActual.isShowing())
                    {
                        cancionActual.setIconified(true);
                    }
                }
                else
                {
                    if (cancionActual.isIconified())
                        cancionActual.setIconified(false);
                }
            }
        });

    }

    private void seekBarValue(final Duration duration)
    {
        updateSeekBar(duration);
        botones.seekBar.valueProperty().addListener(new InvalidationListener()
        {
            @Override
            public void invalidated(Observable ov)
            {
                if (botones.seekBar.isValueChanging())
                {
                    mediaPlayer.setMute(true);
                    mediaPlayer.seek(duration.multiply(botones.seekBar.getValue() / 100.0));
                }

                
            }
        });

    }

    protected void updateSeekBar(final Duration duration)
    {
        mediaPlayer.currentTimeProperty().addListener(new InvalidationListener()
        {
            Duration currentTime;
            String currentDuration, totalDuration, elapsedTime;

            @Override
            public void invalidated(Observable observable)
            {
                if (mediaPlayer.getCurrentTime().toMillis() != 0)
                {
                    Platform.runLater(new Runnable()
                    {

                        @Override
                        public void run()
                        {

                            currentTime = mediaPlayer.getCurrentTime();

                            if ( !botones.seekBar.isValueChanging())
                            {
                                mediaPlayer.setMute(false);
                                botones.seekBar.adjustValue((currentTime.toMillis() / duration.toMillis()) * 100.0);
                                currentDuration = new DecimalFormat("00").format((int) currentTime.toMinutes()) + ":"
                                        + new DecimalFormat("00").format((int) ((currentTime.toMinutes() - (int) currentTime.toMinutes()) * 60));
                                totalDuration = new DecimalFormat("00").format((int) duration.toMinutes()) + ":"
                                        + new DecimalFormat("00").format((int) ((duration.toMinutes() - (int) duration.toMinutes()) * 60));
                                elapsedTime = currentDuration + "/" + totalDuration;
                                botones.time.setText(elapsedTime);
                            }
                        }
                    });
                }
            }
        });
    }

    private String getExtension(String fileName)
    {
        String ext;
        ext = archivoB.getAbsolutePath().substring(fileName.lastIndexOf("."), fileName.length());
        return ext;
    }

    public static void main(String[] args)
    {
        launch(args);
    }
}

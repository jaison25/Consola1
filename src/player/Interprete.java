package player;

import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;

public class Interprete extends VBox
{
    String extension;
    Consola musicPlayer;
    public Interprete(String extension, MediaPlayer mediaPlayer,Stage stage)
    {
        this.extension=extension;
        setAlignment(Pos.CENTER);
        setSpacing(15);
        
            musicPlayer=new Consola();
            getChildren().add(musicPlayer);
        
    }
    
    public void setMetadata(Image albumArt,String titleValue, String artistValue, String albumValue)
    {
       
            if(albumArt==null)
                albumArt=new Image("file:default-album-art.jpg");
            if(titleValue==null)
                titleValue="Unknown";
            if(artistValue==null)
                artistValue="Unknown";
            if(albumValue==null)
                albumValue="Unknown";
            musicPlayer.setMetadata(albumArt, titleValue, artistValue, albumValue);
        
    }
}

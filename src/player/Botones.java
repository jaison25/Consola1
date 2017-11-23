package player;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;

public class Botones extends VBox
{
    public Button play, stop, next, previous;
    public Slider seekBar;
    public Label time;
    
    public Botones()
    {
        setSpacing(15);
        setPadding(new Insets(0, 40, 40, 40));
        setAlignment(Pos.CENTER_RIGHT);
        
        FlowPane espacioBotones=new FlowPane();
        espacioBotones.setAlignment(Pos.BOTTOM_CENTER);
        espacioBotones.setHgap(15);
        play = new Button("Play");
        stop = new Button("Stop");
        next = new Button("Next");
        previous = new Button("Prev");
        previous.setDisable(true);
        next.setDisable(true);
        espacioBotones.getChildren().addAll(previous, play, stop, next);
        
        seekBar=new Slider();
        time=new Label("--:--/--:--");
        getChildren().addAll(time,seekBar,espacioBotones);
    }
}

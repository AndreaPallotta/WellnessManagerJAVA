import javax.swing.*;
import java.awt.*;

/**
 * Class containing the main method
 *
 * @version 1.0 Final
 */
public class Main {
    /**
     * Main method
     *
     * @param args : String[]
     */
    public static void main(String[] args) {

        //UIView_Console uiView_console = new UIView_Console();
        EventQueue.invokeLater(() -> {
            try {
                UIView view = new UIView();
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                Controller controller = new Controller(view);
                view.addObserver(controller);
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
                e.printStackTrace();
            }
        });


    }


}

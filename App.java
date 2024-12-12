import javax.swing.JFrame;
public class App{
    public static void main(String [] a){
        int boardWidth = 360;
        int boardHeight = 640;
        
        JFrame frame = new  JFrame("Dracarys Voyage");
        //frame.setVisible(true);
        frame.setSize(boardWidth,boardHeight);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        DracarysVoyage dracarysvoyage = new DracarysVoyage();
        frame.add(dracarysvoyage);
        frame.pack();
        dracarysvoyage.requestFocus();
        frame.setVisible(true);
    }
}

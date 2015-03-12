import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.applet.*;

public class Quarto extends Applet{
        Board game;
        Choice difficulty;
        Button reset;
        Button myTurn;
        Button takeB;
        Button quarto;
        BoardCanvas bCanvas;
        
        static final int sSearch = 6;
        
        public void init(){
                game = new Board();
        
                setBackground(Color.white);
                setLayout (new BorderLayout());
                
                Panel choicePanel = new Panel();
                choicePanel.setLayout(new GridLayout(1,5));
                
                difficulty = new Choice();
                difficulty.addItem ("Easy");
                difficulty.addItem ("Depth 3");
                difficulty.addItem ("Depth 4");
                difficulty.addItem ("Depth 5");
                difficulty.addItem ("Depth 6");
                difficulty.addItem ("Depth 7");
                difficulty.addItem ("Depth 8");
                difficulty.addItem ("Optimal");
                difficulty.select(2);
                                
                
                reset = new Button("New Game");
                myTurn = new Button("Compute");
                // takeB  = new Button("Take Back");
                quarto = new Button("Quarto!");
                
                choicePanel.add (reset);
                choicePanel.add (myTurn);
                // choicePanel.add (takeB);
                choicePanel.add (quarto);
                choicePanel.add (difficulty);
                
                // load up the images
                MediaTracker tracker = new MediaTracker(this);
                Image images[] = new Image[16];
                int i;
                for (i = 0; i<16; i++){
                    images[i] = getImage(getDocumentBase(), "Pieces/piece" + i + ".gif");
                    tracker.addImage(images[i], 0);
                }

                Panel thing = new Panel();
                bCanvas = new BoardCanvas (game,images);
                game.bC = bCanvas;
                thing.add (bCanvas);
                
                add ("North", choicePanel);
                add ("Center", thing);
                Reset();
                try{
                   tracker.waitForAll();
                } catch(InterruptedException e) {}
        
                repaint();
        }
        
        public static void main(String args[]){
                Quarto q = new Quarto();
                q.resize(400,400);
                q.show();
        }

        public void Reset(){
                game.Reset();
                game.SetDepth(difficulty.getSelectedIndex() + 1);
                game.bC.curString = "";
        }
        
        public boolean handleEvent(Event ev) {
                if (ev.target == reset){
                   Reset();
                   bCanvas.repaint();      
                   return true;
                }
                if (ev.target == myTurn){
                   game.Think();
                   bCanvas.repaint();      
                   return true;
                }
                /* if (ev.target == takeB){
                   game.takeBack();
                   bCanvas.repaint();      
                   return true;
                } */
                if (ev.target == quarto){
                   game.Check();
                   bCanvas.repaint();      
                   return true;
                }
                if (ev.target == difficulty){
                   game.SetDepth(difficulty.getSelectedIndex() + 1);
                   bCanvas.repaint();      
                   return true;
                }
                return false;
        }

}
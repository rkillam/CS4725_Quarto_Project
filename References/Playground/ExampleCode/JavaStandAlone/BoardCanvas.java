import java.awt.*;
import java.util.*;
import java.applet.*;

public class BoardCanvas extends Canvas {
                
        static final int sDraw = 1;
        static final int sYouWin = 2;
        static final int sIWin = 3;
        static final int sPlace = 4;
        static final int sChoose = 5;
        static final int sSearch = 6;
        static final int[] pieceConv = { 85,  86,  89,  90, 101, 102, 105, 106,
                                  149, 150, 153, 154, 165, 166, 169, 170};
        
        private Board game;
        private Image images[];
        public String curString;
        private int[] conv = new int[171];
        
        // coords
        static final int pSz = 32; // size of a piece
        static final int pBox = 40; // size of a piece box
        static final int inset = (pBox - pSz) / 2; // inset
        
        static final int boardX = 80;
        static final int boardY = 0;
        
        static final int piecesX = 0;
        static final int piecesY = 220;
        
        static final int toPlayX = 0;
        static final int toPlayY = 120;
        
        static final int textY = 195;
        
        public BoardCanvas(Board g,Image i[]){
                game = g;
                images = i;
                curString = "";
                resize (piecesX + pBox*8 + 1, piecesY + pBox*2 + 1);
                conv[85] = 0;
                conv[86] = 1;
                conv[89] = 2;
                conv[90] = 3;
                conv[101] = 4;
                conv[102] = 5;
                conv[105] = 6;
                conv[106] = 7;
                conv[149] = 8;
                conv[150] = 9;
                conv[153] = 10;
                conv[154] = 11;
                conv[165] = 12;
                conv[166] = 13;
                conv[169] = 14;
                conv[170] = 15;
        }
                
        public synchronized void paint(Graphics g){
                
                paintBoard(g);
                paintPieces(g);
                paintToPlay(g);
                paintString(g);
        }
        
        private void paintBoard(Graphics g){
                g.setColor (Color.white);
                g.fillRect(boardX, boardY, pBox*4, pBox*4);
                
                g.setColor (Color.black);
                
                int i;
                int j;
                for (i = 0; i<5; i++){
                        g.drawLine (i*pBox + boardX, boardY, i*pBox + boardX,
                                                        boardY + 4 * pBox);
                        g.drawLine (boardX, boardY + i*pBox, boardX + 4*pBox, boardY + i*pBox);
                }
                                
                for (i=0; i<16;i++){
                    if (game.thisBoard[i] != game.EMPTY){
                       g.drawImage(images[conv[game.thisBoard [i]]], (i % 4) * pBox + boardX + inset, 
                                                                           (i / 4) * pBox + boardY + inset, this);
                    }
                }
        }
        
        private void paintPieces (Graphics g){
                g.setColor (Color.white);
                g.fillRect(piecesX, piecesY, pBox *8, pBox * 2);
                
                g.setColor (Color.black);
                
                int i;
                for (i = 0; i<9; i++)
                        g.drawLine (i*pBox + piecesX, piecesY, 
                                                i*pBox + piecesX, piecesY +2*pBox);
                        
                for (i = 0; i<3; i++)
                        g.drawLine (piecesX, piecesY + i*pBox, 
                                                piecesX + pBox*8, piecesY + i*pBox);
                
                for (i = 0; i<16; i++)
                        if (game.piecesAvailable[i])
                                g.drawImage(images[i], piecesX + (i % 8)*pBox + inset,
                                                        (i / 8) * pBox + piecesY + inset, this);
        }
        
        private void paintToPlay(Graphics g){   
                g.setColor (Color.white);
                g.fillRect (toPlayX, toPlayY, pBox, pBox);
                
                g.setColor (Color.black);
                g.drawRect (toPlayX, toPlayY, pBox, pBox);
                
                if (game.pieceToPut != game.EMPTY)
                        g.drawImage (images[conv[game.pieceToPut]], toPlayX + inset,
                                 toPlayY + inset, this);
        }
        
        private void paintString (Graphics g){
                g.setFont (new Font ("My font", Font.PLAIN, 12));
                FontMetrics fm = g.getFontMetrics();
                int x = fm.stringWidth (curString);
                
                g.clearRect (piecesX, boardY + pBox*4 + 1, pBox*8,
                                                piecesY - (boardY + pBox*4 + 1) - 1);
                g.setColor (Color.black);
                g.drawString (curString, boardX + pBox * 2 - x / 2, textY);
        }
        

        private int clickToPieceIndex (int x, int y) {
                if (x < piecesX || x > piecesX + pBox*8 ||
                        y < piecesY || y > piecesY + pBox*2)
                        return -1;
                
                int out;
                out = (x - piecesX) / pBox; // this will give us 0-7
                out = out + 8* ((y - piecesY) / pBox); // this will add 8 if necessary
                
                return out;
        }

        private int clickToPosition (int x, int y) {
                if (x < boardX || x > boardX + pBox*4 ||
                        y < boardY || y > boardY + pBox*4)
                        return -1;
                
                int out;
                out = (x - boardX) / pBox; // this will give us 0-3
                out = out + 4* ((y - boardY) / pBox); // this will add multiple of 4
                
                return out;
        }
                        
                
	private int clickToX (int x){
		if (x < boardX || x > boardX + pBox*4) return -1;
		return (x - boardX) / pBox;
	}
		
	private int clickToY (int y){
		if (y < boardY || y > boardY + pBox*4)	return -1;
		return (y - boardY) / pBox;
	}

        public boolean mouseDown(java.awt.Event evt, int x, int y) {
        
                if (!game.thinking){
                        curString = "";
                        int index = clickToPieceIndex(x, y);
                        if (index!=-1) {
                           if (!game.piecesAvailable[index]){
                              curString = "Empty square choosen!";
                              repaint(); 
                              return true;
                           }
                           if (game.setPieceToPut(index)){ 
                              repaint(); 
                              game.Think();
                           }
                           else curString = "Play piece first !";
                           repaint(); 
                           return true;
                        }
                        index = clickToPosition(x, y);                        
                        if (index!=-1) {
                           game.doMove(index, game.EMPTY); 
                           repaint(); 
                           return true;
                        }
                }
                return true;
        }

}                         
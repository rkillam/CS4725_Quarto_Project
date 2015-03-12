import java.io.*;
import java.awt.*;
import java.util.*;

public class Board {
  final static int   EMPTY  = 0;
  final static int[] to256 = { 85,  86,  89,  90, 101, 102, 105, 106,
                                  149, 150, 153, 154, 165, 166, 169, 170};

  public int[]        to16 = new int[171];
  public boolean[]    piecesAvailable;
  public int          pieceToPut;
  public int[]        thisBoard;
  public int[][]      MovesDone;
  public int          nodesChecked;
  public int          nrMovesDone;
  public int          nrPiecesLeft;
  public int          Depth;
  public int          CurrentDepth;
  public int[]        bestMove;
  public boolean      finished;
  public boolean      rules;
  public boolean      thinking;
  public BoardCanvas  bC; 
  public long         t2, evalt; 
  public boolean      Optimal;

  public Board(){
    to16[85] = 0;
    to16[86] = 1;
    to16[89] = 2;
    to16[90] = 3;
    to16[101] = 4;
    to16[102] = 5;
    to16[105] = 6;
    to16[106] = 7;
    to16[149] = 8;
    to16[150] = 9;
    to16[153] = 10;
    to16[154] = 11;
    to16[165] = 12;
    to16[166] = 13;
    to16[169] = 14;
    to16[170] = 15;
    this.Depth   = 2;
    this.CurrentDepth   = 2;
    Optimal = false;
    finished = false;
    Reset();
  }
    
  public void Reset(){
    bestMove     = new int[2];
    nrMovesDone  = 0;
    thisBoard    = new int[16];
    MovesDone    = new int[50][4];
    piecesAvailable   = new boolean[16];
    for(int i=0;i < 16;i++){
      thisBoard[i] = EMPTY; 
      piecesAvailable[i] = true;
    }
    pieceToPut   = EMPTY;
    thinking     = false;
    finished = false;
  }

  public void Load(){
       Reset();
       try{
         FileInputStream in = new FileInputStream("Save.quarto");
         DataInputStream datainfile = new DataInputStream(in);
         String line = datainfile.readLine();
         StringTokenizer st = new StringTokenizer(line);
         for(int i = 0;i<16;i++){
            String s = st.nextToken();
            int p = Integer.parseInt(s);
            thisBoard[i] = p; 
            if (p != EMPTY){
               piecesAvailable[to16[p]] = false; 
               nrMovesDone++;
            }
         }
         String s = st.nextToken();
         int p = Integer.parseInt(s);
         this.pieceToPut = p;
         if (p != EMPTY) piecesAvailable[to16[p]] = false; 
       }catch (Exception e){
         System.out.println("Error loading Save.quarto");
       }
       System.out.println("Position loaded. Moves Done = " + nrMovesDone);
  } 

  public void Save(){
       try{
         FileOutputStream out = new FileOutputStream("Save.quarto");
         PrintStream dataoutfile = new PrintStream(out);
         
         String l = "";
         for(int i = 0;i<16;i++){
            l = l + thisBoard[i] + " ";
         }
         l = l + this.pieceToPut;
         dataoutfile.println(l);
       }catch (Exception e){
         System.out.println("Error saving Save.quarto");
       }
  } 

  public void CalculateDepth() {
       CurrentDepth = Depth;
       if (nrMovesDone >= 8) CurrentDepth = 8;
       if (CurrentDepth > (15 - nrMovesDone)) CurrentDepth = (15 - nrMovesDone);
  }
  
  public void Check () {
    if (eval() >= 1){
       finished = true;
       bC.curString="OK you win";
    }
    else bC.curString="No way cheater !";
  }

  public void SetDepth(int Depth){
    this.Depth = Depth;
    this.CurrentDepth = Depth;
    if (Depth == 8) this.Optimal = true;
    else this.Optimal = false;
  }
  
  public boolean setPieceToPut(int ptp){
    int p = to256[ptp];
    if (!piecesAvailable[ptp]) {
       System.out.println("ERROR: Non existing piece choosen: " + p);
       return false;
    }
    if (pieceToPut != EMPTY) {
       System.out.println("ERROR: Play piece first");
       bC.curString = "Play current piece first !";
       return false;
    }
    pieceToPut = p;
    piecesAvailable[ptp] = false;
    if ((nrMovesDone > 0) && (MovesDone[nrMovesDone-1][2] == EMPTY)) MovesDone[nrMovesDone-1][2] = p;
    bC.curString = "";
    return true;
  }
  
  public int chooseRandomPiece(){
    Random r = new Random();
    int x = r.nextInt();
    if (x < 0) x *= -1;
    x = x % 16;
    int i = x;
    while(!piecesAvailable[i]){
      i++;
      if (i == 16) i = 0;
      if (i==x) return EMPTY;
    }
    return to256[i];
  }

  public int chooseEmptySquare(){
    Random r = new Random();
    int x = r.nextInt();
    if (x < 0) x *= -1;
    x = x % 16;
    int i = x;
    while(thisBoard[i] != EMPTY){
      i++;
      if (i == 16) i = 0;
      if (i==x) return -1;
    }
    return i;
  }


  public int minimax(int Level, int alfa, int beta){
    int res;

    /* BEGIN OF EVAL */
    if (thisBoard[0]==0) {
       nodesChecked++;
       if (((this.pieceToPut & thisBoard[1] & thisBoard[2] & thisBoard[3])!=0) ||
           ((this.pieceToPut & thisBoard[4] & thisBoard[8] & thisBoard[12])!=0) ||
           ((this.pieceToPut & thisBoard[5] & thisBoard[10] & thisBoard[15])!=0)){
           if (Level == CurrentDepth){
               bestMove[0] = 0;
               bestMove[1] = EMPTY;
           }
           return 1;
        }       
    }
    if (thisBoard[1]==0) {
       nodesChecked++;
       if (((this.pieceToPut & thisBoard[0] & thisBoard[2] & thisBoard[3])!=0) ||
           ((this.pieceToPut & thisBoard[5] & thisBoard[9] & thisBoard[13])!=0)){
           if (Level == CurrentDepth){
               bestMove[0] = 1;
               bestMove[1] = EMPTY;
           }
           return 1;
       }       
    }
    if (thisBoard[2]==0) {
       nodesChecked++;
       if (((this.pieceToPut & thisBoard[0] & thisBoard[1] & thisBoard[3])!=0) ||
           ((this.pieceToPut & thisBoard[6] & thisBoard[10] & thisBoard[14])!=0)){
           if (Level == CurrentDepth){
               bestMove[0] = 2;
               bestMove[1] = EMPTY;
           }
           return 1;
       }       
    }
    if (thisBoard[3]==0) {
       nodesChecked++;
       if (((this.pieceToPut & thisBoard[0] & thisBoard[1] & thisBoard[2])!=0) ||
           ((this.pieceToPut & thisBoard[7] & thisBoard[11] & thisBoard[15])!=0) ||
           ((this.pieceToPut & thisBoard[6] & thisBoard[9] & thisBoard[12])!=0)){
           if (Level == CurrentDepth){
               bestMove[0] = 3;
               bestMove[1] = EMPTY;
           }
           return 1;
       }       
    }
    if (thisBoard[4]==0) {
       nodesChecked++;
       if (((this.pieceToPut & thisBoard[5] & thisBoard[6] & thisBoard[7])!=0) ||
           ((this.pieceToPut & thisBoard[0] & thisBoard[8] & thisBoard[12])!=0)){
           if (Level == CurrentDepth){
               bestMove[0] = 4;
               bestMove[1] = EMPTY;
           }
           return 1;
       }       
    }
    if (thisBoard[5]==0) {
       nodesChecked++;
       if (((this.pieceToPut & thisBoard[4] & thisBoard[6] & thisBoard[7])!=0) ||
           ((this.pieceToPut & thisBoard[1] & thisBoard[9] & thisBoard[13])!=0) ||
           ((this.pieceToPut & thisBoard[0] & thisBoard[10] & thisBoard[15])!=0)){
           if (Level == CurrentDepth){
               bestMove[0] = 5;
               bestMove[1] = EMPTY;
           }
           return 1;
       }       
    }
    if (thisBoard[6]==0) {
       nodesChecked++;
       if (((this.pieceToPut & thisBoard[4] & thisBoard[5] & thisBoard[7])!=0) ||
           ((this.pieceToPut & thisBoard[2] & thisBoard[10] & thisBoard[14])!=0) ||
           ((this.pieceToPut & thisBoard[3] & thisBoard[9] & thisBoard[12])!=0)){
           if (Level == CurrentDepth){
               bestMove[0] = 6;
               bestMove[1] = EMPTY;
           }
           return 1;
       }       
    }
    if (thisBoard[7]==0) {
       nodesChecked++;
       if (((this.pieceToPut & thisBoard[4] & thisBoard[5] & thisBoard[6])!=0) ||
           ((this.pieceToPut & thisBoard[3] & thisBoard[11] & thisBoard[15])!=0)){
           if (Level == CurrentDepth){
               bestMove[0] = 7;
               bestMove[1] = EMPTY;
           }
           return 1;
       }       
    }
    if (thisBoard[8]==0) {
       nodesChecked++;
       if (((this.pieceToPut & thisBoard[9] & thisBoard[10] & thisBoard[11])!=0) ||
           ((this.pieceToPut & thisBoard[0] & thisBoard[4] & thisBoard[12])!=0)){
           if (Level == CurrentDepth){
               bestMove[0] = 8;
               bestMove[1] = EMPTY;
           }
           return 1;
       }       
    }
    if (thisBoard[9]==0) {
       nodesChecked++;
       if (((this.pieceToPut & thisBoard[8] & thisBoard[10] & thisBoard[11])!=0) ||
           ((this.pieceToPut & thisBoard[1] & thisBoard[5] & thisBoard[13])!=0) ||
           ((this.pieceToPut & thisBoard[3] & thisBoard[6] & thisBoard[12])!=0)){
           if (Level == CurrentDepth){
               bestMove[0] = 9;
               bestMove[1] = EMPTY;
           }
           return 1;
       }       
    }
    if (thisBoard[10]==0) {
       nodesChecked++;
       if (((this.pieceToPut & thisBoard[8] & thisBoard[9] & thisBoard[11])!=0) ||
           ((this.pieceToPut & thisBoard[2] & thisBoard[6] & thisBoard[14])!=0) ||
           ((this.pieceToPut & thisBoard[0] & thisBoard[5] & thisBoard[15])!=0)){
           if (Level == CurrentDepth){
               bestMove[0] = 10;
               bestMove[1] = EMPTY;
           }
           return 1;
       }       
    }
    if (thisBoard[11]==0) {
       nodesChecked++;
       if (((this.pieceToPut & thisBoard[8] & thisBoard[9] & thisBoard[10])!=0) ||
           ((this.pieceToPut & thisBoard[3] & thisBoard[7] & thisBoard[15])!=0)){
           if (Level == CurrentDepth){
               bestMove[0] = 11;
               bestMove[1] = EMPTY;
           }
           return 1;
       }       
    }
    if (thisBoard[12]==0) {
       nodesChecked++;
       if (((this.pieceToPut & thisBoard[13] & thisBoard[14] & thisBoard[15])!=0) ||
           ((this.pieceToPut & thisBoard[0] & thisBoard[4] & thisBoard[8])!=0) ||
           ((this.pieceToPut & thisBoard[3] & thisBoard[6] & thisBoard[9])!=0)){
           if (Level == CurrentDepth){
               bestMove[0] = 12;
               bestMove[1] = EMPTY;
           }
           return 1;
       }       
    }
    if (thisBoard[13]==0) {
       nodesChecked++;
       if (((this.pieceToPut & thisBoard[12] & thisBoard[14] & thisBoard[15])!=0) ||
           ((this.pieceToPut & thisBoard[1] & thisBoard[5] & thisBoard[9])!=0)){
           if (Level == CurrentDepth){
               bestMove[0] = 13;
               bestMove[1] = EMPTY;
           }
           return 1;
       }       
    }
    if (thisBoard[14]==0) {
       nodesChecked++;
       if (((this.pieceToPut & thisBoard[12] & thisBoard[13] & thisBoard[15])!=0) ||
           ((this.pieceToPut & thisBoard[2] & thisBoard[6] & thisBoard[10])!=0)){
           if (Level == CurrentDepth){
               bestMove[0] = 14;
               bestMove[1] = EMPTY;
           }
           return 1;
       }       
    }
    if (thisBoard[15]==0) {
       nodesChecked++;
       if (((this.pieceToPut & thisBoard[12] & thisBoard[13] & thisBoard[14])!=0) ||
           ((this.pieceToPut & thisBoard[3] & thisBoard[7] & thisBoard[11])!=0) ||
           ((this.pieceToPut & thisBoard[0] & thisBoard[5] & thisBoard[10])!=0)){
           if (Level == CurrentDepth){
               bestMove[0] = 15;
               bestMove[1] = EMPTY;
           }
           return 1;
       }       
    }
    /* END OF EVAL */

    for(int i=0;(i < 16);i++){
       if (thisBoard[i] == EMPTY){
          thisBoard[i] = this.pieceToPut;
          for(int k = 0;(k < 16); k++){
              if (piecesAvailable[k]){
                     this.pieceToPut = to256[k];
                     /* nodesChecked++; */
                     if (Level == 1){
                        res = -1; 
                        /* BEGIN OF EVAL */
                        while (true){                 
                          if (thisBoard[0]==0) {
                            nodesChecked++;
                            if (((this.pieceToPut & thisBoard[1] & thisBoard[2] & thisBoard[3])!=0) ||
                                ((this.pieceToPut & thisBoard[4] & thisBoard[8] & thisBoard[12])!=0) ||
                                ((this.pieceToPut & thisBoard[5] & thisBoard[10] & thisBoard[15])!=0)){
                                    res = 0;
                                    break;
                            }       
                          }
                          if (thisBoard[1]==0) {
                            nodesChecked++;
                            if (((this.pieceToPut & thisBoard[0] & thisBoard[2] & thisBoard[3])!=0) ||
                                ((this.pieceToPut & thisBoard[5] & thisBoard[9] & thisBoard[13])!=0)){
                                    res = 0;
                                    break;
                            }       
                          }
                          if (thisBoard[2]==0) {
                            nodesChecked++;
                            if (((this.pieceToPut & thisBoard[0] & thisBoard[1] & thisBoard[3])!=0) ||
                                ((this.pieceToPut & thisBoard[6] & thisBoard[10] & thisBoard[14])!=0)){
                                   res = 0;
                                   break;
                            }       
                          }
                          if (thisBoard[3]==0) {
                            nodesChecked++;
                            if (((this.pieceToPut & thisBoard[0] & thisBoard[1] & thisBoard[2])!=0) ||
                                ((this.pieceToPut & thisBoard[7] & thisBoard[11] & thisBoard[15])!=0) ||
                                ((this.pieceToPut & thisBoard[6] & thisBoard[9] & thisBoard[12])!=0)){
                                   res = 0;
                                   break;
                            }       
                          }
                          if (thisBoard[4]==0) {
                            nodesChecked++;
                            if (((this.pieceToPut & thisBoard[5] & thisBoard[6] & thisBoard[7])!=0) ||
                                ((this.pieceToPut & thisBoard[0] & thisBoard[8] & thisBoard[12])!=0)){
                                   res = 0;
                                   break;
                            }       
                          }
                          if (thisBoard[5]==0) {
                            nodesChecked++;
                            if (((this.pieceToPut & thisBoard[4] & thisBoard[6] & thisBoard[7])!=0) ||
                                ((this.pieceToPut & thisBoard[1] & thisBoard[9] & thisBoard[13])!=0) ||
                                ((this.pieceToPut & thisBoard[0] & thisBoard[10] & thisBoard[15])!=0)){
                                   res = 0;
                                   break;
                            }       
                          }
                          if (thisBoard[6]==0) {
                            nodesChecked++;
                            if (((this.pieceToPut & thisBoard[4] & thisBoard[5] & thisBoard[7])!=0) ||
                                ((this.pieceToPut & thisBoard[2] & thisBoard[10] & thisBoard[14])!=0) ||
                                ((this.pieceToPut & thisBoard[3] & thisBoard[9] & thisBoard[12])!=0)){
                                   res = 0;
                                   break;
                            }       
                          }
                          if (thisBoard[7]==0) {
                            nodesChecked++;
                            if (((this.pieceToPut & thisBoard[4] & thisBoard[5] & thisBoard[6])!=0) ||
                                ((this.pieceToPut & thisBoard[3] & thisBoard[11] & thisBoard[15])!=0)){
                                   res = 0;
                                   break;
                            }       
                          }
                          if (thisBoard[8]==0) {
                            nodesChecked++;
                            if (((this.pieceToPut & thisBoard[9] & thisBoard[10] & thisBoard[11])!=0) ||
                                ((this.pieceToPut & thisBoard[0] & thisBoard[4] & thisBoard[12])!=0)){
                                   res = 0;
                                   break;
                            }       
                          }
                          if (thisBoard[9]==0) {
                            nodesChecked++;
                            if (((this.pieceToPut & thisBoard[8] & thisBoard[10] & thisBoard[11])!=0) ||
                                ((this.pieceToPut & thisBoard[1] & thisBoard[5] & thisBoard[13])!=0) ||
                                ((this.pieceToPut & thisBoard[3] & thisBoard[6] & thisBoard[12])!=0)){
                                   res = 0;
                                   break;
                            }       
                          }
                          if (thisBoard[10]==0) {
                            nodesChecked++;
                            if (((this.pieceToPut & thisBoard[8] & thisBoard[9] & thisBoard[11])!=0) ||
                                ((this.pieceToPut & thisBoard[2] & thisBoard[6] & thisBoard[14])!=0) ||
                                ((this.pieceToPut & thisBoard[0] & thisBoard[5] & thisBoard[15])!=0)){
                                   res = 0;
                                   break;
                            }       
                          }
                          if (thisBoard[11]==0) {
                            nodesChecked++;
                            if (((this.pieceToPut & thisBoard[8] & thisBoard[9] & thisBoard[10])!=0) ||
                                ((this.pieceToPut & thisBoard[3] & thisBoard[7] & thisBoard[15])!=0)){
                                   res = 0;
                                   break;
                            }       
                          }
                          if (thisBoard[12]==0) {
                            nodesChecked++;
                            if (((this.pieceToPut & thisBoard[13] & thisBoard[14] & thisBoard[15])!=0) ||
                                ((this.pieceToPut & thisBoard[0] & thisBoard[4] & thisBoard[8])!=0) ||
                                ((this.pieceToPut & thisBoard[3] & thisBoard[6] & thisBoard[9])!=0)){
                                   res = 0;
                                   break;
                            }       
                          }
                          if (thisBoard[13]==0) {
                            nodesChecked++;
                            if (((this.pieceToPut & thisBoard[12] & thisBoard[14] & thisBoard[15])!=0) ||
                                ((this.pieceToPut & thisBoard[1] & thisBoard[5] & thisBoard[9])!=0)){
                                   res = 0;
                                   break;
                            }       
                          }
                          if (thisBoard[14]==0) {
                            nodesChecked++;
                            if (((this.pieceToPut & thisBoard[12] & thisBoard[13] & thisBoard[15])!=0) ||
                                ((this.pieceToPut & thisBoard[2] & thisBoard[6] & thisBoard[10])!=0)){
                                   res = 0;
                                   break;
                            }       
                          }
                          if (thisBoard[15]==0) {
                            nodesChecked++;
                            if (((this.pieceToPut & thisBoard[12] & thisBoard[13] & thisBoard[14])!=0) ||
                                ((this.pieceToPut & thisBoard[3] & thisBoard[7] & thisBoard[11])!=0) ||
                                ((this.pieceToPut & thisBoard[0] & thisBoard[5] & thisBoard[10])!=0)){
                                   res = 0;
                                   break;
                            }       
                          }                     
                          break;
                        }
                        /* END OF EVAL */
                       
                        if (res == -1){ 
                          if (0 > alfa){
                            if (Level == CurrentDepth){
                              bestMove[0] = i;
                              bestMove[1] = this.pieceToPut;
                            }
                            alfa = 0;
                          }
                          if (0 >= beta){
                            this.pieceToPut    = thisBoard[i];    
                            thisBoard[i]       = EMPTY;
                            return 0;
                          }
                        }
                     }
                     else{
                        piecesAvailable[k] = false;
                        res = 0 - minimax(Level - 1, 0 - beta, 0 - alfa);
                        if (res > alfa){
                           if (Level == CurrentDepth){
                              bestMove[0] = i;
                              bestMove[1] = this.pieceToPut;
                           }
                           alfa = res;
                        }
                        piecesAvailable[k] = true;
                        if (alfa >= beta){
                           this.pieceToPut = thisBoard[i];    
                           thisBoard[i]    = EMPTY;
                           return alfa;
                        }
                     }
              }
          }
          this.pieceToPut = thisBoard[i];    
          thisBoard[i]    = EMPTY;
          if (Level == CurrentDepth){
             System.out.print(".(" +nodesChecked +")");
             System.out.flush();
          }
       }
    }
    return alfa;
  }


  public void Think () {
     bestMove[0] = -99;
     bestMove[1] = -99;
     
     if (pieceToPut == EMPTY){
       if (nrMovesDone != 0) bC.curString = "Choose a piece to play first ...";
       else{
          thinking = true;
          pieceToPut = chooseRandomPiece();
          piecesAvailable[to16[pieceToPut]] = false;
          thinking = false;
       }   
     }
     else{
       thinking = true;
       if (Optimal) CalculateDepth();
       int res = 0;
       boolean found = false;
       if (nrMovesDone == 15) {
          for (int i = 0; (i < 16) && !found;i++){
              if (thisBoard[i] == EMPTY) {
                 doMove(i,EMPTY);
                 found = true;
              }
          }
          bC.curString="Nodes: " + nodesChecked + " (Depth: " + (this.CurrentDepth + 1) +" Res: " + eval() +")";
       }
       else{   
          nodesChecked = 0;
          System.out.print("Thinking on depth " + (this.CurrentDepth + 1) + ": ");
          res = minimax(this.CurrentDepth, -1, 1);
          System.out.println();
          System.out.println("Calculated " + nodesChecked + " nodes.");
          int orDepth = this.CurrentDepth;
          int orNodesChecked = nodesChecked;
          if (res < 0){
             int res2 = -1;
             while ((res2 < 0) && (this.CurrentDepth > 2)){
               this.CurrentDepth = this.CurrentDepth - 1;
               System.out.println();
               System.out.print("Retrying on depth " + this.CurrentDepth +": ");
               res2 = minimax(this.CurrentDepth, -1, 1);
               orNodesChecked += nodesChecked;
             }
             if (bestMove[1] == EMPTY) bestMove[1] = chooseRandomPiece();
             doMove(bestMove[0], bestMove[1]);
             bC.curString="Nodes: " + orNodesChecked + " (Depth: " + (orDepth+1) +" Res: " + res +" New res: " + res2 + ")";
          }
          else{           
             if (bestMove[1] == EMPTY) bestMove[1] = chooseRandomPiece();
             doMove(bestMove[0], bestMove[1]);
             bC.curString="Nodes: " + nodesChecked + " (Depth: " + (this.CurrentDepth + 1) +" Res: " + res +")";
          }
       }
       if (nrMovesDone == 16) {
          bC.curString = "Draw. Nice Game";
          finished = true;
       }
       if (eval() >= 1){
          bC.curString = "Quarto ! Damn I'm good !";
          finished = true;
       }
       System.out.println();
       thinking = false;
     }
  }

  
  public boolean doMove(int x, int pieceToGive){
    return doMoveGUI(x, pieceToPut, pieceToGive);
  }

  public boolean doMoveGUI(int x, int pieceToPut, int pieceToGive){
    if (x == -99) x = chooseEmptySquare();
    if (pieceToGive == -99) pieceToGive = chooseRandomPiece();
    if (pieceToPut == EMPTY){
       bC.curString = "Choose a piece to play first ...";
       return false;
    }
    else bC.curString = "";
    if (thisBoard[x] == EMPTY){
       thisBoard[x] = pieceToPut;
       this.pieceToPut = pieceToGive;
       if (pieceToPut != EMPTY) piecesAvailable[to16[pieceToPut]] = false;
       if (pieceToGive != EMPTY) piecesAvailable[to16[pieceToGive]] = false;
       MovesDone[nrMovesDone][0] = x;
       MovesDone[nrMovesDone][1] = pieceToPut;
       MovesDone[nrMovesDone][2] = pieceToGive;
       nrMovesDone++;
       return true;
    }
    else return false;
  }
  
  
  public boolean takeBack(){
    if (nrMovesDone > 0){
      nrMovesDone--;
      if (MovesDone[nrMovesDone][2] != EMPTY) piecesAvailable[to16[MovesDone[nrMovesDone][2]]] = true;
      thisBoard[MovesDone[nrMovesDone][0]] = EMPTY;
      this.pieceToPut = MovesDone[nrMovesDone][1];    
      return true;
    }
    else{
      if (pieceToPut != EMPTY){
          piecesAvailable[to16[pieceToPut]] = true;
          pieceToPut = EMPTY;
          return true;
      }
      else{
         System.out.println("Unable to take back - no moves left");
         return false;
      }
    }
  }
  
 
  
  public int eval(){
    for (int i = 0; i < 4; i++){
      if ((thisBoard[i*4] & 
           thisBoard[i*4 + 1] &  
           thisBoard[i*4 + 2] &  
           thisBoard[i*4 + 3]) != 0) {
           return 1; 
      }
      if ((thisBoard[i] & 
           thisBoard[4 + i] &  
           thisBoard[8 + i] &  
           thisBoard[12 + i]) != 0){
           return 1; 
      }
    }

    if ((thisBoard[0] & 
         thisBoard[5] &  
         thisBoard[10] &  
         thisBoard[15]) != 0){
         return 1; 
    }
  
    if ((thisBoard[3] & 
         thisBoard[6] &  
         thisBoard[9] &  
         thisBoard[12]) != 0){
         return 1; 
    }
 
    return 0;
  } 

}


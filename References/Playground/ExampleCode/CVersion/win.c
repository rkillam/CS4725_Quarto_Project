#include <stdio.h>

  #define EMPTY 0
  int to256[] = { 85,  86,  89,  90, 101, 102, 105, 106,
                  149, 150, 153, 154, 165, 166, 169, 170};

   int 	to16[171];
   int    piecesAvailable[16]={1,1,1,1, 1,1,1,1, 1,1,1,1, 1,1,1,1};  /* Boolean */
   int    pieceToPut;
   int    thisBoard[16]={0,0,0,0, 0,0,0,0, 0,0,0,0, 0,0,0,0};
   int    nodesChecked;
   int    nrMovesDone;
   int          nrPiecesLeft;
   int          Depth;
   int          CurrentDepth;
   int        bestMove[2];

/* Mag weg */
   int      rules;
   int      thinking;

void Load() {
         FILE *in;
         int p,i,k ;
         
         in = fopen("Save.quarto", "r");

         for(i = 0;i<16;i++){
            fscanf(in, "%d", &p);
            printf(" %d ", p);
            thisBoard[i] = p; 
            
            if (p != EMPTY){
               piecesAvailable[to16[p]] = 0; 
               nrMovesDone++;
            }
         }

         fscanf(in, "%d", &p);
         printf("Piece to Put  : %d \n", p);

         pieceToPut = p;
         if (p != EMPTY) piecesAvailable[to16[p]] = 0; 

         printf("Position loaded. Moves Done = %d \n", nrMovesDone);
         fclose(in);
  } 

void Save(){
         FILE *out;
         int i ;
         
         out = fopen("Save.quarto.out", "w");

         for(i = 0;i<16;i++){
            fprintf(out, "%d ", thisBoard[i]);
            printf("%d ", thisBoard[i]);
         }
         fprintf(out, "%d \n", pieceToPut);
         fclose(out);
  } 

  int minimax (int Level, int alfa, int beta){
    int res;
    int i, k, test;

/***** VANAF HIER EVAL ***/
        if (thisBoard[0]==0) {
                nodesChecked++;
                if (((pieceToPut & thisBoard[1] & thisBoard[2] & thisBoard[3])!=0) ||
                        ((pieceToPut & thisBoard[4] & thisBoard[8] & thisBoard[12])!=0) ||
                        ((pieceToPut & thisBoard[5] & thisBoard[10] & thisBoard[15])!=0)){
                        if (Level == CurrentDepth){
                           bestMove[0] = 0;
                           bestMove[1] = EMPTY;
                        }
                        return 1;
                }       
        }
        if (thisBoard[1]==0) {
                nodesChecked++;
                if (((pieceToPut & thisBoard[0] & thisBoard[2] & thisBoard[3])!=0) ||
                        ((pieceToPut & thisBoard[5] & thisBoard[9] & thisBoard[13])!=0)){
                        if (Level == CurrentDepth){
                           bestMove[0] = 1;
                           bestMove[1] = EMPTY;
                        }
                        return 1;
                }       
        }
        if (thisBoard[2]==0) {
                nodesChecked++;
                if (((pieceToPut & thisBoard[0] & thisBoard[1] & thisBoard[3])!=0) ||
                        ((pieceToPut & thisBoard[6] & thisBoard[10] & thisBoard[14])!=0)){
                        if (Level == CurrentDepth){
                           bestMove[0] = 2;
                           bestMove[1] = EMPTY;
                        }
                        return 1;
                }       
        }
        if (thisBoard[3]==0) {
                nodesChecked++;
                if (((pieceToPut & thisBoard[0] & thisBoard[1] & thisBoard[2])!=0) ||
                        ((pieceToPut & thisBoard[7] & thisBoard[11] & thisBoard[15])!=0) ||
                        ((pieceToPut & thisBoard[6] & thisBoard[9] & thisBoard[12])!=0)){
                        if (Level == CurrentDepth){
                           bestMove[0] = 3;
                           bestMove[1] = EMPTY;
                        }
                        return 1;
                }       
        }
        if (thisBoard[4]==0) {
                nodesChecked++;
                if (((pieceToPut & thisBoard[5] & thisBoard[6] & thisBoard[7])!=0) ||
                        ((pieceToPut & thisBoard[0] & thisBoard[8] & thisBoard[12])!=0)){
                        if (Level == CurrentDepth){
                           bestMove[0] = 4;
                           bestMove[1] = EMPTY;
                        }
                        return 1;
                }       
        }
        if (thisBoard[5]==0) {
                nodesChecked++;
                if (((pieceToPut & thisBoard[4] & thisBoard[6] & thisBoard[7])!=0) ||
                        ((pieceToPut & thisBoard[1] & thisBoard[9] & thisBoard[13])!=0) ||
                        ((pieceToPut & thisBoard[0] & thisBoard[10] & thisBoard[15])!=0)){
                        if (Level == CurrentDepth){
                           bestMove[0] = 5;
                           bestMove[1] = EMPTY;
                        }
                        return 1;
                }       
        }
        if (thisBoard[6]==0) {
                nodesChecked++;
                if (((pieceToPut & thisBoard[4] & thisBoard[5] & thisBoard[7])!=0) ||
                        ((pieceToPut & thisBoard[2] & thisBoard[10] & thisBoard[14])!=0) ||
                        ((pieceToPut & thisBoard[3] & thisBoard[9] & thisBoard[12])!=0)){
                        if (Level == CurrentDepth){
                           bestMove[0] = 6;
                           bestMove[1] = EMPTY;
                        }
                        return 1;
                }       
        }
        if (thisBoard[7]==0) {
                nodesChecked++;
                if (((pieceToPut & thisBoard[4] & thisBoard[5] & thisBoard[6])!=0) ||
                        ((pieceToPut & thisBoard[3] & thisBoard[11] & thisBoard[15])!=0)){
                        if (Level == CurrentDepth){
                           bestMove[0] = 7;
                           bestMove[1] = EMPTY;
                        }
                        return 1;
                }       
        }
        if (thisBoard[8]==0) {
                nodesChecked++;
                if (((pieceToPut & thisBoard[9] & thisBoard[10] & thisBoard[11])!=0) ||
                        ((pieceToPut & thisBoard[0] & thisBoard[4] & thisBoard[12])!=0)){
                        if (Level == CurrentDepth){
                           bestMove[0] = 8;
                           bestMove[1] = EMPTY;
                        }
                        return 1;
                }       
        }
        if (thisBoard[9]==0) {
                nodesChecked++;
                if (((pieceToPut & thisBoard[8] & thisBoard[10] & thisBoard[11])!=0) ||
                        ((pieceToPut & thisBoard[1] & thisBoard[5] & thisBoard[13])!=0) ||
                        ((pieceToPut & thisBoard[3] & thisBoard[6] & thisBoard[12])!=0)){
                        if (Level == CurrentDepth){
                           bestMove[0] = 9;
                           bestMove[1] = EMPTY;
                        }
                        return 1;
                }       
        }
        if (thisBoard[10]==0) {
                nodesChecked++;
                if (((pieceToPut & thisBoard[8] & thisBoard[9] & thisBoard[11])!=0) ||
                        ((pieceToPut & thisBoard[2] & thisBoard[6] & thisBoard[14])!=0) ||
                        ((pieceToPut & thisBoard[0] & thisBoard[5] & thisBoard[15])!=0)){
                        if (Level == CurrentDepth){
                           bestMove[0] = 10;
                           bestMove[1] = EMPTY;
                        }
                        return 1;
                }       
        }
        if (thisBoard[11]==0) {
                nodesChecked++;
                if (((pieceToPut & thisBoard[8] & thisBoard[9] & thisBoard[10])!=0) ||
                        ((pieceToPut & thisBoard[3] & thisBoard[7] & thisBoard[15])!=0)){
                        if (Level == CurrentDepth){
                           bestMove[0] = 11;
                           bestMove[1] = EMPTY;
                        }
                        return 1;
                }       
        }
        if (thisBoard[12]==0) {
                nodesChecked++;
                if (((pieceToPut & thisBoard[13] & thisBoard[14] & thisBoard[15])!=0) ||
                        ((pieceToPut & thisBoard[0] & thisBoard[4] & thisBoard[8])!=0) ||
                        ((pieceToPut & thisBoard[3] & thisBoard[6] & thisBoard[9])!=0)){
                        if (Level == CurrentDepth){
                           bestMove[0] = 12;
                           bestMove[1] = EMPTY;
                        }
                        return 1;
                }       
        }
        if (thisBoard[13]==0) {
                nodesChecked++;
                if (((pieceToPut & thisBoard[12] & thisBoard[14] & thisBoard[15])!=0) ||
                        ((pieceToPut & thisBoard[1] & thisBoard[5] & thisBoard[9])!=0)){
                        if (Level == CurrentDepth){
                           bestMove[0] = 13;
                           bestMove[1] = EMPTY;
                        }
                        return 1;
                }       
        }
        if (thisBoard[14]==0) {
                nodesChecked++;
                if (((pieceToPut & thisBoard[12] & thisBoard[13] & thisBoard[15])!=0) ||
                        ((pieceToPut & thisBoard[2] & thisBoard[6] & thisBoard[10])!=0)){
                        if (Level == CurrentDepth){
                           bestMove[0] = 14;
                           bestMove[1] = EMPTY;
                        }
                        return 1;
                }       
        }
        if (thisBoard[15]==0) {
                nodesChecked++;
                if (((pieceToPut & thisBoard[12] & thisBoard[13] & thisBoard[14])!=0) ||
                        ((pieceToPut & thisBoard[3] & thisBoard[7] & thisBoard[11])!=0) ||
                        ((pieceToPut & thisBoard[0] & thisBoard[5] & thisBoard[10])!=0)){
                        if (Level == CurrentDepth){
                           bestMove[0] = 15;
                           bestMove[1] = EMPTY;
                        }
                        return 1;
                }       
                
        }

/***** TOT HIER EVAL ***/


    for(i=0;(i < 16);i++){
       if (thisBoard[i] == EMPTY){
          thisBoard[i] = pieceToPut;
          for(k = 0;(k < 16); k++){
              if (piecesAvailable[k]){
                     pieceToPut = to256[k];
                     nodesChecked++;
                     if (Level == 1){
                     
/* HIER START EVAL */   
        res = -1; 
        test = 1;
        while (test){                 
          if (thisBoard[0]==0) {
                nodesChecked++;
                if (((pieceToPut & thisBoard[1] & thisBoard[2] & thisBoard[3])!=0) ||
                        ((pieceToPut & thisBoard[4] & thisBoard[8] & thisBoard[12])!=0) ||
                        ((pieceToPut & thisBoard[5] & thisBoard[10] & thisBoard[15])!=0)){
                        res = 0;
                        test=0; continue;
                }       
          }
          if (thisBoard[1]==0) {
                nodesChecked++;
                if (((pieceToPut & thisBoard[0] & thisBoard[2] & thisBoard[3])!=0) ||
                        ((pieceToPut & thisBoard[5] & thisBoard[9] & thisBoard[13])!=0)){
                        res = 0;
                        test=0; continue;
                }       
          }
          if (thisBoard[2]==0) {
                nodesChecked++;
                if (((pieceToPut & thisBoard[0] & thisBoard[1] & thisBoard[3])!=0) ||
                        ((pieceToPut & thisBoard[6] & thisBoard[10] & thisBoard[14])!=0)){
                        res = 0;
                        test=0; continue;
                }       
          }
          if (thisBoard[3]==0) {
                nodesChecked++;
                if (((pieceToPut & thisBoard[0] & thisBoard[1] & thisBoard[2])!=0) ||
                        ((pieceToPut & thisBoard[7] & thisBoard[11] & thisBoard[15])!=0) ||
                        ((pieceToPut & thisBoard[6] & thisBoard[9] & thisBoard[12])!=0)){
                        res = 0;
                        test=0; continue;
                }       
          }
          if (thisBoard[4]==0) {
                nodesChecked++;
                if (((pieceToPut & thisBoard[5] & thisBoard[6] & thisBoard[7])!=0) ||
                        ((pieceToPut & thisBoard[0] & thisBoard[8] & thisBoard[12])!=0)){
                        res = 0;
                        test=0; continue;
                }       
          }
          if (thisBoard[5]==0) {
                nodesChecked++;
                if (((pieceToPut & thisBoard[4] & thisBoard[6] & thisBoard[7])!=0) ||
                        ((pieceToPut & thisBoard[1] & thisBoard[9] & thisBoard[13])!=0) ||
                        ((pieceToPut & thisBoard[0] & thisBoard[10] & thisBoard[15])!=0)){
                        res = 0;
                        test=0; continue;
                }       
          }
          if (thisBoard[6]==0) {
                nodesChecked++;
                if (((pieceToPut & thisBoard[4] & thisBoard[5] & thisBoard[7])!=0) ||
                        ((pieceToPut & thisBoard[2] & thisBoard[10] & thisBoard[14])!=0) ||
                        ((pieceToPut & thisBoard[3] & thisBoard[9] & thisBoard[12])!=0)){
                        res = 0;
                        test=0; continue;
                }       
          }
          if (thisBoard[7]==0) {
                nodesChecked++;
                if (((pieceToPut & thisBoard[4] & thisBoard[5] & thisBoard[6])!=0) ||
                        ((pieceToPut & thisBoard[3] & thisBoard[11] & thisBoard[15])!=0)){
                        res = 0;
                        test=0; continue;
                }       
          }
          if (thisBoard[8]==0) {
                nodesChecked++;
                if (((pieceToPut & thisBoard[9] & thisBoard[10] & thisBoard[11])!=0) ||
                        ((pieceToPut & thisBoard[0] & thisBoard[4] & thisBoard[12])!=0)){
                        res = 0;
                        test=0; continue;
                }       
          }
          if (thisBoard[9]==0) {
                nodesChecked++;
                if (((pieceToPut & thisBoard[8] & thisBoard[10] & thisBoard[11])!=0) ||
                        ((pieceToPut & thisBoard[1] & thisBoard[5] & thisBoard[13])!=0) ||
                        ((pieceToPut & thisBoard[3] & thisBoard[6] & thisBoard[12])!=0)){
                        res = 0;
                        test=0; continue;
                }       
          }
          if (thisBoard[10]==0) {
                nodesChecked++;
                if (((pieceToPut & thisBoard[8] & thisBoard[9] & thisBoard[11])!=0) ||
                        ((pieceToPut & thisBoard[2] & thisBoard[6] & thisBoard[14])!=0) ||
                        ((pieceToPut & thisBoard[0] & thisBoard[5] & thisBoard[15])!=0)){
                        res = 0;
                        test=0; continue;
                }       
          }
          if (thisBoard[11]==0) {
                nodesChecked++;
                if (((pieceToPut & thisBoard[8] & thisBoard[9] & thisBoard[10])!=0) ||
                        ((pieceToPut & thisBoard[3] & thisBoard[7] & thisBoard[15])!=0)){
                        res = 0;
                        test=0; continue;
                }       
          }
          if (thisBoard[12]==0) {
                nodesChecked++;
                if (((pieceToPut & thisBoard[13] & thisBoard[14] & thisBoard[15])!=0) ||
                        ((pieceToPut & thisBoard[0] & thisBoard[4] & thisBoard[8])!=0) ||
                        ((pieceToPut & thisBoard[3] & thisBoard[6] & thisBoard[9])!=0)){
                        res = 0;
                        test=0; continue;
                }       
          }
          if (thisBoard[13]==0) {
                nodesChecked++;
                if (((pieceToPut & thisBoard[12] & thisBoard[14] & thisBoard[15])!=0) ||
                        ((pieceToPut & thisBoard[1] & thisBoard[5] & thisBoard[9])!=0)){
                        res = 0;
                        test=0; continue;
                }       
          }
          if (thisBoard[14]==0) {
                nodesChecked++;
                if (((pieceToPut & thisBoard[12] & thisBoard[13] & thisBoard[15])!=0) ||
                        ((pieceToPut & thisBoard[2] & thisBoard[6] & thisBoard[10])!=0)){
                        res = 0;
                        test=0; continue;
                }       
          }
          if (thisBoard[15]==0) {
                nodesChecked++;
                if (((pieceToPut & thisBoard[12] & thisBoard[13] & thisBoard[14])!=0) ||
                        ((pieceToPut & thisBoard[3] & thisBoard[7] & thisBoard[11])!=0) ||
                        ((pieceToPut & thisBoard[0] & thisBoard[5] & thisBoard[10])!=0)){
                        res = 0;
                        test=0; continue;
                }       
                
          }                     
          test=0; continue;
        }
                        
/* TO HIER EVAL : res = canMakeQuarto(pieceToPut) */
                       
                     if (res == -1){ 
                          if (0 > alfa){
                            if (Level == CurrentDepth){
                              bestMove[0] = i;
                              bestMove[1] = pieceToPut;
                            }
                            alfa = 0;
                          }
                          if (0 >= beta){
                            pieceToPut    = thisBoard[i];    
                            thisBoard[i]       = EMPTY;
                            return 0;
                          }
                        }
                     }
                     else{
                        piecesAvailable[k] = 0;
                        res = 0 - minimax(Level - 1, 0 - beta, 0 - alfa);
                        if (Level == CurrentDepth){
/*                            printf("Positie: " + i + " Piece: " + k + " res: " + res); */
                        }
                        if (res > alfa){
                           if (Level == CurrentDepth){
                              bestMove[0] = i;
                              bestMove[1] = pieceToPut;
                           }
                           alfa = res;
                        }
                        piecesAvailable[k] = 1;
                        if (alfa >= beta){ 
                           pieceToPut = thisBoard[i];    
                           thisBoard[i]    = EMPTY;

          if (Level == CurrentDepth){
             printf("(%d,%d)", alfa, beta);
             printf(".( %d ) cut\n" ,nodesChecked);
          }
          if (Level == CurrentDepth-1){
             printf("(%d,%d)", alfa, beta);
             printf(".   .( %d, %d ) cut\n" ,nodesChecked, res);
          }
          if (Level == CurrentDepth-2){
             printf("(%d,%d)", alfa, beta);
             printf(".   .   .( %d, %d ) cut\n" ,nodesChecked, res);
          }

                           return alfa;
                        }
                     }
              }
          }
          pieceToPut = thisBoard[i];    
          thisBoard[i]    = EMPTY;
          if (Level == CurrentDepth){
             printf("(%d,%d)", alfa, beta);
             printf(".( %d )\n" ,nodesChecked);
          }
          if (Level == CurrentDepth-1){
             printf("(%d,%d)", alfa, beta);
             printf(".   .( %d, %d )\n" ,nodesChecked, res);
          }
          if (Level == CurrentDepth-2){
             printf("(%d,%d)", alfa, beta);
             printf(".   .   .( %d, %d )\n" ,nodesChecked, res);
          }
       }
    }
    return alfa;
  }

/*
  public void Think () {
     int i, k, res, res2, orDepth, orNodesChecked;
     bestMove[0] = -99;
     bestMove[1] = -99;
     
     if (pieceToPut == EMPTY){
       if (nrMovesDone != 0) bC.curString = "Choose a piece to play first ...";
       else{
          thinking = 1;
          pieceToPut = chooseRandomPiece();
          piecesAvailable[to16[pieceToPut]] = 0;
          thinking = 0;
       }   
     }
     else{
       thinking = 1;
       CalculateDepth();
        res = 0;
       boolean found = 0;
       if (nrMovesDone == 15) {
          for (i = 0; (i < 16) && !found;i++){
              if (thisBoard[i] == EMPTY) {
                 doMove(i,EMPTY);
                 found = 1;
              }
          }
          bC.curString="Nodes: " + nodesChecked + " (Depth: " + (CurrentDepth + 1) +" Res: " + eval() +")";
       }
       else{   
          nodesChecked = 0;
          printf("Thinking on depth %d : , " CurrentDepth+1);
          long t = System.currentTimeMillis();
          res = minimax(CurrentDepth, -1, 1);
          t = System.currentTimeMillis() - t;
          printf("/n");
          printf("Calculated %d nodes. This took: %d millisecs or %d secs", nodesChecked, t,t/1000);
          orDepth = CurrentDepth;
          orNodesChecked = nodesChecked;
          if (res < 0){
             res2 = -1;
             while ((res2 < 0) && (CurrentDepth > 2)){
               CurrentDepth = CurrentDepth - 1;
               printf("/n");
               printf("Retrying on depth " + CurrentDepth +": ");
               res2 = minimax(CurrentDepth, -1, 1);
               orNodesChecked += nodesChecked;
             }
             if (bestMove[1] == EMPTY) bestMove[1] = chooseRandomPiece();
             doMove(bestMove[0], bestMove[1]);
             bC.curString="Nodes: " + orNodesChecked + " (Depth: " + (orDepth+1) +" Res: " + res +" New res: " + res2 + ")";
          }
          else{           
             if (bestMove[1] == EMPTY) bestMove[1] = chooseRandomPiece();
             doMove(bestMove[0], bestMove[1]);
             bC.curString="Nodes: " + nodesChecked + " (Depth: " + (CurrentDepth + 1) +" Res: " + res +")";
          }
       }
       if (nrMovesDone == 16) {
          bC.curString = "Draw. Nice Game";
       }
       if (eval() >= 1) bC.curString = "Quarto ! Damn I'm good !";
       printf("/n");
       thinking = 0;
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
       return 0;
    }
    else bC.curString = "";
    if (thisBoard[x] == EMPTY){
       thisBoard[x] = pieceToPut;
       pieceToPut = pieceToGive;
       if (pieceToPut != EMPTY) piecesAvailable[to16[pieceToPut]] = 0;
       if (pieceToGive != EMPTY) piecesAvailable[to16[pieceToGive]] = 0;
       MovesDone[nrMovesDone][0] = x;
       MovesDone[nrMovesDone][1] = pieceToPut;
       MovesDone[nrMovesDone][2] = pieceToGive;
       nrMovesDone++;
       return 1;
    }
    else return 0;
  }
  
*/  

 void CalculateDepth() {
       CurrentDepth = Depth;
       if (nrMovesDone >= 8) CurrentDepth = 8;
       if (CurrentDepth > (15 - nrMovesDone)) CurrentDepth = (15 - nrMovesDone);
  }
  


void main() {
   int res;
   int fakeDepth;
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
   Load();
   
   Depth=15;
   CurrentDepth=15;
   CalculateDepth();
   
   fakeDepth= CurrentDepth + 1;
   nodesChecked = 0;
   res = 0;
   printf("\n\n\n Thinking on depth %d :\n", fakeDepth);
   res=minimax(CurrentDepth, 0, 1);
   printf("\n\n\n Best move position: %d piece: %d \n", bestMove[0], to16[bestMove[1]]);
   printf("Result : %d\n",res);
   Save();
   printf("\n");
}
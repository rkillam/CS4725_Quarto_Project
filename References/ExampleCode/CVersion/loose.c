                  include <stdio.h>

                    define EMPTY 0
  int to256[] = { 85,  86,  89,  90, 101, 102, 105, 106,
                  149, 150, 153, 154, 165, 166, 169, 170};

   int 	to16[171];
   int  piecesAvailable[16]={1,1,1,1, 1,1,1,1, 1,1,1,1, 1,1,1,1};  /* Boolean */
   int  pieceToPut;
   int  thisBoard[16]={0,0,0,0, 0,0,0,0, 0,0,0,0, 0,0,0,0};
   int  nodesChecked;
   int  nrMovesDone;
   int  Depth;
   int  CurrentDepth;
   int  bestMove[2];


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
     fprintf(out, "%d", pieceToPut);
     printf("Put: %d\n", pieceToPut);
     fclose(out);
} 

void PrintBoard(){
     int i ;

     for(i = 0;i<16;i++){
        printf("%d ", thisBoard[i]);
     }
     printf("Put: %d\n", pieceToPut);
} 

int minimax (int Level, int alfa, int beta){
    int res;
    int i, k, test;

    /* BEGIN EVAL */
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



    for(i=0;(i < 16);i++){
       if (thisBoard[i] == EMPTY){
          thisBoard[i] = pieceToPut;
          for(k = 0;(k < 16); k++){
              if (piecesAvailable[k]){
                     pieceToPut = to256[k];
                     if (Level == 1){
                        /* HIER START EVAL */   
                        res = -1; 
                        test = 1;
                        while (test){                 
                          if (thisBoard[0]==0) {
                            nodesChecked++;
                            if (((this.pieceToPut & thisBoard[1] & thisBoard[2] & thisBoard[3])!=0) ||
                                ((this.pieceToPut & thisBoard[4] & thisBoard[8] & thisBoard[12])!=0) ||
                                ((this.pieceToPut & thisBoard[5] & thisBoard[10] & thisBoard[15])!=0)){
                                    res = 0;
                                    test=0;continue;
                            }       
                          }
                          if (thisBoard[1]==0) {
                            nodesChecked++;
                            if (((this.pieceToPut & thisBoard[0] & thisBoard[2] & thisBoard[3])!=0) ||
                                ((this.pieceToPut & thisBoard[5] & thisBoard[9] & thisBoard[13])!=0)){
                                    res = 0;
                                    test=0;continue;
                            }       
                          }
                          if (thisBoard[2]==0) {
                            nodesChecked++;
                            if (((this.pieceToPut & thisBoard[0] & thisBoard[1] & thisBoard[3])!=0) ||
                                ((this.pieceToPut & thisBoard[6] & thisBoard[10] & thisBoard[14])!=0)){
                                   res = 0;
                                   test=0;continue;
                            }       
                          }
                          if (thisBoard[3]==0) {
                            nodesChecked++;
                            if (((this.pieceToPut & thisBoard[0] & thisBoard[1] & thisBoard[2])!=0) ||
                                ((this.pieceToPut & thisBoard[7] & thisBoard[11] & thisBoard[15])!=0) ||
                                ((this.pieceToPut & thisBoard[6] & thisBoard[9] & thisBoard[12])!=0)){
                                   res = 0;
                                   test=0;continue;
                            }       
                          }
                          if (thisBoard[4]==0) {
                            nodesChecked++;
                            if (((this.pieceToPut & thisBoard[5] & thisBoard[6] & thisBoard[7])!=0) ||
                                ((this.pieceToPut & thisBoard[0] & thisBoard[8] & thisBoard[12])!=0)){
                                   res = 0;
                                   test=0;continue;
                            }       
                          }
                          if (thisBoard[5]==0) {
                            nodesChecked++;
                            if (((this.pieceToPut & thisBoard[4] & thisBoard[6] & thisBoard[7])!=0) ||
                                ((this.pieceToPut & thisBoard[1] & thisBoard[9] & thisBoard[13])!=0) ||
                                ((this.pieceToPut & thisBoard[0] & thisBoard[10] & thisBoard[15])!=0)){
                                   res = 0;
                                   test=0;continue;
                            }       
                          }
                          if (thisBoard[6]==0) {
                            nodesChecked++;
                            if (((this.pieceToPut & thisBoard[4] & thisBoard[5] & thisBoard[7])!=0) ||
                                ((this.pieceToPut & thisBoard[2] & thisBoard[10] & thisBoard[14])!=0) ||
                                ((this.pieceToPut & thisBoard[3] & thisBoard[9] & thisBoard[12])!=0)){
                                   res = 0;
                                   test=0;continue;
                            }       
                          }
                          if (thisBoard[7]==0) {
                            nodesChecked++;
                            if (((this.pieceToPut & thisBoard[4] & thisBoard[5] & thisBoard[6])!=0) ||
                                ((this.pieceToPut & thisBoard[3] & thisBoard[11] & thisBoard[15])!=0)){
                                   res = 0;
                                   test=0;continue;
                            }       
                          }
                          if (thisBoard[8]==0) {
                            nodesChecked++;
                            if (((this.pieceToPut & thisBoard[9] & thisBoard[10] & thisBoard[11])!=0) ||
                                ((this.pieceToPut & thisBoard[0] & thisBoard[4] & thisBoard[12])!=0)){
                                   res = 0;
                                   test=0;continue;
                            }       
                          }
                          if (thisBoard[9]==0) {
                            nodesChecked++;
                            if (((this.pieceToPut & thisBoard[8] & thisBoard[10] & thisBoard[11])!=0) ||
                                ((this.pieceToPut & thisBoard[1] & thisBoard[5] & thisBoard[13])!=0) ||
                                ((this.pieceToPut & thisBoard[3] & thisBoard[6] & thisBoard[12])!=0)){
                                   res = 0;
                                   test=0;continue;
                            }       
                          }
                          if (thisBoard[10]==0) {
                            nodesChecked++;
                            if (((this.pieceToPut & thisBoard[8] & thisBoard[9] & thisBoard[11])!=0) ||
                                ((this.pieceToPut & thisBoard[2] & thisBoard[6] & thisBoard[14])!=0) ||
                                ((this.pieceToPut & thisBoard[0] & thisBoard[5] & thisBoard[15])!=0)){
                                   res = 0;
                                   test=0;continue;
                            }       
                          }
                          if (thisBoard[11]==0) {
                            nodesChecked++;
                            if (((this.pieceToPut & thisBoard[8] & thisBoard[9] & thisBoard[10])!=0) ||
                                ((this.pieceToPut & thisBoard[3] & thisBoard[7] & thisBoard[15])!=0)){
                                   res = 0;
                                   test=0;continue;
                            }       
                          }
                          if (thisBoard[12]==0) {
                            nodesChecked++;
                            if (((this.pieceToPut & thisBoard[13] & thisBoard[14] & thisBoard[15])!=0) ||
                                ((this.pieceToPut & thisBoard[0] & thisBoard[4] & thisBoard[8])!=0) ||
                                ((this.pieceToPut & thisBoard[3] & thisBoard[6] & thisBoard[9])!=0)){
                                   res = 0;
                                   test=0;continue;
                            }       
                          }
                          if (thisBoard[13]==0) {
                            nodesChecked++;
                            if (((this.pieceToPut & thisBoard[12] & thisBoard[14] & thisBoard[15])!=0) ||
                                ((this.pieceToPut & thisBoard[1] & thisBoard[5] & thisBoard[9])!=0)){
                                   res = 0;
                                   test=0;continue;
                            }       
                          }
                          if (thisBoard[14]==0) {
                            nodesChecked++;
                            if (((this.pieceToPut & thisBoard[12] & thisBoard[13] & thisBoard[15])!=0) ||
                                ((this.pieceToPut & thisBoard[2] & thisBoard[6] & thisBoard[10])!=0)){
                                   res = 0;
                                   test=0;continue;
                            }       
                          }
                          if (thisBoard[15]==0) {
                            nodesChecked++;
                            if (((this.pieceToPut & thisBoard[12] & thisBoard[13] & thisBoard[14])!=0) ||
                                ((this.pieceToPut & thisBoard[3] & thisBoard[7] & thisBoard[11])!=0) ||
                                ((this.pieceToPut & thisBoard[0] & thisBoard[5] & thisBoard[10])!=0)){
                                   res = 0;
                                   test=0;continue;
                            }       
                          }                     
                          test=0;continue;
                        }
                        /* END OF EVAL */

                     }
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
                        if (res > alfa){
                           if (Level == CurrentDepth){
                              bestMove[0] = i;
                              bestMove[1] = pieceToPut;
                           }
                           alfa = res;
                        }
                        piecesAvailable[k] = 1;
                        if (alfa >= beta){ 
                           pieceToPut   = thisBoard[i];    
                           thisBoard[i] = EMPTY;
                           /* ********************* */
                           /* DEBUGGING INFORMATION */
                           /* ********************* */
                            if (Level == CurrentDepth){
                               printf("(%2d,%2d)", alfa, beta);
                               printf("*.( %d ) cut\n" ,nodesChecked);
                            }
                            if (Level == CurrentDepth-1){
                               printf("(%2d,%2d)", alfa, beta);
                               printf(".   .     ( %d, %d ) cut\n" ,nodesChecked, res);
                            }
                            if (Level == CurrentDepth-2){
                               if (nodesChecked < 0 && twist==0) { twistcount++; twist = 1; }
                               if (nodesChecked > 0 && twist==1) { twistcount++; twist = 0; }
                               printf("(%2d,%2d)", alfa, beta);
                               printf(".   .   .                            ( %d, %d, %d ) cut\n" ,twistcount ,nodesChecked, res);
                            }
                            return alfa;
                           /* ************************* */
                           /* END DEBUGGING INFORMATION */
                           /* ************************* */
                        }
                        else{
                           /* ********************* */
                           /* DEBUGGING INFORMATION */
                           /* ********************* */
                            if (Level == CurrentDepth){
                               printf("(%2d,%2d)", alfa, beta);
                               printf("*.( %2d, %2d) no cut\n" ,nodesChecked, res);
                            }
                            if (Level == CurrentDepth-1){
                               printf("(%2d,%2d)", alfa, beta);
                               printf(".   .     ( %d, %d ) no cut\n" ,nodesChecked, res);
                            }
                            if (Level == CurrentDepth-2){
                               if (nodesChecked < 0 && twist==0) { twistcount++; twist = 1; }
                               if (nodesChecked > 0 && twist==1) { twistcount++; twist = 0; }
                               printf("(%2d,%2d)", alfa, beta);
                               printf(".   .   .                             ( %d, %d, %d ) no cut\n", twistcount ,nodesChecked, res);
                            }
                           /* ************************* */
                           /* END DEBUGGING INFORMATION */
                           /* ************************* */
                        }
                     }
              }
          }
          pieceToPut = thisBoard[i];    
          thisBoard[i]    = EMPTY;

          /* ********************* */
          /* DEBUGGING INFORMATION */
          /* ********************* */
          if (Level == CurrentDepth){
             printf("(%2d,%2d)", alfa, beta);
             printf("*.( %d, %d)\n" ,nodesChecked, res);
          }
          if (Level == CurrentDepth-1){
             printf("(%2d,%2d)", alfa, beta);
             printf(".   .     ( %d, %d )\n" ,nodesChecked, res);
          }
          if (Level == CurrentDepth-2){
             if (nodesChecked < 0 && twist==0) { twistcount++; twist = 1; }
             if (nodesChecked > 0 && twist==1) { twistcount++; twist = 0; }
             printf("(%2d,%2d)", alfa, beta);
             printf(".   .   .                             ( %d, %d, %d )\n", twistcount,nodesChecked, res);
          }
          /* ************************* */
          /* END DEBUGGING INFORMATION */
          /* ************************* */
       }
    }
    return alfa;
  }

 void CalculateDepth() {
       CurrentDepth = Depth;
       if (nrMovesDone >= 8) CurrentDepth = 8;
       if (CurrentDepth > (15 - nrMovesDone)) CurrentDepth = (15 - nrMovesDone);
       if (CurrentDepth % 2 == 0) CurrentDepth--;
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

   printf("\n\n\n Loading .... \n");
   Load();
   
   printf("\n\n\n Calculating Depth .... \n");
   Depth=15;
   CurrentDepth=15;
   CalculateDepth();
   
   fakeDepth= CurrentDepth + 1;
   nodesChecked = 0;
   res = 0;
   printf("\n\n\n Thinking on depth %d :\n", fakeDepth);
   res=minimax(CurrentDepth, -1, 1);
   printf("\n\n\n Best move position: %d piece: %d \n", bestMove[0], to16[bestMove[1]]);
   printf("Result : %d\n",res);
   printf("\n\n\n Saving .... \n");
   Save();
   printf("Done.\n");
}
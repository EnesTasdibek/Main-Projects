#include <ctype.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <stdbool.h>
#include <time.h>
#include <unistd.h>
#include <arpa/inet.h>
#include <errno.h>
#include <netinet/in.h>
#include <netdb.h>
#include <netinet/tcp.h>

#define PORT "1024"
#define BACKLOG 5


struct Move {
    int number;
    int number2;
    char letter;
    char letter2;
    //check if it went to next question
    bool wentNext;
    bool DONE;
    int i;
    int j;
    bool finish;
};

 enum shipType{
    CARRIER = 5,//5
    BATTLESHIP = 4,//4
    CRUISER =3,//3
    SUBMARINE = 2,//
    DESTROYER = 1,//1
    NO_SHIP =0,
    destroyed =-1

};

enum trackFire{
    HIT,
    MISS,
    NO_TRY
};

//there are two modes,
//1 for initialization(questions set up)
//2 for singleplayer
//3 for multiplayer
//4 for client(still multiplayer but little but advanced)(3,4 are same)
//I change it from getDisplay
//start with mode 1(initialization)
int mode = 1;
bool finish = false;
bool ifServerDONE = false;
bool ifClientDONE = false;

bool ServerTURN =false;
//we start with client
bool ClientTURN =true;
//check whether someone won, then go to teardown
volatile bool someoneWon =false;

int rows = 10;
int col = 10;
enum shipType **myMatrix;
enum shipType **computerMatrix;
enum trackFire **hitmissgrid;
enum trackFire **hitmissgridCOMPUTER;

enum shipType **serverMatrix;
enum shipType **clientMatrix;
enum trackFire **hitmissgridServer;
enum trackFire **hitmissgridClient;

int questionCount =5;

//forward declaration
int movetonext(bool state);
void TeardownSinglePlayer();
void SetupSinglePlayer();
int MakeSinglePlayerShot();
int GetSinglePlayerShot();
void SinglePlayerResponse();
int GetSinglePlayerResponse();
bool SinglePlayerDidWin(enum shipType **matrix);
int updateNextState();
void SetupMultiPlayer();
void TeardownMultiPlayer(enum shipType **normalMatrix, enum trackFire **hitMissGrid);

void  displayhitmissgridComputer() {

    // Update DisplayWorld() – print out the hit/miss board and the ship grid status (you should have ½ of this code already!).

    //col numbers
    printf("COMPUTER'S HIT/MISS GRID, (DEBUG PURPOSES ONLY)\n");
    for (int n = 0; n <= 9; n++) {

        printf("    %d", n);
    }
    printf("\n");

    //row letters
    for (int i =0; i < rows; i++) {

        printf("%c", 'A' + i);
        //printing ships
        for (int j = 0; j < col; j++) {
            printf("   ");
            switch (hitmissgridCOMPUTER[i][j]) {
                case HIT:
                    printf("HT");
                    break;
                case MISS:
                    printf("MS");
                    break;
                default:
                    printf("..");
                    break;
            }
        }
        printf("\n");

    }

}
void displayhitmissgrid() {

    //col numbers
    printf("MY HIT/MISS GRID\n");
    for (int n = 0; n <= 9; n++) {

        printf("    %d", n);
    }
    printf("\n");

    //row letters
    for (int i =0; i < rows; i++) {

        printf("%c", 'A' + i);
        //printing ships
        for (int j = 0; j < col; j++) {
            printf("   ");
            switch (hitmissgrid[i][j]) {
                case HIT:
                    printf("HT");
                    break;
                case MISS:
                    printf("MS");
                    break;
                default:
                    printf("..");
                    break;
            }
        }
        printf("\n");
    }

}

//this displays hitmissgrid of anyone
void displayHITORMISS(enum trackFire **matrix) {

if (someoneWon == false) {
    //col numbers
    printf("Updated HIT/MISS GRID\n");
    for (int n = 0; n <= 9; n++) {

        printf("    %d", n);
    }
    printf("\n");

    //row letters
    for (int i =0; i < rows; i++) {

        printf("%c", 'A' + i);
        //printing ships
        for (int j = 0; j < col; j++) {
            printf("   ");
            switch (matrix[i][j]) {
                case HIT:
                    printf("HT");
                    break;
                case MISS:
                    printf("MS");
                    break;
                default:
                    printf("..");
                    break;
            }
        }
        printf("\n");

    }
    }
    else {
        printf("GAME END\n");
    }



}


void getDisplay() {
    if (mode==1) {
        //col numbers
        for (int n = 0; n <= 9; n++) {

            printf("    %d", n);
        }
        printf("\n");

        //row letters
        for (int i =0; i < rows; i++) {

            printf("%c", 'A' + i);
            //printing ships
            for (int j = 0; j < col; j++) {
                printf("   ");
                switch (myMatrix[i][j]) {
                    case CARRIER:
                        printf("CV");
                        break;
                    case BATTLESHIP:
                        printf("BB");
                        break;
                    case CRUISER:
                        printf("CA");
                        break;
                    case SUBMARINE:
                        printf("SS");
                        break;
                    case DESTROYER:
                        printf("DD");
                        break;
                    default:
                        printf("..");
                        break;
                }
            }
            printf("\n");
        }
    }
    //if singleplayer
    if (mode==2) {

        displayhitmissgrid();
        displayhitmissgridComputer();
    }
    //I will add mode 3 for multiplayer for battleship4

    if (mode==3) {

        //displayhitmissgridServer();
        displayHITORMISS(hitmissgridServer);
        //col numbers
        printf("SERVER BATTLESHIP GRID\n");
        for (int n = 0; n <= 9; n++) {

            printf("    %d", n);
        }
        printf("\n");

        //row letters
        for (int i =0; i < rows; i++) {

            printf("%c", 'A' + i);
            //printing ships
            for (int j = 0; j < col; j++) {
                printf("   ");
                switch (serverMatrix[i][j]) {
                    case CARRIER:
                        printf("CV");
                        break;
                    case BATTLESHIP:
                        printf("BB");
                        break;
                    case CRUISER:
                        printf("CA");
                        break;
                    case SUBMARINE:
                        printf("SS");
                        break;
                    case DESTROYER:
                        printf("DD");
                        break;
                    default:
                        printf("..");
                        break;
                }
            }
            printf("\n");
        }
    }
    if (mode==4) {

        //displayhitmissgridServer();
        //displayhitmissgridClient();
        displayHITORMISS(hitmissgridClient);
        //col numbers
        printf("CLIENT BATTLESHIP GRID\n");
        for (int n = 0; n <= 9; n++) {

            printf("    %d", n);
        }
        printf("\n");

        //row letters
        for (int i =0; i < rows; i++) {

            printf("%c", 'A' + i);
            //printing ships
            for (int j = 0; j < col; j++) {
                printf("   ");
                switch (clientMatrix[i][j]) {
                    case CARRIER:
                        printf("CV");
                        break;
                    case BATTLESHIP:
                        printf("BB");
                        break;
                    case CRUISER:
                        printf("CA");
                        break;
                    case SUBMARINE:
                        printf("SS");
                        break;
                    case DESTROYER:
                        printf("DD");
                        break;
                    default:
                        printf("..");
                        break;
                }
            }
            printf("\n");
        }
    }


}


//Initialize both grids to “no ships” / “haven’t tried this spot yet”.
//Initialize – print out instructions, print the board
int Initialization(){

    //csi333 slide implementation
    //this also sets up the singleplayer
    if (mode==1) {

        myMatrix = (enum shipType **)malloc(rows * sizeof(enum shipType *));
        for (int i=0;i<rows;i++) {
            // fill pointers
            *(myMatrix+i) =  (enum shipType *)malloc(col *
            sizeof(enum shipType));
            //if memory allocation is faulty, exit
            if (*(myMatrix+i) == NULL) {
                exit(-1);
            }
            for (int j=0;j<col;j++) {
                myMatrix[i][j] = NO_SHIP;
            }
        }
        printf("Place your ships. The format for this is: AE4 (a carrier from A4-E4) or J37 (a carrier from J3-J7).\n");
        getDisplay();
    }
     if (mode==2) {
    SetupSinglePlayer();
    }
    //server should set the matrices only
    else if (mode==3 || mode==4) {
        SetupMultiPlayer();
        getDisplay();
    }

}
//TearDown – print that the board is complete. --DONE
int Teardown() {

    //freeing memory allocation by rows
    //csi333 slide implementation
    //int *pInt = NULL;
   // pInt = (int *)malloc(rows * sizeof(int));
    for (int i=0;i<rows;i++) {
        free(*(myMatrix+i));
        //int* pInt = myMatrix[i];
        //free(pInt);
    }
    free(myMatrix);

    TeardownSinglePlayer();
    printf("Memory successfully freed\n");
    //if server or client won
}

//forward declaration to be recognized by acceptInput
bool updateState(struct Move move);

 struct Move acceptInput() {
     
     //struct only returns if in mode 1,2 or3(initialization)
     if (mode ==1 || mode ==3|| mode ==4) {

         //initializing the struct variables
         struct Move line = {0,0};
         char str[100];

         while(fgets(str, sizeof(str), stdin) == NULL)
         {
             printf("Error, enter something");
         }
         //Here, made input uppercase by looping each value one by one
         for (int i = 0; i < strlen(str); i++) {
             str[i] = toupper(str[i]);
         }
         //if the input is column
         if (isalpha(str[0]) && isalpha(str[1]) && isdigit(str[2])) {
             char letter = str[0];
             char letter2 = str[1];
             char nbr= str[2];

             printf("So move is between : %c%c  and : %c%c \n", letter, nbr, letter2, nbr );


             line.letter = letter;
             line.letter2 = letter2;
             line.number = nbr;

             if (letter < 'A' || letter > 'J' || letter2 < 'A' || letter2 > 'J' || nbr < '0' || nbr > '9') {
                 printf("Error, input out of bounds");
             }
             //check if column letter2 not bigger than each first
             if((letter-'A') > (letter2-'A')){
                 printf("Ship is %d in size, but should be %d\n", ((letter-'A') - (letter2-'A'))+1,questionCount);
             }
             //strict substraction
             if (((letter2 -'A') - (letter-'A' -1)) != questionCount) {
                 line.wentNext = false;
                 movetonext(false);
                 return line;
             }
             line.wentNext = true;
             return line;
         }
         //using atoi to parse out the integer
         int number = atoi(str);
         char nbr1 = str[1];
         char nbr2 = str[2];

         //tokenize the number as nbr1 and nbr2 DONE
         if(nbr1 > nbr2){
             printf("Ship is -%d in size, but should be %d\n", ((nbr1-'0')-(nbr2-'0')),questionCount);
         }
         else if (number < 0 || number > 9 || str[0] < 'A'   ||  str[0] > 'J'  ){
             printf("Location specification is not in bounds \n");
         }
         else
         {
             printf("So move is between : %c%c  and : %c%c \n", str[0], nbr1, str[0], nbr2 );
             //saving to struct
             line.number = nbr1;
             line.number2 = nbr2;
             line.letter = str[0];
         }
         //strict substraction
         //for example, 7-2 =5 but in grid c72 takes 6 spots
         //same thing applied to column
         if (((nbr2 - '0' )-((nbr1-'0')-1)) != questionCount) {
             line.wentNext = false;
             movetonext(false);
             return line;
         }
         line.wentNext = true;
         return line;
     }
 }
//if true, decrement the question count
//else stays same, asking the same question until user corrects
 int movetonext(bool state) {

     if (state==true) {
         questionCount=questionCount-1;
         if (questionCount == 0) {
             return false;
         }
         printf("Please enter a location for a ship of %d squares: \n",  questionCount);
         return questionCount;
     }
     if (state==false){
         questionCount = questionCount;
         if (questionCount == 0) {
             return false;
         }
         printf("cannot move next, Please enter a location for a ship of %d squares: \n",  questionCount);
         return questionCount;
     }
 }

/*UpdateState – if the input was valid, update the grid and mark this ship as placed
Things to check:
    Input is in range (A-J, 0-9) --DONE
    Input doesn’t overwrite an existing ship --DONE
    Input is the right length for the ship that we are placing.--DONE
*/
bool updateState(struct Move move) {
     //add mode ==3 server
     //addf mode == 4 client
     //doing this because we need to store in their own matrices
     if (mode==4) {
            //transforming to int
         int row = move.letter - 'A';
         int start_col = move.number - '0';
         int end_col = move.number2 - '0';
         int end_row_col = move.letter2 - 'A';
         //for columns
         //letter row
         //letter2 _end_drow_col
         //number start_col
         //if cf4
         //c4 and f4
         //so 4th column to be populated
         //so number, which is start_col is operated
         //and letter2 -letter times populated or is the range

         //casting enum to int because question count = shipType
         enum shipType ship =  (enum shipType) questionCount;
         //IF COLUMN -- if letter2 is present in this case
         if (move.wentNext ==true){
             if (move.letter2 != 0) {
                 //example, 4 times populated starting from c
                 for (int j = row; j <= end_row_col; j++) {

                     if (clientMatrix[j][start_col] != NO_SHIP) {

                         printf("Error, Ship found at: %c\n", clientMatrix[j][start_col]);
                         //getDisplay();
                         printf("try again: \n");

                         move.wentNext = false;
                         return false;
                     }
                 }
                 for (int j = row; j <= end_row_col; j++) {
                     //where to populate
                     //4th column is the number which is start_col
                     clientMatrix[j][start_col] = ship;
                 }

                 return true;

             }
             //IF ROWS -- if number2 is present in this case
             else if (move.number2 != 0) {
                 //for rows
                 //letter, number, number2
                 //number to number 2 is range
                 //so letter stays the same, number differentiates
                 for (int j = start_col; j <= end_col; j++) {
                     //if coincides
                     if (clientMatrix[row][j] != NO_SHIP) {

                         printf("Ship already found at: %c\n", clientMatrix[row][j]);
                         //getDisplay();
                         printf("try again: \n");
                         move.wentNext = false;
                         movetonext(false);
                         return false;

                     }
                 }//populating row
                 for (int j = start_col; j <= end_col; j++) {
                     clientMatrix[row][j] = ship;
                 }

                 return true;

             }
             return false;

         }
         else if (move.wentNext == false) {
             movetonext(false);
             move.wentNext = false;
             getDisplay();
             return false;
         }


        // return true;
     }
     //servermatrix
     if (mode ==3) {

         //transforming to int
         int row = move.letter - 'A';
         int start_col = move.number - '0';
         int end_col = move.number2 - '0';
         int end_row_col = move.letter2 - 'A';
         //for columns
         //letter row
         //letter2 _end_drow_col
         //number start_col
         //if cf4
         //c4 and f4
         //so 4th column to be populated
         //so number, which is start_col is operated
         //and letter2 -letter times populated or is the range

         //casting enum to int because question count = shipType
         enum shipType ship =  (enum shipType) questionCount;
         //IF COLUMN -- if letter2 is present in this case
         if (move.wentNext ==true){
             if (move.letter2 != 0) {
                 //example, 4 times populated starting from c
                 for (int j = row; j <= end_row_col; j++) {

                     if (serverMatrix[j][start_col] != NO_SHIP) {

                         printf("Error, Ship found at: %c\n", serverMatrix[j][start_col]);
                         //getDisplay();
                         printf("try again: \n");

                         move.wentNext = false;
                         return false;
                     }
                 }
                 for (int j = row; j <= end_row_col; j++) {
                     //where to populate
                     //4th column is the number which is start_col
                     serverMatrix[j][start_col] = ship;
                 }

                 return true;

             }
             //IF ROWS -- if number2 is present in this case
             else if (move.number2 != 0) {
                 //for rows
                 //letter, number, number2
                 //number to number 2 is range
                 //so letter stays the same, number differentiates
                 for (int j = start_col; j <= end_col; j++) {
                     //if coincides
                     if (serverMatrix[row][j] != NO_SHIP) {

                         printf("Ship already found at: %c\n", serverMatrix[row][j]);
                         //getDisplay();
                         printf("try again: \n");
                         move.wentNext = false;
                         movetonext(false);
                         return false;

                     }
                 }//populating row
                 for (int j = start_col; j <= end_col; j++) {
                     serverMatrix[row][j] = ship;
                 }

                 return true;

             }
             return false;

         }
         else if (move.wentNext == false) {
             movetonext(false);
             move.wentNext = false;
             getDisplay();
             return false;
         }
        // return true;
     }

     if(mode==2) {
         //if somebody win, return false
         if (updateNextState()==false) {
             move.finish = true;
             finish =true;
             //mode =5;
             return false;
         }
     }

     if (mode ==1){

         //transforming to int
         int row = move.letter - 'A';
         int start_col = move.number - '0';
         int end_col = move.number2 - '0';
         int end_row_col = move.letter2 - 'A';
         //for columns
         //letter row
         //letter2 _end_drow_col
         //number start_col
         //if cf4
         //c4 and f4
         //so 4th column to be populated
         //so number, which is start_col is operated
         //and letter2 -letter times populated or is the range

         //casting enum to int because question count = shipType
         enum shipType ship =  (enum shipType) questionCount;
         //IF COLUMN -- if letter2 is present in this case
         if (move.wentNext ==true){
             if (move.letter2 != 0) {
                 //example, 4 times populated starting from c
                 for (int j = row; j <= end_row_col; j++) {

                     if (myMatrix[j][start_col] != NO_SHIP) {

                         printf("Error, Ship found at: %c\n", myMatrix[j][start_col]);
                         //getDisplay();
                         printf("try again: \n");

                         move.wentNext = false;
                         return false;
                     }
                 }
                 for (int j = row; j <= end_row_col; j++) {
                     //where to populate
                     //4th column is the number which is start_col
                     myMatrix[j][start_col] = ship;
                 }

                 return true;

             }
             //IF ROWS -- if number2 is present in this case
             else if (move.number2 != 0) {
                 //for rows
                 //letter, number, number2
                 //number to number 2 is range
                 //so letter stays the same, number differentiates
                 for (int j = start_col; j <= end_col; j++) {
                     //if coincides
                     if (myMatrix[row][j] != NO_SHIP) {

                         printf("Ship already found at: %c\n", myMatrix[row][j]);
                         //getDisplay();
                         printf("try again: \n");
                         move.wentNext = false;
                         movetonext(false);
                         return false;

                     }
                 }//populating row
                 for (int j = start_col; j <= end_col; j++) {
                     myMatrix[row][j] = ship;
                 }

                 return true;

             }
             return false;

         }
         else if (move.wentNext == false) {
             movetonext(false);
             move.wentNext = false;
             getDisplay();
             return false;
         }
     }
     return true;
 }
//display the state/WORLD
//DisplayWorld – print the board – call a function for this.
int displayState(bool state){
     if (state ==true) {
         getDisplay();

     }
     else if (state ==false) {
         return false;
     }
}

void SetupMultiPlayer() {
     
     hitmissgridServer = (enum trackFire **)malloc(rows * sizeof(enum trackFire *));
     for (int i=0;i<rows;i++) {
         // fill pointers
         *(hitmissgridServer+i) =  (enum trackFire *)malloc(col *
         sizeof(enum trackFire));
         //if memory allocation is faulty, exit
         if (*(hitmissgridServer+i) == NULL) {
             exit(-1);
         }
         for (int j=0;j<col;j++) {
             hitmissgridServer[i][j] = NO_TRY;
         }
     }

     //FOR client
     hitmissgridClient = (enum trackFire **)malloc(rows * sizeof(enum trackFire *));
     for (int i=0;i<rows;i++) {
         // fill pointers
         *(hitmissgridClient+i) =  (enum trackFire *)malloc(col *
         sizeof(enum trackFire));
         //if memory allocation is faulty, exit
         if (*(hitmissgridClient+i) == NULL) {
             exit(-1);
         }
         for (int j=0;j<col;j++) {
             hitmissgridClient[i][j] = NO_TRY;
         }
     }
     int j =0;
     serverMatrix = (enum shipType **)malloc(rows * sizeof(enum shipType *));
     for (int i=0;i<rows;i++) {
         // fill pointers
         *(serverMatrix+i) =  (enum shipType *)malloc(col *
         sizeof(enum shipType));
         //if memory allocation is faulty, exit
         if (*(serverMatrix+i) == NULL) {
             exit(-1);
         }
         for (j=0;j<col;j++) {
             serverMatrix[i][j] = NO_SHIP;
         }
     }

     int k =0;
     clientMatrix = (enum shipType **)malloc(rows * sizeof(enum shipType *));
     for (int i=0;i<rows;i++) {
         // fill pointers
         *(clientMatrix+i) =  (enum shipType *)malloc(col *
         sizeof(enum shipType));
         //if memory allocation is faulty, exit
         if (*(clientMatrix+i) == NULL) {
             exit(-1);
         }
         for (k=0;k<col;k++) {
             clientMatrix[i][k] = NO_SHIP;
         }
     }
 }

//Create a SetupSinglePlayer() function.
//It should allocate a new pair of grids (like we did last assignment),
//but it doesn’t need a loop or to accept any input. Instead, it should randomly place the ships.
// Some things to watch for –
// ships that go off the edge of the board or that overlap with other ships.
//Call this function from Initialization.
 void SetupSinglePlayer() {
//HITMISSGRID INITILIZAtion

     hitmissgrid = (enum trackFire **)malloc(rows * sizeof(enum trackFire *));
     for (int i=0;i<rows;i++) {
         // fill pointers
         *(hitmissgrid+i) =  (enum trackFire *)malloc(col *
         sizeof(enum trackFire));
         //if memory allocation is faulty, exit
         if (*(hitmissgrid+i) == NULL) {
             exit(-1);
         }
         for (int j=0;j<col;j++) {
             hitmissgrid[i][j] = NO_TRY;
         }

     }

//FOR COMPUTER
     hitmissgridCOMPUTER = (enum trackFire **)malloc(rows * sizeof(enum trackFire *));
     for (int i=0;i<rows;i++) {
         // fill pointers
         *(hitmissgridCOMPUTER+i) =  (enum trackFire *)malloc(col *
         sizeof(enum trackFire));
         //if memory allocation is faulty, exit
         if (*(hitmissgridCOMPUTER+i) == NULL) {
             exit(-1);
         }
         for (int j=0;j<col;j++) {
             hitmissgridCOMPUTER[i][j] = NO_TRY;
         }

     }

     srand(time(0));
     //int randomNumber;
     //csi333 slide implementation
     int j =0;
     int randomCol = 0;
     int randomRow = 0;
     int randomChoice = 0;
     computerMatrix = (enum shipType **)malloc(rows * sizeof(enum shipType *));
     for (int i=0;i<rows;i++) {
         // fill pointers
         *(computerMatrix+i) =  (enum shipType *)malloc(col *
         sizeof(enum shipType));
         //if memory allocation is faulty, exit
         if (*(computerMatrix+i) == NULL) {
             exit(-1);
         }
         for (j=0;j<col;j++) {
             computerMatrix[i][j] = NO_SHIP;
         }


     }
     //place ships randomly, do it for 5 times, for 5th allocate CV, for 4th allocate BB..

     //random
     //generate random numbers betweeen 1 and 2
     //switch for loops for rows and col

     //for cols with each being constant


     //randomFirsdt and second difference must be 5
     //generate  a random number using this condition
     //if this is range 5
     //random first needs to be at most 5 because addition of 6 would go out of bounds
     int randomFirst = rand() % 5;

     //then it shoudl select a random row or column

     randomCol =  rand() % 10;
     randomRow =  rand() % 10;

     //for CARRIER, this number will be
     //randomGenerated + 5 times to be populated (for columns in this case)

     //this decides the row or column population
     randomChoice =  rand() % 2;

     if (randomChoice ==0 ) {

         for (int i=randomFirst;i<randomFirst+5;i++) {

             computerMatrix[i][randomCol] = CARRIER;
         }
     }
     //randomGenerated + 5 times to be populated (for rows in this case)
     if (randomChoice == 1) {

         for (int i=randomFirst;i<randomFirst+5;i++) {
             computerMatrix[randomRow][i] = CARRIER;
         }

     }

     //so the next ship has to be
     //constrainted by this ship's indexes
     //to prevent overloading

//randomcol (or row) - randomFirst should be equal to ship size
     randomFirst = rand() % 6;
     randomCol =  rand() % 10;
     randomRow =  rand() % 10;
     randomChoice =  rand() % 2;

     if (randomChoice ==0 ) {
         //while (computerMatrix[randomRow][randomCol] != NO_SHIP) {

         ///this finds a free spot until there is no ship
             for (int i=randomFirst;i<randomFirst+4;i++) {
                 if(computerMatrix[i][randomCol] != NO_SHIP) {

                    while (computerMatrix[i][randomCol] != NO_SHIP) {
                        //generate random again
                        randomFirst = rand() % 6;
                        i = randomFirst;
                        randomCol =  rand() % 10 ;
                    }
                     //randomRow =  rand() % 9;
                     //randomChoice =  rand() % 2;
                     //break;
                 }

             }
//then we can put the ship

             for (int i=randomFirst;i<randomFirst+4;i++) {

                 computerMatrix[i][randomCol] = BATTLESHIP;
             }
     }
     //randomGenerated + 5 times to be populated (for rows in this case)
     if (randomChoice == 1) {

         ///this finds a free spot until there is no ship beacuse I update randomFirst
         for (int i=randomFirst;i<randomFirst+4;i++) {
             if(computerMatrix[randomRow][i] != NO_SHIP) {

            while (computerMatrix[randomRow][i] != NO_SHIP) {
                //generate random again
                randomFirst = rand() % 6;
                i = randomFirst;
                //randomCol =  rand() % 9 ;
                randomRow =  rand() % 10;
            }
                 //or 4 , 9
                 //randomChoice =  rand() % 2;
                 //break;
             }

         }
         //then we can put the ship

         for (int i=randomFirst;i<randomFirst+4;i++) {
             computerMatrix[randomRow][i] = BATTLESHIP;
         }

     }

//------cruiser

     randomFirst = rand() % 7;
     randomCol =  rand() % 10;
     randomRow =  rand() % 10;
     randomChoice =  rand() % 2;

     if (randomChoice ==0 ) {
         //while (computerMatrix[randomRow][randomCol] != NO_SHIP) {

         ///this finds a free spot until there is no ship
         for (int i=randomFirst;i<randomFirst+3;i++) {
             if(computerMatrix[i][randomCol] != NO_SHIP) {

                while (computerMatrix[i][randomCol] != NO_SHIP) {
                    //generate random again
                    randomFirst = rand() % 7;
                    i = randomFirst;
                    randomCol =  rand() % 10 ;
                    //randomRow =  rand() % 9;
                    //randomChoice =  rand() % 2;
                    //break;
                }
             }

         }
         //then we can put the ship
         for (int i=randomFirst;i<randomFirst+3;i++) {

             computerMatrix[i][randomCol] = CRUISER;
         }
     }
     //randomGenerated + 5 times to be populated (for rows in this case)
     if (randomChoice == 1) {

         ///this finds a free spot until there is no ship beacuse I update randomFirst
         for (int i=randomFirst;i<randomFirst+3;i++) {
             if(computerMatrix[randomRow][i] != NO_SHIP) {
                 while (computerMatrix[randomRow][i] != NO_SHIP) {
                     //generate random again
                     randomFirst = rand() % 7;
                     i = randomFirst;
                     //randomCol =  rand() % 9 ;
                     randomRow =  rand() % 10;
                     //randomChoice =  rand() % 2;
                     //break;
                 }
             }

         }
         //then we can put the ship

         for (int i=randomFirst;i<randomFirst+3;i++) {
             computerMatrix[randomRow][i] = CRUISER;
         }

     }
     //------Submarine

     randomFirst = rand() % 8;
     randomCol =  rand() % 10;
     randomRow =  rand() % 10;
     randomChoice =  rand() % 2;

     if (randomChoice ==0 ) {
         //while (computerMatrix[randomRow][randomCol] != NO_SHIP) {

         ///this finds a free spot until there is no ship
         for (int i=randomFirst;i<randomFirst+2;i++) {
             if(computerMatrix[i][randomCol] != NO_SHIP) {

                while (computerMatrix[i][randomCol] != NO_SHIP) {
                    //generate random again
                    randomFirst = rand() % 8;
                    i = randomFirst;
                    randomCol =  rand() % 10 ;
                    //randomRow =  rand() % 9;
                    //randomChoice =  rand() % 2;
                    //break;
                }
             }
         }
         //then we can put the ship

         for (int i=randomFirst;i<randomFirst+2;i++) {

             computerMatrix[i][randomCol] = SUBMARINE;
         }
     }
     //randomGenerated + 5 times to be populated (for rows in this case)
     if (randomChoice == 1) {

         ///this finds a free spot until there is no ship beacuse I update randomFirst
         for (int i=randomFirst;i<randomFirst+2;i++) {
             if(computerMatrix[randomRow][i] != NO_SHIP) {
                 while (computerMatrix[randomRow][i] != NO_SHIP) {
                     //generate random again
                     randomFirst = rand() % 8;
                     i = randomFirst;
                     //randomCol =  rand() % 9 ;
                     randomRow =  rand() % 10;
                     //randomChoice =  rand() % 2;
                     //break;
                 }
             }
         }
         //then we can put the ship

         for (int i=randomFirst;i<randomFirst+2;i++) {
             computerMatrix[randomRow][i] = SUBMARINE;
         }
     }

//----Destroyer

     randomFirst = rand() % 9;
     randomCol =  rand() % 10;
     randomRow =  rand() % 10;
     randomChoice =  rand() % 2;

     if (randomChoice ==0 ) {
         //while (computerMatrix[randomRow][randomCol] != NO_SHIP) {

         ///this finds a free spot until there is no ship
         for (int i=randomFirst;i<randomFirst+1;i++) {
             if(computerMatrix[i][randomCol] != NO_SHIP) {
                 while (computerMatrix[i][randomCol] != NO_SHIP) {
                     //generate random again
                     randomFirst = rand() % 9;
                     i = randomFirst;
                     randomCol =  rand() % 10 ;
                     //randomRow =  rand() % 9;
                     //randomChoice =  rand() % 2;
                     //break;
                 }
             }
         }
         //then we can put the ship

         for (int i=randomFirst;i<randomFirst+1;i++) {

             computerMatrix[i][randomCol] = DESTROYER;
         }
     }
     //randomGenerated + 5 times to be populated (for rows in this case)
     if (randomChoice == 1) {

         ///this finds a free spot until there is no ship beacuse I update randomFirst
         for (int i=randomFirst;i<randomFirst+1;i++) {
             if(computerMatrix[randomRow][i] != NO_SHIP) {
                 while (computerMatrix[randomRow][i] != NO_SHIP) {
                     //generate random again
                     randomFirst = rand() % 9;
                     i = randomFirst;
                     //randomCol =  rand() % 9 ;
                     randomRow =  rand() % 10;
                     //randomChoice =  rand() % 2;
                     //break;
                 }
             }

         }
         //then we can put the ship
         for (int i=randomFirst;i<randomFirst+1;i++) {
             computerMatrix[randomRow][i] = DESTROYER;
         }
     }
     //display the computer choices
     //col numbers
     for (int n = 0; n <= 9; n++) {

         printf("    %d", n);
     }
     printf("\n");
     //display computer board
     //row letters
     for (int h =0; h < rows; h++) {

         printf("%c", 'A' + h);
         //printing ships
         for (int j = 0; j < col; j++) {
             printf("   ");
             switch (computerMatrix[h][j]) {
                 case CARRIER:
                     printf("CV");
                     break;
                 case BATTLESHIP:
                     printf("BB");
                     break;
                 case CRUISER:
                     printf("CA");
                     break;
                 case SUBMARINE:
                     printf("SS");
                     break;
                 case DESTROYER:
                     printf("DD");
                     break;

                 default:
                     printf("..");
                     break;
             }
         }
         printf("\n");
     }
     printf("Computer also placed its ships, (DEBUG PURPOSES ONLY)\n");
}


    //Create a TeardownSinglePlayer() --DONE
    //that frees the pair of grids. Call that function from teardown.
    void TeardownSinglePlayer() {

     for (int i=0;i<rows;i++) {

         free(*(computerMatrix+i));
         //int* pInt = myMatrix[i];
         //free(pInt);
     }
     free(computerMatrix);

     for (int i=0;i<rows;i++) {
         free(*(hitmissgrid+i));
         //int* pInt = myMatrix[i];
         //free(pInt);
     }
     free(hitmissgrid);

     for (int i=0;i<rows;i++) {
         free(*(hitmissgridCOMPUTER+i));
         //int* pInt = myMatrix[i];
         //free(pInt);
     }
     free(hitmissgridCOMPUTER);

 }

    char* makeShotTEST(enum trackFire **hitmissgrid1) {
     // ask for a location [i][j], one block
     // check the location
     // return hit if occupied else miss

     fflush(stdin);
     char str[50];
     static char result[3];

     printf("Enter a location to hit, format of 'G4'\n");


     while(fgets(str, sizeof(str), stdin) == NULL)
     {
         printf("Error, enter something\n");
         return "ERROR";
     }

     char letter = toupper(str[0]);
     //int nbrr = 5;
     int nbrr= str[1] -'0';
     //if input is num
     if (!isalpha(str[0])) {
         printf("incorrect format: should must enter with letter first\n");
         return "ERROR";
     }
     if (isalpha(str[0]) && !isdigit(str[1])) {
         printf("incorrect format: must enter with number\n");
         return "ERROR";
     }


     //if 3rd char is digit or char
     if (isdigit(str[2]) || isalpha(str[2])) {

         printf("number not bounds, OR incorrect format\n");
         return "ERROR";
     }



     //atoi did not seem to work, I used isDigit to check the 3rd character is a digit
     int numericLetter;



     switch (letter) {
         case 'A':
             numericLetter = 0;break;
         case 'B':numericLetter = 1;break;
         case 'C':numericLetter = 2;break;
         case 'D':numericLetter = 3;break;
         case 'E':numericLetter = 4;break;
         case 'F':numericLetter = 5;break;
         case 'G':numericLetter = 6;break;
         case 'H':numericLetter = 7;break;
         case 'I':numericLetter = 8;break;
         case 'J':numericLetter = 9;break;
         default: printf("Error, Letter out of bounds, OR format is wrong\n");
             return "ERROR";
             //break;

     }

     result[0] = letter;
     result[1] = '0' + nbrr;
     result[2] = '\0';

     if (hitmissgrid1[numericLetter][nbrr] != NO_TRY ) {
         printf("already HIT OR MISS this spot!, TRY AGAIN\n");
         return "ERROR";
     }

     
     return result;


 }

    int updateNextState() {

     while (true) {
         if (MakeSinglePlayerShot() != 2 ) {
             break;
         }
     }
         //we will terminate the loop if someone won and print an appropriate message.
         if (SinglePlayerDidWin(computerMatrix) == false) {

             //move.DONE = false;
             printf("YOU WON\n");
             displayhitmissgrid();
             return false;
         }

         SinglePlayerResponse();

         //we will terminate the loop if someone won and print an appropriate message.
         if (SinglePlayerDidWin(myMatrix) == false) {

             //move.DONE = false;
             printf("COMPUTER WON\n");
             displayhitmissgridComputer();
             //displayhitmissgrid();
             return false;
         }

         //returning true to keep looping
         //(we check if this functions return false, we break, from main)
     return true;
     }

    int MakeSinglePlayerShot() {

         // ask for a location [i][j], one block
         // check the location
         // return hit if occupied, else miss

         fflush(stdin);
         char str[50];

         printf("Enter a location to hit, format of 'G4'\n");


         while(fgets(str, sizeof(str), stdin) == NULL)
         {
             printf("Error, enter something\n");
             return 2;
         }
         //here made it upper case for ease of use
         // if (isalpha(str[0])) {
         //   toupper(str[0]);
         //}

         //format of letter and number
         //example g4
         //int numbertoCheck;

         //numbertoCheck= atoi(str);

     char letter = toupper(str[0]);
     //int nbrr = 5;
     int nbrr= str[1] -'0';
     //if input is num
     if (!isalpha(str[0])) {
         printf("incorrect format: should must enter with letter first\n");
         return 2;
     }
     if (isalpha(str[0]) && !isdigit(str[1])) {
         printf("incorrect format: must enter with number\n");
         return 2;
     }

    
        //if 3rd char is digit or char
     if (isdigit(str[2]) || isalpha(str[2])) {

         printf("number not bounds, OR incorrect format\n");
         return 2;
     }



         //atoi did not seem to work, I used isDigit to check the 3rd character is a digit
         int numericLetter;



         switch (letter) {
             case 'A':
                 numericLetter = 0;break;
             case 'B':numericLetter = 1;break;
             case 'C':numericLetter = 2;break;
             case 'D':numericLetter = 3;break;
             case 'E':numericLetter = 4;break;
             case 'F':numericLetter = 5;break;
             case 'G':numericLetter = 6;break;
             case 'H':numericLetter = 7;break;
             case 'I':numericLetter = 8;break;
             case 'J':numericLetter = 9;break;
             default: printf("Error, Letter out of bounds, OR format is wrong\n");
                 return 2;
                 //break;

         }




         if (computerMatrix[numericLetter][nbrr] != NO_SHIP) {

             //need to put hit mark, ..shot
             //already hit
             if (hitmissgrid[numericLetter][nbrr] == HIT) {
                 printf("already HIT this spot!, try again for another spot\n");
                 return 2;
             }
             hitmissgrid[numericLetter][nbrr] = HIT;
             computerMatrix[numericLetter][nbrr] = destroyed;
             printf("COMPUTER: YOU HIT MY SHIP\n");
             return 1;//hit
         }
         //already missed
         if (hitmissgrid[numericLetter][nbrr] == MISS) {
             printf("already missed this spot!, try again for another spot\n");
             return 2;
         }
         hitmissgrid[numericLetter][nbrr] = MISS;
         printf("COMPUTER: YOU MISSED MY SHIP\n");
         return 0;//miss



     }

    //Write GetSinglePlayerShot () – pick a random square that hasn’t already been shot at.
    //receives the coordinates from the OTHER player (the computer in our case).
    //Picks random, unshot location and returns it
    int GetSinglePlayerShot(){

         //make computer shot
         // i = random % 10
         // j = random % 10
         //while true
         //for random grid[i][j]
         // if grid[i][j] != shot
         // i = random % 10
         // j = random % 10
         //break;
         //return grid[i][j]

         int i = rand() % 10;
         int j = rand() % 10;

         //picks random unshot lcoatiobn from hitmissgridCOMPUETR and returns it

         //shotsfired grid should be called
         //trying to get a space in computer's hit/miss grid that is not shot
         while (hitmissgridCOMPUTER[i][j] != NO_TRY) {
             i = rand() % 10;
             j = rand() % 10;
         }
         //then

         if (hitmissgridCOMPUTER[i][j] ==NO_TRY) {


             if (myMatrix[i][j] != NO_SHIP) {

                 myMatrix[i][j] = destroyed;
                 hitmissgridCOMPUTER[i][j] = HIT;
                 return hitmissgridCOMPUTER[i][j];
             }
             else {
                 hitmissgridCOMPUTER[i][j] = MISS;
                 return hitmissgridCOMPUTER[i][j];
             }


         }

         return hitmissgridCOMPUTER[i][j];


     }

    //Updates hit/miss grid
    void SinglePlayerResponse() {


         //if blank place in computer miss is a ship in myMatrix, then send hit and update hit/miss grid

         if (GetSinglePlayerShot() == HIT) {

             printf("COMPUTER HIT ME\n");
             // hitmissgridCOMPUTER[move.i][move.j] = HIT;

         }else  {

             printf("COMPUTER MISSED ME\n");
             // hitmissgridCOMPUTER[move.i][move.j] = MISS;

         }

     }

     //There are a few ways to check to see if someone won. The nicest way would be to add another
     //enum value to the board – destroyed.
     //Finds if game is over
     bool SinglePlayerDidWin(enum shipType **matrix) {
            int counter = 0;

         //counter for all ships: 15
         //here I am checking whether the ships are destroyed(optional),
         //and checkinng all 15 squares for someone to win
         while (true) {

             bool carrierCheck=false;
             bool bbCheck=false;
             bool cruiserCheck =false;
             bool submarineCheck = false;
             bool destroyerCheck=false;

             for (int i=0;i<rows;i++) {
                 for (int j=0;j<col;j++) {


                     if (matrix[i][j] == CARRIER) {

                         carrierCheck = true;
                     }
                     if (matrix[i][j] == BATTLESHIP) {

                         bbCheck = true;
                     }

                     if (matrix[i][j] == CRUISER) {

                         cruiserCheck = true;
                     }

                     if (matrix[i][j] == SUBMARINE) {

                         submarineCheck = true;
                     }

                     if (matrix[i][j] == DESTROYER) {

                         destroyerCheck = true;
                     }

                     if (matrix[i][j] == destroyed) {
                         counter++;
                         if (counter ==15) {
                             printf("All SHIPS ARE SUNK\n");
                             return false;
                         }


                     }



                 }
             }


             printf("SUNK SHIPS LIST:\n");
             //if it could not find any carrier then it is sunk
             if (carrierCheck == false) {

                 printf("-YOU SUNK MY CARRIER\n");
             }
             if (bbCheck == false) {
                 printf("-YOU SUNK MY BB\n");
                 //bbcheck = true;
             }
             if (cruiserCheck == false) {
                 printf("-YOU SUNK MY CRUISER\n");
             }

             if (submarineCheck == false) {
                 printf("-YOU SUNK MY SUBMARINE\n");
             }
             if (destroyerCheck == false) {
                 printf("-YOU SUNK MY DESTROYER\n");
             }

             break;
         }
         return true;

     }

     bool MultiPlayerDidWin(enum shipType **matrix) {

            int counter = 0;
             bool carrierCheck=false;
             bool bbCheck=false;
             bool cruiserCheck =false;
             bool submarineCheck = false;
             bool destroyerCheck=false;
         //counter for all ships: 15
         //here I am checking whether the ships are destroyed(optional),
         //and checkinng all 15 squares for someone to win

             for (int i=0;i<rows;i++) {
                 for (int j=0;j<col;j++) {


                     if (matrix[i][j] == CARRIER) {

                         carrierCheck = true;
                     }
                     if (matrix[i][j] == BATTLESHIP) {

                         bbCheck = true;
                     }

                     if (matrix[i][j] == CRUISER) {

                         cruiserCheck = true;
                     }

                     if (matrix[i][j] == SUBMARINE) {

                         submarineCheck = true;
                     }

                     if (matrix[i][j] == DESTROYER) {

                         destroyerCheck = true;
                     }

                     if (matrix[i][j] == destroyed) {
                         counter++;
                         if (counter ==15) {
                             printf("All SHIPS ARE SUNK\n");
                             someoneWon = true;
                             return false;
                         }
                     }
                 }
             }

             printf("MY SUNK SHIPS LIST:\n");
             //if it could not find any carrier then it is sunk
             if (carrierCheck == false) {

                 printf("-CARRIER\n");
             }
             if (bbCheck == false) {
                 printf("-BB\n");
                 //bbcheck = true;
             }
             if (cruiserCheck == false) {
                 printf("-CRUISER\n");
             }

             if (submarineCheck == false) {
                 printf("-SUBMARINE\n");
             }
             if (destroyerCheck == false) {
                 printf("-DESTROYER\n");
             }

         return true;

     }
        //freeing matrix-hitmissgrid tuples because each player needs to free their own grid
     void TeardownMultiPlayer(enum shipType **normalMatrix, enum trackFire **hitMissGrid) {

     for (int i=0;i<rows;i++) {

         free(*(normalMatrix+i));
         //int* pInt = myMatrix[i];
         //free(pInt);
     }
     free(normalMatrix);
     printf("matrix memory successfully freed\n");


     for (int i=0;i<rows;i++) {

         free(*(hitMissGrid+i));
         //int* pInt = myMatrix[i];
         //free(pInt);
     }
     free(hitMissGrid);
     printf("hit miss grid memory successfully freed\n");

        }

//returns hit or miss regarding the location sent,
//sets matrix to destroyed
int checkGrid(char * str, enum shipType **matrix) {
        int number;
        int letter;
     letter = str[0] - 'A';
     number = str[1] - '0';
     if (matrix[letter][number] != NO_SHIP) {
         matrix[letter][number] = destroyed;
         return 1;
     }
     return 0;
 }

//this function updates the hit miss grid regarding message rceived
//and sets the grid belonged to this table to destroyed
//normal matrix should be other player because
//it is destroyed if we get hit message
void updatehitmissgrid(const char * msg2, enum trackFire **matrix, const char * str, enum shipType **normalMatrix) {
     int row1=0;
     int col1=0;
        row1 = msg2[0] -'A';
        col1 = msg2[1]-'0';
     if (strcmp(str, "HIT") == 0) {
        matrix[row1][col1] = HIT;
         normalMatrix[row1][col1] = destroyed;

     }
     else if (strcmp(str, "MISS") == 0) { 
         matrix[row1][col1] = MISS;
     }

     displayHITORMISS(matrix);

 }
     int main(int argc, char *argv[]) {
     
    if(argc == 2) {
        printf("WE ARE SERVER\n");
        //set mode to online i.e. mode 3
        mode=3;
        int sockfd, new_fd, yes=1, rv; // listen on sock_fd, new connection on new_fd
        struct addrinfo hints, *servinfo, *cur, *p;
        char string[1000];
        char *msg;
        memset(&hints, 0, sizeof(hints));
        hints.ai_family=AF_UNSPEC; // IPv4 or IPv6
        hints.ai_socktype=SOCK_STREAM;
        hints.ai_flags=AI_PASSIVE; //use my IP
        rv = getaddrinfo(NULL, PORT, &hints, &servinfo);

         for (cur=servinfo;cur!=NULL;cur=cur->ai_next) { // loop over them all
             if (cur->ai_addr->sa_family == AF_INET) { // IPv4
                struct sockaddr_in *in = (struct sockaddr_in *)cur->ai_addr;
                inet_ntop(AF_INET,&(in->sin_addr), string, 1000);
             } else { // IPv6
                 struct sockaddr_in6 *in = (struct sockaddr_in6 *)cur->ai_addr;
                 inet_ntop(AF_INET6,&(in->sin6_addr), string, 1000);
             }
             printf ("%s\n",string);
         }
        //freeaddrinfo(servinfo);
        int socket_fd;
        for(p = servinfo; p != NULL; p = p->ai_next) { // loop through all the results and bind ASAP
            if ((socket_fd = socket(p->ai_family, p->ai_socktype, p->ai_protocol)) == -1) continue;
            if (bind(socket_fd, p->ai_addr, p->ai_addrlen) == -1) {
                close(socket_fd);
                continue;
            }
            break;
        }
        printf("Bound socket on server\n");
        int listen_info;
        listen_info = listen(socket_fd, BACKLOG);
        if (listen_info == -1) {
            perror("listen");
            exit(1);
        }
        printf("Listening on server...\n");
        freeaddrinfo(servinfo);
        struct sockaddr_storage their_addr; // connector's address information
        socklen_t sin_size;
        sin_size = sizeof(their_addr);
        int their_fd;
        their_fd = accept(socket_fd, (struct sockaddr *)&their_addr, &sin_size);
        if(their_fd == -1){
            perror("accept");
            exit(1);
        }
        printf("Connection established...\n");
        Initialization();
        //if question asked for 6th time, break
        while(questionCount>0){//this needs to has its own matrices initialized
            if (questionCount ==0) {
                break;
            }
            struct Move res = acceptInput();
            //change updatestatedunctoin, now it shoudlc cxheck
            //ifServer (for updating servermatirx) and call update multiplayer
            //vice versa for client
            if (updateState(res)) {
                res.wentNext = true;
                displayState(true);
                if (movetonext(true) == false) {
                    break;
                }
            }
            else {
                displayState(false);
                if (movetonext(false)==false) {
                    break;
                }
            }
        }
        ifServerDONE =true;
        //mode =3;
        //we opened socket, bound, listened,
        //now, the server waits for a client connection with accept()
        //if serve's turn, it receives and sends, then set cleint turn to true
        //ServerTURN = true;
        printf("WAITING FOR OTHER PLAYER TO PLACE THEIR SHIPS...\n");
        while (someoneWon == false) {
            int recv_info;
            // if (MultiPlayerDidWin(serverMatrix)== false) {
            //     printf("SERVER LOST");
            //     printf("MOVING TO TEARDOWN\n");
            //     break;
            // }
            if (someoneWon == true) {
                break;
            }

                if (ServerTURN == true) {

                    if (someoneWon == true) {
                        break;
                    }
                    msg = makeShotTEST(hitmissgridServer);

                    while (strcmp(msg, "ERROR") == 0){

                        msg = makeShotTEST(hitmissgridServer);

                        if ((strcmp(msg, "ERROR") != 0)) {
                            break;
                        }
                    }

                    int msg_len = strlen(msg);
                    int bytes_sent;
                    //string[recv_info] = '\0';
                    //printf("Message received from client socket %d: \"%s\"\n", their_addr, string);
                    bytes_sent = send(their_fd, msg, msg_len, 0);
                    if (bytes_sent == -1) {
                        perror("send");
                    }
                    else if (bytes_sent == msg_len) {
                        printf("Sent full message to client socket %d: \"%s\"\n", their_fd, msg);
                        //ServerTURN= false;
                        //ClientTURN = true;
                    }
                    else {
                        printf("Sent partial message to client socket %d: %d bytes sent.\n", their_fd, bytes_sent);
                        //ServerTURN = false;
                        //ClientTURN = true;
                    }
                    //we receive to know if we hit
                    recv_info = recv(their_fd, string, sizeof(string), 0);
                    if(recv_info == 0){
                        printf("Connection closed...\n");
                        break;
                    }
                    if(recv_info == -1){
                        printf("recv error\n");
                        break;
                    }
                    else{
                        string[recv_info] = '\0';
                        printf("Message received from client socket %d: \"%s\"\n", their_addr, string);
                        //if we receive null message, it means we won
                        //if (strcmp(string, "") == 0) {

                        //}
                        //to update grid after win
                        if (strcmp(string, "SERVERWON")==0) {
                            //string ="HIT";
                            updatehitmissgrid(msg, hitmissgridServer, "HIT", clientMatrix);
                            printf("YOU WON\n");
                            break;

                        }
                        updatehitmissgrid(msg, hitmissgridServer, string, clientMatrix);

                        ServerTURN= false;
                        ClientTURN = true;
                    }
                }
                //if (ServerTURN==false){
                //ServerTURN=true;
                //here we receive the shot, and send hit or miss
                else{
                    recv_info = recv(their_fd, string, sizeof(string), 0);
                    //if(recv_info == 0){
                    //    printf("Connection closed...\n");
                    //    break;
                    //}
                    if(recv_info == -1){
                        printf("recv error\n");
                        break;
                    }
                    else{
                //we received the message string, check for it
                //msg ="HIT";
                if (strcmp(string, "SERVERWON") == 0) {
                //HIT
                //msg2 ="MISS";
                    printf("YOU WON\n");
                    printf("MOVING TO TEARDOWN\n");
                    someoneWon = true;

                break;
                //then free servergrids, do the same with client
                }
                else if (checkGrid(string, serverMatrix) == 1) {


                    //HIT
                   msg ="HIT";
                }
                else if (checkGrid(string, serverMatrix) == 0) {
                    //HIT
                    msg ="MISS";
                }
                 if (MultiPlayerDidWin(serverMatrix)== false) {
                 printf("CLIENT WON\n");
                 printf("SENDING MESSAGE...\n");
                     msg = "CLIENTWON";
                 //break;
                        }


                //char* msg = checkGrid(string);
                //char *msg = "PONG";
                int msg_len = strlen(msg);
                int bytes_sent;

                string[recv_info] = '\0';
                printf("Message received from client socket %d: \"%s\"\n", their_addr, string);

                bytes_sent = send(their_fd, msg, msg_len, 0);
                if (bytes_sent == -1) {
                    perror("send");
                }
                else if (bytes_sent == msg_len) {
                    printf("Sent full message to client socket %d: \"%s\"\n", their_fd, msg);
                  if (strcmp(msg, "CLIENTWON")==0) {
                      //no need to loop back
                      break;
                  }
                    ServerTURN= true;
                    ClientTURN = false;
                }
                else {
                    printf("Sent partial message to client socket %d: %d bytes sent.\n", their_fd, bytes_sent);
                    ServerTURN = true;
                    ClientTURN = false;

                }
                }//turn brace
            }
        }
        close(their_fd);
        close(socket_fd);
        //free server grid now
        //TeardownServer
        TeardownMultiPlayer(serverMatrix,hitmissgridServer);
        return 0;
    }
        //CLIENT MODE
        else if(argc == 3){
            mode=4;
            //set mode to online i.e. mode 4
            struct addrinfo hints, *servinfo, *cur, *p;
            int socket_fd2;
            char string2[1000];
            int recv_info;
            char *msg2;
            int msg_len2;
            int bytes_sent2;
            memset(&hints, 0, sizeof(hints));
            hints.ai_family=AF_UNSPEC; // IPv4 or IPv6
            hints.ai_socktype=SOCK_STREAM; // TCP only
            int rv;
            rv = getaddrinfo(argv[1], argv[2], &hints, &servinfo); // Get the destination data
            if (rv != 0) {
                perror("getaddrinfo");
            }
            for(p = servinfo; p != NULL; p = p->ai_next) {// loop and connect to the first
                if ((socket_fd2 = socket(p->ai_family, p->ai_socktype, p->ai_protocol)) == -1) { continue; // this one failed. Try another.
                }
                if (connect(socket_fd2, p->ai_addr, p->ai_addrlen) == -1) {
                    close(socket_fd2);

                    continue; // this one failed. Try another.
                }
                break;
            }
            if (strcmp(argv[1],"127.0.0.1") == 0 ) {
                printf("connected to localhost\n");
            }
            printf("Connected socket to localhost port %s\n", argv[2]);
            // Send a message to server
            //send ping
            Initialization();//this needs to has its own matrices initialized
            //if question asked for 6th time, break
            while(questionCount>0){

                if (questionCount ==0) {
                    break;
                }
                struct Move res = acceptInput();

                if (updateState(res)) {
                    res.wentNext = true;
                    displayState(true);
                    if (movetonext(true) == false) {
                        break;
                    }
                }
                else {
                    displayState(false);
                    if (movetonext(false)==false) {
                        break;
                    }
                }
            }
            //make client hit
            ifClientDONE = true;
            printf("WAITING FOR OTHER PLAYER TO PLACE THEIR SHIPS...\n");
            while (someoneWon == false) {
                //if-----------------------------
                //after sending and receiving, it is other player's turn
                //Here we tell them which char location we want to shot, get an stdin function here and store in char,
                //then send this char
                if (someoneWon == true) {
                    break;
                }
                //if we detect that it is client turn, we send shot and receive, now its server turn
                //if we detect that it is server turn, we receives and send, now its ourn trun
                if (ClientTURN==true) {
                    //go to teardown

                    // if (MultiPlayerDidWin(clientMatrix)== false) {
                    //     printf("YOU LOST");
                    //     printf("MOVING TO TEARDOWN\n");
                    //     break;
                    // }
                    if (someoneWon == true) {
                        break;
                    }
                    msg2 = makeShotTEST(hitmissgridClient);

                    while (strcmp(msg2, "ERROR") == 0){

                        msg2 = makeShotTEST(hitmissgridClient);
                        if ((strcmp(msg2, "ERROR") != 0)) {
                            break;
                        }
                    }

                    //msg2 = hello;
                    msg_len2 = strlen(msg2);
                    bytes_sent2 = send(socket_fd2, msg2, msg_len2, 0);

                    //here add if already hit or miss
                    if (bytes_sent2 == -1) {
                        printf("send error");
                        perror("send");
                    }
                    else if (bytes_sent2 == msg_len2) {
                        printf("Sent full message: \"%s\"\n", msg2);
                    }
                    else {
                        printf("Sent partial message: %d bytes sent.\n", bytes_sent2);
                    }

                    recv_info = recv(socket_fd2, string2, 1000, 0);
                    if (recv_info == 0) {
                        printf("Server closed connection.\n");
                        break ;
                        //exit(0);
                    }
                    else if (recv_info == -1) {
                        perror("recv");
                        break ;
                        //exit(0);
                    }
                    else {
                        string2[recv_info] = '\0';
                        printf("Message received: \"%s\"\n", string2);
                        //this updates
                        //need to mark serverMatrix location destroyed if we hit
                        if (strcmp(string2, "CLIENTWON")==0) {
                            //string ="HIT";
                            updatehitmissgrid(msg2, hitmissgridClient, "HIT", serverMatrix);
                            printf("YOU WON\n");
                            break;

                        }
                        
                        updatehitmissgrid(msg2,hitmissgridClient,string2,serverMatrix);


                        ServerTURN =true;
                        ClientTURN = false;
                    }
                    //ServerTURN =true;
                }

                //here we receive the shot, and send hit or miss
               // if (ClientTURN ==false) {
                    else{
                    recv_info = recv(socket_fd2, string2, 1000, 0);
                    if (recv_info == 0) {
                        printf("Server closed connection.\n");
                        break ;
                        //exit(0);
                    }
                    else if (recv_info == -1) {
                        perror("recv");
                        break ;
                        //exit(0);
                    }
                    else {
                        string2[recv_info] = '\0';
                        printf("Message received: \"%s\"\n", string2);
                        //this updates
                        //when we update, it is now server's turn
                        //now we send hit or miss, if error, receive again

                        if (strcmp(string2, "CLIENTWON") == 0) {
                            //HIT
                            //msg2 ="MISS";
                            //msg2 = "SERVERWON";
                            printf("YOU WON\n");
                            printf("MOVING TO TEARDOWN\n");
                            someoneWon = true;
                            break;
                            //then free clientgrids, do the same with server
                        }
                        else if (checkGrid(string2, clientMatrix) == 1) {
                            //HIT
                            msg2 ="HIT";

                        }
                        else if (checkGrid(string2, clientMatrix) == 0) {
                            //HIT
                            msg2 ="MISS";
                        }


                            if (MultiPlayerDidWin(clientMatrix)== false) {
                                printf("SERVER WON\n");
                                printf("MOVING TO TEARDOWN\n");
                                msg2 = "SERVERWON";
                                //break;
                            }
                        


                        //msg2 = hello;
                        msg_len2 = strlen(msg2);
                        bytes_sent2 = send(socket_fd2, msg2, msg_len2, 0);
                        if (bytes_sent2 == -1) {
                            printf("send error");
                            perror("send");
                        }
                        else if (bytes_sent2 == msg_len2) {
                            printf("Sent full message: \"%s\"\n", msg2);

                            if (strcmp(msg2, "SERVERWON") == 0) {
                                //break if we sent message
                                someoneWon = true;
                                break;
                            }
                            ClientTURN= true;
                            ServerTURN = false;
                        }
                        else {
                            printf("Sent partial message: %d bytes sent.\n", bytes_sent2);
                            ClientTURN= true;
                            ServerTURN = false;

                        }
                    }


                    // }
                }
            }
            //need to go teardown

            printf("Closing socket\n");
            close(socket_fd2);
            freeaddrinfo(servinfo);
            TeardownMultiPlayer(clientMatrix,hitmissgridClient);
            return 0;
        }

     
     mode =1;
        //ELSE singlePlayer
     //-------------//-----------//-----------------//-----------------//--------------//----------------------
         Initialization();

         //if question asked for 6th time, break
         while(questionCount>0){

             if (questionCount ==0) {
                 break;
             }
             struct Move res = acceptInput();

             if (updateState(res)) {
                 res.wentNext = true;
                 displayState(true);
                 if (movetonext(true) == false) {
                     break;
                 }
             }
             else {
                 displayState(false);
                 if (movetonext(false)==false) {
                     break;
                 }
             }
         }

         mode =2;
        Initialization();
        //add two player functions
         struct Move res2 = acceptInput();
         while (updateState(res2) ==true) {
             //somebody won
             displayState(true);
         }
         //In the main game loop’s Teardown, free all grids. --DONE
         Teardown();
     }

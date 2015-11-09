package com.shantanu.cubetimer;

import java.util.Random;

public class ScrambleGenerator {


    final String[][] moveArray2x2 = new  String[][]
            {{"F","F'","F2"},
            {"R","R'","R2"},
            {"B","B'","B2"}};
    final String[][] moveArray3x3 = new String[][]
            {{"F","F'","F2","B","B'","B2"},
            {"R","R'","R2","L","L2","L'"},
            {"U","U2","U'","D","D2","D'"}};
    final String[][] moveArray4x4and5x5 = new String[][]
            {{"F","F'","F2","B","B'","B2","Fw","Fw'","Fw2","Bw","Bw'","Bw2"},
            {"R","R'","R2","L","L2","L'","Rw","Rw'","Rw2","Lw","Lw'","Lw2"},
            {"U","U2","U'","D","D2","D'","Uw","Uw'","Uw2","Dw","Dw'","Dw2"}};


    Integer i;
    String scramble;
    int length;


    public String getScramble(Puzzle puzzle){
        scramble = "";
        Random r = new Random();
        int moveset = -1;
        int temp;

        //Default values
        int moveSetCount =3;
        int movesPerSet =6;

        String [][] movelist = new String[0][];

        switch(puzzle){

            case PUZZLE_NONE:
                return "";
            case PUZZLE_3x3:
                moveSetCount = 3;
                movesPerSet = 6;
                length = 25;
                movelist = moveArray3x3;
                break;
            case PUZZLE_2x2:
                moveSetCount =3;
                movesPerSet = 3;
                movelist = moveArray2x2;
                length = 10;
                break;
            case PUZZLE_4x4:
                moveSetCount =3;
                movesPerSet = 12;
                movelist = moveArray4x4and5x5;
                length = 35;
                break;
            case PUZZLE_5x5:
                moveSetCount =3;
                movesPerSet = 12;
                movelist = moveArray4x4and5x5;
                length = 50;
                break;
        }

        for(int i=0;i<length;i++){

            do {
                temp = r.nextInt(moveSetCount);
            }while(temp==moveset);
            moveset = temp;

            scramble +=movelist[moveset][r.nextInt(movesPerSet)]+" ";
        }

        return scramble;
    }

    public int getFontSize(Puzzle puzzle){
        switch(puzzle){
            case PUZZLE_NONE:
            case PUZZLE_2x2:
            case PUZZLE_3x3:
                return 26;

            case PUZZLE_4x4:
                return 21;
            case PUZZLE_5x5:
                return 18;
        }

        return 22;
    }

}

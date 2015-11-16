package com.shantanu.cubetimer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;




import puzzle.ClockPuzzle;
import puzzle.FourByFourCubePuzzle;
import puzzle.PyraminxPuzzle;
import puzzle.SkewbPuzzle;
import puzzle.SquareOnePuzzle;
import puzzle.ThreeByThreeCubePuzzle;
import puzzle.TwoByTwoCubePuzzle;

public class ScrambleGenerator {


    final String[][] moveArray4x4and5x5 = new String[][]
            {{"F","F'","F2","B","B'","B2","Fw","Fw'","Fw2","Bw","Bw'","Bw2"},
            {"R","R'","R2","L","L2","L'","Rw","Rw'","Rw2","Lw","Lw'","Lw2"},
            {"U","U2","U'","D","D2","D'","Uw","Uw'","Uw2","Dw","Dw'","Dw2"}};

    final String[][] moveArray6x6and7x7 = new String[][]
            {{"F","F'","F2","B","B'","B2","2F","2F'","2F2","2B","2B'","2B2","3F","3F'","3F2","3B","3B'","3B2"},
            {"R","R'","R2","L","L2","L'","2R","2R'","2R2","2L","2L'","2L2","3R","3R'","3R2","3L","3L'","3L2"},
            {"U","U2","U'","D","D2","D'","2U","2U'","2U2","2D","2D'","2D2","3U","3U'","3U2","3D","3D'","D32"}};



    String scramble;
    int length;

    public String getScramble(com.shantanu.cubetimer.Puzzle puzzle){
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
                return new ThreeByThreeCubePuzzle().generateScramble();
            case PUZZLE_2x2:
                return new TwoByTwoCubePuzzle().generateScramble();
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
            case PUZZLE_6x6:
                moveSetCount =3;
                movesPerSet = 18;
                movelist = moveArray6x6and7x7;
                length = 60;
                break;
            case PUZZLE_7x7:
                moveSetCount =3;
                movesPerSet = 18;
                movelist = moveArray6x6and7x7;
                length = 70;
                break;
            case PUZZLE_PYRAMINX:
                return new PyraminxPuzzle().generateScramble();
            case PUZZLE_SKEWB:
                return new SkewbPuzzle().generateScramble();
            case PUZZLE_CLOCK:
                return new ClockPuzzle().generateScramble();
            case PUZZLE_SQUARE1:
                return new Square1().scramble();


        }

        if(puzzle == com.shantanu.cubetimer.Puzzle.PUZZLE_MEGAMINX){
            for(int i=0;i<7;i++){

                for(int j=0;j<5;j++){

                    scramble+="R" + (r.nextBoolean()?"++ ":"-- ");
                    scramble+="D" + (r.nextBoolean()?"++ ":"-- ");
                }

                scramble+="U" + (r.nextBoolean()?"' ":" ");
            }
            return scramble;
        }

        for(int i=0;i<length;i++) {

            do {
                temp = r.nextInt(moveSetCount);
            } while (temp == moveset);
            moveset = temp;

            scramble += movelist[moveset][r.nextInt(movesPerSet)] + " ";
        }


        return scramble;

    }

    public class Square1 {
        Random rand = new Random(System.nanoTime());

        int[] p = {1,0,0,1,0,0,1,0,0,1,0,0,0,1,0,0,1,0,0,1,0,0,1,0};
        ArrayList<int[]> seq = new ArrayList<int[]>();

        public  String scramble() {
            seq.clear();
            p = new int[]{1,0,0,1,0,0,1,0,0,1,0,0,0,1,0,0,1,0,0,1,0,0,1,0};

            getseq();
            String s = "";
            for (int i = 0; i < seq.size(); i++) {
                if (seq.get(i)[0] == 7) s += "/";
                else s += " (" + seq.get(i)[0] + "," + seq.get(i)[1] + ") ";
            }
            return s;
        }
        public void getseq() {
            int cnt = 0;
            int len = 16;
            while (cnt < len) {
                int x = rand.nextInt(12) - 5;
                int y = rand.nextInt(12) - 5;
                int size = ((x == 0) ? 0 : 1) + ((y == 0) ? 0 : 1);
                if (size > 0 || cnt == 0) {
                    if (domove(x, y)) {
                        int[] m = {x, y};
                        if (size > 0) seq.add(m);
                        cnt++;
                        int[] n = {7, 0};
                        seq.add(n);
                        domove(7, 0);
                    }
                }
            }
        }
        private boolean domove(int x, int y) {
            if (x == 7) {
                for (int i = 0; i < 6; i++) {
                    int temp = p[i + 6];
                    p[i + 6] = p[i + 12];
                    p[i + 12] = temp;
                }
                return true;
            }
            else {
                if (p[(17 - x) % 12] == 1 || p[(11 - x) % 12] == 1 || p[12 + ((17 - y) % 12)] == 1 || p[12 + ((11 - y) % 12)] == 1) {
                    return false;
                }
                else {
                    int[] px = Arrays.copyOfRange(p, 0, 12);
                    int[] py = Arrays.copyOfRange(p, 12, 24);
                    for (int i = 0; i < 12; i++) {
                        p[i] = px[(12 + i - x) % 12];
                        p[i + 12] = py[(12 + i - y) % 12];
                    }
                    return true;
                }
            }
        }
    }


}

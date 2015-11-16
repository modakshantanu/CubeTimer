package com.shantanu.cubetimer;


public enum Puzzle {

    PUZZLE_NONE(0),PUZZLE_3x3(2),PUZZLE_2x2(1),PUZZLE_4x4(3),PUZZLE_5x5(4),PUZZLE_6x6(5),PUZZLE_7x7(6),PUZZLE_PYRAMINX(7),PUZZLE_MEGAMINX(8)
    ,PUZZLE_SKEWB(9),PUZZLE_SQUARE1(10),PUZZLE_CLOCK(11);

    int id;
    Puzzle(){

    }
    public static Puzzle getById(int id) {
        for(Puzzle e : values()) {
            if(e.id ==id)
                return e;
        }

        return null;
    }

    Puzzle(int num){
        id = num;
    }

    public void setId(int id) {
        this.id = id;
    }
    public int getFontSize(){
        switch(this){
            case PUZZLE_NONE:
            case PUZZLE_2x2:
            case PUZZLE_3x3:
            case PUZZLE_SKEWB:
            case PUZZLE_PYRAMINX:
            case PUZZLE_CLOCK:
                return 26;

            case PUZZLE_4x4:
                return 21;
            case PUZZLE_5x5:
            case PUZZLE_SQUARE1:
                return 18;

            case PUZZLE_MEGAMINX:
                return 14;
            case PUZZLE_6x6:
            case PUZZLE_7x7:
                return 16;
        }

        return 22;
    }
}

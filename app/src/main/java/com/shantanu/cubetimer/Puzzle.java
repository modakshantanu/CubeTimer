package com.shantanu.cubetimer;

/**
 * Created by Bharat Modak on 3/11/2015.
 */
public enum Puzzle {

    PUZZLE_NONE(0),PUZZLE_3x3(2),PUZZLE_2x2(1),PUZZLE_4x4(3),PUZZLE_5x5(4);

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
}

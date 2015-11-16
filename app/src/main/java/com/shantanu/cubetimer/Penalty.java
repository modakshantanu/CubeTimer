package com.shantanu.cubetimer;

/**
 * Created by Bharat Modak on 16/11/2015.
 */
public enum Penalty {

    NONE(0),PLUS2(1),DNF(2);
    int id;
    Penalty(int id){
        this.id = id;
    }
    public static Penalty getById(int id) {
        for(Penalty e : values()) {
            if(e.id ==id)
                return e;
        }

        return null;
    }

}

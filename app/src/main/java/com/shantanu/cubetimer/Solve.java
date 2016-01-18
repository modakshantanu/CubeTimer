package com.shantanu.cubetimer;


public class Solve {


    public int _id;
    public long solvetime;
    public Penalty penalty;

    String toStr(){
        long mins,secs,ms;
        long temp = solvetime;

        if(penalty == Penalty.DNF)
            return "DNF";
        if(penalty == Penalty.PLUS2)
            temp+=2000;

        secs = (int) (temp / 1000);
        mins = secs / 60;
        secs = secs % 60;
        ms = (int) (temp % 1000) / 10;
        return(mins > 0 ? (String.format("%d.%02d.%02d", mins, secs, ms)) : (String.format((secs > 9 ? "%02d." : "%01d.") + "%02d", secs, ms)));

    }

}


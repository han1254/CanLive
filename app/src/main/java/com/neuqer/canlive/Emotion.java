package com.neuqer.canlive;

import java.util.Comparator;

/**
 * Time:2020/5/10 9:46
 * Author: han1254
 * Email: 1254763408@qq.com
 * Function:
 */
public class Emotion  {
    private int id;
    private Double score;

    public Emotion(int id, Double score) {
        this.id = id;
        this.score = score;
    }

    public int getId() {
        return id;
    }

    public Double getScore() {
        return score;
    }

}

package com.neuqer.canlive;

import java.util.Comparator;

/**
 * Time:2020/5/10 9:55
 * Author: han1254
 * Email: 1254763408@qq.com
 * Function:
 */
public class CompareUtil implements Comparator<Emotion> {
    @Override
    public int compare(Emotion o1, Emotion o2) {
        return Double.compare(o2.getScore(), o1.getScore());
    }
}

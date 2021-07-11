package com.masai.flairbnbapp;

public class Main {
    public static void main(String[] args) {
        int[] a = {1, 2, 3, 6, 9, 8, 7, 4, 5};

        int[][] mat = new int[3][3];
        for (int i = 0; i <= a.length / 2; i++) {
            if (a.length % 2 == 1) {

            }
            for (int j = i * 2; j < a.length * (i + 1) - i; j++) {
                mat[i][j] = a[j];
            }
            for (int j = i; j < a.length * (i + 1) - i; j++) {
                mat[j][i] = a[j];
            }
        }

    }
    //1   2  3  4
    //5   6  7  8
    //9  10 11 12
    //13 14 15 16
}
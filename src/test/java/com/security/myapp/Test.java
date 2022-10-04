package com.security.myapp;


public class Test {

    int depth = 0;

    public static void main(String[] args) {
        System.out.println("bac");
        //array of int , total amt of coin
        int[] coins = new int[]{1, 3, 5};
        int coinsAmt = 11;

        System.out.println(getCoin(coins, coinsAmt));
    }

    private static int getCoin(int[] coins, int amt) {
        if (amt == 0) {
            return 0;
        }

        if (amt < 0) {
            return -1;
        }

        //look for child problem...
        Integer res = Integer.MAX_VALUE;
        for (int i = 0; i < coins.length; i++) {
            int remainingCoin = getCoin(coins, amt - coins[i]);
            if (remainingCoin == -1) continue;

            System.out.println("Post Traverse " + remainingCoin);
            res = Math.min(res, remainingCoin + 1);
        }

        return res == Integer.MAX_VALUE ? -1 : res;

    }
}
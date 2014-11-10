package com.example.words.util;

/**
 * Created by dima on 11/9/2014.
 */
public class Letter {
    private char character;
    private int successCount;
    private int totalCount;

    public char GetCharacter(){
        return this.character;
    }

    public void SetCharacter(char c){
        this.character = c;
    }

    public int GetSuccessCount(){
        return this.successCount;
    }

    public void SetSuccessCount(int i){
        this.successCount = i;
    }

    public int GetTotalCount(){
        return this.totalCount;
    }

    public void SetTotalCount(int i){
        this.totalCount = i;
    }

    public double GetSuccessRation(){
        if(this.totalCount == 0){
            return 0d;
        }
        return Math.round(this.successCount / this.totalCount * 100) / 100; //round to 2 decimals
    }
}

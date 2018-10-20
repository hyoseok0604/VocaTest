package com.jhsapps.vocatest;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class TestBuildTool {

    // word : 3 3 3 3 4

    private int wordPlus = -1;

    private int[][] word = null;
    private int[] words = null;

    // sen : 0 1 1 1 1

    private int senIgnore = -1;

    private int[] sen = null;

    private WordDownloader wordDownloader = null;

    public TestBuildTool(WordDownloader wordDownloader){
        this.wordDownloader = wordDownloader;
    }

    public void build(){
        word = new int[5][4];
        sen = new int[4];

        wordPlus = random(0, 4);
        senIgnore = random(0, 4);

        int cursorSen = 0;

        for(int i = 0 ; i < 5 ; i++){
            int a = i == wordPlus ? 4 : 3;
            for(int j = 0 ; j < a ; j++){
                word[i][j] = random(1, 50, word[i]);
            }

            if (i == senIgnore) continue;
            sen[cursorSen++] = i * 50 + random(1, 50, word[i]);
        }

        words = shuffle(wordArrayChange(word));
        sen = shuffle(sen);
    }

    public void checkWordLength(){
        for(int i = 0 ; i < words.length ; i++){
            int requireLength = i % 8 + 1;
            int wordLength = wordDownloader.getWordEng(words[i]).length();

            if(wordLength < requireLength){
                words[i] = newSameUnitWordRequireLength(words[i], requireLength);
            }
        }
    }

    private int newSameUnitWordRequireLength(int beforeWordId, int requireLength){
        int unit = ( beforeWordId - 1 ) / 50;
        int afterWordId = random(50 * unit + 1, 50 * (unit + 1), words);

        while(wordDownloader.getWordEng(afterWordId).length() < requireLength){
            afterWordId = random(50 * unit + 1, 50 * (unit + 1), words);
        }

        return afterWordId;
    }

    private int random(int s, int e){
        int n = (int) (Math.random() * ( e - s + 1 ) ) + s;

        return n;
    }

    private int random(int s, int e, int[] overlap){

        int n = (int) (Math.random() * ( e - s + 1 ) ) + s;

        Integer[] newArray = new Integer[overlap.length];
        int i = 0;
        for (int value : overlap) {
            newArray[i++] = value;
        }

        while(Arrays.asList(newArray).contains(n)){
            n = (int) (Math.random() * ( e - s + 1 ) ) + s;
        }

        return n;
    }

    private int[] wordArrayChange(int[][] array){
        int[] result = new int[16];

        int cursor = 0;

        for(int i = 0 ; i < array.length ; i++){
            for(int j = 0 ; j < array[i].length ; j++){
                if(array[i][j] != 0) result[cursor++] = i * 50 + array[i][j];
            }
        }

        return result;
    }

    private int[] shuffle(int[] array){
        Random rnd = new Random();
        for (int i = array.length - 1 ; i > 0 ; i--){
            int index = rnd.nextInt(i + 1);
            int tmp = array[index];
            array[index] = array[i];
            array[i] = tmp;
        }

        return array;
    }

    public String toString(){
        StringBuilder s = new StringBuilder();

        s.append("word[");
        for(int i = 0 ; i < words.length - 1; i++)  s.append(wordDownloader.getWordEng(words[i])).append(", ");
        s.append(wordDownloader.getWordEng(words[words.length - 1])).append("]\n");
        s.append("sen[");
        for(int i = 0 ; i < sen.length - 1; i++)  s.append(wordDownloader.getWordEng(sen[i])).append(", ");
        s.append(wordDownloader.getWordEng(sen[sen.length - 1])).append("]");

        return s.toString();
    }

    public int[] getWords(){
        return words;
    }

    public int[] getSen(){
        return sen;
    }
}

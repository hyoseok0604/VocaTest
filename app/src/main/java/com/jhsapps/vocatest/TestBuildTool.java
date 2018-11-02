package com.jhsapps.vocatest;

import android.support.annotation.NonNull;

import java.util.Arrays;
import java.util.Random;

class TestBuildTool {

    // word : 3 3 3 3 4

    private int[] words = null;

    // sen : 0 1 1 1 1

    private int[] sen = null;

    private WordDownloader wordDownloader;

    TestBuildTool(WordDownloader wordDownloader){
        this.wordDownloader = wordDownloader;
    }

    void build(){
        int[][] word = new int[5][4];
        sen = new int[4];

        int wordPlus = random(0, 4);
        int senIgnore = random(0, 4);

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

    void checkWordLength(){
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
        return (int) (Math.random() * ( e - s + 1 ) ) + s;
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

    int[] getWords(){
        return words;
    }

    int[] getSen(){
        return sen;
    }
}

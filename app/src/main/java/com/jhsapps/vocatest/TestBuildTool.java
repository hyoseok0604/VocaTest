package com.jhsapps.vocatest;

import java.util.Arrays;
import java.util.Random;

public class TestBuildTool {

    /*
    단어시험은 총 16~20과 즉 5개의 과에서 문제가 나오게 된다
    5개의 과에서 골고루 연습문제를 출제하기 위하여 가장 고르게 분포를 시킨다
    단어는 16문제이므로 3, 3, 3, 3, 4개의 문제를 각 과에서 출제하고
    문장은 4문제이므로 0, 1, 1, 1, 1개의 문제를 각 과에서 출제해야한다.
     */


    // 문제들을 저장하는 배열
    private int[] words = null; // 단어

    private int[] sen = null; // 문장

    private WordDownloader wordDownloader = null; // worddownloader정의

    // 이 클래스의 생성자를 정의하는 부분이다
    public TestBuildTool(WordDownloader wordDownloader){
        // 생성자로 들어온 worddownloader 객체를 저장한다
        this.wordDownloader = wordDownloader;
    }

    public void build(){
        // 단어 시험을 생성할 때에 일단은 2차원 배열로 저장한 후 1차원 배열로 변환하는 과정을 거치게 된다
        int[][] word = new int[5][4];
        // 문장은 그냥 1차원 배열로 생성을 한다
        sen = new int[4];

        /*
        단어시험은 3, 3, 3, 3, 4로 하나는 더 출제를 하게 되고
        문장은 0, 1, 1, 1, 1로 하나는 무시를 하게 된다

        더 출제를 하거나 무시를 할 과를 선택하기 위해서 랜덤으로 선택을 하게 된다.
         */
        int wordPlus = random(0, 4);
        int senIgnore = random(0, 4);

        // 문장 배열의 위치를 지정할 커서를 하나 생성한다
        int cursorSen = 0;

        // 총 5개의 과가 존재하므로 5번 반복하는 반복문이다.
        for(int i = 0 ; i < 5 ; i++){
            // 삼항 연산자와 비교 연산자가 결합한 형태
            // Step1. 가장 먼저 i == wordPlus 라는 비교 연산자를 실행한다
            // i가 wordPlus 와 같으면 True 다르면 False 를 반환한다.
            // Step2. 다음으로는 Step1에서 가져온 True 또는 False 값을 가지고 삼항 연산자를 실행하게 된다.
            // 조건문 ? a : b 의 꼴이 되는데 조건문이 True 면 a를 반환하고 조건문이 False 면 b를 반환한다.
            // 즉 이 식은 랜덤으로 정해진 단어를 더 추출할 과가 i와 같다면 4를 반환하고 다르면 3을 반환한다.
            int a = i == wordPlus ? 4 : 3;

            // 위의 식에서 가져온 4 또는 3 번 만큼 반복하는 반복문이다.
            for(int j = 0 ; j < a ; j++){
                // 한과에 단어는 총 50개 이므로 1부터 50 사이의 값을 랜덤으로 가져오고
                // 자신과 동일한 과의 출제 값을 넘겨주어서 중복된 문제가 출제되는 것을 방지하게 된다.
                word[i][j] = random(1, 50, word[i]);
            }

            // 만약 i가 문장 출제를 무시하는 과라면 다음 코드를 패스하는 조건문이다.
            if (i == senIgnore) continue;

            // 위의 조건문에서 걸리지 않는다면 문장을 출제하는데 문장과 단어 시험이 겹치지 않기 위해서
            // 문장을 나중에 출제하면서 단어 값을 넘겨 중복을 방지한다.
            // 또한 출제를 한 후에 커서를 하나 더해서 배열의 다음 칸에 저장이 되도록 한다.
            // 또한 랜덤값에 i x 50을 더하여 단어에 부여된 고유 아이디 값으로 만들어 준다.
            // 16과의 첫번째 단어는 1이고 그 뒤로 계속 증가하는 방식으로 아이디는 정의되어 있다.
            sen[cursorSen++] = i * 50 + random(1, 50, word[i]);
        }

        // 최종적으로 단어들을 저장하게 된다.
        // 가장 먼저 wordArrayChange 라는 함수를 이용해 2차원 배열을 1차원 배열로 바꾸는 동시에
        // 과와 단어를 조합하여 단어의 고유 아이디 값으로 만들게 된다.
        // 그후에는 shuffle 함수로 섞어준다.
        words = shuffle(wordArrayChange(word));

        // 문장은 이미 아이디 값으로 만들어서 저장을 하였기 때문에 shuffle 함수로 순서를 섞어준다.
        sen = shuffle(sen);
    }

    public void checkWordLength(){
        // 단어 배열을 통해서 요구하는 단어 길이를 구하고 요구하는 단어 길이에 맞도록 적절하게 단어를 조정한다.
        for(int i = 0 ; i < words.length ; i++){
            int requireLength = i % 8 + 1;
            int wordLength = wordDownloader.getWordEng(words[i]).length();

            if(wordLength < requireLength){
                words[i] = newSameUnitWordRequireLength(words[i], requireLength);
            }
        }
    }

    // 단어의 길이를 확인하고 단어의 길이가 모자라면 같은 과의 단어를 랜덤으로 뽑아오게 된다.
    // 다시 뽑아온 단어도 길이가 모자랄 수 있는 확률은 존재하기 때문에 조건에 알맞을 때 까지 반복을 하게 된다.
    private int newSameUnitWordRequireLength(int beforeWordId, int requireLength){
        // 이전의 단어 고유 아이디를 통해서 몇과의 단어인지 알아온다.
        int unit = ( beforeWordId - 1 ) / 50;

        // 랜덤으로 같은 과에서 다른 단어를 가져오게 된다
        // 이때에도 단어의 중복을 방지하기 위해 words를 중복 방지 확인 배열로 넘긴다.
        int afterWordId = random(50 * unit + 1, 50 * (unit + 1), words);

        // 다시 뽑아온 단어의 길이가 요구하는 길이보다 짧으면 다시 단어를 가져오면서
        // 결국 요구 길이를 만족 할 때까지 반복이 된다.
        while(wordDownloader.getWordEng(afterWordId).length() < requireLength){
            afterWordId = random(50 * unit + 1, 50 * (unit + 1), words);
        }

        // 요구 길이를 만족한 단어 고유 아이디를 반환한다.
        return afterWordId;
    }

    private int random(int s, int e){
        // 랜덤을 정의한 함수이다
        // s 는 시작값, e는 마지막 값으로 s 부터 e 까지 랜덤한 정수를 반환하게 된다.
        // Math.random()은 0부터 1미만의 값을 반환하게 되는 함수이다.
        // 따라서 Math.random()에 최대와 최소 사이의 값을 곱하고 최소를 더하면
        // 원하는 범위에서 랜덤한 정수를 가져올 수 있게 된다.
        int n = (int) (Math.random() * ( e - s + 1 ) ) + s;

        return n;
    }

    // 랜덤을 정의한 또다른 함수이다 이번에는 overlap 으로 중복을 방지해야할 배열을 받아서 중복을 방지한다.
    private int random(int s, int e, int[] overlap){

        // 랜덤을 구하는 부분은 동일하다
        int n = (int) (Math.random() * ( e - s + 1 ) ) + s;

        // 중복을 방지하는 부분
        // 일단 int[]를 Integer[]로 변환을 한다.
        Integer[] newArray = new Integer[overlap.length];
        int i = 0;
        for (int value : overlap) {
            newArray[i++] = value;
        }

        // 만약 배열에 새로운 랜덤값이 존재한다면 랜덤값을 다시 만들고 존재하지 않으면 랜덤값을 반환하게 된다.
        while(Arrays.asList(newArray).contains(n)){
            n = (int) (Math.random() * ( e - s + 1 ) ) + s;
        }

        return n;
    }

    // 2차원 배열을 해당 과와 단어의 순번을 조합하여서 단어 고유 아이디를 저장한 1차원 배열로 변환한다.
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

    // 배열의 순서를 섞어주는 함수이다.
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

    // 로그를 찍기 위해서 만든 함수 실제 사용은 안함
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

    // 배열 반환
    int[] getWords(){
        return words;
    }

    // 배열 반환
    int[] getSen(){
        return sen;
    }
}

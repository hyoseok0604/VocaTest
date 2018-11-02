package com.jhsapps.vocatest;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    /**
     * 변수 선언
     */

    private WordDownloader wordDownloader = null;
    private TestBuildTool testBuildTool = null;
    private InputMethodManager imm = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /**
         * 객체 선언
         * @see WordDownloader#WordDownloader(Context, WordDownloader.WordDownloaderListener)
         */

        wordDownloader = new WordDownloader(this, new WordDownloader.WordDownloaderListener() {

            /**
             * 커스텀 Listener 객체
             * 단어 다운로드가 끝나면 실행할 내용들을 적는다
             * @see WordDownloader.WordDownloaderListener#onWordDownloadEnd()
             */
            @Override
            public void onWordDownloadEnd() {
                /**
                 * testBuildTool 객체 선언
                 *
                 * testBuildTool = new TestBuildTool(wordDownloader);
                 * wordDownloader을 testBuildTool의 생성인자로 넘겨준다
                 * @see TestBuildTool#TestBuildTool(WordDownloader)
                 *
                 * testBuildTool.build();
                 * testBuildTool의 build함수를 실행한다
                 * @see TestBuildTool#build()
                 *
                 * testBuildTool.checkWordLength();
                 * testBuildTool의 checkWordLength함수를 실행한다
                 * @see TestBuildTool#checkWordLength()
                 *
                 * showTest();
                 * showTest함수를 실행한다
                 * @see MainActivity#showTest()
                 */

                testBuildTool = new TestBuildTool(wordDownloader);
                testBuildTool.build();
                testBuildTool.checkWordLength();

                showTest();
            }
        });

        /**
         * wordDownloader.parse();
         * wordDownloader의 parse함수를 실행한다
         * @see WordDownloader#parse()
         *
         * imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
         * imm객체를 생성한다
         */

        wordDownloader.parse();

        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
    }

    /**
     * 단어 시험을 보여주는 함수
     */

    public void showTest(){
        /**
         * activity_main.xml에서 id가 R.id.testmain인 LinearLayout을 가져온다
         */

        LinearLayout ll = findViewById(R.id.testmain);

        /**
         * words, sen 배열에 각각 getWords, getSen을 저장한다
         *
         * int 배열에는 각 단어에 지정되어있는 고유 아이디가 들어가있다
         */

        int[] words = testBuildTool.getWords();
        int[] sen = testBuildTool.getSen();

        /**
         * foreach문을 이용해서 단어와 문장을 화면에 띄워준다
         */

        int i = 0;
        for(int id : words){
            makeWord(ll, id, i++);
        }

        for(int id : sen){
            makeSen(ll, id, i++);
        }
    }

    /**
     * 단어를 화면에 띄워주는 함수
     * @param ll 단어를 띄울 대상 레이아웃
     * @param id 단어의 고유 아이디 1~250
     * @param index 단어시험의 번호 1, 2, 3, 4, 5 ...
     */

    public void makeWord(LinearLayout ll, int id, final int index){
        // 레이아웃 인플레이터 생성
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

        // layout폴더에 row_word파일의 전체 내용을 v에 넣어준다
        ViewGroup v = (ViewGroup) inflater.inflate(R.layout.row_word, null);

        // row_word파일의 0번째 자식 뷰를 가져온다
        TextView kor = (TextView) v.getChildAt(0);
        // eng 그룹에는 1번째 자식의 0번째 자식을 가져오게 된다.
        final ViewGroup eng = (ViewGroup) ((ViewGroup) v.getChildAt(1)).getChildAt(0);

        // 단어에 정해져있는 고유 id를 이용해서 영어 단어를 가져온다
        String engWord = wordDownloader.getWordEng(id);
        // 단어에 정해져있는 고유 id를 이용해서 뜻을 가져온다
        String korWord = index + 1 + ". " + wordDownloader.getWordKor(id);

        // 태그를 달아주는데 이 태그는 나중에 단어를 채점할때에 사용을 하기 위해서 단어의 길이를 저장한다
        eng.setTag(engWord.length());

        // 불러온 한글을 kor의 텍스트로 설정한다
        kor.setText(korWord);

        // eng의 뷰의 자식의 개수만큼 반복한다
        for(int i = 0 ; i < eng.getChildCount() ; i++){
            final int a = i;
            final TextView one = (TextView) eng.getChildAt(i);

            one.setTextColor(getResources().getColor(R.color.black));

            if(i == eng.getChildCount() - 1) {
                if (i < engWord.length()) {
                    one.setBackgroundResource(R.drawable.background_default_last);
                }else{
                    one.setBackgroundResource(R.drawable.background_gray_last);
                    one.setEnabled(false);
                }
            }else {
                if (i < engWord.length()) {
                    one.setBackgroundResource(R.drawable.background_default);
                }else{
                    one.setBackgroundResource(R.drawable.background_gray);
                    one.setEnabled(false);
                }
            }

            if(i + 1 == index % 8 + 1){
                one.setText(engWord.substring(i, i+1));
                one.setEnabled(false);
                one.setTextColor(getResources().getColor(R.color.word_disablecolor));
            }else if (i < engWord.length()){
                one.setTag(engWord.substring(i, i+1));
            }

            one.setInputType(InputType.TYPE_CLASS_TEXT);
            one.setPrivateImeOptions("defaultInputmode=english;");

            if (((index % 8 + 1 != engWord.length()) && i < engWord.length() - 1) || (index % 8 + 1 == engWord.length()) && i < engWord.length() - 2) {
                one.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (s.length() == 1) {
                            int b = (a + 1 == index % 8) ? a + 2 : a + 1;
                            eng.getChildAt(b).setFocusableInTouchMode(true);
                            eng.getChildAt(b).requestFocus();
                            imm.hideSoftInputFromWindow(eng.getChildAt(a).getWindowToken(), 0);
                            imm.showSoftInput(eng.getChildAt(b), 0);
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
            }else{
                one.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (s.length() == 1) {
                            imm.hideSoftInputFromWindow(eng.getChildAt(a).getWindowToken(), 0);
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
            }
        }
        ll.addView(v);
    }

    public void makeSen(LinearLayout ll, int id, int index){
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        ViewGroup v = (ViewGroup) inflater.inflate(R.layout.row_sen, null);
        ViewGroup word = (ViewGroup) v.getChildAt(1);

        boolean isSenFNull = wordDownloader.getWordSenF(id) == null;

        String senF = index + 1 + ".\n" + (isSenFNull ? "" :  wordDownloader.getWordSenF(id) + " ");
        String senB = " " + wordDownloader.getWordSenB(id) + "\n";
        final String engWord = wordDownloader.getWordEng(id);

        boolean isWordLen3 = engWord.length() == 3;

        TextView tv0 = (TextView) v.getChildAt(0);
        TextView tv1 = (TextView) word.getChildAt(0);
        TextView tv3 = (TextView) word.getChildAt(2);
        final TextView tv5 = (TextView) v.getChildAt(2);

        final EditText et2 = (EditText) word.getChildAt(1);
        final EditText et4 = (EditText) word.getChildAt(3);

        if (isSenFNull){
            tv0.setVisibility(View.INVISIBLE);
        } else {
            tv0.setText(senF);
        }
        tv1.setText(isSenFNull ? engWord.substring(0, 1).toUpperCase() : engWord.substring(0, 1));
        tv3.setText(engWord.substring(2, 3));
        tv5.setText(senB);

        et2.setTag(engWord.substring(1, 2));
        et4.setTag(engWord.substring(3));
        et4.setEms(engWord.length() - 3);

        et2.setInputType(InputType.TYPE_CLASS_TEXT);
        et2.setPrivateImeOptions("defaultInputmode=english;");

        et4.setInputType(InputType.TYPE_CLASS_TEXT);
        et4.setPrivateImeOptions("defaultInputmode=english;");
        et4.setFilters(new InputFilter[]{new InputFilter.LengthFilter(engWord.length()- 3)});

        if (isWordLen3){
            et4.setVisibility(View.INVISIBLE);
        }else{
            et2.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.length() == 1){
                        et4.setFocusableInTouchMode(true);
                        et4.requestFocus();
                        imm.hideSoftInputFromWindow(et2.getWindowToken(), 0);
                        imm.showSoftInput(et4, 0);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            et4.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.length() == engWord.length() - 3){
                        imm.hideSoftInputFromWindow(et4.getWindowToken(), 0);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
        }

        ll.addView(v);
    }

    private void check(){
        LinearLayout ll = findViewById(R.id.testmain);

        int[] words = testBuildTool.getWords();
        int[] sen = testBuildTool.getSen();

        for (int i = 0 ; i < 16 ; i++){
            ViewGroup v = (ViewGroup) ll.getChildAt(i);
            TextView kor = (TextView) v.getChildAt(0);

            kor.append("\n정답 : " + wordDownloader.getWordEng(words[i]));

            ViewGroup eng = (ViewGroup) ((ViewGroup) v.getChildAt(1)).getChildAt(0);

            kor.setTextColor(getResources().getColor(R.color.word_disableblue));

            for(int j = 0 ; j < (int) eng.getTag() ; j++){
                if(j + 1 == i % 8 + 1) continue;
                TextView one = (TextView) eng.getChildAt(j);

                one.setEnabled(false);

                if (one.getText().toString().equals(one.getTag())) one.setTextColor(getResources().getColor(R.color.word_disableblue));
                else {
                    one.setTextColor(getResources().getColor(R.color.word_disablered));
                    kor.setTextColor(getResources().getColor(R.color.word_disablered));
                }
            }
        }

        for (int i = 16 ; i < 20 ; i++){
            ViewGroup vg = ((ViewGroup) ((ViewGroup) ll.getChildAt(i)).getChildAt(1));

            TextView sentv = (TextView) ((ViewGroup) ll.getChildAt(i)).getChildAt(0);

            sentv.setText(sentv.getText().toString().replaceAll((i + 1) + ".", (i + 1) + ". 정답 : " + wordDownloader.getWordEng(sen[i-16])));

            EditText word1 = (EditText) vg.getChildAt(1);
            EditText word2 = (EditText) vg.getChildAt(3);

            boolean isCorrect = word1.getText().toString().equals(word1.getTag()) && word2.getText().toString().equals(word2.getTag());

            int c = isCorrect ? getResources().getColor(R.color.word_disableblue) : getResources().getColor(R.color.word_disablered);

            for (int j = 0 ; j < vg.getChildCount() ; j++){
                ((TextView) vg.getChildAt(j)).setTextColor(c);
            }

            vg = (ViewGroup) ll.getChildAt(i);
            ((TextView) vg.getChildAt(0)).setTextColor(c);
            ((TextView) vg.getChildAt(2)).setTextColor(c);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getTitle().equals("채점하기")) {
            check();
            item.setTitle("새로운 문제");
        }else if(item.getTitle().equals("새로운 문제")){
            LinearLayout ll = findViewById(R.id.testmain);

            ll.removeAllViews();

            testBuildTool.build();
            testBuildTool.checkWordLength();

            showTest();
            item.setTitle("채점하기");
        }
        return super.onOptionsItemSelected(item);
    }
}

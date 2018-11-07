package com.jhsapps.vocatest;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

class WordDownloader {

    private Context context = null;

    //private final String[] fields = new String[]{"id", "eng", "kor", "sen_f", "sen_b", "unit"};

    private WordDB wordDB = null;

    private WordDownloaderListener wordDownloaderListener = null;

    WordDownloader(Context context, WordDownloaderListener wordDownloaderListener){
        this.context = context;
        wordDB = new WordDB(context);
        this.wordDownloaderListener = wordDownloaderListener;
    }

    void parse(){
        if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean("isDownloadWord", false)){
            wordDownloaderListener.onWordDownloadEnd(true);
            return;
        }

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo == null || !networkInfo.isConnected()){
            Toast.makeText(context, "인터넷 연결이 필요합니다.", Toast.LENGTH_SHORT).show();
            wordDownloaderListener.onWordDownloadEnd(false);
            return;
        }

        WordDownloadTask wordDownloadTask = new WordDownloadTask();
        wordDownloadTask.execute();
    }

    /* table query :
    CREATE TABLE words_15_to_20(
    id    INTEGER  NOT NULL PRIMARY KEY
    ,eng   VARCHAR(13) NOT NULL
    ,kor   VARCHAR(22) NOT NULL
    ,sen_f VARCHAR(81)
    ,sen_b VARCHAR(80) NOT NULL
    ,unit  INTEGER  NOT NULL
    );
     */

    private class WordDownloadTask extends AsyncTask<Void, String, String[]>{

        @Override
        protected String[] doInBackground(Void... voids) {
            StringBuilder result = new StringBuilder();
            String[] queries = null;
            try {
                URL url = new URL("http://jhsapps.com/myfolder/word.php");
                HttpURLConnection huc = (HttpURLConnection) url.openConnection();
                huc.connect();

                BufferedReader br = new BufferedReader(new InputStreamReader(huc.getInputStream(), "UTF-8"));

                String line;
                while ((line = br.readLine()) != null){
                    result.append(line).append("\n");
                }

                JSONArray jsonArray = new JSONArray(result.toString());

                queries = new String[jsonArray.length()];

                for (int i = 0 ; i < jsonArray.length() ; i++){
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    queries[i] = buildQuery(jsonObject);
                }
            }catch(Exception e){
                e.printStackTrace();
            }

            return queries;
        }

        @Override
        protected void onPostExecute(String[] strings) {
            super.onPostExecute(strings);

            for(String query : strings){
                wordDB.query(query);
            }

            wordDownloaderListener.onWordDownloadEnd(true);

            PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean("isDownloadWord", true).apply();
        }
    }


    // ex)  INSERT INTO words_15_to_20(id,kor,eng,sen_f,sen_b,unit) VALUES (1,'attitude','태도','First impressions include their facial expressions, the way they talk, and their','.',16);
    private String buildQuery(JSONObject jsonObject) throws JSONException {
        StringBuilder result = new StringBuilder();

        result
                .append("INSERT INTO words_15_to_20(id,eng,kor,sen_f,sen_b,unit) VALUES (")
                .append(jsonObject.getString("id"))
                .append(",'")
                .append(jsonObject.getString("eng"))
                .append("','")
                .append(jsonObject.getString("kor"))
                .append("',")
                .append(jsonObject.isNull("sen_f") ? "NULL" : "'"+jsonObject.getString("sen_f").replaceAll("'", "''")+"'")
                .append(",'")
                .append(jsonObject.getString("sen_b").replaceAll("'", "''"))
                .append("',")
                .append(jsonObject.getString("unit"))
                .append(");");

        return result.toString();
    }

    String getWordEng(int id){
        return wordDB.getWordEng(id);
    }

    String getWordKor(int id){
        return wordDB.getWordKor(id);
    }

    String getWordSenF(int id){
        return wordDB.getWordSenF(id);
    }

    String getWordSenB(int id){
        return wordDB.getWordSenB(id);
    }

    private class WordDB extends SQLiteOpenHelper{

        WordDB(Context context){
            super(context, "jhsapps", null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE words_15_to_20(id INTEGER NOT NULL PRIMARY KEY, kor VARCHAR(22) NOT NULL, eng VARCHAR(13) NOT NULL, sen_f VARCHAR(81),sen_b VARCHAR(80) NOT NULL, unit INTEGER NOT NULL);");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }

        public void query(String q){
            SQLiteDatabase db = getWritableDatabase();
            db.execSQL(q);
        }


        String getWordEng(int id){
            SQLiteDatabase db = getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT `eng` FROM `words_15_to_20` WHERE id=" + id, null);
            cursor.moveToNext();
            String s = cursor.getString(0);
            cursor.close();
            return s;
        }

        String getWordSenF(int id){
            SQLiteDatabase db = getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT `sen_f` FROM `words_15_to_20` WHERE id=" + id, null);
            cursor.moveToNext();
            String s = cursor.getString(0);
            cursor.close();
            return s;
        }

        String getWordSenB(int id){
            SQLiteDatabase db = getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT `sen_b` FROM `words_15_to_20` WHERE id=" + id, null);
            cursor.moveToNext();
            String s = cursor.getString(0);
            cursor.close();
            return s;
        }

        String getWordKor(int id){
            SQLiteDatabase db = getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT `kor` FROM `words_15_to_20` WHERE id=" + id, null);
            cursor.moveToNext();
            String s = cursor.getString(0);
            cursor.close();
            return s;
        }
    }

    public interface WordDownloaderListener{
        void onWordDownloadEnd(boolean success);
    }

}

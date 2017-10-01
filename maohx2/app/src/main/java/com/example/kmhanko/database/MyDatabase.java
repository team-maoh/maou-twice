package com.kmhanko.database;

import android.content.Context;
import java.io.IOException;
import java.sql.SQLException;
import android.database.sqlite.SQLiteDatabase;

import android.database.Cursor;

//配列関係
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;

import com.kmhanko.sound.SoundAdmin;

/**
 * Created by user on 2017/09/03.
 * version : 1.00
 */

public class MyDatabase {
    Context mContext;

    private SQLiteDatabase db;
    private MySQLiteOpenHelper mDbHelper;

    private String db_name;
    private String db_asset;
    private int db_version;

    public MyDatabase(Context context) {
        super();
        mContext = context;
    }

    public boolean init(String _db_name, String _db_asset, int _db_version, String load_mode) {
        if (load_mode != "r" && load_mode != "w") {
            System.out.println("dg_mes:" + "MyDatabase : please set r or w");
            return false;
        }
        db_name = _db_name;
        db_asset = _db_asset;
        db_version = _db_version;

        //データベース関係の処理
        mDbHelper = new MySQLiteOpenHelper(mContext, db_name, db_asset, db_version);//ヘルパーのインスタンス

        try {
            if (load_mode == "r") {
                //DBファイルがあろうがなかろうが、Assetsからコピー
                mDbHelper.copyDataBaseFromAssets();

                //TODO:コピーが成功したファイルがちゃんとあるかどうかチェック

                //DBファイルをdbに格納する。
                db = mDbHelper.openDataBase("r");
            }
            if (load_mode == "w") {
                //DBファイルが無い場合はAssetsからコピーし、DBファイルがある場合は特に何もしない関数
                mDbHelper.createEmptyDataBase_w();

                //DBファイルをdbに格納する。
                db = mDbHelper.openDataBase("w");
            }
        } catch (IOException e) {
            //TODO : エラー処理
            e.printStackTrace();
        }

        return true;
    }

    //'をつけた文字列を返す関数
    public String s_quo (String str) {
        return "'"+str+"'";
    }

    //query
    public Cursor query (String table,
                         String[] columns,
                         String selection,
                         String[] selectionArgs,
                         String groupBy,
                         String having,
                         String orderBy,
                         String limit) {
        return db.query(table,columns,selection,selectionArgs,groupBy,having,orderBy,limit);
    }


    //カーソル関係
    public Cursor getCursor(String sql_script) {
        return db.rawQuery(sql_script , null);
    }
    private Cursor getCursor(String t_name, String c_name) {
        return db.rawQuery("SELECT "+c_name+" FROM "+t_name, null);
    }
    private Cursor getCursor(String t_name, String c_name, String w_script) {
        if (w_script == null) {
            return getCursor(t_name, c_name);
        }
        return db.rawQuery("SELECT "+c_name+" FROM "+t_name+" WHERE "+w_script,null);
    }


    //データベースからデータを取得する関数
    //TODO:LIKE,GLOBなどをオーバーライドして対応させる
    public List<Integer> getRowID(String t_name, String w_script) {
        return getInt(t_name, "rowid", w_script);
    }

    public int getOneRowID(String t_name, String w_script) {
        List<Integer> buf = getInt(t_name, "rowid", w_script);
        if (buf.size() == 1) {
            return buf.get(0);
        } else {
            System.out.println("dg_mes:" + "MyDatabase.getOneRowID : Found rowids are not only one.");
            return buf.get(0);
        }
    }

    public int getOneRowIDForArray(String t_name, String w_script) {
        //System.out.println("dg_mes:" + getOneRowID(t_name, w_script));
        return getOneRowID(t_name, w_script) - 1;
    }

    public List<Integer> getInt(String t_name, String c_name) {
        return getInt(t_name, c_name, null);
    }
    public List<Integer> getInt(String t_name, String c_name, String w_script) {
        Cursor c = getCursor(t_name, c_name, w_script);
        List<Integer> buf = new ArrayList<Integer>();
        //該当要素が存在しない場合
        if (c.getCount() == 0) {
            return null;
        }
        boolean isEof = c.moveToFirst();
        while (isEof) {
            buf.add(c.getInt(0));
            isEof = c.moveToNext();
        }
        c.close();
        return buf;
    }

    public List<String> getString(String t_name, String c_name) {
        return getString(t_name, c_name, null);
    }
    public List<String> getString(String t_name, String c_name, String w_script) {
        Cursor c = getCursor(t_name, c_name, w_script);
        List<String> buf = new ArrayList<String>();
        //該当要素が存在しない場合
        if (c.getCount() == 0) {
            return buf;
        }
        boolean isEof = c.moveToFirst();
        while (isEof) {
            buf.add(c.getString(0));
            isEof = c.moveToNext();
        }
        c.close();
        return buf;
    }

    public List<Double> getDouble(String t_name, String c_name) {
        return getDouble(t_name, c_name, null);
    }
    public List<Double> getDouble(String t_name, String c_name, String w_script) {
        Cursor c = getCursor(t_name, c_name, w_script);
        List<Double> buf = new ArrayList<Double>();
        //該当要素が存在しない場合
        if (c.getCount() == 0) {
            return buf;
        }
        boolean isEof = c.moveToFirst();
        while (isEof) {
            buf.add(c.getDouble(0));
            isEof = c.moveToNext();
        }
        c.close();
        return buf;
    }

    public List<Float> getFloat(String t_name, String c_name) {
        return getFloat(t_name, c_name, null);
    }
    public List<Float> getFloat(String t_name, String c_name, String w_script) {
        Cursor c = getCursor(t_name, c_name, w_script);
        List<Float> buf = new ArrayList<Float>();
        //該当要素が存在しない場合
        if (c.getCount() == 0) {
            return buf;
        }
        boolean isEof = c.moveToFirst();
        while (isEof) {
            buf.add(c.getFloat(0));
            isEof = c.moveToNext();
        }
        c.close();
        return buf;
    }
    public List<String> getOneLine(String t_name, int rowid) {
        return getOneLine(t_name,"rowid = "+rowid);
    }

    public List<String> getOneLine(String t_name, String w_script) {
        Cursor c = getCursor(t_name, "*", w_script);
        List<String> buf = new ArrayList<String>();
        if (c.getCount() == 0) {
            return buf;
        }
        c.moveToFirst();
        for(int i=0; i < c.getColumnCount(); i++) {
            buf.add(c.getString(i));
        }
        c.close();
        return buf;
    }

    //データベースにデータを書き出す関数
    public void execSQL(String sql) {
        db.execSQL(sql);
    }


    private ContentValues makeContentValues(String[] c_names, String[] values) {
        if (c_names.length != values.length) {
            throw new Error("MyDatabase.insert : c_names.size != values.size");
        }
        int size = c_names.length;

        //ContentValuesに格納する
        ContentValues content_values = new ContentValues(size);
        for(int i = 0; i < size; i++) {
            content_values.put(c_names[i], values[i]);
        }
        return content_values;
    }

    public boolean insert(String t_name, String[] c_names, int[] values) {
        String[] new_values = new String[values.length];
        for(int i = 0; i < new_values.length; i++){
            new_values[i] = String.valueOf(values[i]);
        }
        return insert(t_name, c_names, new_values);
    }

    public boolean insert(String t_name, String[] c_names, String[] values) {
        //ContentValuesを作成する。
        ContentValues content_values = makeContentValues(c_names, values);

        //insertを行う
        //TODO:例外処理
        long id = db.insert(t_name, null, content_values);
        if (id == -1) {
            return false;
        }
        return true;
    }

    //返り値は削除された項目数
    public int delete(String t_name, String w_script) {
        return db.delete(t_name, w_script, null);
    }

    //返り値は変更された項目数
    public int rewrite(String t_name, String[] c_names, String[] values, String w_script) {
        //ContentValuesを作成する。
        ContentValues content_values = makeContentValues(c_names, values);

        //書き換えを行う
        return db.update(t_name, content_values, w_script, null);
    }

    //ゲッター
    public String getDbName() {
        return db_name;
    }
    public String getDbAsset() { return db_asset; }
    public int getDbVersion() { return db_version; }


    public void close() {
        db.close();
        mDbHelper.close();
    }

    /***ここから旧版***/

    /*
    private String[] makeRowIDAndNameColums(String c_name) {
        String cl[] = new String[2];
        cl[0] = "rowid";
        cl[1] = c_name;
        return cl;
    }

    //あるカラムにおける全ての値を返す。
    public List<Integer> getIntAll(String t_name, String c_name) {
        String cl[] = new String[1];
        cl[0] = c_name;

        Cursor c = db.query(t_name, cl, null, null, null, null, null);
        List<Integer> buf = new ArrayList<Integer>();

        boolean isEof = c.moveToFirst();
        while (isEof) {
            buf.add(c.getInt(0));
            isEof = c.moveToNext();
        }
        return buf;
    }

    public List<String> getStringAll(String t_name, String c_name) {
        String cl[] = new String[1];
        cl[0] = c_name;

        Cursor c = db.query(t_name, cl, null, null, null, null, null);
        List<String> buf = new ArrayList<String>();

        boolean isEof = c.moveToFirst();
        while (isEof) {
            buf.add(c.getString(0));
            isEof = c.moveToNext();
        }
        return buf;
    }

    public List<Double> getDoubleAll(String t_name, String c_name) {
        String cl[] = new String[1];
        cl[0] = c_name;

        Cursor c = db.query(t_name, cl, null, null, null, null, null);
        List<Double> buf = new ArrayList<Double>();

        boolean isEof = c.moveToFirst();
        while (isEof) {
            buf.add(c.getDouble(0));
            isEof = c.moveToNext();
        }
        return buf;
    }

    //あるROWIDについて、あるカラムにおける値を返す。
    public int getIntFromRowID(String t_name, String c_name, int r_id) {
        String cl[] = makeRowIDAndNameColums(c_name);
        Cursor c = db.query(t_name, cl, "rowid == " + r_id, null, null, null, null);
        if (c.getCount() == 0) {
            return -1;
        }
        c.moveToFirst();
        return c.getInt(1);
    }

    public String getStringFromRowID(String t_name, String c_name, int r_id) {
        String cl[] = makeRowIDAndNameColums(c_name);
        Cursor c = db.query(t_name, cl, "rowid == " + r_id, null, null, null, null);
        if (c.getCount() == 0) {
            return null;
        }
        c.moveToFirst();
        return c.getString(1);
    }

    public double getDoubleFromRowID(String t_name, String c_name, int r_id) {
        String cl[] = makeRowIDAndNameColums(c_name);
        Cursor c = db.query(t_name, cl, "rowid == " + r_id, null, null, null, null);
        if (c.getCount() == 0) {
            return -1.0;
        }
        c.moveToFirst();
        return c.getDouble(1);
    }
    */

    /*
    //あるカラムにおいて、該当する値であるものの、ROWIDの配列を返す
    public List<Integer> getRowID(String t_name, String selection) {
        String[] buf = selection.split(" ", 0);
        String cl[] = makeRowIDAndNameColums(buf[0]);

        String buf2[] = new String[1];
        buf2[0] = buf[2];

        Cursor c = db.query(t_name, cl, buf[0] + " " + buf[1] + " ?", buf2, null, null, null);

        //Cursor c = db.query(t_name, cl , buf[0] +" == "+ buf[2], null, null, null, null);
        //Cursor c = db.query(t_name, cl , selection, null, null, null, null);
        //Cursor c = db.query(t_name, new String[] { "rowid", "score" } , "score == 50" , null, null, null, null);

        //System.out.println("dg_mes:"+c.getCount());

        if (c.getCount() == 0) {
            return null;
        }

        List<Integer> r_id = new ArrayList<Integer>();

        boolean isEof = c.moveToFirst();
        while (isEof) {
            r_id.add(c.getInt(0));
            isEof = c.moveToNext();
        }

        return r_id;
    }

    public int getOneRowID(String t_name, String selection) {
        List<Integer> buf = getRowID(t_name, selection);
        if (buf.size() == 1) {
            return buf.get(0);
        } else {
            //TODO:わかりやすい値
            return -999999;
        }
    }

    public int getOneRowIDForArray(String t_name, String selection) {
        return getOneRowID(t_name, selection) - 1;
    }
*/


}

/*
データベースの内容は他のメンバーの編集する。
場合によっては、項目が増えることもあるし、テーブルが増えることもある。
テーブルには各列に名前付けられるから、その名前によって指定してもらってこられると良いかもしれん。
*/

/*
あるテーブルに対して

あるカラムにおいて、該当する値であるものの、ROWIDの配列・大きさを返す
あるROWIDについて、あるカラムにおける値を返す。返り値の型も、関数の名前という形で指定する






*/







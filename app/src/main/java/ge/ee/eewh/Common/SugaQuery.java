package ge.ee.eewh.Common;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

//import com.ktg.ktginspector.ktginspector;
import com.orm.Database;

import ge.ee.eewh.eewhapp;

/**
 * Created by beka-work on 25.09.2017.
 */

public class SugaQuery {
//    public static String FreeQuery(ktginspector app, String Query){
//
//        Database db = app.GetSugaDb();
//        SQLiteDatabase sqLiteDatabase = db.getDB();
//
//        SQLiteStatement sqLiteStatament = sqLiteDatabase
//                .compileStatement(Query);
//
////        try {
////            Result = sqLiteStatament.simpleQueryForString();
////        } catch (Exception e) {
////            e.printStackTrace();
////        } finally {
////            sqLiteStatament.close();
////        }
//        return "";
//    }
//
    public static String[] ReadColumnNames(String TableName){
        Database db = eewhapp.app.GetSugaDb();
        SQLiteDatabase sqLiteDatabase = db.getDB();

        Cursor dbCursor = sqLiteDatabase.query(TableName, null, null, null, null, null, null);
        String[] columnNames = dbCursor.getColumnNames();
        return columnNames;
    }
}

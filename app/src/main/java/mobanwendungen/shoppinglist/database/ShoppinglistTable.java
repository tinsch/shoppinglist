package mobanwendungen.shoppinglist.database;


import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class ShoppinglistTable {

    public static final String TABLE_SHOPPINGLIST = "shoppinglist";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_CATEGORY = "category";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_DESCRIPTION = "description";


    public static final String DATABASE_CREATE = " create table "
            + TABLE_SHOPPINGLIST
            + "("
            + COLUMN_ID + " integer primary key, "
            + COLUMN_CATEGORY + " text not null, "
            + COLUMN_TITLE + " text not null,"
            + COLUMN_DESCRIPTION + " text not null"
            + ");";

    private static final String DATABASE_DROP = " DROP TABLE if exists " + TABLE_SHOPPINGLIST + ";";

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    public static void dropTable(SQLiteDatabase database){
        database.execSQL(DATABASE_DROP);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        Log.w(ShoppinglistTable.class.getName(),
                "Upgrading database from version " + oldVersion + " to " + newVersion
                + ", which will destroy all old data");
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_SHOPPINGLIST + ";");
        onCreate(database);
    }
}

package mobanwendungen.shoppinglist.database;


import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class ShoppinglistTable {

    public static final String TABLE_SHOPPINGLIST = "shoppinglist";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TITLE = "title";

    private static final String DATABASE_CREATE = " create table "
            + TABLE_SHOPPINGLIST
            + "("
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_TITLE + " text not null,"
            + ");";

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        Log.w(ShoppinglistTable.class.getName(),
                "Upgrading database from version " + oldVersion + " to " + newVersion
                + ", which will destroy all old data");
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_SHOPPINGLIST + ";");
        onCreate(database);
    }
}

package mobanwendungen.shoppinglist.database;


import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class ShoppingItemTable {

    public static final String TABLE_SHOPPINGITEM = "shoppingitem";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_SHOPPINGLISTKEY = "shoppinglistkey";
    public static final String FOREIGN_KEY_STRING = "shoppinglist(_id)";

    private static final String DATABASE_CREATE = " create table "
            + TABLE_SHOPPINGITEM
            + "("
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_TITLE + " text not null, "
            + COLUMN_DESCRIPTION + " text, "
            + COLUMN_SHOPPINGLISTKEY + " integer not null, FOREIGN KEY("
            + COLUMN_SHOPPINGLISTKEY + ") REFERENCES "
            + FOREIGN_KEY_STRING
            + ");";

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        Log.w(ShoppinglistTable.class.getName(),
                "Upgrading database from version " + oldVersion + " to " + newVersion
                        + ", which will destroy all old data");
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_SHOPPINGITEM + ";");
        onCreate(database);
    }

}

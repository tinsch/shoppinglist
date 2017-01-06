package mobanwendungen.shoppinglist.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ShoppinglistDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "shoppinglisttable.db";
    private static final int DATABASE_VERSION = 3;

    public ShoppinglistDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        ShoppinglistTable.onCreate(database);
        ShoppingItemTable.onCreate(database);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        ShoppinglistTable.onUpgrade(database, oldVersion, newVersion);
        ShoppingItemTable.onUpgrade(database, oldVersion, newVersion);
    }
}


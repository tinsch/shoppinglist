package mobanwendungen.shoppinglist.contentprovider;

        import java.util.Arrays;
        import java.util.HashSet;

        import android.content.ContentProvider;
        import android.content.ContentResolver;
        import android.content.ContentValues;
        import android.content.UriMatcher;
        import android.database.Cursor;
        import android.database.sqlite.SQLiteDatabase;
        import android.database.sqlite.SQLiteQueryBuilder;
        import android.net.Uri;
        import android.text.TextUtils;
        import mobanwendungen.shoppinglist.database.ShoppinglistDatabaseHelper;
        import mobanwendungen.shoppinglist.database.ShoppinglistTable;

public class ShoppinglistContentProvider extends ContentProvider {

    // database
    private ShoppinglistDatabaseHelper database;

    // used for the UriMatcher
    private static final int SHOPPINGLIST = 10;
    private static final int SHOPPINGLIST_ITEM_ID = 20;

    private static final String AUTHORITY = "mobanwendungen.shoppinglist.contentprovider";

    private static final String BASE_PATH = "shoppinglist";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
            + "/" + BASE_PATH);

    public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
            + "/shoppinglist";
    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
            + "/shoppinglist";

    private static final UriMatcher sURIMatcher = new UriMatcher(
            UriMatcher.NO_MATCH);
    static {
        sURIMatcher.addURI(AUTHORITY, BASE_PATH, SHOPPINGLIST);
        sURIMatcher.addURI(AUTHORITY, BASE_PATH + "/#", SHOPPINGLIST_ITEM_ID);
    }

    @Override
    public boolean onCreate() {
        database = new ShoppinglistDatabaseHelper(getContext());
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        // Uisng SQLiteQueryBuilder instead of query() method
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        // check if the caller has requested a column which does not exists
        checkColumns(projection);

        // Set the table
        queryBuilder.setTables(ShoppinglistTable.TABLE_SHOPPINGLIST);

        int uriType = sURIMatcher.match(uri);
        switch (uriType) {
            case SHOPPINGLIST:
                break;
            case SHOPPINGLIST_ITEM_ID:
                // adding the ID to the original query
                queryBuilder.appendWhere(ShoppinglistTable.COLUMN_ID + "="
                        + uri.getLastPathSegment());
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        SQLiteDatabase db = database.getWritableDatabase();
        Cursor cursor = queryBuilder.query(db, projection, selection,
                selectionArgs, null, null, sortOrder);
        // make sure that potential listeners are getting notified
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = database.getWritableDatabase();
        long id = 0;
        switch (uriType) {
            case SHOPPINGLIST:
                id = sqlDB.insert(ShoppinglistTable.TABLE_SHOPPINGLIST, null, values);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return Uri.parse(BASE_PATH + "/" + id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = database.getWritableDatabase();
        int rowsDeleted = 0;
        switch (uriType) {
            case SHOPPINGLIST:
                rowsDeleted = sqlDB.delete(ShoppinglistTable.TABLE_SHOPPINGLIST, selection,
                        selectionArgs);
                break;
            case SHOPPINGLIST_ITEM_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsDeleted = sqlDB.delete(
                            ShoppinglistTable.TABLE_SHOPPINGLIST,
                            ShoppinglistTable.COLUMN_ID + "=" + id,
                            null);
                } else {
                    rowsDeleted = sqlDB.delete(
                            ShoppinglistTable.TABLE_SHOPPINGLIST,
                            ShoppinglistTable.COLUMN_ID + "=" + id
                                    + " and " + selection,
                            selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {

        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = database.getWritableDatabase();
        int rowsUpdated = 0;
        switch (uriType) {
            case SHOPPINGLIST:
                rowsUpdated = sqlDB.update(ShoppinglistTable.TABLE_SHOPPINGLIST,
                        values,
                        selection,
                        selectionArgs);
                break;
            case SHOPPINGLIST_ITEM_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsUpdated = sqlDB.update(ShoppinglistTable.TABLE_SHOPPINGLIST,
                            values,
                            ShoppinglistTable.COLUMN_ID + "=" + id,
                            null);
                } else {
                    rowsUpdated = sqlDB.update(ShoppinglistTable.TABLE_SHOPPINGLIST,
                            values,
                            ShoppinglistTable.COLUMN_ID + "=" + id
                                    + " and "
                                    + selection,
                            selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsUpdated;
    }

    private void checkColumns(String[] projection) {
        String[] available = { ShoppinglistTable.COLUMN_ID,
                ShoppinglistTable.COLUMN_TITLE, ShoppinglistTable.COLUMN_CATEGORY, ShoppinglistTable.COLUMN_DESCRIPTION
                };
        if (projection != null) {
            HashSet<String> requestedColumns = new HashSet<String>(
                    Arrays.asList(projection));
            HashSet<String> availableColumns = new HashSet<String>(
                    Arrays.asList(available));
            // check if all columns which are requested are available
            if (!availableColumns.containsAll(requestedColumns)) {
                throw new IllegalArgumentException(
                        "Unknown columns in projection");
            }
        }
    }

}

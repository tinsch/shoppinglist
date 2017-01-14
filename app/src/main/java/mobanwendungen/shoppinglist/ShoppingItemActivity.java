package mobanwendungen.shoppinglist;

        import android.app.Activity;
        import android.content.ContentValues;
        import android.database.Cursor;
        import android.net.Uri;
        import android.os.Bundle;
        import android.text.TextUtils;
        import android.util.Log;
        import android.view.View;
        import android.widget.Button;
        import android.widget.EditText;
        import android.widget.Spinner;
        import android.widget.Toast;
        import mobanwendungen.shoppinglist.contentprovider.ShoppinglistContentProvider;
        import mobanwendungen.shoppinglist.database.ShoppinglistTable;
        import mobanwendungen.shoppinglist.remotedatabase.OwnQuery;
        import mobanwendungen.shoppinglist.remotedatabase.SynchronizeRemoteDatabase;


public class ShoppingItemActivity extends Activity {
    private Spinner mCategory;
    private EditText mTitleText;
    private EditText mBodyText;
    private SynchronizeRemoteDatabase remoteDB;
    private static String DEBUG_TAG = "ShoppingItemActivity: ";

    private Uri itemUri;
    private long id;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.shoppinglist_item_edit);
        Log.d(DEBUG_TAG, "onCreate() was called.");

        remoteDB = new SynchronizeRemoteDatabase(this);

        mCategory = (Spinner) findViewById(R.id.category);
        mTitleText = (EditText) findViewById(R.id.item_edit_title);
        mBodyText = (EditText) findViewById(R.id.item_edit_description);
        Button confirmButton = (Button) findViewById(R.id.item_edit_button);

        Bundle extras = getIntent().getExtras();

        //Todo: ask Schwotzer?
        // check from the saved Instance
        itemUri = (bundle == null) ? null : (Uri) bundle
                .getParcelable(ShoppinglistContentProvider.CONTENT_ITEM_TYPE);

        // Or passed from the other activity
        if (extras != null) {
        //    Log.d( //itemUri.toString());
            itemUri = extras
                    .getParcelable(ShoppinglistContentProvider.CONTENT_ITEM_TYPE);
            id = extras.getLong("ID");
            fillData(itemUri);
        }

        confirmButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Log.d(DEBUG_TAG, "onCklickListener() was called.");

                if (TextUtils.isEmpty(mTitleText.getText().toString())) {
                    makeToast();
                } else {
                    setResult(RESULT_OK);
                    finish();
                }
            }

        });
    }

    //Todo: geht das nicht leichter zu implementieren?!
    private void fillData(Uri uri) {
        Log.d(DEBUG_TAG, "fillData() was called.");
        String[] projection = { ShoppinglistTable.COLUMN_TITLE,
                ShoppinglistTable.COLUMN_DESCRIPTION, ShoppinglistTable.COLUMN_CATEGORY };
        Cursor cursor = getContentResolver().query(uri, projection, null, null,
                null);
        if (cursor != null) {
            cursor.moveToFirst();
            String category = cursor.getString(cursor
                    .getColumnIndexOrThrow(ShoppinglistTable.COLUMN_CATEGORY));

            for (int i = 0; i < mCategory.getCount(); i++) {

                String s = (String) mCategory.getItemAtPosition(i);
                if (s.equalsIgnoreCase(category)) {
                    mCategory.setSelection(i);
                }
            }

            mTitleText.setText(cursor.getString(cursor
                    .getColumnIndexOrThrow(ShoppinglistTable.COLUMN_TITLE)));
            mBodyText.setText(cursor.getString(cursor
                    .getColumnIndexOrThrow(ShoppinglistTable.COLUMN_DESCRIPTION)));

            // always close the cursor
            cursor.close();
        }
    }

    protected void onSaveInstanceState(Bundle outState) {
        Log.d(DEBUG_TAG, "onSaveInstanceState() was called.");
        super.onSaveInstanceState(outState);
        saveState();
        outState.putParcelable(ShoppinglistContentProvider.CONTENT_ITEM_TYPE, itemUri);
    }

    @Override
    protected void onPause() {
        Log.d(DEBUG_TAG, "onPause() was called.");
        super.onPause();
        saveState();
    }

    private void saveState() {
        Log.d(DEBUG_TAG, "saveState() was called.");
        String category = (String) mCategory.getSelectedItem();
        String itemTitle = mTitleText.getText().toString();
        String description = mBodyText.getText().toString();

        // only save if either itemTitle or description
        // is available

        if (description.length() == 0 && itemTitle.length() == 0) {
            return;
        }

        ContentValues values = new ContentValues();
//        values.put(ShoppinglistTable.COLUMN_ID, )
        values.put(ShoppinglistTable.COLUMN_CATEGORY, category);
        values.put(ShoppinglistTable.COLUMN_TITLE, itemTitle);
        values.put(ShoppinglistTable.COLUMN_DESCRIPTION, description);
        OwnQuery ownQuery = new OwnQuery(category, itemTitle, description);

        if (itemUri == null) {

            // New, if it's a new entry
         //   itemUri = getContentResolver().insert(
           //         ShoppinglistContentProvider.CONTENT_URI, values);
            Log.d(DEBUG_TAG, "remoteDB.insert(ownQuery) called.");
            remoteDB.insert(ownQuery);
        } else {
            // Update, if entry already exists
           // getContentResolver().update(itemUri, values, null, null);
            remoteDB.change(id, ownQuery);
        }
    }

    /*
    Notification, if user forgot to enter a title
     */
    private void makeToast() {
        Toast.makeText(ShoppingItemActivity.this, R.string.error_message_edit_item,
                Toast.LENGTH_LONG).show();
    }
}

package mobanwendungen.shoppinglist;

        import android.app.Activity;
        import android.content.ContentValues;
        import android.database.Cursor;
        import android.net.Uri;
        import android.os.Bundle;
        import android.text.TextUtils;
        import android.view.View;
        import android.widget.Button;
        import android.widget.EditText;
        import android.widget.Spinner;
        import android.widget.Toast;
        import mobanwendungen.shoppinglist.contentprovider.ShoppinglistContentProvider;
        import mobanwendungen.shoppinglist.database.ShoppinglistTable;
        import mobanwendungen.shoppinglist.remotedatabase.SynchronizeRemoteDatabase;


public class ShoppingItemActivity extends Activity {
    private Spinner mCategory;
    private EditText mTitleText;
    private EditText mBodyText;
    private SynchronizeRemoteDatabase remoteDB;

    private Uri itemUri;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.shoppinglist_item_edit);

        remoteDB = new SynchronizeRemoteDatabase();
        remoteDB.connect(this);

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
            itemUri = extras
                    .getParcelable(ShoppinglistContentProvider.CONTENT_ITEM_TYPE);

            fillData(itemUri);
        }

        confirmButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
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
        super.onSaveInstanceState(outState);
        saveState();
        outState.putParcelable(ShoppinglistContentProvider.CONTENT_ITEM_TYPE, itemUri);
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveState();
    }

    private void saveState() {
        String category = (String) mCategory.getSelectedItem();
        String itemTitle = mTitleText.getText().toString();
        String description = mBodyText.getText().toString();

        // only save if either itemTitle or description
        // is available

        if (description.length() == 0 && itemTitle.length() == 0) {
            return;
        }

        ContentValues values = new ContentValues();
        values.put(ShoppinglistTable.COLUMN_CATEGORY, category);
        values.put(ShoppinglistTable.COLUMN_TITLE, itemTitle);
        values.put(ShoppinglistTable.COLUMN_DESCRIPTION, description);

        if (itemUri == null) {
            // New todo
            itemUri = getContentResolver().insert(
                    ShoppinglistContentProvider.CONTENT_URI, values);
        } else {
            // Update todo
            getContentResolver().update(itemUri, values, null, null);
        }
    }

    private void makeToast() {
        Toast.makeText(ShoppingItemActivity.this, R.string.error_message_edit_item,
                Toast.LENGTH_LONG).show();
    }
}
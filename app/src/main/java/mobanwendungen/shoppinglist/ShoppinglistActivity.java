package mobanwendungen.shoppinglist;

import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import mobanwendungen.shoppinglist.contentprovider.ShoppinglistContentProvider;
import mobanwendungen.shoppinglist.database.ShoppinglistTable;
import mobanwendungen.shoppinglist.remotedatabase.SynchronizeRemoteDatabase;


public class ShoppinglistActivity extends ListActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final int ACTIVITY_CREATE = 0;
    private static final int ACTIVITY_EDIT = 1;
    private static final int DELETE_ID = Menu.FIRST + 1;
    private static final String DEBUG_TAG = "ShoppinglistActivity: ";
    // private Cursor cursor;
    private SimpleCursorAdapter adapter;
    SynchronizeRemoteDatabase syncRemoteDB;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(DEBUG_TAG, "onCreate entered.");
        syncRemoteDB = new SynchronizeRemoteDatabase(this);
        syncRemoteDB.fetchData();
        Log.d(DEBUG_TAG, "fetchData called.");
        setContentView(R.layout.shoppinglist_list);
        this.getListView().setDividerHeight(2);
        fillData();
        Log.d(DEBUG_TAG, "fillData() called");
        registerForContextMenu(getListView());
    }

    // create the menu based on the XML defintion
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.listmenu, menu);
        return true;
    }

    // Reaction to the menu selection
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.insert:
                createItem();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            //Todo: check if getItemId always returns 2 when an item is pushed long!?!
            case DELETE_ID:
                AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
                        .getMenuInfo();
                Uri uri = Uri.parse(ShoppinglistContentProvider.CONTENT_URI + "/"
                        + info.id);
                try {
                    getContentResolver().delete(uri, null, null);
                    syncRemoteDB.delete(info.id);
                } catch (IllegalArgumentException e){
                    e.printStackTrace();
                } catch (Exception e){
                    Log.d(DEBUG_TAG, "Any other error when deleting item");
                }
                fillData();
                return true;
        }
        return super.onContextItemSelected(item);
    }

    private void createItem() {
        Intent i = new Intent(this, ShoppingItemActivity.class);
        startActivity(i);
    }

    // Opens the second activity if an entry is clicked
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Intent i = new Intent(this, ShoppingItemActivity.class);
        //Todo: fragen, ob andere Key sinnvoll waere!?
        Uri itemUri = Uri.parse(ShoppinglistContentProvider.CONTENT_URI + "/" + id);
        i.putExtra(ShoppinglistContentProvider.CONTENT_ITEM_TYPE, itemUri);
        i.putExtra("ID", id);
        startActivity(i);
    }



    public void fillData() {
        // Fields from the database (projection)
        // Must include the _id column for the adapter to work
        String[] from = new String[] { ShoppinglistTable.COLUMN_TITLE};
        Log.d(DEBUG_TAG, "in fillData() COLUMN_TITLE:" + ShoppinglistTable.COLUMN_TITLE);
        // Fields on the UI to which we map
        int[] to = new int[] { R.id.text_of_row };

        getLoaderManager().initLoader(0, null, this);
        adapter = new SimpleCursorAdapter(this, R.layout.shoppinglist_row, null, from,
                to, 0);

        setListAdapter(adapter);
    }

    @Override
    //Todo: exchange createContextMenu with small item on the listView (e.g. red cross)
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, DELETE_ID, 0, R.string.menu_delete);
    }

    // creates a new loader after the initLoader () call
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = { ShoppinglistTable.COLUMN_ID, ShoppinglistTable.COLUMN_TITLE};
        CursorLoader cursorLoader = new CursorLoader(this,
                ShoppinglistContentProvider.CONTENT_URI, projection, null, null, null);
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // data is not available anymore, delete reference
        adapter.swapCursor(null);
    }

}


package mobanwendungen.shoppinglist.remotedatabase;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import java.sql.DriverManager;
import java.sql.SQLException;


import mobanwendungen.shoppinglist.ShoppinglistActivity;
import mobanwendungen.shoppinglist.contentprovider.ShoppinglistContentProvider;
import mobanwendungen.shoppinglist.database.ShoppinglistDatabaseHelper;
import mobanwendungen.shoppinglist.database.ShoppinglistTable;

/**
 * Created by l on 07.01.17.
 */

public class SynchronizeRemoteDatabase {

    private static final String DEBUG_TAG = "SynchronizeRemoteDB: ";
    private static String DELETEQUERY = "DELETE FROM shoppinglist WHERE _id = ";
    private static String INSERTQUERY = "INSERT INTO shoppinglist(category, title, description) VALUES ";
    private static String CREATETABLEQUERY = "CREATE TABLE shoppinglist(_id SERIAL primary key, title VARCHAR(15) not null, category VARCHAR(20) not null, description VARCHAR(30) not null);";
    private static String SELECTQUERY = "SELECT * FROM shoppinglist;";
    private Context m_context;
    private String query;


    public SynchronizeRemoteDatabase(Context context){
        m_context = context;
    }

    public void fetchData(){
        this.query = SELECTQUERY;
        connect();
    }

    public void insert(OwnQuery query){
        this.query = INSERTQUERY + query.toString();
        connect();
        fetchData();
    }

    public void delete(long id){
        this.query = DELETEQUERY + id + ";";
        connect();
        fetchData();
    }

    public void createTable(){
        this.query = CREATETABLEQUERY;
        connect();
    }


    public void connect(){
        ConnectivityManager connMgr = (ConnectivityManager)
                m_context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            Log.d(DEBUG_TAG, "NetworkConnection exists");

            // String url =  activity.getResources().getString(R.string.urlText);
            new Download(m_context).execute();
        } else {
            Log.d(DEBUG_TAG, "in connect() Network Connection not existent.");
        }
    }

    /**
     * makes an asynchronous request from URL
     */
    private class Download extends AsyncTask<Void, Void, java.sql.ResultSet > {

        ProgressDialog mProgressDialog;
        Context context;
        private String url;

        public Download(Context context) {
            this.context = context;
           this.url = "jdbc:postgresql://db.f4.htw-berlin.de/_s0551814__shoppinglist";
        }

        protected void onPreExecute() {
            Log.d(DEBUG_TAG, "onPreExecute() in AsyncTask Download is called");
            mProgressDialog = ProgressDialog.show(context, "",
                    "Please wait, getting database...");
        }

        protected java.sql.ResultSet  doInBackground(Void... params) {
            Log.d(DEBUG_TAG, "doInBackground() in AsyncTask Download is called");
            java.sql.ResultSet result = null;
            //  DriverManager.register(new org.postgresql.Driver());
            try {
                Class.forName("org.postgresql.Driver");
            } catch (ClassNotFoundException e){
                e.printStackTrace();
                Log.d(DEBUG_TAG, "Class.forName Failure");
            }
            try {
                java.sql.Connection con = DriverManager.getConnection(url, "_s0551814__shoppinglist_generic", "shoppinglist1234");
                java.sql.Statement st = con.createStatement();
                result = st.executeQuery(query);
                //  list = new ArrayList<objClass>();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            ShoppinglistDatabaseHelper databaseHelper = new ShoppinglistDatabaseHelper(m_context);
            SQLiteDatabase sqlDB = databaseHelper.getWritableDatabase();

            if (result != null) {
                databaseHelper.dropTable(sqlDB);
                databaseHelper.onCreate(sqlDB);
                ContentValues values = new ContentValues();
                Log.d(DEBUG_TAG, "While iteration starts: ");
                try {
                    while (result.next()) {
                        values.put(ShoppinglistTable.COLUMN_ID, result.getString("_id"));
                        values.put(ShoppinglistTable.COLUMN_CATEGORY, result.getString("category"));
                        values.put(ShoppinglistTable.COLUMN_TITLE, result.getString("title"));
                        values.put(ShoppinglistTable.COLUMN_DESCRIPTION, result.getString("description"));
                        m_context.getContentResolver().insert(
                                ShoppinglistContentProvider.CONTENT_URI, values);
                        Log.d(DEBUG_TAG, "Id, Title, Description and Category should have been entered in Table: " + result.getString("_id") + ", " + result.getString("title") + ", " + result.getString("description") + ", " +result.getString("category"));
                    }
                    databaseHelper.close();
                }catch (java.sql.SQLException e){
                    e.printStackTrace();
                }


            }
            return result;
        }

        protected void onPostExecute(java.sql.ResultSet result) {
            mProgressDialog.dismiss();
        }



    }

}

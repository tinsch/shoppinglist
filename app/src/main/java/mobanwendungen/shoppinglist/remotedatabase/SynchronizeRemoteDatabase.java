package mobanwendungen.shoppinglist.remotedatabase;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import java.sql.DriverManager;
import java.sql.SQLException;


import mobanwendungen.shoppinglist.R;

import static android.R.id.list;

/**
 * Created by l on 07.01.17.
 */

public class SynchronizeRemoteDatabase {

    private static final String DEBUG_TAG = "SynchronizeRemoteDB: ";
    private static String DELETEQUERY = "DELETE FROM shoppinglist WHERE _id = ";
    private static String INSERTQUERY = "INSERT INTO shoppinglist(category, title, description) VALUES ";
    private Context m_context;
    private String query;


    public SynchronizeRemoteDatabase(Context context){
        m_context = context;
    }

    public void insert(Query query){
        this.query = INSERTQUERY + query.toString();
        connect();
    }

    public void delete(long id){
        this.query = DELETEQUERY + id + ";";
        connect();
    }

    public void createTable(){
        this.query = "CREATE TABLE shoppinglist(_id SERIAL primary key, category VARCHAR(20) not null, title VARCHAR(15) not null, description VARCHAR(30) not null);";
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
    private class Download extends AsyncTask<Void, Void, String> {

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

        protected String doInBackground(Void... params) {
            Log.d(DEBUG_TAG, "doInBackground() in AsyncTask Download is called");

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
                java.sql.ResultSet rs = st.executeQuery(query);
                //  list = new ArrayList<objClass>();

                while (rs.next()) {
                    String field= rs.getString("field");
                    //        MainActivity.playerList.add(new objectClass(field));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return "Complete";
        }

        protected void onPostExecute(String result) {
            if (result.equals("Complete")) {
                mProgressDialog.dismiss();
            }
        }



    }

}

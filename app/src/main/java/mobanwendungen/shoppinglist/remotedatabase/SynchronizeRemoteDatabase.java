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

    public SynchronizeRemoteDatabase(){

    }

    public void connect(Context context){
        ConnectivityManager connMgr = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            Log.d(DEBUG_TAG, "NetworkConnection exists");

            // String url =  activity.getResources().getString(R.string.urlText);
            String args = "https://offenedaten-koeln.de/api/action/datastore/search.json?resource_id=b6af53d5-82fd-496e-9813-1f69290fee27";
            // String args = "https://offenedaten-koeln.de/api/action/datastore/search.json?resource_id=b6af53d5-82fd-496e-9813-1f69290fee27&limit=5";
            //String args = "https://www.htw-berlin.de";
            // new DownloadWebpageTask().execute(getResources().getString(R.string.urlText));
            new Download(context, args).execute();
        } else {
            Log.d(DEBUG_TAG, "OnCreate() is called.");
        }
    }

    /**
     * makes an asynchronous request from URL
     */
    private class Download extends AsyncTask<Void, Void, String> {

        ProgressDialog mProgressDialog;
        Context context;
        private String url;

        public Download(Context context, String url) {
            this.context = context;
//            this.url = url;
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
                java.sql.ResultSet rs = st.executeQuery("CREATE TABLE phonebook(phone VARCHAR(32), firstname VARCHAR(32), lastname VARCHAR(32), address VARCHAR(64));");
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


    public boolean add(){
        return true;
    }

}

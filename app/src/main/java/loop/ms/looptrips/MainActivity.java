package loop.ms.looptrips;

import android.content.BroadcastReceiver;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ms.loop.loopsdk.core.LoopSDK;
import ms.loop.loopsdk.profile.GeoCoder;
import ms.loop.loopsdk.profile.IProfileDownloadCallback;
import ms.loop.loopsdk.profile.IProfileItemChangedCallback;
import ms.loop.loopsdk.profile.Trip;
import ms.loop.loopsdk.profile.Trips;
import ms.loop.loopsdk.util.LoopError;

public class MainActivity extends AppCompatActivity {

    private Trips trips;
    private TripsViewAdapter tripsViewAdapter;
    private ListView tripListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        trips = Trips.createAndLoad(Trips.class, Trip.class);
        tripsViewAdapter = new TripsViewAdapter(this,
                R.layout.tripview, new ArrayList<Trip>(trips.itemList.values()));

        tripListView = (ListView)findViewById(R.id.tripslist);
        trips.registerItemChangedCallback("Trips", new IProfileItemChangedCallback()
        {

            @Override
            public void onItemChanged(String entityId) {


                trips.load();
                final Map<String, Trip> itemList = trips.itemList;

                runOnUiThread(new Runnable() {
                    public void run() {
                        tripsViewAdapter.update(new ArrayList<Trip>(itemList.values()));
                    }
                });

            }

            @Override
            public void onItemAdded(String entityId) {

            }

            @Override
            public void onItemRemoved(String entityId) {

            }
        });

        if (LoopSDK.isInitialized()) {
            LoopSDK.forceSync();
            download(true);
        }

    }

    public void download(boolean overwrite) {
        trips.download(overwrite, new IProfileDownloadCallback() {
            @Override
            public void onProfileDownloadComplete(int itemCount) {

                runOnUiThread(new Runnable() {
                    public void run() {
                        tripsViewAdapter.update(new ArrayList<Trip>(trips.itemList.values()));
                    }
                });
            }
            @Override
            public void onProfileDownloadFailed(LoopError error) {
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

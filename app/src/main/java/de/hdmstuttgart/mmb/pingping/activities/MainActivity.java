package de.hdmstuttgart.mmb.pingping.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import de.hdmstuttgart.mmb.pingping.R;
import de.hdmstuttgart.mmb.pingping.adapter.ListAdapter;
import de.hdmstuttgart.mmb.pingping.util.ServerManager;

public class MainActivity extends Activity {
    ListView listView;
    Intent intent;
    ListAdapter adapter;
    TextView lastRefresh;
    ServerManager serverManager = ServerManager.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Holen der View Elemente
        lastRefresh = (TextView) findViewById(R.id.lastRefreshTime);
        listView = (ListView) findViewById(R.id.listView1);

        // Anlegen initialer Server Liste zu Testzwecken
        /*if (serverManager.serverList.isEmpty()) {
            serverManager.addServer(new Server("HdM Stuttgart", "http://www.hdm-stuttgart.de"));
            serverManager.addServer(new Server("Web.de", "http://www.web.de"));
            serverManager.addServer(new Server("GMX", "http://www.gmx.de"));
            serverManager.addServer(new Server("Bild", "http://www.bild.de"));
            serverManager.addServer(new Server("Google", "http://www.google.de"));
            serverManager.addServer(new Server("Facebook", "http://www.facebook.de"));
            serverManager.addServer(new Server("Yahoo", "http://www.yahoo.com"));
        }*/


        // Initialisierung des ListViewAdapters, um die ListView zu befüllen
        // und Mapping des Adapters auf die ListView
        adapter = new ListAdapter(this, R.layout.listview_item,
                serverManager.viewableServerList);
        this.listView.setAdapter(this.adapter);

        // Öffnet DetailActivity bei Click auf Listeneintrag
        this.listView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                intent = new Intent(MainActivity.this, DetailActivity.class);
                intent.putExtra("itemPosition", position);
                startActivity(intent);
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Update der ListView um aktuelle Daten anzuzeigen, wenn ein neuer
        // Server angelegt wurde.
        serverManager.updateViewableServerList();
        this.adapter.notifyDataSetChanged();

        updateList();
    }

    @Override
    // Menü anzeigen
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_activity_actions, menu);
        return true;
    }

    @Override
    // Gedrückten Menübutton abfragen
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Serververfügbarkeit abfragen
            case R.id.action_refresh:
                if (deviceIsOnline()) {
                    //if (!serverManager.updateServers(adapter)) {
                    if (updateList()) {
                        Toast.makeText(this, getString(R.string.mainActivity_listEmptyMessage), Toast.LENGTH_LONG).show();
                    }
                } else {
                    // AlertDialog ausgeben
                    AlertDialog.Builder builder = new AlertDialog.Builder(
                            MainActivity.this);

                    builder.setTitle(R.string.mainActivity_dialogTitle)
                            .setMessage(R.string.mainActivity_dialogMessage)
                            .setCancelable(true);

                    builder.setPositiveButton(R.string.mainActivity_dialogConfirm,
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    dialog.dismiss();
                                }
                            });

                    AlertDialog alert = builder.create();
                    alert.show();
                }

                return true;
            // Neuen Server anlegen
            case R.id.action_createNewServer:
                intent = new Intent(this, AddEditServerActivity.class);
                intent.putExtra("key1", getResources().getString(R.string.action_createNewServer).toString());
                startActivity(intent);
                return true;
            // Impressum anzeigen
            case R.id.action_legalNotice:
                intent = new Intent(this, LegalNoticeActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private boolean updateList() {
        boolean noServer = serverManager.updateServers(adapter);
        // LastRefresh im View anzeigen
        if (serverManager.getLastRefresh() != null) {
            lastRefresh.setText(serverManager.getLastRefresh());
        }
        return noServer;
    }

    // Erreichbarkeit des Internets prüfen
    public boolean deviceIsOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }
}

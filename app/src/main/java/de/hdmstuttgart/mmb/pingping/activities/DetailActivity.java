package de.hdmstuttgart.mmb.pingping.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import de.hdmstuttgart.mmb.pingping.R;
import de.hdmstuttgart.mmb.pingping.util.Availability;
import de.hdmstuttgart.mmb.pingping.model.Server;
import de.hdmstuttgart.mmb.pingping.util.ServerManager;

public class DetailActivity extends Activity {
    ImageView imgFavIcon;
    TextView txtCurrentStatus;
    TextView txtUpdateTime;
    TextView txtScore;
    TextView txtUrl;
    Button btnEdit;
    Button btnDelete;

    Server server;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        imgFavIcon = (ImageView) findViewById(R.id.detail_favIcon);
        txtCurrentStatus = (TextView) findViewById(R.id.detail_currentStatus);
        txtUpdateTime = (TextView) findViewById(R.id.detail_updateTime);
        txtUrl = (TextView) findViewById(R.id.detail_url);
        txtScore = (TextView) findViewById(R.id.detail_score);
        btnEdit = (Button) findViewById(R.id.detail_btnEdit);
        btnDelete = (Button) findViewById(R.id.detail_btnDelete);

        // Position aus Liste abfragen
        server = ServerManager.getInstance().viewableServerList.get(getIntent()
                .getExtras().getInt("itemPosition"));

        // Titel für Action Bar setzen
        setTitle(server.getAlias());

        // URL anzeigen
        txtUrl.setText(server.getUrl().toString());

        if (server.getLastUpdate() != null) {
            if (server.isOnline())
                txtCurrentStatus
                        .setText(getString(R.string.detailActivity_serverOnline));
            else
                txtCurrentStatus
                        .setText(getString(R.string.detailActivity_serverOffline));
            txtUpdateTime.setText(server.getLastUpdate().toString());
        } else {
            txtCurrentStatus
                    .setText(getString(R.string.detailActivity_serverNoStatus));
            txtUpdateTime
                    .setText(getString(R.string.detailActivity_serverNoData));
        }

        // Score anzeigen
        if (server.getScore() != -1)
            txtScore.setText(server.getAvailability().toString()
                    + String.format(" (%.0f%%)", ((1 - server.getScore()) * 100)));
        if (server.getAvailability() == Availability.HIGH_AVAILABILITY)
            txtScore.setBackgroundColor(Color.GREEN);
        else if (server.getAvailability() == Availability.AVAILABLE)
            txtScore.setBackgroundColor(Color.YELLOW);
        else if (server.getAvailability() == Availability.LOW_AVAILABILITY)
            txtScore.setBackgroundColor(Color.RED);
        else {
            txtScore.setBackgroundColor(Color.LTGRAY);
        }

        // Activity für Bearbeiten starten
        this.btnEdit.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailActivity.this,
                        AddEditServerActivity.class);
                intent.putExtra("key1",
                        ((Button) findViewById(R.id.detail_btnEdit)).getText()
                                .toString());
                intent.putExtra("serverPosition", getIntent().getExtras()
                        .getInt("itemPosition"));
                startActivity(intent);
            }
        });

        // Löschen des Servers
        this.btnDelete.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                // AlertDialog für Warnung
                AlertDialog.Builder builder = new AlertDialog.Builder(
                        DetailActivity.this);

                builder.setTitle(R.string.detailActivity_dialogTitle)
                        .setMessage(R.string.detailActivity_dialogMessage)
                        .setCancelable(true);

                builder.setPositiveButton(
                        R.string.detailActivity_dialogConfirm,
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                server.setVisible(false);
                                finish();
                                dialog.dismiss();
                                Toast.makeText(
                                        DetailActivity.this,
                                        DetailActivity.this
                                                .getString(R.string.detailActivity_deletedServerMessage),
                                        Toast.LENGTH_LONG).show();
                            }
                        });

                builder.setNegativeButton(R.string.detailActivity_dialogCancel,
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
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        // Alias und URL aktualisieren nach Bearbeiten
        setTitle(server.getAlias());
        txtUrl.setText(server.getUrl().toString());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Auf Home und zurück reagieren
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }
}

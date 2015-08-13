package de.hdmstuttgart.mmb.pingping.activities;

import java.util.Locale;

import org.apache.commons.validator.routines.UrlValidator;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import de.hdmstuttgart.mmb.pingping.R;
import de.hdmstuttgart.mmb.pingping.model.Server;
import de.hdmstuttgart.mmb.pingping.util.ServerManager;

public class AddEditServerActivity extends Activity {
    EditText alias;
    EditText url;
    Server server;
    ServerManager serverManager = ServerManager.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addeditserver);
        setTitle(getIntent().getExtras().getString("key1"));
        alias = (EditText) findViewById(R.id.serverAlias);
        url = (EditText) findViewById(R.id.serverURL);
        if (getIntent().getExtras().containsKey("serverPosition")) {
            int position = getIntent().getExtras().getInt("serverPosition");
            server = serverManager.viewableServerList.get(position);
            alias.setText(server.getAlias());
            url.setText(server.getUrl().toString());
        }

    }

    @Override
    // Menü Leiste anzeigen
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.addeditserver_activity_actions, menu);
        return true;
    }

    @Override
    // Gedrückten Menübutton abfragen
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_accept:
                String[] schemes = {"http", "https"};
                UrlValidator validator = new UrlValidator(schemes);
                String alias = this.alias.getText().toString();
                String url = this.url.getText().toString()
                        .toLowerCase(Locale.getDefault());
                String toastMessage = "";

                if (validator.isValid(url)) {
                    // Prüfen ob Server editiert werdeb muss oder neu erstellt
                    if (serverExists()) {
                        // Prüfen ob eingetippte URL bereits existiert
                        Server serverCandidate = serverManager.getServerByURL(url);
                        if (serverCandidateExists(serverCandidate)) {
                            if (serverCandidate == server) {
                                server.setAlias(alias);
                                server.setUrl(url);

                                toastMessage = getString(R.string.addEditServerActivity_serverEditedMessage);
                                finish();
                            } else {
                                toastMessage = getString(R.string.addEditServerActivity_urlExistsMessage);
                                this.url.requestFocus();
                            }
                        } else {
                            // Editieren
                            server.setAlias(alias);
                            server.setUrl(url);
                            toastMessage = getString(R.string.addEditServerActivity_serverEditedMessage);
                            finish();
                        }
                    } else {
                        // Server erstellen
                        Server newServer = serverManager.getServerByURL(url);
                        // Prüfen ob Server bereits existiert
                        if (serverCandidateExists(newServer)) {
                            if (newServer.isVisible()) {
                                toastMessage = getString(R.string.addEditServerActivity_serverExistsMessage)
                                        + " \"" + url + "\".";
                                this.url.requestFocus();
                            } else {
                                newServer.setVisible(true);
                                newServer.setAlias(alias);
                                newServer.setUrl(url);
                                toastMessage = getString(R.string.addEditServerActivity_serverExistedMessage)
                                        + " \"" + alias + "\".";
                                finish();
                            }
                        } else {
                            newServer = new Server(alias, url);
                            serverManager.addServer(newServer);
                            toastMessage = getString(R.string.addEditServerActivity_serverAddedMessage);
                            finish();
                        }
                    }
                } else {
                    toastMessage = getString(R.string.addEditServerActivity_urlInvalidMessage);
                    this.url.requestFocus();
                }
                // Nachricht bei valider URL ausgeben
                Toast.makeText(this, toastMessage, Toast.LENGTH_SHORT).show();

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private boolean serverCandidateExists(Server serverCandidate) {
        return serverCandidate != null;
    }

    private boolean serverExists() {
        return serverCandidateExists(server);
    }
}

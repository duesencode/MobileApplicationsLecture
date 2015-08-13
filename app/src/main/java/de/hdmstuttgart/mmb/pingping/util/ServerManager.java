package de.hdmstuttgart.mmb.pingping.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import org.apache.http.Header;

import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import de.hdmstuttgart.mmb.pingping.adapter.ListAdapter;
import de.hdmstuttgart.mmb.pingping.model.Server;

public class ServerManager {
    private static ServerManager instance = null;
    public ArrayList<Server> serverList;
    public ArrayList<Server> viewableServerList;
    private String lastRefresh = null;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
            "dd.MM.yyyy - HH:mm:ss", Locale.getDefault());

    private ServerManager() {
        this.serverList = new ArrayList<>();
        this.viewableServerList = new ArrayList<>();
    }

    public String getLastRefresh() {
        return lastRefresh;
    }

    public void setLastRefresh(String lastRefresh) {
        this.lastRefresh = lastRefresh;
    }

    public static ServerManager getInstance() {
        if (ServerManager.instance == null) {
            ServerManager.instance = new ServerManager();
        }
        instance.updateViewableServerList();
        return ServerManager.instance;
    }

    public boolean addServer(final Server server) {
        if (serverList.contains(server)) {
            return false;
        } else {
            return serverList.add(server);
        }
    }

    public void updateViewableServerList() {
        viewableServerList.clear();
        for (final Server s : serverList) {
            if (s.isVisible()) {
                viewableServerList.add(s);
            }
        }
    }

    public Server getServerByURL(String url) {
        for (Server s : serverList)
            if (s.getUrl().equals(url))
                return s;
        return null;
    }

    public boolean updateServers(final ListAdapter adapter) {

        updateViewableServerList();
        if (viewableServerList.isEmpty()) {
            return true;
        }
        setLastRefresh(simpleDateFormat.format(new Date()));

        for (final Server server : viewableServerList) {
            AsyncHttpClient asyncClient = new AsyncHttpClient();
            asyncClient.get(server.getUrl(), new AsyncHttpResponseHandler() {
                @Override
                public void onStart() {
                    super.onStart();
                    Log.v("ServerManager", "onStart: " + server.getUrl());
                }

                @Override
                public void onFailure(int arg0, Header[] arg1, byte[] arg2,
                                      Throwable arg3) {
                    super.onFailure(arg0, arg1, arg2, arg3);
                    server.incrementBadRequests();
                    server.calculateScore();
                    Log.v("ServerManager", "onFailure: " + server.getUrl());
                }

                @Override
                public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
                    super.onSuccess(arg0, arg1, arg2);
                    server.incrementGoodRequests();
                    server.calculateScore();
                    Log.v("ServerManager", "onSuccess: " + server.getUrl());
                }

                @Override
                public void onRetry() {
                    super.onRetry();
                    Log.v("ServerManager", "onRetry: " + server.getUrl());
                }

                @Override
                public void onFinish() {
                    super.onFinish();
                    setLastRefresh(simpleDateFormat.format(new Date()));
                    adapter.notifyDataSetChanged();
                    Log.v("ServerManager", "onFinish: " + server.getUrl());
                }
            });
        }
        return false;
    }
}

package de.hdmstuttgart.mmb.pingping.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import de.hdmstuttgart.mmb.pingping.R;
import de.hdmstuttgart.mmb.pingping.model.Server;
import de.hdmstuttgart.mmb.pingping.util.Availability;

public class ListAdapter extends ArrayAdapter<Server> {
    private ArrayList<Server> items;

    public ListAdapter(Context context, int listViewResourceId,
                       ArrayList<Server> items) {
        super(context, listViewResourceId, items);
        this.items = items;

    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.listview_item, null);
        }
        Server server = items.get(position);

        ImageView statusIcon = (ImageView) view
                .findViewById(R.id.rowItem_currentStatusSymbol);
        TextView alias = (TextView) view.findViewById(R.id.rowItem_Alias);
        TextView Status = (TextView) view
                .findViewById(R.id.rowItem_currentStatus);
        TextView scoreImage = (TextView) view
                .findViewById(R.id.rowItem_scoreImage);
        alias.setText(server.getAlias());

        switch (server.getAvailability()) {
            case HIGH_AVAILABILITY:
                scoreImage.setBackgroundColor(Color.GREEN);
                break;
            case AVAILABLE:
                scoreImage.setBackgroundColor(Color.YELLOW);
                break;
            case LOW_AVAILABILITY:
                scoreImage.setBackgroundColor(Color.RED);
                break;
            default:
                scoreImage.setBackgroundColor(Color.LTGRAY);
                break;
        }

        if (server.isOnline()) {
            statusIcon.setImageResource(R.drawable.like);
            Status.setText(R.string.listAdapter_online);
        } else {
            if (serverHasAScore(server)) {
                Status.setText(R.string.listAdapter_offline);
                statusIcon.setImageResource(R.drawable.dislike);
            } else {
                Status.setText(R.string.listAdapter_nodata);
                statusIcon.setImageResource(R.drawable.questionmark);
            }
        }

        return view;
    }

    private boolean serverHasAScore(Server server) {
        return server.getAvailability() != Availability.NO_SCORE;
    }
}

package com.example.oyun.cse;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by oyun on 2018-02-03.
 */

public class InformationListAdapter extends BaseAdapter {

    private Context context;
    private List<Information> informationList;

    public InformationListAdapter(Context context, List<Information> informationList) {
        this.context = context;
        this.informationList = informationList;
    }

    @Override
    public int getCount() {
        return informationList.size();
    }

    @Override
    public Object getItem(int position) {
        return informationList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = View.inflate(context, R.layout.informationitem, null);
        TextView atitle = (TextView) v.findViewById(R.id.title);
        TextView anumber = (TextView) v.findViewById(R.id.number);
        TextView awriter = (TextView) v.findViewById(R.id.writer);
        TextView atime = (TextView) v.findViewById(R.id.time);

        atitle.setText(informationList.get(position).getTitle());
        anumber.setText(informationList.get(position).getNumber());
        awriter.setText(informationList.get(position).getWriter());
        atime.setText(informationList.get(position).getTime());

        v.setTag(informationList.get(position).getWriter());
        return v;
    }
}

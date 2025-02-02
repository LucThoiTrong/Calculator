package com.example.calculator;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class HistoryAdapter extends BaseAdapter {
    private final Context context;
    private final int Layout;
    private final List<History> arrhistory;

    public HistoryAdapter(Context context, int layout, List<History> arrhistory) {
        this.context = context;
        Layout = layout;
        this.arrhistory = arrhistory;
    }

    @Override
    public int getCount() {
        return arrhistory.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    //View holder
    public static class ViewHolder {
        TextView txtHistory;
        TextView txtResult;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView == null) {
            holder = new ViewHolder();

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = inflater.inflate(Layout, null);

            holder.txtHistory = convertView.findViewById(R.id.txtHistory);
            holder.txtResult = convertView.findViewById(R.id.txtResult);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        History history = this.arrhistory.get(position);
        holder.txtHistory.setText(history.getInput());
        holder.txtResult.setText(history.getResult());
        return convertView;
    }
}

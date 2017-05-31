package com.udacity.stockhawk.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;

import java.util.ArrayList;
import java.util.List;

public class StackWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new StackRemoteViewsFactory(this.getApplicationContext(), intent);
    }
}

class StackRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    private Uri content_uri = Contract.BASE_URI.buildUpon().appendPath(Contract.PATH_QUOTE).build();
    private List<String[]> mWidgetItems = new ArrayList<String[]>();
    private Context mContext;
    private int mAppWidgetId;

    public StackRemoteViewsFactory(Context context, Intent intent) {
        mContext = context;
        mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
    }

    private void loadData(Context context) {
        mWidgetItems = new ArrayList<String[]>();
        Cursor mCursor = context.getContentResolver().query(
                content_uri,
                Contract.Quote.QUOTE_COLUMNS.toArray(new String[Contract.Quote.QUOTE_COLUMNS.size()]),
                null,
                null,
                null);

        if (mCursor.moveToFirst()) {
            do {
                String[] data = new String[3];
                data[0] = mCursor.getString(Contract.Quote.POSITION_SYMBOL);
                data[1] = mCursor.getString(Contract.Quote.POSITION_PRICE);
                data[2] = mCursor.getString(Contract.Quote.POSITION_PERCENTAGE_CHANGE);
                mWidgetItems.add(data);
            } while (mCursor.moveToNext());
        }
    }

    @Override
    public void onCreate() {
        loadData(mContext);
    }

    @Override
    public void onDataSetChanged() {
        loadData(mContext);
    }

    @Override
    public void onDestroy() {
    }

    @Override
    public int getCount() {
        return mWidgetItems.size();
    }
    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.stock_widget);
        rv.setTextViewText(R.id.symbol, mWidgetItems.get(position)[0]);
        rv.setTextViewText(R.id.price,  mWidgetItems.get(position)[1]);
        rv.setTextViewText(R.id.change, mWidgetItems.get(position)[2]+ " %");

        if (Float.parseFloat(mWidgetItems.get(position)[2]) < 0f) {
            rv.setInt(R.id.change, "setBackgroundResource", R.drawable.percent_change_pill_red);
        }

        Bundle extras = new Bundle();
        extras.putString(StockWidgetCollection.EXTRA_ITEM, mWidgetItems.get(position)[0]);
        Intent fillInIntent = new Intent();
        fillInIntent.putExtras(extras);
        rv.setOnClickFillInIntent(R.id.widgetLayout, fillInIntent);
        
        return rv;
    }
    @Override
    public RemoteViews getLoadingView() {
        return null;
    }
    @Override
    public int getViewTypeCount() {
        return 1;
    }
    @Override
    public long getItemId(int i) {
        return 0;
    }
    @Override
    public boolean hasStableIds() {
        return false;
    }
}
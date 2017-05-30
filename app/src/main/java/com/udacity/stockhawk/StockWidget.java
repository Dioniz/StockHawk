package com.udacity.stockhawk;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.widget.RemoteViews;

import com.udacity.stockhawk.data.Contract;
import com.udacity.stockhawk.sync.QuoteSyncJob;

public class StockWidget extends AppWidgetProvider {

    private String[] data = {"-","00.00","0%"};
    private Uri content_uri = Contract.BASE_URI.buildUpon().appendPath(Contract.PATH_QUOTE).build();

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId, String[] data) {

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.stock_widget);
        views.setTextViewText(R.id.symbol,data[0]);
        views.setTextViewText(R.id.price, data[1]);
        views.setTextViewText(R.id.change,data[2]);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    private void loadData(Context context) {
        Cursor mCursor = context.getContentResolver().query(
                content_uri,
                Contract.Quote.QUOTE_COLUMNS.toArray(new String[Contract.Quote.QUOTE_COLUMNS.size()]),
                null,
                null,
                null);

        if (mCursor.moveToFirst()) {
            do {
                data[0] = mCursor.getString(Contract.Quote.POSITION_SYMBOL);
                data[1] = mCursor.getString(Contract.Quote.POSITION_PRICE);
                data[2] = mCursor.getString(Contract.Quote.POSITION_PERCENTAGE_CHANGE)+ " %";
            } while (mCursor.moveToNext());
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (QuoteSyncJob.ACTION_DATA_UPDATED.equals(intent.getAction())) {
            loadData(context);
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            loadData(context);
            updateAppWidget(context, appWidgetManager, appWidgetId,data);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }
    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

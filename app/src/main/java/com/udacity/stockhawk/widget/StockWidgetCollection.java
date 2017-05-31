package com.udacity.stockhawk.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.sync.QuoteSyncJob;
import com.udacity.stockhawk.ui.MainActivity;
import com.udacity.stockhawk.ui.chartActivity;

public class StockWidgetCollection extends AppWidgetProvider {
    public static final String TOAST_ACTION = "com.example.android.stackwidget.TOAST_ACTION";
    public static final String EXTRA_ITEM = "com.example.android.stackwidget.EXTRA_ITEM";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(TOAST_ACTION)) {
            int appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
            String symbolClick = intent.getStringExtra(EXTRA_ITEM);
            goToStock(context,appWidgetId,symbolClick);
        }
        super.onReceive(context, intent);
    }

    private void goToStock(Context context, int appWidgetId, String symbol) {
        Intent intentMainActivity = new Intent(context,chartActivity.class);
        intentMainActivity.putExtra(MainActivity.SYMBOL_PARAM,symbol);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, appWidgetId, intentMainActivity, PendingIntent.FLAG_UPDATE_CURRENT);
        try {
            pendingIntent.send(context, 0, intentMainActivity);
        } catch (PendingIntent.CanceledException e) {}
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int i = 0; i < appWidgetIds.length; ++i) {
            Intent intent = new Intent(context, StackWidgetService.class);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));

            RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.stock_widget_collection);
            rv.setRemoteAdapter(appWidgetIds[i],R.id.stack_view,intent);

            rv.setEmptyView(R.id.stack_view, R.id.empty_view);

            Intent toastIntent = new Intent(context, StockWidgetCollection.class);

            toastIntent.setAction(StockWidgetCollection.TOAST_ACTION);
            toastIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));

            PendingIntent toastPendingIntent = PendingIntent.getBroadcast(context, 0, toastIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            rv.setPendingIntentTemplate(R.id.stack_view, toastPendingIntent);

            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds[i],R.id.stack_view);
            appWidgetManager.updateAppWidget(appWidgetIds[i], rv);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onEnabled(Context context) {
        Intent dataUpdatedIntent = new Intent(QuoteSyncJob.ACTION_DATA_UPDATED);
        context.sendBroadcast(dataUpdatedIntent);
    }
    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

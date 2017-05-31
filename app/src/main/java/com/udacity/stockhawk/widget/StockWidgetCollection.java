package com.udacity.stockhawk.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.sync.QuoteSyncJob;
import com.udacity.stockhawk.ui.MainActivity;

public class StockWidgetCollection extends AppWidgetProvider {
    public static final String TOAST_ACTION = "com.example.android.stackwidget.TOAST_ACTION";
    public static final String EXTRA_ITEM = "com.example.android.stackwidget.EXTRA_ITEM";

    @Override
    public void onReceive(Context context, Intent intent) {
        AppWidgetManager mgr = AppWidgetManager.getInstance(context);
        if (intent.getAction().equals(TOAST_ACTION)) {
            int appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
            int viewIndex = intent.getIntExtra(EXTRA_ITEM, 0);
            Toast.makeText(context, "Touched view " + viewIndex, Toast.LENGTH_SHORT).show();
            Log.i("","Touched");
        }
        super.onReceive(context, intent);
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

            //Intent intentMainActivity = new Intent(context, MainActivity.class);
            //PendingIntent pendingIntent = PendingIntent.getActivity(context, appWidgetIds[i], intentMainActivity, PendingIntent.FLAG_UPDATE_CURRENT);
            //rv.setPendingIntentTemplate(R.id.stack_view, pendingIntent);

            // This section makes it possible for items to have individualized behavior.
            // It does this by setting up a pending intent template. Individuals items of a collection
            // cannot set up their own pending intents. Instead, the collection as a whole sets
            // up a pending intent template, and the individual items set a fillInIntent
            // to create unique behavior on an item-by-item basis.
            Intent toastIntent = new Intent(context, StockWidgetCollection.class);
            // Set the action for the intent.
            // When the user touches a particular view, it will have the effect of
            // broadcasting TOAST_ACTION.
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

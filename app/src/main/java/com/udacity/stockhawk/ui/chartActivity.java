package com.udacity.stockhawk.ui;

import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.provider.UserDictionary;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;

import java.util.ArrayList;

public class chartActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener, OnChartValueSelectedListener {

    public static final Uri CONTENT_URI = Contract.BASE_URI.buildUpon().appendPath(Contract.PATH_QUOTE).appendPath("ATOS").build();

    private LineChart mChart;
    private Typeface mTfLight;

    private Cursor mCursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_linechart);

        loadData();


        mTfLight = Typeface.createFromAsset(getAssets(), "OpenSans-Light.ttf");

        mChart = (LineChart) findViewById(R.id.chart1);
        mChart.setOnChartValueSelectedListener(this);

        // no description text
        mChart.getDescription().setEnabled(false);

        // enable touch gestures
        mChart.setTouchEnabled(true);

        mChart.setDragDecelerationFrictionCoef(0.9f);

        // enable scaling and dragging
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);
        mChart.setDrawGridBackground(false);
        mChart.setHighlightPerDragEnabled(true);

        // if disabled, scaling can be done on x- and y-axis separately
        mChart.setPinchZoom(true);

        // set an alternative background color
        mChart.setBackgroundColor(Color.LTGRAY);

        // add data
        setData(20, 30);

        mChart.animateX(2500);

        // get the legend (only possible after setting data)
        Legend l = mChart.getLegend();

        // modify the legend ...
        l.setForm(Legend.LegendForm.LINE);
        l.setTypeface(mTfLight);
        l.setTextSize(11f);
        l.setTextColor(Color.WHITE);
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
//        l.setYOffset(11f);

        XAxis xAxis = mChart.getXAxis();
        xAxis.setTypeface(mTfLight);
        xAxis.setTextSize(11f);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(false);

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setTypeface(mTfLight);
        leftAxis.setTextColor(ColorTemplate.getHoloBlue());
        leftAxis.setAxisMaximum(200f);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setDrawGridLines(true);
        leftAxis.setGranularityEnabled(true);

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setTypeface(mTfLight);
        rightAxis.setTextColor(Color.RED);
        rightAxis.setAxisMaximum(900);
        rightAxis.setAxisMinimum(-200);
        rightAxis.setDrawGridLines(false);
        rightAxis.setDrawZeroLine(false);
        rightAxis.setGranularityEnabled(false);
    }

    private void loadData() {
         mCursor = getContentResolver().query(
                CONTENT_URI,
                Contract.Quote.QUOTE_COLUMNS.toArray(new String[Contract.Quote.QUOTE_COLUMNS.size()]),
                null,
                null,
                null);
        String symbol = "PRUEBA";
        if (mCursor.moveToFirst())
        {
            do
            {
                symbol = mCursor.getString(Contract.Quote.POSITION_HISTORY);
                Log.i("test",symbol);

            } while (mCursor.moveToNext());
        }
    }

    private void setData(int count, float range) {

            ArrayList<Entry> yVals1 = new ArrayList<Entry>();

            for (int i = 0; i < count; i++) {
                float mult = range / 2f;
                float val = (float) (Math.random() * mult) + 50;
                yVals1.add(new Entry(i, val));
            }

            ArrayList<Entry> yVals2 = new ArrayList<Entry>();

            for (int i = 0; i < count-1; i++) {
                float mult = range;
                float val = (float) (Math.random() * mult) + 450;
                yVals2.add(new Entry(i, val));
//            if(i == 10) {
//                yVals2.add(new Entry(i, val + 50));
//            }
            }

            ArrayList<Entry> yVals3 = new ArrayList<Entry>();

            for (int i = 0; i < count; i++) {
                float mult = range;
                float val = (float) (Math.random() * mult) + 500;
                yVals3.add(new Entry(i, val));
            }

            LineDataSet set1, set2, set3;

            if (mChart.getData() != null &&
                    mChart.getData().getDataSetCount() > 0) {
                set1 = (LineDataSet) mChart.getData().getDataSetByIndex(0);
                set2 = (LineDataSet) mChart.getData().getDataSetByIndex(1);
                set3 = (LineDataSet) mChart.getData().getDataSetByIndex(2);
                set1.setValues(yVals1);
                set2.setValues(yVals2);
                set3.setValues(yVals3);
                mChart.getData().notifyDataChanged();
                mChart.notifyDataSetChanged();
            } else {
                // create a dataset and give it a type
                set1 = new LineDataSet(yVals1, "DataSet 1");

                set1.setAxisDependency(YAxis.AxisDependency.LEFT);
                set1.setColor(ColorTemplate.getHoloBlue());
                set1.setCircleColor(Color.WHITE);
                set1.setLineWidth(2f);
                set1.setCircleRadius(3f);
                set1.setFillAlpha(65);
                set1.setFillColor(ColorTemplate.getHoloBlue());
                set1.setHighLightColor(Color.rgb(244, 117, 117));
                set1.setDrawCircleHole(false);
                //set1.setFillFormatter(new MyFillFormatter(0f));
                //set1.setDrawHorizontalHighlightIndicator(false);
                //set1.setVisible(false);
                //set1.setCircleHoleColor(Color.WHITE);

                // create a dataset and give it a type
                set2 = new LineDataSet(yVals2, "DataSet 2");
                set2.setAxisDependency(YAxis.AxisDependency.RIGHT);
                set2.setColor(Color.RED);
                set2.setCircleColor(Color.WHITE);
                set2.setLineWidth(2f);
                set2.setCircleRadius(3f);
                set2.setFillAlpha(65);
                set2.setFillColor(Color.RED);
                set2.setDrawCircleHole(false);
                set2.setHighLightColor(Color.rgb(244, 117, 117));
                //set2.setFillFormatter(new MyFillFormatter(900f));

                set3 = new LineDataSet(yVals3, "DataSet 3");
                set3.setAxisDependency(YAxis.AxisDependency.RIGHT);
                set3.setColor(Color.YELLOW);
                set3.setCircleColor(Color.WHITE);
                set3.setLineWidth(2f);
                set3.setCircleRadius(3f);
                set3.setFillAlpha(65);
                set3.setFillColor(ColorTemplate.colorWithAlpha(Color.YELLOW, 200));
                set3.setDrawCircleHole(false);
                set3.setHighLightColor(Color.rgb(244, 117, 117));

                // create a data object with the datasets
                LineData data = new LineData(set1, set2, set3);
                data.setValueTextColor(Color.WHITE);
                data.setValueTextSize(9f);

                // set data
                mChart.setData(data);
            }
        }


    //CALLBACKS SEEKBAR
    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
    }
    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }
    //CALLBACKS CHART
    @Override
    public void onValueSelected(Entry e, Highlight h) {
    }
    @Override
    public void onNothingSelected() {
    }
}
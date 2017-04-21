package com.udacity.stockhawk.ui;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.LinearLayout;

import butterknife.BindView;
import butterknife.ButterKnife;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;

import java.util.ArrayList;
import java.util.List;

public class StockDetailActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final int STOCK_LOADER = 1;
    public static final String EXTRA_SYMBOL = "EXTRA_SYMBOL";

    @BindView(R.id.chart)
    LineChart mLineChart;

    @BindView(R.id.parent_container)
    LinearLayout parentContainer;

    private List<Entry> entries = new ArrayList<>();
    private String mChosenSymbol;

    public static Intent newIntent(Context context, String symbol) {
        Intent intent = new Intent(context, StockDetailActivity.class);
        intent.putExtra(EXTRA_SYMBOL, symbol);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_detail);
        ButterKnife.bind(this);
        initData();
        getSupportLoaderManager().initLoader(STOCK_LOADER, null, this);
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, Contract.Quote.URI,
                Contract.Quote.QUOTE_COLUMNS.toArray(new String[]{}),
                Contract.Quote.COLUMN_SYMBOL + " = ?", new String[]{mChosenSymbol},
                Contract.Quote.COLUMN_SYMBOL);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data.getCount() != 0) {
            makeChart(data);
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {

    }

    private void initData() {
        mChosenSymbol = getIntent().getStringExtra(EXTRA_SYMBOL);
    }

    private void makeChart(Cursor data) {
        int i = 1;
        for (data.moveToFirst(); !data.isAfterLast(); data.moveToNext()) {
            String label = data.getString(data.getColumnIndexOrThrow(Contract.Quote.COLUMN_PRICE));
            entries.add(new Entry(i, Float.parseFloat(label)));
            i++;
        }
        LineDataSet lineDataSet = new LineDataSet(entries, "label");
        lineDataSet.setColor(R.color.colorPrimary);
        lineDataSet.setValueTextColor(R.color.colorPrimary);

        LineData lineData = new LineData(lineDataSet);
        mLineChart.setData(lineData);
        mLineChart.invalidate();
    }

}

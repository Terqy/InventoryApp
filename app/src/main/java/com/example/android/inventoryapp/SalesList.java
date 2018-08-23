package com.example.android.inventoryapp;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.android.inventoryapp.Data.ItemContract;
import com.example.android.inventoryapp.Data.ItemDbHelper;

public class SalesList extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private ItemDbHelper mDbHelper;

    private ItemAdapter adapter;

    private static final int ITEM_LOADER = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_list);
        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar_sales_list);
        setSupportActionBar(toolbar);
        mDbHelper = new ItemDbHelper(this);

        ListView salesListView = findViewById(R.id.empty_list);
        adapter = new ItemAdapter(this, null);
        salesListView.setAdapter(adapter);

        salesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent i = new Intent(SalesList.this, SalesActivity.class);
                Uri currentItemUri = ContentUris.withAppendedId(ItemContract.ItemEntry.CONTENT_URI, id);
                i.setData(currentItemUri);
                startActivity(i);
            }
        });

        getLoaderManager().initLoader(ITEM_LOADER, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_sales_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.sale_back_button:
                Intent i = new Intent(SalesList.this, Catalog.class);
                startActivity(i);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                ItemContract.ItemEntry._ID,
                ItemContract.ItemEntry.COLUMN_PRODUCT_NAME,
                ItemContract.ItemEntry.COLUMN_PRICE
        };

        return new CursorLoader(this, ItemContract.ItemEntry.CONTENT_URI,
                projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }
}

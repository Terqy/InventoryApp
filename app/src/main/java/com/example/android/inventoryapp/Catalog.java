package com.example.android.inventoryapp;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventoryapp.Data.ItemContract;
import com.example.android.inventoryapp.Data.ItemDbHelper;

import java.util.List;

public class Catalog extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private ItemDbHelper mDbHelper;
    private ItemAdapter adapter;
    private static final String TAG = Catalog.class.getSimpleName();
    private static final int ITEM_LOADER = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mDbHelper = new ItemDbHelper(this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Catalog.this, Editor.class);
                startActivity(i);
            }
        });

        ListView itemListView = (ListView) findViewById(R.id.empty_list);

        adapter = new ItemAdapter(this, null);
        itemListView.setAdapter(adapter);

        itemListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent i = new Intent(Catalog.this, Editor.class);
                Uri currentItemUri = ContentUris.withAppendedId(ItemContract.ItemEntry.CONTENT_URI, id);
                i.setData(currentItemUri);
                startActivity(i);
            }
        });

        getLoaderManager().initLoader(ITEM_LOADER, null, this);
        Log.v(TAG, "" + ItemContract.ItemEntry.CONTENT_URI.toString());
    }

    private void insertDummyData() {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(ItemContract.ItemEntry.COLUMN_PRODUCT_NAME, "Hammer");
        values.put(ItemContract.ItemEntry.COLUMN_PRICE, "5");
        values.put(ItemContract.ItemEntry.COLUMN_QUANTITY, "1");
        values.put(ItemContract.ItemEntry.COLUMN_SUPPLIER_NAME, "Quality hammers");
        values.put(ItemContract.ItemEntry.COLUMN_SUPPLIER_PHONE_NUMBER, "12345678");

        Uri newUri = getContentResolver().insert(ItemContract.ItemEntry.CONTENT_URI, values);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.insert_dummy_data:
                insertDummyData();
                return true;

            case R.id.delete_all_data:
                deleteAllItems();
                return true;

            case R.id.go_to_sales:
                Intent i = new Intent(Catalog.this, SalesList.class) ;
                startActivity(i);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteAllItems() {
        int rowsDeleted = getContentResolver().delete(ItemContract.ItemEntry.CONTENT_URI, null, null);
        Toast.makeText(this, "" + rowsDeleted, Toast.LENGTH_SHORT).show();
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

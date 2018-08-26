package com.example.android.inventoryapp;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventoryapp.Data.ItemContract;
import com.example.android.inventoryapp.Data.ItemDbHelper;

public class SalesActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int EXISTING_LOADER = 0;

    private int productPrice;
    private int itemQuantity;
    private int phoneNumber;

    private Boolean mItemHasChanged = false;

    private Uri mCurrentItemUri;

    String productNameString;
    String productPriceString;
    String productQuantityString;
    String productSupplierNameString;
    String productSupplierPhoneString;

    TextView productName;
    TextView productPriceTextView;
    TextView productQuantity;
    TextView productSupplierName;
    TextView productSupplierPhone;

    Button callSupplier, plussButton, minusButton;

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mItemHasChanged = true;
            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales);
        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar_sales);
        setSupportActionBar(toolbar);

        final Intent intent = getIntent();
        mCurrentItemUri = intent.getData();
        if(mCurrentItemUri == null) {
            setTitle("Sell item");
            invalidateOptionsMenu();
        } else {
            setTitle("Sell item");
            getLoaderManager().initLoader(EXISTING_LOADER, null, this);
        }

        productName = findViewById(R.id.product_name_sales);
        productPriceTextView = findViewById(R.id.product_price_sales_information);
        productQuantity = findViewById(R.id.product_quantity_sales_information);
        productSupplierName = findViewById(R.id.product_supplier_name);
        productSupplierPhone = findViewById(R.id.product_supplier_phone_sales_information);

        productName.setOnTouchListener(mTouchListener);
        productPriceTextView.setOnTouchListener(mTouchListener);
        productQuantity.setOnTouchListener(mTouchListener);
        productSupplierName.setOnTouchListener(mTouchListener);
        productSupplierPhone.setOnTouchListener(mTouchListener);

        callSupplier = findViewById(R.id.call_supplier);
        callSupplier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_DIAL, Uri.parse("tel: " + phoneNumber));
                startActivity(i);
            }
        });

        plussButton = (Button) findViewById(R.id.pluss_button_detailed_view);
        plussButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemQuantity += 1;
                productQuantity.setText(String.valueOf(itemQuantity));
            }
        });

        minusButton = (Button) findViewById(R.id.minus_button_detailed_view);
        minusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(itemQuantity > 0) {
                    itemQuantity -= 1;
                }
                productQuantity.setText(String.valueOf(itemQuantity));
            }
        });
    }

    public void updateItemQuantity() {
        ItemDbHelper dbHelper = new ItemDbHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ItemContract.ItemEntry.COLUMN_QUANTITY, itemQuantity);
        getContentResolver().update(ItemContract.ItemEntry.CONTENT_URI, values, null, null);
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
                Intent i = new Intent(SalesActivity.this, SalesList.class);
                startActivity(i);
                return true;

            case R.id.save_quantity_button:
                updateItemQuantity();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                ItemContract.ItemEntry._ID,
                ItemContract.ItemEntry.COLUMN_PRODUCT_NAME,
                ItemContract.ItemEntry.COLUMN_PRICE,
                ItemContract.ItemEntry.COLUMN_QUANTITY,
                ItemContract.ItemEntry.COLUMN_SUPPLIER_NAME,
                ItemContract.ItemEntry.COLUMN_SUPPLIER_PHONE_NUMBER
        };
        return new CursorLoader(this,
                mCurrentItemUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if(cursor == null || cursor.getCount() < 1) {
            return;
        }
        if(cursor.moveToFirst()) {
            int productNameIndex = cursor.getColumnIndex(ItemContract.ItemEntry.COLUMN_PRODUCT_NAME);
            int productPriceIndex = cursor.getColumnIndex(ItemContract.ItemEntry.COLUMN_PRICE);
            int productQuantityIndex = cursor.getColumnIndex(ItemContract.ItemEntry.COLUMN_QUANTITY);
            int productSupplierNameIndex = cursor.getColumnIndex(ItemContract.ItemEntry.COLUMN_SUPPLIER_NAME);
            int productSupplierPhoneIndex = cursor.getColumnIndex(ItemContract.ItemEntry.COLUMN_SUPPLIER_PHONE_NUMBER);

            productPrice = cursor.getInt(productPriceIndex);
            itemQuantity = cursor.getInt(productQuantityIndex);
            phoneNumber = cursor.getInt(productSupplierPhoneIndex);

            productNameString = cursor.getString(productNameIndex);
            productPriceString = cursor.getString(productPriceIndex);
            productQuantityString = cursor.getString(productQuantityIndex);
            productSupplierNameString = cursor.getString(productSupplierNameIndex);
            productSupplierPhoneString = cursor.getString(productSupplierPhoneIndex);

            productName.setText(productNameString);
            productPriceTextView.setText(productPriceString);
            productQuantity.setText(productQuantityString);
            productSupplierName.setText(productSupplierNameString);
            productSupplierPhone.setText(productSupplierPhoneString);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        productName.setText("");
        productPriceTextView.setText("");
        productQuantity.setText("");
        productSupplierName.setText("");
        productSupplierPhone.setText("");
    }
}

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
    private int finalPrice;
    private int itemQuantity;
    private int orderQuantity;

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
    TextView finalSalesQuantity;
    TextView finalPriceTextView;

    Button plussButton;
    Button minusButton;
    Button orderButton;

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

        Intent intent = getIntent();
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
        finalSalesQuantity = findViewById(R.id.final_sales_quantity);
        finalPriceTextView = findViewById(R.id.final_price);

        productName.setOnTouchListener(mTouchListener);
        productPriceTextView.setOnTouchListener(mTouchListener);
        productQuantity.setOnTouchListener(mTouchListener);
        productSupplierName.setOnTouchListener(mTouchListener);
        productSupplierPhone.setOnTouchListener(mTouchListener);

        plussButton = (Button) findViewById(R.id.pluss_button_sales);
        minusButton = (Button) findViewById(R.id.minus_button);
        orderButton = (Button) findViewById(R.id.order_item);

        plussButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                orderQuantity += 1;
                if(orderQuantity > itemQuantity) {
                    orderQuantity = itemQuantity;
                }
                finalPrice = productPrice * orderQuantity;
                finalSalesQuantity.setText(String.valueOf(orderQuantity));
                finalPriceTextView.setText(String.valueOf(finalPrice) + ",-");
            }
        });

        minusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                orderQuantity -= 1;
                if(orderQuantity < 0) {
                    orderQuantity = 0;
                }
                finalPrice = productPrice * orderQuantity;
                finalSalesQuantity.setText(String.valueOf(orderQuantity));
                finalPriceTextView.setText(String.valueOf(finalPrice) + ",-");
            }
        });

        orderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemQuantity -= orderQuantity;
                if(itemQuantity <= 0) {
                    deleteItem();
                }
                updateItem(itemQuantity);
                orderQuantity = 0;
                finalPrice = 0;
                finalSalesQuantity.setText(String.valueOf(orderQuantity));
                finalPriceTextView.setText(String.valueOf(finalPrice) + ",-");
            }
        });

    }

    private void updateItem(int quantity) {

        ItemDbHelper dbHelper = new ItemDbHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ItemContract.ItemEntry.COLUMN_PRODUCT_NAME, productNameString);
        values.put(ItemContract.ItemEntry.COLUMN_PRICE, productPrice);
        values.put(ItemContract.ItemEntry.COLUMN_QUANTITY, quantity);
        values.put(ItemContract.ItemEntry.COLUMN_SUPPLIER_NAME, productSupplierNameString);
        values.put(ItemContract.ItemEntry.COLUMN_SUPPLIER_PHONE_NUMBER, productSupplierPhoneString);

        if(mCurrentItemUri == null) {
            Uri newUri = getContentResolver().insert(ItemContract.ItemEntry.CONTENT_URI, values);
            if(newUri == null) {
                Toast.makeText(this, getString(R.string.insert_item_fail), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.insert_item_success), Toast.LENGTH_SHORT).show();
                return;
            }
        } else {
            int rowsAffected = getContentResolver().update(mCurrentItemUri, values, null, null);
            if(rowsAffected == 0) {
                Toast.makeText(this, getString(R.string.deleted_item_after_order), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_item_update_success), Toast.LENGTH_SHORT).show();
            }
            finish();
            return;
        }

        Uri newUri = getContentResolver().insert(ItemContract.ItemEntry.CONTENT_URI, values);
        if(newUri == null) {
            Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show();
        }
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

            Log.e("Wow", "" + productPrice + " " + itemQuantity);
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

    private void deleteItem() {
        if(mCurrentItemUri != null) {
            int rowsDeleted = getContentResolver().delete(mCurrentItemUri, null, null);
            if(rowsDeleted == 0) {
                Toast.makeText(this, getString(R.string.insert_item_fail), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.insert_item_success), Toast.LENGTH_SHORT).show();
            }
        }
    }
}

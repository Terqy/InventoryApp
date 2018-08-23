package com.example.android.inventoryapp;

import android.app.AlertDialog;
import android.app.LoaderManager;
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
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.inventoryapp.Data.ItemContract;
import com.example.android.inventoryapp.Data.ItemDbHelper;

import org.w3c.dom.Text;

import java.net.URI;

public class Editor extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int EXISTING_ITEM_LOADER = 0;
    private Uri mCurrentItemUri;
    private boolean mItemHasChanged = false;

    EditText editProductName;
    EditText editPrice;
    EditText editQuantity;
    EditText editSupplierName;
    EditText editSupplierPhone;

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mItemHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        Intent intent = getIntent();
        mCurrentItemUri = intent.getData();
        if(mCurrentItemUri == null) {
            setTitle(getString(R.string.new_item));
            invalidateOptionsMenu();
        } else {
            setTitle(getString(R.string.edit_item));
            getLoaderManager().initLoader(EXISTING_ITEM_LOADER, null, this);
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        editProductName = (EditText) findViewById(R.id.entered_product_name);
        editPrice = (EditText) findViewById(R.id.entered_price);
        editQuantity = (EditText) findViewById(R.id.entered_quantity);
        editSupplierName = (EditText) findViewById(R.id.supplier_name_edit);
        editSupplierPhone = (EditText) findViewById(R.id.supplier_phone_edit);

        editProductName.setOnTouchListener(mTouchListener);
        editPrice.setOnTouchListener(mTouchListener);
        editQuantity.setOnTouchListener(mTouchListener);
        editSupplierName.setOnTouchListener(mTouchListener);
        editSupplierPhone.setOnTouchListener(mTouchListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if(mCurrentItemUri == null) {
            MenuItem item = menu.findItem(R.id.action_delete);
            item.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_save:
                insertItem();
                finish();
                return true;

            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void insertItem() {
        String productName = editProductName.getText().toString().trim();
        String price = editPrice.getText().toString().trim();
        String quantity = editQuantity.getText().toString().trim();
        String supplierName = editSupplierName.getText().toString().trim();
        String supplierPhone = editSupplierPhone.getText().toString().trim();

        if(mCurrentItemUri == null && TextUtils.isEmpty(productName) && TextUtils.isEmpty(price) &&
                TextUtils.isEmpty(quantity) && TextUtils.isEmpty(supplierName) &&
                TextUtils.isEmpty(supplierPhone)) {
                return;
        }

        ItemDbHelper dbHelper = new ItemDbHelper(this);

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ItemContract.ItemEntry.COLUMN_PRODUCT_NAME, productName);
        values.put(ItemContract.ItemEntry.COLUMN_PRICE, price);
        values.put(ItemContract.ItemEntry.COLUMN_QUANTITY, quantity);
        values.put(ItemContract.ItemEntry.COLUMN_SUPPLIER_NAME, supplierName);
        values.put(ItemContract.ItemEntry.COLUMN_SUPPLIER_PHONE_NUMBER, supplierPhone);

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
                Toast.makeText(this, getString(R.string.editor_item_upate_failed), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_item_update_success), Toast.LENGTH_SHORT).show();
            }
            return;
        }

        Uri newUri = getContentResolver().insert(ItemContract.ItemEntry.CONTENT_URI, values);
        if(newUri == null) {
            Toast.makeText(this, getString(R.string.insert_item_fail), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, getString(R.string.insert_item_success), Toast.LENGTH_SHORT).show();
        }
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
            int productNameColumnIndex = cursor.getColumnIndex(ItemContract.ItemEntry.COLUMN_PRODUCT_NAME);
            int producPriceColumnIndex = cursor.getColumnIndex(ItemContract.ItemEntry.COLUMN_PRICE);
            int productQuantityColumnIndex = cursor.getColumnIndex(ItemContract.ItemEntry.COLUMN_QUANTITY);
            int productSupplierNameColumnIndex = cursor.getColumnIndex(ItemContract.ItemEntry.COLUMN_SUPPLIER_NAME);
            int productSupplierPhoneNumberColumnIndex = cursor.getColumnIndex(ItemContract.ItemEntry.COLUMN_SUPPLIER_PHONE_NUMBER);

            String productName = cursor.getString(productNameColumnIndex);
            String productPrice = cursor.getString(producPriceColumnIndex);
            String productQuantity = cursor.getString(productQuantityColumnIndex);
            String productSupplierName = cursor.getString(productSupplierNameColumnIndex);
            String productSupplerPhone = cursor.getString(productSupplierPhoneNumberColumnIndex);

            editProductName.setText(productName);
            editPrice.setText(productPrice);
            editQuantity.setText(productQuantity);
            editSupplierName.setText(productSupplierName);
            editSupplierPhone.setText(productSupplerPhone);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        editProductName.setText("");
        editPrice.setText("");
        editQuantity.setText("");
        editSupplierName.setText("");
        editSupplierPhone.setText("");
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("delete");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteItem();
            }
        });
        builder.setNegativeButton("Do not delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(dialogInterface != null) {
                    dialogInterface.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteItem() {
        if(mCurrentItemUri != null) {
            int rowsDeleted = getContentResolver().delete(mCurrentItemUri, null, null);
            if(rowsDeleted == 0) {
                Toast.makeText(this, "Failed deleting item", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Deleted item", Toast.LENGTH_SHORT).show();
            }
        }
    finish();
    }
}

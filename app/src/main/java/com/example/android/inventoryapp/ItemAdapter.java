package com.example.android.inventoryapp;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventoryapp.Data.ItemContract;

public class ItemAdapter extends CursorAdapter {

    public ItemAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.item_list, viewGroup, false);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        TextView productNameTextView = (TextView) view.findViewById(R.id.product_name_list_view);
        TextView productPriceTextView = (TextView) view.findViewById(R.id.product_price_list_view);
        final TextView productQuantityTextView = (TextView) view.findViewById(R.id.product_quantity_list_view);
        Button salesButton = (Button) view.findViewById(R.id.sales_button_list_view);

        int itemIdColumnIndex = cursor.getColumnIndex(ItemContract.ItemEntry._ID);
        int itemNameColumnIndex = cursor.getColumnIndex(ItemContract.ItemEntry.COLUMN_PRODUCT_NAME);
        int itemPriceColumnIndex = cursor.getColumnIndex(ItemContract.ItemEntry.COLUMN_PRICE);
        int itemQuantityColumndIndex = cursor.getColumnIndex(ItemContract.ItemEntry.COLUMN_QUANTITY);

        final long itemId = cursor.getLong(itemIdColumnIndex);
        final String itemName = cursor.getString(itemNameColumnIndex);
        final int itemPrice = cursor.getInt(itemPriceColumnIndex);
        final int itemQuantity = cursor.getInt(itemQuantityColumndIndex);

        productNameTextView.setText(itemName);
        productPriceTextView.setText(String.valueOf(itemPrice));
        productQuantityTextView.setText(String.valueOf(itemQuantity));

        salesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri currentItemUri = ContentUris.withAppendedId(ItemContract.ItemEntry.CONTENT_URI, itemId);
                ContentValues values = new ContentValues();

                values.put(ItemContract.ItemEntry.COLUMN_SUPPLIER_NAME, itemName);
                values.put(ItemContract.ItemEntry.COLUMN_PRICE, itemPrice);
                if(Integer.valueOf(itemQuantity) > 0) {
                    values.put(ItemContract.ItemEntry.COLUMN_QUANTITY, Integer.valueOf(itemQuantity) - 1);
                }
                context.getContentResolver().update(currentItemUri, values, null, null);
                productQuantityTextView.setText(String.valueOf(itemQuantity));
            }
        });
    }
}

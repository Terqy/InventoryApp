package com.example.android.inventoryapp;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.android.inventoryapp.Data.ItemContract;

public class ItemAdapter extends CursorAdapter {

    private int itemQuantity;

    public ItemAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.item_list, viewGroup, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView productNameTextView = (TextView) view.findViewById(R.id.product_name_list_view);
        final TextView productPriceTextView = (TextView) view.findViewById(R.id.product_price_list_view);
        TextView productQuantityTetView = (TextView) view.findViewById(R.id.product_quantity_list_view);
        //Button orderButtonListView = (Button) view.findViewById(R.id.order_button_list_view);

        String productName = cursor.getString(cursor.getColumnIndexOrThrow(ItemContract.ItemEntry.COLUMN_PRODUCT_NAME));
        int productPrice = cursor.getInt(cursor.getColumnIndexOrThrow(ItemContract.ItemEntry.COLUMN_PRICE));
        int productQuantity = cursor.getInt(cursor.getColumnIndexOrThrow(ItemContract.ItemEntry.COLUMN_QUANTITY));

        productNameTextView.setText(productName);
        productPriceTextView.setText(String.valueOf(productPrice));
        productQuantityTetView.setText(String.valueOf(productQuantity));

        /*orderButtonListView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Catalog catalog = new Catalog();
                String itemQuantityString = productPriceTextView.getText().toString();
                itemQuantity = Integer.parseInt(itemQuantityString);
                itemQuantity -= 1;
                if(itemQuantity <= 0) {
                    itemQuantity = 0;
                    catalog.deleteItem();
                }
                catalog.updateItem(itemQuantity);
            }
        });*/
    }
}

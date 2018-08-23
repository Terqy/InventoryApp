package com.example.android.inventoryapp.Data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public class ItemContract {

    public static final String CONTENT_AUTHORTIY = "com.example.android.inventoryapp";
    public static final String PATH_ITEMS = "item_table";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORTIY);

    private ItemContract() {

    }

    public static class ItemEntry implements BaseColumns{

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_ITEMS);

        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORTIY + "/" + PATH_ITEMS;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORTIY + "/" + PATH_ITEMS;

        public final static String _ID = BaseColumns._ID;
        public final static String TABLE_NAME = "item_table";
        public final static String COLUMN_PRODUCT_NAME = "product_name";
        public final static String COLUMN_PRICE = "price";
        public final static String COLUMN_QUANTITY = "quantity";
        public final static String COLUMN_SUPPLIER_NAME = "supplier_name";
        public final static String COLUMN_SUPPLIER_PHONE_NUMBER ="supplier_phone_number";
    }
}

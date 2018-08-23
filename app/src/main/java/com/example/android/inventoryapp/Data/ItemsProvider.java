package com.example.android.inventoryapp.Data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

public class ItemsProvider extends ContentProvider {

    String TAG = ItemsProvider.class.getSimpleName();

    private ItemDbHelper mDbHelper;

    private static final int ITEMS = 1;
    private static final int ITEMS_ID = 2;

    private long id;

    private static final UriMatcher matcher;

    static {
        matcher = new UriMatcher(UriMatcher.NO_MATCH);
        matcher.addURI(ItemContract.CONTENT_AUTHORTIY, ItemContract.PATH_ITEMS, ITEMS);
        matcher.addURI(ItemContract.CONTENT_AUTHORTIY, ItemContract.PATH_ITEMS + "/#", ITEMS_ID);
    }
    @Override
    public boolean onCreate() {
        mDbHelper = new ItemDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor cursor;

        final int match = matcher.match(uri);
        switch(match) {
            case ITEMS:
                cursor = db.query(ItemContract.ItemEntry.TABLE_NAME, projection, selection,
                        selectionArgs,null, null, sortOrder);
                break;
            case ITEMS_ID:
                selection = ItemContract.ItemEntry._ID + "=?";
                String id = uri.getLastPathSegment();
                selectionArgs = new String[] {id};
                cursor = db.query(ItemContract.ItemEntry.TABLE_NAME, projection, selection,
                        selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query URI: " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = matcher.match(uri);
        switch(match) {
            case ITEMS:
                return ItemContract.ItemEntry.CONTENT_LIST_TYPE;
            case ITEMS_ID:
                return ItemContract.ItemEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unkown URI " + uri + " with match " + match);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        final int match = matcher.match(uri);
        switch(match) {
            case ITEMS:
                return insertItem(uri, contentValues);
            default:
                throw new IllegalArgumentException("Cannot insert pet on URI: " + uri);
        }
    }

    public Uri insertItem(Uri uri, ContentValues values) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        String name = values.getAsString(ItemContract.ItemEntry.COLUMN_PRODUCT_NAME);
        if(name == null) {
            throw new IllegalArgumentException("Product requires name");
        }
        Integer price = values.getAsInteger(ItemContract.ItemEntry.COLUMN_PRICE);
        if(price != null && price < 1) {
            throw new IllegalArgumentException("Product requires a price");
        }
        Integer quantity = values.getAsInteger(ItemContract.ItemEntry.COLUMN_QUANTITY);
        if(quantity != null && quantity < 1) {
            throw new IllegalArgumentException("You need to enter a quantity of atleast 1");
        }
        String supplierName = values.getAsString(ItemContract.ItemEntry.COLUMN_SUPPLIER_NAME);
        if(supplierName == null) {
            throw new IllegalArgumentException("Supplier name required");
        }
        String supplierPhone = values.getAsString(ItemContract.ItemEntry.COLUMN_SUPPLIER_PHONE_NUMBER);
        if(supplierPhone != null && supplierPhone.length() < 8 && supplierPhone.length() > 8) {
            throw new IllegalArgumentException("Supplier phone number has to be 8 digits long");
        }

        id = db.insert(ItemContract.ItemEntry.TABLE_NAME, null, values);
        if(id == -1) {
            Log.e(TAG, "Failed to insert row for " + uri);
            return null;
        } else {
            Log.e(TAG, "Inserted at row " + id + " for " + uri);
        }
        getContext().getContentResolver().notifyChange(uri,null);
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int rowsDeleted;
        final int match = matcher.match(uri);
        switch(match) {
            case ITEMS:
                rowsDeleted = db.delete(ItemContract.ItemEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case ITEMS_ID:
                selection = ItemContract.ItemEntry._ID + "=?";
                String id = uri.getLastPathSegment();
                selectionArgs = new String[] {id};
                rowsDeleted = db.delete(ItemContract.ItemEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
        if(rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String selection, @Nullable String[] selectionArgs) {
        final int match = matcher.match(uri);
        switch(match) {
            case ITEMS:
                return updateItem(uri, contentValues, selection, selectionArgs);
            case ITEMS_ID:
                selection = ItemContract.ItemEntry._ID + "=?";
                String id = uri.getLastPathSegment();
                selectionArgs = new String[]{id};
                return updateItem(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    public int updateItem(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if(values.containsKey(ItemContract.ItemEntry.COLUMN_PRODUCT_NAME)) {
            String name = values.getAsString(ItemContract.ItemEntry.COLUMN_PRODUCT_NAME);
            if(name == null) {
                throw new IllegalArgumentException("Product requires a name");
            }
        }

        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int rowsUpdated = db.update(ItemContract.ItemEntry.TABLE_NAME, values, selection, selectionArgs);
        if(rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }
}

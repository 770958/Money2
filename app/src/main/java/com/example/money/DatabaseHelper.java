package com.example.money;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据库连接帮助类
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "money_db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_SUPPLIER = "Supplier";
    public static final String TABLE_SIGNER = "Signer";
    public static final String TABLE_RECORD = "Record";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 创建厂商表
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_SUPPLIER + " ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "name TEXT)");

        // 创建库管表
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_SIGNER + " ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "name TEXT)");

        // 创建记录表
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_RECORD + " ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "supplierName TEXT, "
                + "productName TEXT, "
                + "unit TEXT, "
                + "quantity REAL, "
                + "unitPrice REAL, "
                + "otherFees REAL, "
                + "totalAmount REAL, "
                + "signerName TEXT, "
                + "time TEXT, "
                + "remarks TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 这里只是简单删除表结构，建议使用更安全的数据迁移方案
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SUPPLIER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SIGNER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECORD);
        onCreate(db);
    }

    // 插入厂商
    public void insertSupplier(String name) {
        SQLiteDatabase db = getWritableDatabase();
        try {
            // 检查是否已有相同的厂商名称
            if (!isSupplierExist(name)) {
                ContentValues values = new ContentValues();
                values.put("name", name);
                db.insert(TABLE_SUPPLIER, null, values);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            db.close(); // 关闭数据库
        }
    }

    // 插入库管
    public void insertSigner(String name) {
        SQLiteDatabase db = getWritableDatabase();
        try {
            // 检查是否已有相同的库管名称
            if (!isSignerExist(name)) {
                ContentValues values = new ContentValues();
                values.put("name", name);
                db.insert(TABLE_SIGNER, null, values);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            db.close(); // 关闭数据库
        }
    }

    // 插入记录
    public long insertRecord(String supplierName, String productName, String unit, double quantity,
                             double unitPrice, double otherFees, double totalAmount, String signerName,
                             String time, String remarks) {
        SQLiteDatabase db = getWritableDatabase();
        long result = -1;
        try {
            ContentValues values = new ContentValues();
            values.put("supplierName", supplierName);
            values.put("productName", productName);
            values.put("unit", unit);
            values.put("quantity", quantity);
            values.put("unitPrice", unitPrice);
            values.put("otherFees", otherFees);
            values.put("totalAmount", totalAmount);
            values.put("signerName", signerName);
            values.put("time", time);
            values.put("remarks", remarks);
            result = db.insert(TABLE_RECORD, null, values);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            db.close(); // 关闭数据库
        }
        return result;
    }

    // 获取所有供应商
    public Cursor getAllSuppliers() {
        SQLiteDatabase db = getReadableDatabase();
        return db.query(TABLE_SUPPLIER, new String[]{"name"}, null, null, null, null, null);
    }

    // 获取所有库管
    public Cursor getAllSigners() {
        SQLiteDatabase db = getReadableDatabase();
        return db.query(TABLE_SIGNER, new String[]{"name"}, null, null, null, null, null);
    }

    // 获取所有记录
    public List<Record> getAllRecords() {
        List<Record> records = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_RECORD, null);

        if (cursor.moveToFirst()) {
            do {
                Record record = new Record(
                        cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("supplierName")),
                        cursor.getString(cursor.getColumnIndexOrThrow("productName")),
                        cursor.getString(cursor.getColumnIndexOrThrow("unit")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("quantity")),
                        cursor.getDouble(cursor.getColumnIndexOrThrow("unitPrice")),
                        cursor.getDouble(cursor.getColumnIndexOrThrow("otherFees")),
                        cursor.getDouble(cursor.getColumnIndexOrThrow("totalAmount")),
                        cursor.getString(cursor.getColumnIndexOrThrow("signerName")),
                        cursor.getString(cursor.getColumnIndexOrThrow("time")),
                        cursor.getString(cursor.getColumnIndexOrThrow("remarks"))
                );
                records.add(record);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return records;
    }

    // 检查厂商是否存在
    private boolean isSupplierExist(String name) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_SUPPLIER, new String[]{"id"}, "name = ?", new String[]{name}, null, null, null);
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    // 检查库管是否存在
    private boolean isSignerExist(String name) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_SIGNER, new String[]{"id"}, "name = ?", new String[]{name}, null, null, null);
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    // 获取指定厂商的记录
    public List<Record> getRecordsBySupplier(String supplierName) {
        List<Record> records = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Record WHERE supplierName = ?", new String[]{supplierName});
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    // 将cursor中的数据填充到Record对象
                    Record record = new Record(
                            cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                            cursor.getString(cursor.getColumnIndexOrThrow("supplierName")),
                            cursor.getString(cursor.getColumnIndexOrThrow("productName")),
                            cursor.getString(cursor.getColumnIndexOrThrow("unit")),
                            cursor.getInt(cursor.getColumnIndexOrThrow("quantity")),
                            cursor.getDouble(cursor.getColumnIndexOrThrow("unitPrice")),
                            cursor.getDouble(cursor.getColumnIndexOrThrow("otherFees")),
                            cursor.getDouble(cursor.getColumnIndexOrThrow("totalAmount")),
                            cursor.getString(cursor.getColumnIndexOrThrow("signerName")),
                            cursor.getString(cursor.getColumnIndexOrThrow("time")),
                            cursor.getString(cursor.getColumnIndexOrThrow("remarks"))
                    );
                    records.add(record);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return records;
    }

    /**
     * 删除记录
     */
    public void deleteRecord(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("Record", "id = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    /**
     *  更新记录
     */
    public void updateRecord(Record record) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("remarks", record.getRemarks());
        values.put("signerName", record.getSignerName());
        values.put("time", record.getTime());
        values.put("totalAmount", record.getTotalAmount());
        values.put("otherFees", record.getOtherFees());
        values.put("unitPrice", record.getUnitPrice());
        values.put("quantity", record.getQuantity());
        values.put("unit", record.getUnit());
        values.put("productName", record.getProductName());
        values.put("supplierName", record.getSupplierName());
        // 更新数据库中的记录
        db.update("Record", values, "id = ?", new String[]{String.valueOf(record.getId())});
        db.close();
    }



}

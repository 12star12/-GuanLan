package com.guanlan.wealthos;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class WealthDatabaseHelper extends SQLiteOpenHelper {

    // 1. 数据库基础信息
    public static final String DB_NAME = "guanlan.db";   // 数据库文件名
    public static final int DB_VERSION = 1;              // 数据库版本号

    // 2. 资产表 (assets)
    public static final String TABLE_ASSETS = "assets";
    public static final String COL_ASSET_ID = "id";
    public static final String COL_ASSET_NAME = "name";
    public static final String COL_ASSET_CATEGORY = "category";
    public static final String COL_ASSET_AMOUNT = "amount";

    // 3. 负债表 (liabilities)
    public static final String TABLE_LIABILITIES = "liabilities";
    public static final String COL_LIABILITY_ID = "id";
    public static final String COL_LIABILITY_NAME = "name";
    public static final String COL_LIABILITY_CATEGORY = "category";
    public static final String COL_LIABILITY_AMOUNT = "amount";

    // 4. 流水表 (transactions)
    public static final String TABLE_TRANSACTIONS = "transactions";
    public static final String COL_TRANS_ID = "id";
    public static final String COL_TRANS_TYPE = "type";
    public static final String COL_TRANS_AMOUNT = "amount";
    public static final String COL_TRANS_CATEGORY = "category";
    public static final String COL_TRANS_LEDGER = "ledger_type";
    public static final String COL_TRANS_NOTE = "note";
    public static final String COL_TRANS_DATE = "date";

    // 构造函数：传入上下文，创建数据库
    public WealthDatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    // 当数据库第一次被创建时调用，这里执行建表语句
    @Override
    public void onCreate(SQLiteDatabase db) {
        // 创建资产表
        String createAssetsTable = "CREATE TABLE " + TABLE_ASSETS + " (" +
                COL_ASSET_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_ASSET_NAME + " TEXT, " +
                COL_ASSET_CATEGORY + " TEXT, " +
                COL_ASSET_AMOUNT + " REAL)";
        db.execSQL(createAssetsTable);

        // 创建负债表
        String createLiabilitiesTable = "CREATE TABLE " + TABLE_LIABILITIES + " (" +
                COL_LIABILITY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_LIABILITY_NAME + " TEXT, " +
                COL_LIABILITY_CATEGORY + " TEXT, " +
                COL_LIABILITY_AMOUNT + " REAL)";
        db.execSQL(createLiabilitiesTable);

        // 创建流水表
        String createTransactionsTable = "CREATE TABLE " + TABLE_TRANSACTIONS + " (" +
                COL_TRANS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_TRANS_TYPE + " TEXT, " +
                COL_TRANS_AMOUNT + " REAL, " +
                COL_TRANS_CATEGORY + " TEXT, " +
                COL_TRANS_LEDGER + " TEXT, " +
                COL_TRANS_NOTE + " TEXT, " +
                COL_TRANS_DATE + " TEXT)";
        db.execSQL(createTransactionsTable);
    }

    // 当数据库版本号增加时调用（当前版本 1，暂时留空即可，以后学了升级再写）
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 暂时不写升级逻辑
    }
}
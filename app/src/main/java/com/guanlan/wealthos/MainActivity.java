package com.guanlan.wealthos;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        WealthDatabaseHelper dbHelper = new WealthDatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // ===== 1. 计算总资产 (SUM assets) =====
        double totalAssets = 0.00;
        Cursor assetCursor = db.query(WealthDatabaseHelper.TABLE_ASSETS,
                null, null, null, null, null, null);
        while (assetCursor.moveToNext()) {
            totalAssets += assetCursor.getDouble(assetCursor.getColumnIndexOrThrow(WealthDatabaseHelper.COL_ASSET_AMOUNT));
        }
        assetCursor.close();

        // ===== 2. 计算总负债 (SUM liabilities) =====
        double totalLiabilities = 0.00;
        Cursor liabilityCursor = db.query(WealthDatabaseHelper.TABLE_LIABILITIES,
                null, null, null, null, null, null);
        while (liabilityCursor.moveToNext()) {
            totalLiabilities += liabilityCursor.getDouble(liabilityCursor.getColumnIndexOrThrow(WealthDatabaseHelper.COL_LIABILITY_AMOUNT));
        }
        liabilityCursor.close();

        // ===== 3. 计算本期收入/支出/结余 =====
        double totalIncome = 0.00;
        double totalExpense = 0.00;
        Cursor cursor = db.query(WealthDatabaseHelper.TABLE_TRANSACTIONS,
                null, null, null, null, null, null);
        while (cursor.moveToNext()) {
            String type = cursor.getString(cursor.getColumnIndexOrThrow(WealthDatabaseHelper.COL_TRANS_TYPE));
            double amount = cursor.getDouble(cursor.getColumnIndexOrThrow(WealthDatabaseHelper.COL_TRANS_AMOUNT));
            if ("收入".equals(type)) {
                totalIncome += amount;
            } else if ("支出".equals(type)) {
                totalExpense += amount;
            }
        }
        cursor.close();
        db.close(); // 全部查询完毕，关闭数据库

        double totalBalance = totalIncome - totalExpense;
        double netWorth = totalAssets - totalLiabilities; // 净资产 = 总资产 - 总负债

        // ===== 4. 更新 UI =====
        TextView tvTotalAssets = findViewById(R.id.tv_total_assets);
        TextView tvTotalLiabilities = findViewById(R.id.tv_total_liabilities);
        TextView tvNetWorth = findViewById(R.id.tv_net_worth);
        TextView tvTotalIncome = findViewById(R.id.tv_total_income);
        TextView tvTotalExpense = findViewById(R.id.tv_total_expense);
        TextView tvTotalBalance = findViewById(R.id.tv_total_balance);

        tvTotalAssets.setText("总资产：¥ " + String.format("%.2f", totalAssets));
        tvTotalLiabilities.setText("总负债：¥ " + String.format("%.2f", totalLiabilities));
        tvNetWorth.setText("净资产：¥ " + String.format("%.2f", netWorth));
        tvTotalIncome.setText("本期收入：¥ " + String.format("%.2f", totalIncome));
        tvTotalExpense.setText("本期支出：¥ " + String.format("%.2f", totalExpense));
        tvTotalBalance.setText("本期结余：¥ " + String.format("%.2f", totalBalance));

        // 组装给第二页的 Intent 数据
        String netWorthText = "总资产：¥ " + String.format("%.2f", netWorth);

        // ===== 5. 跳转逻辑 =====
        Button btnBalanceSheet = findViewById(R.id.btn_balance_sheet);
        btnBalanceSheet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, BalanceSheetActivity.class);
                intent.putExtra("net_worth", netWorthText);
                startActivity(intent);
            }
        });

        Button btnAddTransaction = findViewById(R.id.btn_add_transaction);
        btnAddTransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddTransactionActivity.class);
                startActivity(intent);
            }
        });
    }
}
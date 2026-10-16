package com.guanlan.wealthos;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class BalanceSheetActivity extends AppCompatActivity {

    private EditText etAssetName;
    private EditText etAssetAmount;
    private Button btnAddAsset;
    private ListView lvAssets;
    private Button btnBack;

    private WealthDatabaseHelper dbHelper;

    private List<String> assetList;
    private List<Long> assetIds; // 记录数据库中的 id
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_balance_sheet);

        // 接收首页传递过来的数据
        TextView tvReceivedData = findViewById(R.id.tv_received_data);
        Intent intent = getIntent();
        String data = intent.getStringExtra("net_worth");
        if (data != null) {
            tvReceivedData.setText(data);
        }

        dbHelper = new WealthDatabaseHelper(this);

        etAssetName = findViewById(R.id.et_asset_name);
        etAssetAmount = findViewById(R.id.et_asset_amount);
        btnAddAsset = findViewById(R.id.btn_add_asset);
        lvAssets = findViewById(R.id.lv_assets);
        btnBack = findViewById(R.id.btn_back);

        assetList = new ArrayList<>();
        assetIds = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, assetList);
        lvAssets.setAdapter(adapter);

        loadAssets();

        btnAddAsset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addAsset();
            }
        });

        // 长按删除
        lvAssets.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                showDeleteDialog(position);
                return true;
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void addAsset() {
        String name = etAssetName.getText().toString().trim();
        String amountStr = etAssetAmount.getText().toString().trim();

        if (name.isEmpty() || amountStr.isEmpty()) {
            Toast.makeText(this, "请填写完整信息", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount = Double.parseDouble(amountStr);

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(WealthDatabaseHelper.COL_ASSET_NAME, name);
        values.put(WealthDatabaseHelper.COL_ASSET_CATEGORY, "其他");
        values.put(WealthDatabaseHelper.COL_ASSET_AMOUNT, amount);

        long result = db.insert(WealthDatabaseHelper.TABLE_ASSETS, null, values);
        db.close();

        if (result != -1) {
            Toast.makeText(this, "添加成功", Toast.LENGTH_SHORT).show();
            etAssetName.setText("");
            etAssetAmount.setText("");
            loadAssets();
        } else {
            Toast.makeText(this, "添加失败", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadAssets() {
        assetList.clear();
        assetIds.clear();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query(WealthDatabaseHelper.TABLE_ASSETS,
                null, null, null, null, null, null);

        while (cursor.moveToNext()) {
            long id = cursor.getLong(cursor.getColumnIndexOrThrow(WealthDatabaseHelper.COL_ASSET_ID));
            String name = cursor.getString(cursor.getColumnIndexOrThrow(WealthDatabaseHelper.COL_ASSET_NAME));
            double amount = cursor.getDouble(cursor.getColumnIndexOrThrow(WealthDatabaseHelper.COL_ASSET_AMOUNT));

            assetIds.add(id);
            assetList.add(name + "：" + amount + " 元");
        }
        cursor.close();
        db.close();
        adapter.notifyDataSetChanged();
    }

    private void showDeleteDialog(final int position) {
        new AlertDialog.Builder(this)
                .setTitle("删除资产")
                .setMessage("确定要删除这条资产记录吗？")
                .setPositiveButton("删除", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        long assetId = assetIds.get(position);
                        SQLiteDatabase db = dbHelper.getWritableDatabase();
                        db.delete(WealthDatabaseHelper.TABLE_ASSETS,
                                WealthDatabaseHelper.COL_ASSET_ID + " = ?",
                                new String[]{String.valueOf(assetId)});
                        db.close();
                        Toast.makeText(BalanceSheetActivity.this, "已删除", Toast.LENGTH_SHORT).show();
                        loadAssets();
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }
}
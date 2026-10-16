package com.guanlan.wealthos;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddTransactionActivity extends AppCompatActivity {

    // 1. 声明界面上要用到的控件
    private EditText etAmount;
    private EditText etNote;
    private RadioGroup rgType;
    private Spinner spCategory;
    private Button btnSave;

    // 2. 声明数据库帮助类
    private WealthDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_transaction);

        // 3. 初始化数据库帮助类（准备写入数据）
        dbHelper = new WealthDatabaseHelper(this);

        // 4. 绑定界面控件
        etAmount = findViewById(R.id.et_amount);
        etNote = findViewById(R.id.et_note);
        rgType = findViewById(R.id.rg_type);
        spCategory = findViewById(R.id.sp_category);
        btnSave = findViewById(R.id.btn_save);
        // ===== 新增：给分类 Spinner 填充数据 =====
        String[] categories = {"餐饮", "交通", "购物", "工资", "投资", "其他"};
        android.widget.ArrayAdapter<String> adapter = new android.widget.ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCategory.setAdapter(adapter);
        // 5. 给“保存”按钮设置点击事件
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveTransaction(); // 调用下面的保存方法
            }
        });
    }

    // 6. 执行保存数据的核心逻辑
    private void saveTransaction() {
        // 获取金额
        String amountStr = etAmount.getText().toString().trim();
        if (amountStr.isEmpty()) {
            Toast.makeText(this, "请输入金额", Toast.LENGTH_SHORT).show();
            return;
        }
        double amount = Double.parseDouble(amountStr);

        // 判断收入还是支出
        String type = "收入";
        if (rgType.getCheckedRadioButtonId() == R.id.rb_expense) {
            type = "支出";
        }

        // 获取分类 (暂时写死为“未分类”，下节课再处理 Spinner)
        // 读取 Spinner 选中的分类
        String category = spCategory.getSelectedItem().toString();
        String note = etNote.getText().toString().trim();

        // 获取当前日期
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String date = sdf.format(new Date());
        String ledgerType = "生活账";

        // 7. 写入数据库
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(WealthDatabaseHelper.COL_TRANS_TYPE, type);
        values.put(WealthDatabaseHelper.COL_TRANS_AMOUNT, amount);
        values.put(WealthDatabaseHelper.COL_TRANS_CATEGORY, category);
        values.put(WealthDatabaseHelper.COL_TRANS_LEDGER, ledgerType);
        values.put(WealthDatabaseHelper.COL_TRANS_NOTE, note);
        values.put(WealthDatabaseHelper.COL_TRANS_DATE, date);

        // 执行插入操作
        long newRowId = db.insert(WealthDatabaseHelper.TABLE_TRANSACTIONS, null, values);

        if (newRowId != -1) {
            Toast.makeText(this, "保存成功！", Toast.LENGTH_SHORT).show();
            finish(); // 保存成功后关闭当前页面，自动返回首页
        } else {
            Toast.makeText(this, "保存失败，请重试", Toast.LENGTH_SHORT).show();
        }

        db.close(); // 关闭数据库连接
    }
}
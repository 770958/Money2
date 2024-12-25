package com.example.money;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * 新建厂商活动类
 */
public class NewSupplierActivity extends AppCompatActivity {

    private EditText edtSupplierName;
    private Button btnSave;
    private RecyclerView recyclerView;
    private SupplierAdapter supplierAdapter;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_supplier);

        edtSupplierName = findViewById(R.id.edtSupplierName);
        btnSave = findViewById(R.id.btnSave);
        recyclerView = findViewById(R.id.recyclerView);
        databaseHelper = new DatabaseHelper(this);

        // 设置RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 获取并展示所有厂商
        displaySuppliers();

        // 设置保存按钮点击事件
        btnSave.setOnClickListener(v -> saveNewSupplier());
    }

    private void saveNewSupplier() {
        String supplierName = edtSupplierName.getText().toString();

        if (!supplierName.isEmpty()) {
            // 插入新的厂商
            databaseHelper.insertSupplier(supplierName);
            Toast.makeText(this, "厂商已添加", Toast.LENGTH_SHORT).show();

            // 更新RecyclerView
            displaySuppliers();

            edtSupplierName.setText(""); // 清空输入框
        } else {
            Toast.makeText(this, "请输入厂商名称", Toast.LENGTH_SHORT).show();
        }
    }

    private void displaySuppliers() {
        // 获取所有厂商数据
        List<String> suppliers =  new ArrayList<>();
        Cursor cursor = databaseHelper.getAllSuppliers();
        // 检查Cursor是否有效
        if (cursor != null) {
            // 遍历Cursor中的数据
            while (cursor.moveToNext()) {
                // 假设我们只取第一列的数据
                String item = cursor.getString(0); // 获取第一列的数据（根据你查询的列，索引可能不同）
                suppliers.add(item);
            }
            cursor.close(); // 记得关闭Cursor
        }

        // 如果列表为空，显示提示
        if (suppliers.isEmpty()) {
            Toast.makeText(this, "没有厂商数据！", Toast.LENGTH_SHORT).show();
        }

        // 更新RecyclerView
        supplierAdapter = new SupplierAdapter(suppliers);
        recyclerView.setAdapter(supplierAdapter);
    }
}

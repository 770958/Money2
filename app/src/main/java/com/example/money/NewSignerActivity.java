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
 * 新建库管活动
 */
public class NewSignerActivity extends AppCompatActivity {

    private EditText edtSignerName;
    private Button btnSave;
    private RecyclerView recyclerViewSigners;
    private SignerAdapter signerAdapter;
    private DatabaseHelper dbHelper;
    private List<String> signerList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_signer);

        edtSignerName = findViewById(R.id.edtSignerName);
        btnSave = findViewById(R.id.btnSave);
        recyclerViewSigners = findViewById(R.id.recyclerViewSigners);

        dbHelper = new DatabaseHelper(this);

        // 设置 RecyclerView
        recyclerViewSigners.setLayoutManager(new LinearLayoutManager(this));
        signerList = new ArrayList<>();
        signerAdapter = new SignerAdapter(signerList);
        recyclerViewSigners.setAdapter(signerAdapter);

        // 加载所有库管
        loadSigners();

        // 保存按钮点击事件
        btnSave.setOnClickListener(v -> saveNewSigner());
    }

    // 加载所有库管
    private void loadSigners() {
        signerList.clear();
        List<String> resultList = new ArrayList<>();
        Cursor cursor = dbHelper.getAllSigners();
        // 检查Cursor是否有效
        if (cursor != null) {
            // 遍历Cursor中的数据
            while (cursor.moveToNext()) {
                // 假设我们只取第一列的数据
                String item = cursor.getString(0); // 获取第一列的数据（根据你查询的列，索引可能不同）
                resultList.add(item);
            }
            cursor.close(); // 记得关闭Cursor
        }
        signerList.addAll(resultList);
        signerAdapter.notifyDataSetChanged();
    }

    // 保存新的库管
    private void saveNewSigner() {
        String signerName = edtSignerName.getText().toString().trim();

        if (!signerName.isEmpty()) {
            dbHelper.insertSigner(signerName);
            Toast.makeText(this, "库管已添加", Toast.LENGTH_SHORT).show();

            // 清空输入框，方便继续添加
            edtSignerName.setText("");

            // 重新加载库管列表
            loadSigners();
        } else {
            Toast.makeText(this, "请输入库管名称", Toast.LENGTH_SHORT).show();
        }
    }
}

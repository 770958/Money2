package com.example.money;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

/**
 * 主界面
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        dbHelper.insertSupplier("供应商A");
        dbHelper.insertSupplier("供应商B");
        dbHelper.insertSigner("库管A");
        dbHelper.insertSigner("库管B");

        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) Button btnNewRecord = findViewById(R.id.btnNewRecord);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) Button btnNewSupplier = findViewById(R.id.btnNewSupplier);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) Button btnNewSigner = findViewById(R.id.btnNewSigner);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) Button btnViewRecords = findViewById(R.id.btnViewRecords);

        // 点击新增记录按钮，跳转到新增记录页面
        btnNewRecord.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, NewRecordActivity.class);
            startActivity(intent);
        });

        // 点击新增厂商按钮，跳转到新增厂商页面
        btnNewSupplier.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, NewSupplierActivity.class);
            startActivity(intent);
        });

        // 点击新增库管按钮，跳转到新增库管页面
        btnNewSigner.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, NewSignerActivity.class);
            startActivity(intent);
        });

        // 设置按钮点击事件
        btnViewRecords.setOnClickListener(v -> {
            // 跳转到查看现有记录的界面
            Intent intent = new Intent(MainActivity.this, ViewRecordsActivity.class);
            startActivity(intent);
        });

    }
}

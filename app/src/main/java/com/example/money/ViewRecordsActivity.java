package com.example.money;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ViewRecordsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecordAdapter recordAdapter;
    private DatabaseHelper databaseHelper;
    private Spinner reportSupplier;
    private Button btnExportReport;
    private TextView tvTotalAmount;  // 用于显示总金额

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_records);

        recyclerView = findViewById(R.id.recyclerView);
        reportSupplier = findViewById(R.id.reportSupplier);
        btnExportReport = findViewById(R.id.btnExportReport);
        tvTotalAmount = findViewById(R.id.tvTotalAmount);

        // 初始化数据库助手
        databaseHelper = new DatabaseHelper(this);

        // 获取所有厂商名称并绑定到 Spinner
        ArrayAdapter<String> supplierAdapter = getStringArrayAdapter();
        reportSupplier.setAdapter(supplierAdapter);

        // 默认展示所有记录
        List<Record> recordList = databaseHelper.getRecordsBySupplier("全部");
        setUpRecyclerView(recordList);

        // Spinner选择事件
        reportSupplier.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedSupplier = (String) parentView.getItemAtPosition(position);
                List<Record> filteredRecords;
                if ("全部".equals(selectedSupplier)) {
                    filteredRecords = databaseHelper.getAllRecords();
                } else {
                    filteredRecords = databaseHelper.getRecordsBySupplier(selectedSupplier);
                }
                setUpRecyclerView(filteredRecords);
                updateTotalAmount(filteredRecords);  // 更新总金额

            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing
            }
        });


        // 获取生成报表按钮并设置点击事件
        Button btnGenerateReport = findViewById(R.id.btnGenerateReport);
        btnGenerateReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 点击按钮跳转到报表生成界面
                Intent intent = new Intent(ViewRecordsActivity.this, ReportActivity.class);
                startActivity(intent);
            }
        });

        // 导出报表按钮点击事件
        btnExportReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generateReport();
            }
        });
    }

    private @NotNull ArrayAdapter<String> getStringArrayAdapter() {
        List<String> supplierList = new ArrayList<>();
        Cursor cursor = databaseHelper.getAllSuppliers();
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    // 获取供应商名称
                    @SuppressLint("Range") String supplierName = cursor.getString(cursor.getColumnIndex("name"));
                    supplierList.add(supplierName);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        // 将"全部"选项放在第一个位置，以便用户可以快速选择所有记录。
        supplierList.add(0, "全部");
        ArrayAdapter<String> supplierAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, supplierList);
        supplierAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        return supplierAdapter;
    }

    // 计算并更新总金额
    private void updateTotalAmount(List<Record> records) {
        double totalAmount = 0;
        for (Record record : records) {
            totalAmount += record.getTotalAmount();  // 假设 getTotalAmount() 返回的是记录的总金额
        }
        // 更新 UI 显示总金额
        tvTotalAmount.setText("该厂总额：" + totalAmount);
    }

    // 设置RecyclerView
    private void setUpRecyclerView(List<Record> recordList) {
        if (recordList.isEmpty()) {
            Toast.makeText(this, "没有找到记录！", Toast.LENGTH_SHORT).show();
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recordAdapter = new RecordAdapter(recordList);
        recyclerView.setAdapter(recordAdapter);
    }

    // 生成报表
    private void generateReport() {
        // 获取当前选中的厂商
        String selectedSupplier = (String) reportSupplier.getSelectedItem();
        List<Record> recordsToExport;

        // 获取选择厂商的记录
        if ("全部".equals(selectedSupplier)) {
            recordsToExport = databaseHelper.getAllRecords();
        } else {
            recordsToExport = databaseHelper.getRecordsBySupplier(selectedSupplier);
        }

        if (recordsToExport.isEmpty()) {
            Toast.makeText(this, "没有记录可导出", Toast.LENGTH_SHORT).show();
            return;
        }

        // 创建报表导出文件
        File exportDir = new File(getExternalFilesDir(null), "Reports");
        if (!exportDir.exists()) {
            exportDir.mkdirs();  // 创建文件夹
        }

        long currentTimeMillis = System.currentTimeMillis();
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日");
        String time = dateFormat.format(new Date(currentTimeMillis));

        // 创建CSV文件
        File csvFile = new File(exportDir, "report_" + selectedSupplier + "_" + time + ".csv");
        Log.d("Export", "文件路径: " + csvFile.getAbsolutePath());

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(csvFile))) {

            // 写入CSV文件头部
            writer.write("总金额\n");
            List<Record> filteredRecords;
            if ("全部".equals(selectedSupplier)) {
                filteredRecords = databaseHelper.getAllRecords();
            } else {
                filteredRecords = databaseHelper.getRecordsBySupplier(selectedSupplier);
            }
            double totalAmount = 0;
            for (Record record : filteredRecords) {
                totalAmount += record.getTotalAmount();
            }
            writer.write(totalAmount + "\n");


            writer.write("厂商名称,产品名称,单位,数量,单价,其他费用,总金额,库管签字人,时间,备注\n");

            // 写入每一条记录
            for (Record record : recordsToExport) {
                writer.write(record.getSupplierName() + ","
                        + record.getProductName() + ","
                        + record.getUnit() + ","
                        + record.getQuantity() + ","
                        + record.getUnitPrice() + ","
                        + record.getOtherFees() + ","
                        + record.getTotalAmount() + ","
                        + record.getSignerName() + ","
                        + record.getTime() + ","
                        + record.getRemarks() + "\n");
                Log.d("Export", "写入记录: " + record.toString());
            }

            writer.flush();  // 强制写入
            Toast.makeText(this, "报表已导出至：" + csvFile.getAbsolutePath(), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            Log.e("Export", "导出失败: " + e.getMessage());
            e.printStackTrace();
            Toast.makeText(this, "导出失败", Toast.LENGTH_SHORT).show();
        }
    }
}
package com.example.money;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * 报告活动，用于显示每月销售额。
 */
public class ReportActivity extends AppCompatActivity {

    private Spinner yearSpinner;
    private TextView yearTitle;
    private TextView[] monthSalesViews = new TextView[12];
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);


        yearSpinner = findViewById(R.id.yearSpinner);
        yearTitle = findViewById(R.id.yearTitle);

        // 获取月份展示视图
        monthSalesViews[0] = findViewById(R.id.janSales);
        monthSalesViews[1] = findViewById(R.id.febSales);
        monthSalesViews[2] = findViewById(R.id.marSales);
        monthSalesViews[3] = findViewById(R.id.aprSales);
        monthSalesViews[4] = findViewById(R.id.maySales);
        monthSalesViews[5] = findViewById(R.id.junSales);
        monthSalesViews[6] = findViewById(R.id.julSales);
        monthSalesViews[7] = findViewById(R.id.augSales);
        monthSalesViews[8] = findViewById(R.id.sepSales);
        monthSalesViews[9] = findViewById(R.id.octSales);
        monthSalesViews[10] = findViewById(R.id.novSales);
        monthSalesViews[11] = findViewById(R.id.decSales);
        dbHelper = new DatabaseHelper(this);

        // 设置年份选择 Spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.year_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        yearSpinner.setAdapter(adapter);

        // 年份选择事件
        yearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedYear = parentView.getItemAtPosition(position).toString();
                yearTitle.setText(selectedYear); // 更新年份
                updateMonthlySales(selectedYear); // 更新销售额
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });

        // 初始化年份下拉列表
        setUpYearSpinner();

        // 初始加载数据, 获取当前年份
        long currentTimeMillis = System.currentTimeMillis();
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy");
        String currentYear = dateFormat.format(new Date(currentTimeMillis));
        updateMonthlySales(currentYear);
    }

    public List<String> getDistinctYears() {
        List<String> yearList = new ArrayList<>();

        // 查询数据库中所有记录的时间字段
        String query = "SELECT time FROM Record";
        Cursor cursor = dbHelper.getReadableDatabase().rawQuery(query, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                // 获取每条记录的时间
                @SuppressLint("Range") String time = cursor.getString(cursor.getColumnIndex("time"));
                // 提取年份部分
                String year = time.split("年")[0];  // 根据"yyyy年MM月dd日"格式拆分，获取年份
                if (!yearList.contains(year)) {
                    yearList.add(year);  // 添加到年份列表，并确保不重复
                }
            }
            cursor.close();
        }

        return yearList;
    }

    public void setUpYearSpinner() {
        List<String> yearList = getDistinctYears();

        // 为 Spinner 设置适配器
        ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, yearList);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        yearSpinner.setAdapter(yearAdapter);
    }


    // 更新每月销售额
    private void updateMonthlySales(String year) {
        SQLiteDatabase db = openOrCreateDatabase("money_db", MODE_PRIVATE, null);

        // 执行查询，获取每月的销售额
        String query = "SELECT strftime('%m', replace(replace(replace(time, '年', '-'), '月', '-'), '日', '')), SUM(totalAmount) " +
                "FROM Record WHERE strftime('%Y', replace(replace(replace(time, '年', '-'), '月', '-'), '日', '')) = ? " +
                "GROUP BY strftime('%m', replace(replace(replace(time, '年', '-'), '月', '-'), '日', ''))";
        Cursor cursor = db.rawQuery(query, new String[]{year});

        // 初始化所有月份销售额为0
        double yearlyTotal = 0; // 年合计
        for (int i = 0; i < 12; i++) {
            monthSalesViews[i].setText("0");
        }

        // 设置查询结果
        if (cursor != null && cursor.moveToFirst()) {
            do {
                String month = cursor.getString(0);  // 获取月份
                double amount = cursor.getDouble(1);  // 获取销售额

                // 累加年合计
                yearlyTotal += amount;

                // 找到对应的月份并更新销售额
                int monthIndex = Integer.parseInt(month) - 1;
                monthSalesViews[monthIndex].setText(String.format("%.2f", amount));
            } while (cursor.moveToNext());
            cursor.close();
        }

        // 更新年合计到UI
        TextView totalSalesView = findViewById(R.id.totalSalesView);
        totalSalesView.setText(String.format("年合计: %.2f", yearlyTotal));
    }

}

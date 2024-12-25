package com.example.money;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import java.util.Date;

import androidx.appcompat.app.AppCompatActivity;

/**
 * 新建记录的 Activity。
 */
public class NewRecordActivity extends AppCompatActivity {

    private EditText edtProductName, edtUnit, edtQuantity, edtUnitPrice, edtOtherFees, edtRemarks;
    private Spinner spinnerSupplier, spinnerSigner;
    private Button btnSave;
    private DatabaseHelper dbHelper;

    private Button btnDatePicker;
    private String selectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_record);

        // 初始化界面组件
        edtProductName = findViewById(R.id.edtProductName);
        edtUnit = findViewById(R.id.edtUnit);
        edtQuantity = findViewById(R.id.edtQuantity);
        edtUnitPrice = findViewById(R.id.edtUnitPrice);
        edtOtherFees = findViewById(R.id.edtOtherFees);
        edtRemarks = findViewById(R.id.edtRemarks);
        spinnerSupplier = findViewById(R.id.spinnerSupplier);
        spinnerSigner = findViewById(R.id.spinnerSigner);
        btnSave = findViewById(R.id.btnSave);


        btnDatePicker = findViewById(R.id.btnDatePicker);

        // 设置默认日期为当前日期
        selectedDate = getCurrentDate();

        btnDatePicker.setOnClickListener(v -> showDatePickerDialog());

        dbHelper = new DatabaseHelper(this);

        // 加载供应商数据
        loadSuppliers();

        // 加载库管数据
        loadSigners();

        // 保存按钮点击事件
        btnSave.setOnClickListener(v -> saveNewRecord());
    }

    private void showDatePickerDialog() {
        // 获取当前日期
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year1, month1, dayOfMonth) -> {
                    // 获取选择的日期
                    selectedDate = String.format("%d年%02d月%02d日", year1, month1 + 1, dayOfMonth);
                    btnDatePicker.setText(selectedDate); // 更新按钮文本为选中的日期
                },
                year, month, day);

        datePickerDialog.show();
    }

    private String getCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        return String.format("%d年%02d月%02d日", year, month + 1, day);
    }

    @SuppressLint("Range")
    private void loadSuppliers() {
        Cursor cursor = dbHelper.getAllSuppliers();
        // 检查是否有供应商数据
        if (cursor != null && cursor.getCount() > 0) {
            // 将查询到的数据放入一个列表中
            String[] suppliers = new String[cursor.getCount()];
            int i = 0;
            while (cursor.moveToNext()) {
                suppliers[i] = cursor.getString(cursor.getColumnIndex("name"));
                i++;
            }
            cursor.close();

            // 使用 ArrayAdapter 来将数据绑定到 Spinner
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, suppliers);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerSupplier.setAdapter(adapter);
        }
    }

    @SuppressLint("Range")
    private void loadSigners() {
        Cursor cursor = dbHelper.getAllSigners();
        // 检查是否有库管数据
        if (cursor != null && cursor.getCount() > 0) {
            // 将查询到的数据放入一个列表中
            String[] signers = new String[cursor.getCount()];
            int i = 0;
            while (cursor.moveToNext()) {
                signers[i] = cursor.getString(cursor.getColumnIndex("name"));
                i++;
            }
            cursor.close();

            // 使用 ArrayAdapter 来将数据绑定到 Spinner
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, signers);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerSigner.setAdapter(adapter);
        }
    }

    private void saveNewRecord() {
        String productName = edtProductName.getText().toString();
        String unit = edtUnit.getText().toString();
        String quantityStr = edtQuantity.getText().toString();
        String unitPriceStr = edtUnitPrice.getText().toString();
        String otherFeesStr = edtOtherFees.getText().toString();
        String remarks = edtRemarks.getText().toString();

        // 检查必填项是否为空
        if (productName.isEmpty() || unit.isEmpty() || quantityStr.isEmpty() || unitPriceStr.isEmpty() || otherFeesStr.isEmpty()) {
            Toast.makeText(this, "请填写所有必填项", Toast.LENGTH_SHORT).show();
            return;
        }

        // 尝试将数量、单价、其他费用转换为 double 类型
        double quantity = 0, unitPrice = 0, otherFees = 0;
        try {
            quantity = Double.parseDouble(quantityStr);
            unitPrice = Double.parseDouble(unitPriceStr);
            otherFees = Double.parseDouble(otherFeesStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "请输入有效的数字", Toast.LENGTH_SHORT).show();
            return;
        }

        // 从 Spinner 中获取选中的厂商和库管
        String supplierName = spinnerSupplier.getSelectedItem().toString();
        String signerName = spinnerSigner.getSelectedItem().toString();

        // 计算金额
        double totalAmount = quantity * unitPrice + otherFees;

        // 使用用户选择的日期
        String time = selectedDate;

        // 插入记录到数据库
        dbHelper.insertRecord(supplierName, productName, unit, quantity, unitPrice, otherFees, totalAmount, signerName, time, remarks);

        Toast.makeText(this, "记录已保存", Toast.LENGTH_SHORT).show();
        finish();
    }




}

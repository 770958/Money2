package com.example.money;

import android.app.AlertDialog;
import android.content.Context;
import android.database.Cursor;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class RecordAdapter extends RecyclerView.Adapter<RecordAdapter.RecordViewHolder> {

    private final List<Record> recordList;
    public RecordAdapter(List<Record> recordList) {
        this.recordList = recordList;
    }

    @NonNull
    @Override
    public RecordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_record, parent, false);
        return new RecordViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecordViewHolder holder, int position) {
        Record record = recordList.get(position);
        holder.tvId.setText("ID：" + record.getId());
        holder.tvSupplierName.setText("供应商：" + record.getSupplierName());
        holder.tvProductName.setText("产品名称：" + record.getProductName());
        holder.tvUnit.setText("单位：" + record.getUnit());
        holder.tvQuantity.setText("数量：" + record.getQuantity());
        holder.tvUnitPrice.setText("单价：" + record.getUnitPrice());
        holder.tvOtherFees.setText("其他费用：" + record.getOtherFees());
        holder.tvSignerName.setText("库管：" + record.getSignerName());
        holder.tvTime.setText("时间：" + record.getTime());
        holder.tvRemarks.setText("备注：" + record.getRemarks());
        holder.tvTotalAmount.setText("总金额：" + record.getTotalAmount());

        // 更新按钮点击事件
        holder.btnUpdate.setOnClickListener(v -> {
            showUpdateDialog(holder.itemView.getContext(), record);
        });

        // 删除按钮点击事件
        holder.btnDelete.setOnClickListener(v -> {
            deleteRecord(holder.itemView.getContext(), record);
        });
    }


    @Override
    public int getItemCount() {
        return recordList.size();
    }

    public static class RecordViewHolder extends RecyclerView.ViewHolder {
        TextView tvId, tvProductName, tvQuantity, tvUnit, tvRemarks;
        TextView tvSupplierName, tvUnitPrice, tvOtherFees, tvSignerName,tvTime,tvTotalAmount;
        Button btnUpdate, btnDelete;

        public RecordViewHolder(@NonNull View itemView) {
            super(itemView);
            tvId = itemView.findViewById(R.id.tvId);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            tvUnit = itemView.findViewById(R.id.tvUnit);
            tvRemarks = itemView.findViewById(R.id.tvRemarks);
            btnUpdate = itemView.findViewById(R.id.btnUpdate);
            btnDelete = itemView.findViewById(R.id.btnDelete);

            tvSupplierName = itemView.findViewById(R.id.tvSupplierName);
            tvUnitPrice = itemView.findViewById(R.id.tvUnitPrice);
            tvOtherFees = itemView.findViewById(R.id.tvOtherFees);
            tvSignerName = itemView.findViewById(R.id.tvSignerName);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvTotalAmount = itemView.findViewById(R.id.tvTotalAmount);
        }
    }

    private void showUpdateDialog(Context context, Record record) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_update_record, null);
        builder.setView(view);

        // 获取所有字段
        EditText etProductName = view.findViewById(R.id.etUpdateProductName);
        EditText etUnit = view.findViewById(R.id.etUpdateUnit);
        EditText etQuantity = view.findViewById(R.id.etUpdateQuantity);
        EditText etUnitPrice = view.findViewById(R.id.etUpdateUnitPrice);
        EditText etOtherFees = view.findViewById(R.id.etUpdateOtherFees);
        EditText etRemarks = view.findViewById(R.id.etUpdateRemarks);
        Spinner spinnerVendor = view.findViewById(R.id.spinnerUpdateVendor);
        Spinner spinnerManager = view.findViewById(R.id.spinnerUpdateManager);
        TextView tvTotalAmount = view.findViewById(R.id.tvUpdateTotalAmount);

        // 填充数据
        etProductName.setText(record.getProductName());
        etUnit.setText(record.getUnit());
        etQuantity.setText(String.valueOf(record.getQuantity()));
        etUnitPrice.setText(String.valueOf(record.getUnitPrice()));
        etOtherFees.setText(String.valueOf(record.getOtherFees()));
        etRemarks.setText(record.getRemarks());
        tvTotalAmount.setText("总金额：" + record.getTotalAmount());

        // 使用DatabaseHelper获取厂商和库管签字人列表
        DatabaseHelper databaseHelper = new DatabaseHelper(context);

        List<String> vendors =  new ArrayList<>();
        Cursor cursor = databaseHelper.getAllSuppliers();
        // 检查Cursor是否有效
        if (cursor != null) {
            // 遍历Cursor中的数据
            while (cursor.moveToNext()) {
                // 假设我们只取第一列的数据
                String item = cursor.getString(0); // 获取第一列的数据（根据你查询的列，索引可能不同）
                vendors.add(item);
            }
            cursor.close(); // 记得关闭Cursor
        }

        List<String> managers =  new ArrayList<>();
        Cursor cursor1 = databaseHelper.getAllSigners();
        // 检查Cursor是否有效
        if (cursor1 != null) {
            // 遍历Cursor中的数据
            while (cursor1.moveToNext()) {
                // 假设我们只取第一列的数据
                String item = cursor1.getString(0); // 获取第一列的数据（根据你查询的列，索引可能不同）
                managers.add(item);
            }
            cursor1.close(); // 记得关闭Cursor
        }

        // 初始化厂商下拉框
        ArrayAdapter<String> vendorAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, vendors);
        vendorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerVendor.setAdapter(vendorAdapter);
        spinnerVendor.setSelection(vendors.indexOf(record.getSupplierName()));

        // 初始化库管签字人下拉框
        ArrayAdapter<String> managerAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, managers);
        managerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerManager.setAdapter(managerAdapter);
        spinnerManager.setSelection(managers.indexOf(record.getSignerName()));

        // 实时计算总金额
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    double quantity = Double.parseDouble(etQuantity.getText().toString().isEmpty() ? "0" : etQuantity.getText().toString());
                    double unitPrice = Double.parseDouble(etUnitPrice.getText().toString().isEmpty() ? "0" : etUnitPrice.getText().toString());
                    double otherFees = Double.parseDouble(etOtherFees.getText().toString().isEmpty() ? "0" : etOtherFees.getText().toString());
                    double totalAmount = (quantity * unitPrice) + otherFees;
                    tvTotalAmount.setText("总金额：" + String.format("%.2f", totalAmount));
                } catch (NumberFormatException e) {
                    tvTotalAmount.setText("总金额：计算错误");
                }
            }
        };

        etQuantity.addTextChangedListener(textWatcher);
        etUnitPrice.addTextChangedListener(textWatcher);
        etOtherFees.addTextChangedListener(textWatcher);

        // 确认和取消按钮
        Button btnConfirm = view.findViewById(R.id.btnUpdateConfirm);
        Button btnCancel = view.findViewById(R.id.btnUpdateCancel);

        AlertDialog dialog = builder.create();

        btnConfirm.setOnClickListener(v -> {
            record.setProductName(etProductName.getText().toString());
            record.setUnit(etUnit.getText().toString());
            record.setQuantity(Double.parseDouble(etQuantity.getText().toString().isEmpty() ? "0" : etQuantity.getText().toString()));
            record.setUnitPrice(Double.parseDouble(etUnitPrice.getText().toString().isEmpty() ? "0" : etUnitPrice.getText().toString()));
            record.setOtherFees(Double.parseDouble(etOtherFees.getText().toString().isEmpty() ? "0" : etOtherFees.getText().toString()));

            double quantity = record.getQuantity();
            double unitPrice = record.getUnitPrice();
            double otherFees = record.getOtherFees();
            record.setTotalAmount((quantity * unitPrice) + otherFees);

            record.setRemarks(etRemarks.getText().toString());
            record.setSupplierName(spinnerVendor.getSelectedItem().toString());
            record.setSignerName(spinnerManager.getSelectedItem().toString());

            // 更新数据库
            databaseHelper.updateRecord(record);

            Toast.makeText(context, "记录已更新", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }


    private void deleteRecord(Context context, Record record) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("确认删除");
        builder.setMessage("确定要删除此记录吗？");
        builder.setPositiveButton("删除", (dialog, which) -> {
            DatabaseHelper databaseHelper = new DatabaseHelper(context);
            databaseHelper.deleteRecord(record.getId());
            Toast.makeText(context, "记录已删除", Toast.LENGTH_SHORT).show();

            // 刷新 RecyclerView
            recordList.remove(record);
            notifyDataSetChanged();
        });
        builder.setNegativeButton("取消", null);
        builder.show();
    }

}

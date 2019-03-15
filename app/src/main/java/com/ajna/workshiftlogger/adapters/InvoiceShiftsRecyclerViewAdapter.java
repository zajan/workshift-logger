package com.ajna.workshiftlogger.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;

import com.ajna.workshiftlogger.R;
import com.ajna.workshiftlogger.model.Shift;

import java.text.DateFormat;
import java.util.List;

public class InvoiceShiftsRecyclerViewAdapter extends RecyclerView.Adapter<InvoiceShiftsRecyclerViewAdapter.InvoiceShiftsViewHolder> {
    private static final String TAG = "InvoiceShiftsRecyclerVi";

    Context context;
    List<Shift> shifts;

    public InvoiceShiftsRecyclerViewAdapter(Context context, List<Shift> shifts) {
        Log.d(TAG, "InvoiceShiftsRecyclerViewAdapter: constructor");
        this.context = context;
        this.shifts = shifts;
    }

    @NonNull
    @Override
    public InvoiceShiftsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: starts");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_invoice_shift, parent, false);
        return new InvoiceShiftsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InvoiceShiftsViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: starts");
        if(shifts == null){
            return;
        }
        Shift shift = shifts.get(position);

        DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(context);

        holder.checkBox.setText(String.format("Shift %s", String.valueOf(position + 1)));
        holder.etDate.setText(dateFormat.format(shift.getStartTime()));
        holder.etDuration.setText(String.format("%s min", String.valueOf(shift.calculateDuration())));

        double factor = shift.getActualFactorInPercent().getFactorInPercent() /100.0;
        holder.etFactor.setText(String.valueOf(factor));
        holder.etBasePayment.setText(String.format("%s EUR", String.valueOf(shift.getBasePayment())));
        holder.etPayment.setText(String.format("%s EUR", String.valueOf(shift.calculatePayment())));
    }

    @Override
    public int getItemCount() {
        Log.d(TAG, "getItemCount: starts" );
        if(shifts == null){
            Log.d(TAG, "getItemCount: shifts == null");
        } else {
            Log.d(TAG, "getItemCount: shifts.size() = " + shifts.size());
        }
        return shifts == null ? 0 : shifts.size();
    }


    public static class InvoiceShiftsViewHolder extends RecyclerView.ViewHolder{
        CheckBox checkBox;
        EditText etDate, etDuration, etBasePayment, etFactor, etPayment;

        public InvoiceShiftsViewHolder(View itemView) {
            super(itemView);

            checkBox = itemView.findViewById(R.id.cb_item_shift);
            etDate = itemView.findViewById(R.id.et_item_date);
            etDuration = itemView.findViewById(R.id.et_item_duration);
            etBasePayment = itemView.findViewById(R.id.et_item_base_payment);
            etFactor = itemView.findViewById(R.id.et_item_factor);
            etPayment = itemView.findViewById(R.id.et_item_payment);
        }
    }
}

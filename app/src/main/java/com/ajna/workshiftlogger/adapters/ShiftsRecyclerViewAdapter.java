package com.ajna.workshiftlogger.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ajna.workshiftlogger.R;
import com.ajna.workshiftlogger.database.ClientsContract;
import com.ajna.workshiftlogger.database.ShiftsContract;
import com.ajna.workshiftlogger.model.Shift;

import java.util.Locale;


public class ShiftsRecyclerViewAdapter extends RecyclerView.Adapter<ShiftsRecyclerViewAdapter.ShiftsViewHolder> {
    private static final String TAG = "ShiftsRecyclerViewAdapt";

    public interface  OnShiftInteractionListener{
        void onShiftClicked(Shift shift);
    }
    private OnShiftInteractionListener listener;
    private Cursor cursor;
    private final java.text.DateFormat dateFormat; // module level so we don't keep instantiating in bindView.
    private final java.text.DateFormat timeFormat;

    public ShiftsRecyclerViewAdapter(Context context, Cursor cursor, OnShiftInteractionListener listener) {
        this.cursor = cursor;
        this.listener = listener;
        dateFormat = android.text.format.DateFormat.getDateFormat(context);
        timeFormat = android.text.format.DateFormat.getTimeFormat(context);
    }

    @NonNull
    @Override
    public ShiftsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_shift, parent, false);

        ShiftsViewHolder viewHolder = new ShiftsViewHolder(view, new ShiftsViewHolder.OnItemClickListener() {
            @Override
            public void onShiftClicked(int position) {
                cursor.moveToPosition(position);
                Shift shift = new Shift();
                shift.set_id(cursor.getLong(cursor.getColumnIndex(ShiftsContract.Columns._ID)));
                shift.setStartTime(cursor.getLong(cursor.getColumnIndex(ShiftsContract.Columns.START_TIME)));
                shift.setEndTime(cursor.getLong(cursor.getColumnIndex(ShiftsContract.Columns.END_TIME)));
                shift.setProjectName(cursor.getString(cursor.getColumnIndex(ShiftsContract.FullInfoColumns.PROJECT_NAME)));
                shift.setClientId(cursor.getLong(cursor.getColumnIndex(ShiftsContract.FullInfoColumns.CLIENT_ID)));
                shift.setClientName(cursor.getString(cursor.getColumnIndex(ShiftsContract.FullInfoColumns.CLIENT_NAME)));
                shift.setBasePayment(cursor.getInt(cursor.getColumnIndex(ClientsContract.Columns.BASE_PAYMENT)));
                shift.setPaymentType(cursor.getInt(cursor.getColumnIndex(ClientsContract.Columns.PAY_TYPE)));
                shift.setPause(cursor.getInt(cursor.getColumnIndex(ShiftsContract.Columns.PAUSE)));

                listener.onShiftClicked(shift);
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ShiftsViewHolder holder, int position) {
        if ((cursor == null) || (cursor.getCount() == 0)) {
            return;
        }
        if (!cursor.moveToPosition(position)) {
            throw new IllegalStateException("Couldn't move cursor to position " + position);
        }

        long startTimeDate = cursor.getLong(cursor.getColumnIndex(ShiftsContract.Columns.START_TIME));
        long endTimeDate = cursor.getLong(cursor.getColumnIndex(ShiftsContract.Columns.END_TIME));
        String clientName = cursor.getString(cursor.getColumnIndex(ShiftsContract.FullInfoColumns.CLIENT_NAME));
        String projectName = cursor.getString(cursor.getColumnIndex(ShiftsContract.FullInfoColumns.PROJECT_NAME));
        int basePayment = cursor.getInt(cursor.getColumnIndex(ClientsContract.Columns.BASE_PAYMENT));
        int payType = cursor.getInt(cursor.getColumnIndex(ClientsContract.Columns.PAY_TYPE));
        int pauseMins = cursor.getInt(cursor.getColumnIndex(ShiftsContract.Columns.PAUSE));


        Shift shift = new Shift();
        shift.setBasePayment(basePayment);
        shift.setStartTime(startTimeDate);
        shift.setEndTime(endTimeDate);
        shift.setPaymentType(payType);
        shift.setPause(pauseMins);

        long duration = shift.calculateDuration();
        long durationHours = duration / 3600000;
        long durationMins = (duration % 3600000) / 60000;
        holder.tvDate.setText(dateFormat.format(shift.getStartTime()));
        holder.tvTime.setText(String.format("%s - %s", timeFormat.format(shift.getStartTime()), timeFormat.format(shift.getEndTime())));
        holder.tvProjectName.setText(projectName);
        holder.tvClientName.setText(clientName);
        holder.tvDuration.setText(String.format("%.2sh %smin", String.valueOf(durationHours), String.valueOf(durationMins)));
        holder.tvPause.setText(String.format(Locale.US, "Pause: %dmin", pauseMins));

    }

    @Override
    public int getItemCount() {
        if(cursor != null){
            Log.d(TAG, "getItemCount: " + cursor.getCount());
        }
        return cursor == null ? 0 : cursor.getCount();
    }

    public Cursor swapCursor(Cursor newCursor) {
        if (newCursor == cursor) {
            return null;
        }

        int numItems = getItemCount(); // store old item count

        final Cursor oldCursor = cursor;
        cursor = newCursor;
        if(newCursor != null) {
            // notify the observers about the new cursor
            notifyDataSetChanged();
        } else {
            // notify the observers about the lack of a data set
            notifyItemRangeRemoved(0, numItems); // Use the old count
        }
        return oldCursor;

    }

    public static class ShiftsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvDate, tvTime, tvClientName, tvDuration, tvPayment, tvProjectName, tvPause;
        OnItemClickListener listener;

        public ShiftsViewHolder(View itemView, OnItemClickListener listener) {
            super(itemView);
            this.tvDate = itemView.findViewById(R.id.tv_report_date);
            this.tvTime = itemView.findViewById(R.id.tv_report_time);
            this.tvClientName = itemView.findViewById(R.id.tv_report_client);
            this.tvDuration = itemView.findViewById(R.id.tv_report_duration);
            this.tvPayment = itemView.findViewById(R.id.tv_report_payment);
            this.tvProjectName = itemView.findViewById(R.id.tv_report_projekt);
            this.tvPause = itemView.findViewById(R.id.tv_report_pause);
            this.listener = listener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            listener.onShiftClicked(getAdapterPosition());
        }

        public interface OnItemClickListener {
            void onShiftClicked(int position);
        }
    }
}

package com.ajna.workshiftlogger.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ajna.workshiftlogger.R;
import com.ajna.workshiftlogger.database.ShiftFullInfoViewContract;

public class ShiftsRecyclerViewAdapter extends RecyclerView.Adapter<ShiftsRecyclerViewAdapter.ShiftsViewHolder> {
    private static final String TAG = "ShiftsRecyclerViewAdapt";

    private Cursor cursor;
    private final java.text.DateFormat mDateFormat; // module level so we don't keep instantiating in bindView.

    public ShiftsRecyclerViewAdapter(Context context, Cursor cursor) {
        this.cursor = cursor;
        mDateFormat = android.text.format.DateFormat.getDateFormat(context);
    }

    @NonNull
    @Override
    public ShiftsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_shift, parent, false);
        return new ShiftsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShiftsViewHolder holder, int position) {
        if ((cursor == null) || (cursor.getCount() == 0)) {
            return;
        }
        if (!cursor.moveToPosition(position)) {
            throw new IllegalStateException("Couldn't move cursor to position " + position);
        }

        long startTimeDate = cursor.getLong(cursor.getColumnIndex(ShiftFullInfoViewContract.Columns.START_TIME));
        long endTimeDate = cursor.getLong(cursor.getColumnIndex(ShiftFullInfoViewContract.Columns.END_TIME));
        String clientName = cursor.getString(cursor.getColumnIndex(ShiftFullInfoViewContract.Columns.CLIENT_NAME));
        String projectName = cursor.getString(cursor.getColumnIndex(ShiftFullInfoViewContract.Columns.PROJECT_NAME));
        long basePayment = cursor.getLong(cursor.getColumnIndex(ShiftFullInfoViewContract.Columns.BASE_PAYMENT));

        holder.tvProjectName.setText(projectName);
        holder.tvClientName.setText(clientName);

    }

    @Override
    public int getItemCount() {
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

    public static class ShiftsViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate, tvTime, tvClientName, tvDuration, tvPayment, tvProjectName;

        public ShiftsViewHolder(View itemView) {
            super(itemView);
            this.tvDate = itemView.findViewById(R.id.tv_report_date);
            this.tvTime = itemView.findViewById(R.id.tv_report_time);
            this.tvClientName = itemView.findViewById(R.id.tv_report_client);
            this.tvDuration = itemView.findViewById(R.id.tv_report_duration);
            this.tvPayment = itemView.findViewById(R.id.tv_report_payment);
            this.tvProjectName = itemView.findViewById(R.id.tv_report_projekt);
        }
    }
}

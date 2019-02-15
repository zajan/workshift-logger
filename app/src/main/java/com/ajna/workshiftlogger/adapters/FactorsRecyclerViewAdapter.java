package com.ajna.workshiftlogger.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ajna.workshiftlogger.R;
import com.ajna.workshiftlogger.model.Factor;

import java.util.List;

public class FactorsRecyclerViewAdapter extends RecyclerView.Adapter<FactorsRecyclerViewAdapter.FactorsViewHolder> {

    public interface OnFactorClickListener{
        void onDeleteClick(Factor factor);
    }

    private List<Factor> factors;
    private OnFactorClickListener onFactorClickListener;

    public FactorsRecyclerViewAdapter(List<Factor> factors, OnFactorClickListener onFactorClickListener) {
        this.factors = factors;
        if(onFactorClickListener == null){
            throw new IllegalArgumentException("Must implement FactorsRecyclerViewAdapter.OnFactorClickListener interface.");
        }
        this.onFactorClickListener = onFactorClickListener;
    }

    @NonNull
    @Override
    public FactorsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_factor, parent, false);
        return new FactorsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final FactorsViewHolder holder, int position) {
        Factor factor = factors.get(position);
        holder.tvHours.setText(String.valueOf(factor.getHours()));
        holder.tvFactor.setText(String.valueOf(factor.getFactorInPercent()));
        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onFactorClickListener.onDeleteClick(factors.get(holder.getAdapterPosition()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return factors.size();
    }

    public static class FactorsViewHolder extends RecyclerView.ViewHolder {
        TextView tvHours, tvFactor;
        ImageView btnDelete;
        public FactorsViewHolder(View itemView) {
            super(itemView);
            tvHours = itemView.findViewById(R.id.tv_factor_hours);
            tvFactor = itemView.findViewById(R.id.tv_factor_percent);
            btnDelete = itemView.findViewById(R.id.btn_factor_delete);
        }
    }
}

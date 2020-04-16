package com.bcil.endlessservicejava;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MsgListAdapter extends RecyclerView.Adapter<MsgListAdapter.MyViewHolder> {
    private List<MsgInfo> loadingInLiftList;
    private Context context;
    private MsgListAdapter.OnItemClickListener onItemClickListener;
    private View itemView;

    public MsgListAdapter(Context context, List<MsgInfo> loadingInLiftList) {
        this.context = context;
        this.loadingInLiftList=loadingInLiftList;
    }

    @NonNull
    @Override
    public MsgListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.msg_list_item, parent, false);
        return new MsgListAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MsgListAdapter.MyViewHolder holder, int position) {
        MsgInfo palletMapping = loadingInLiftList.get(position);
        if(palletMapping.getData()!=null){
            holder.tvText.setText(palletMapping.getData());
        }else {
            holder.tvText.setText(Utils.EMPTY);
        }

        if(palletMapping.getTimeinfo()!=null){
            holder.tvCreateOn.setText(palletMapping.getTimeinfo());
        }else {
            holder.tvCreateOn.setText(Utils.EMPTY);
        }


        holder.itemView.setTag(palletMapping);
    }

    @Override
    public int getItemCount() {
        if(loadingInLiftList!=null&&loadingInLiftList.size()>0){
            return loadingInLiftList.size();
        }else {
            return 0;
        }
    }



    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView tvText,tvCreateOn;

        MyViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            tvText = (TextView) itemView.findViewById(R.id.tvText);
            tvCreateOn = (TextView) itemView.findViewById(R.id.tvCreateOn);
        }

        @Override
        public void onClick(View v) {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(v, (MsgInfo) v.getTag());
            }

        }
    }

    public interface OnItemClickListener {
        void onItemClick(View view, MsgInfo position);
    }



}




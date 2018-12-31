package com.oude.dndclub.adapter;

import com.oude.dndclub.R;
import com.oude.dndclub.bean.*;

import android.support.v7.widget.RecyclerView;

import java.util.List;

import android.view.View;
import android.widget.*;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.content.Context;

public class CommonListAdapter extends RecyclerView.Adapter<CommonListAdapter.ViewHolder> {
    private List<CommonList> mCommonList;
    private Context mContext;
    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {

        this.mOnItemClickListener = onItemClickListener;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        View CommonListView;
        ImageView CommonListImage;
        TextView CommonListName;

        public ViewHolder(View view) {
            super(view);
            CommonListView = view;
            CommonListImage = view.findViewById(R.id.list_image);
            CommonListName = view.findViewById(R.id.list_name);
        }
    }

    public CommonListAdapter(Context context, List<CommonList> otherList) {
        mCommonList = otherList;
        mContext = context;
    }

    //点击接口
    public interface OnItemClickListener {
        void onClick(int position);

        void onLongClick(int position);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_commonlist, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        CommonList otherList = mCommonList.get(position);
        holder.CommonListImage.setImageResource(otherList.getImageId());
        holder.CommonListName.setText(otherList.getName());
        if (mOnItemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onClick(position);
                }
            });
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    mOnItemClickListener.onLongClick(position);
                    return true;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mCommonList.size();
    }
}

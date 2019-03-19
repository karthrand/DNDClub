package com.oude.dndclub.adapter;
import android.support.v7.widget.*;
import java.util.*;
import com.oude.dndclub.bean.*;
import android.view.*;
import android.widget.*;
import com.oude.dndclub.*;
import android.content.*;

public class DBListAdapter extends RecyclerView.Adapter<DBListAdapter.ViewHolder>
{
	private List<ItemsList> mItemsList;
	private Context mContext;
    private OnItemClickListener mOnItemClickListener;

	public void setOnItemClickListener(OnItemClickListener onItemClickListener)
    {

        this.mOnItemClickListener = onItemClickListener;
    }

	static class ViewHolder extends RecyclerView.ViewHolder
	{
		View ItemsView;
		TextView itemsName;

		public ViewHolder(View view)
		{
			super(view);
			ItemsView = view;
			itemsName = view.findViewById(R.id.items_list_name);
		}
	}

	public DBListAdapter(Context context, List<ItemsList> itemsList)
	{
		mItemsList = itemsList;
		mContext = context;
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
	{
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.itemlist_shop, parent, false);
		final ViewHolder holder = new ViewHolder(view);
		return holder;
	}

	//点击接口
    public interface OnItemClickListener
	{
        void onClick(int position,View v);
        void onLongClick(int position,View v);
    }
	
	//两种点击接口
	@Override
	public void onBindViewHolder(ViewHolder holder, final int position)
	{
		ItemsList itemsList = mItemsList.get(position);
		holder.itemsName.setText(itemsList.getName());
        if (mOnItemClickListener != null)
		{
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v)
					{
                        mOnItemClickListener.onClick(position,v);
                    }
                });
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v)
					{
                        mOnItemClickListener.onLongClick(position,v);
                        return true;
                    }
                });
		}
	}

	@Override
	public int getItemCount()
	{
		return mItemsList.size();
	}
	//自定义删除操作
	public void removeItem(int pos)
	{
		mItemsList.remove(pos);
		notifyItemRemoved(pos);
	}      

}

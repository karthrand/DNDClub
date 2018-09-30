package com.oude.dndclub.ui.fragment;
import android.support.v4.app.*;
import android.view.*;
import android.os.*;
import android.support.v7.widget.*;
import java.util.*;
import com.oude.dndclub.*;
import com.oude.dndclub.adapter.*;
import com.oude.dndclub.bean.*;

public class ShopFragment extends Fragment
{
    private List<CommonList> list = new ArrayList<>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view=inflater.inflate(R.layout.fragment_shop, container, false);       
        return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
		initView();
	}

    //初始化布局
    private void initView()
    {
        initShoplist();
		RecyclerView recyclerView=getActivity().findViewById(R.id.shopRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        CommonListAdapter adapter =new CommonListAdapter(getActivity(), list);
        recyclerView.setAdapter(adapter);
		adapter.setOnItemClickListener(new ShopOnClickListener());
    }

	//RecyclerView显示内容
    private void initShoplist()
    {
        CommonList  list1 = new CommonList(getResources().getString(R.string.shop_recyclerview1), R.drawable.ic_launcher);
        list.add(list1);
        CommonList  list2 = new CommonList(getResources().getString(R.string.shop_recyclerview2), R.drawable.ic_launcher);
        list.add(list2);
        CommonList  list3 = new CommonList(getResources().getString(R.string.shop_recyclerview3), R.drawable.ic_launcher);
        list.add(list3);
        CommonList  list4 = new CommonList(getResources().getString(R.string.shop_recyclerview4), R.drawable.ic_launcher);
		list.add(list4);
        CommonList  list5 = new CommonList(getResources().getString(R.string.shop_recyclerview5), R.drawable.ic_launcher);
		list.add(list5);
    }

	//Recyclerview监听器功能实现
	class ShopOnClickListener implements CommonListAdapter.OnItemClickListener
	{

		@Override
		public void onClick(int position)
		{
			// TODO: Implement this method
		}

		@Override
		public void onLongClick(int position)
		{
			// TODO: Implement this method
		}
	}

}

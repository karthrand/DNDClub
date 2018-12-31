package com.oude.dndclub.ui.fragment;
import android.support.v4.app.*;
import android.view.*;
import android.os.*;
import android.support.v7.widget.*;
import java.util.*;
import com.oude.dndclub.*;
import com.oude.dndclub.adapter.*;
import com.oude.dndclub.bean.*;
import com.oude.dndclub.utils.RecycleItemDecoration;

public class ToolFragment extends Fragment
{
	private List<CommonList> list = new ArrayList<>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view=inflater.inflate(R.layout.fragment_tool, container, false);       
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
        initToollist();
		RecyclerView recyclerView=getActivity().findViewById(R.id.toolRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
		recyclerView.addItemDecoration(new RecycleItemDecoration(getActivity(), RecycleItemDecoration.VERTICAL_LIST));
        CommonListAdapter adapter =new CommonListAdapter(getActivity(), list);
        recyclerView.setAdapter(adapter);
		adapter.setOnItemClickListener(new ToolOnClickListener());
    }

	//RecyclerView显示内容
    private void initToollist()
    {
        CommonList  list1 = new CommonList(getResources().getString(R.string.tool_recyclerview1), R.drawable.ic_launcher);
        list.add(list1);
        CommonList  list2 = new CommonList(getResources().getString(R.string.tool_recyclerview2), R.drawable.ic_launcher);
        list.add(list2);
    }

	//Recyclerview监听器功能实现
	class ToolOnClickListener implements CommonListAdapter.OnItemClickListener
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

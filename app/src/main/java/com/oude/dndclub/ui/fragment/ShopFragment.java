package com.oude.dndclub.ui.fragment;
import android.support.v4.app.*;
import android.view.*;
import android.os.*;
import com.oude.dndclub.*;

public class ShopFragment extends Fragment
{
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view=inflater.inflate(R.layout.fragment_shop,container,false);       
        return view;
	}
}

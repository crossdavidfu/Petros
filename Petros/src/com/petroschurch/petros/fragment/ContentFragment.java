package com.petroschurch.petros.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.app.SherlockFragment;
import com.petroschurch.petros.R;

public class ContentFragment extends SherlockFragment
{
	@Override
    public void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {     
        View view = inflater.inflate(R.layout.frag_content,container,false);
        return view;
    }	
	
}

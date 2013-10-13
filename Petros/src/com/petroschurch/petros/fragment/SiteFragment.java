package com.petroschurch.petros.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import com.actionbarsherlock.app.SherlockFragment;
import com.petroschurch.petros.R;
import com.petroschurch.petros.lib.CommonPara;

public class SiteFragment extends SherlockFragment implements OnClickListener
{
    WebView web_forum;
    
    Button button_left;
    Button button_right;
    Button button_main;
    Button button_refresh;
        
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
    }
    
    @SuppressLint("SetJavaScriptEnabled")
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.frag_site, container, false);
         
        web_forum = (WebView)view.findViewById(R.id.web_frag_site_content);  
        WebSettings settings = web_forum.getSettings();  
        settings.setSupportZoom(true);          //支持缩放  
        settings.setBuiltInZoomControls(true);  //启用内置缩放装置  
        settings.setJavaScriptEnabled(true);    //启用JS脚本  
        
        button_left = (Button)view.findViewById(R.id.button_frag_site_left);
        button_right = (Button)view.findViewById(R.id.button_frag_site_right);
        button_main = (Button)view.findViewById(R.id.button_frag_site_main);
        button_refresh = (Button)view.findViewById(R.id.button_frag_site_refresh);
                
        button_left.setOnClickListener(this);
        button_right.setOnClickListener(this);
        button_main.setOnClickListener(this);
        button_refresh.setOnClickListener(this);
                
        web_forum.setWebViewClient(new WebViewClient() 
        {
            public boolean shouldOverrideUrlLoading(WebView view, String url)
            { 
                view.loadUrl(url);
                return true;
            }
        });        
        
        try 
        {  
            web_forum.loadUrl(CommonPara.FORUM_URL); 
        } 
        catch (Exception e) 
        {              
            e.printStackTrace();
        }  
        return view;
    }
    
    @Override
    public void onClick(View v) 
    {        
        switch (v.getId())
        {
            case R.id.button_frag_site_left:
                if (web_forum.canGoBack()) 
                { 
                    web_forum.goBack(); 
                } 
                break;
            case R.id.button_frag_site_right:
                if (web_forum.canGoForward()) 
                { 
                    web_forum.goForward(); 
                }         
                break;
            case R.id.button_frag_site_main:
                try 
                {  
                    web_forum.loadUrl(CommonPara.FORUM_URL); 
                } 
                catch (Exception e) 
                {              
                    e.printStackTrace();
                }  
                break;
            case R.id.button_frag_site_refresh:
                web_forum.reload(); 
                break;  
            
            default:
                break;
        }
    }      
}

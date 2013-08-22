package org.ambientdynamix.contextplugins.hue;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;


import org.ambientdynamix.api.contextplugin.ContextPluginRuntime;
import org.ambientdynamix.api.contextplugin.ContextPluginSettings;
import org.ambientdynamix.api.contextplugin.IContextPluginConfigurationViewFactory;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class HuePluginConfigurationActivity extends Activity implements IContextPluginConfigurationViewFactory
{

	LinearLayout rootLayout;
	private final static String TAG = "HUE PLUGIN";
	private Context ctx;
	Activity activity;
	ProgressBar connectbar;
	Button connectbutton;
	static List<HueBridge> bridges = new ArrayList<HueBridge>();
	
	@Override
	public void destroyView() throws Exception 
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public View initializeView(Context context, ContextPluginRuntime arg1, int arg2) throws Exception 
	{
		ctx=context;
		activity=this;
		// Discover our screen size for proper formatting 
		DisplayMetrics met = context.getResources().getDisplayMetrics();
		Log.d("HUE", "xyz b");
		// Access our Locale via the incoming context's resource configuration to determine language
		String language = context.getResources().getConfiguration().locale.getDisplayLanguage();
		
		//WifiManager wm = (WifiManager) getSystemService(ctx.WIFI_SERVICE);
		//WifiManager.MulticastLock multicastLock = wm.createMulticastLock("multicastLock");
		//multicastLock.setReferenceCounted(true);
		//multicastLock.acquire();
		
        TextView text = new TextView(ctx);
        text.setText("IP");
        //text.setText(HuePluginRuntime.hueID);
        final EditText ipfield = new EditText(ctx);
        connectbar = new ProgressBar(ctx, null, android.R.attr.progressBarStyleHorizontal);
        connectbar.setVisibility(View.GONE);
        connectbutton = new Button(ctx);
        connectbutton.setText("Connect To Hue Bridge xxx");
        connectbutton.setOnClickListener(new View.OnClickListener() 
        {
            public void onClick(View v)
            {
            	//new Countdown().execute();
	    		discoverAndAuthenticate();
            }
        });
        Log.d("HUE", "abc");
		rootLayout = new LinearLayout(context);
		rootLayout.setOrientation(LinearLayout.VERTICAL);
		
	     rootLayout.addView(text,  new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
	        		FrameLayout.LayoutParams.WRAP_CONTENT));
	     
	     rootLayout.addView(connectbutton,  new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
	        		FrameLayout.LayoutParams.WRAP_CONTENT));
	     
	     rootLayout.addView(connectbar,  new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
	        		FrameLayout.LayoutParams.WRAP_CONTENT));
	 
		return rootLayout;
	}

	 public static void discoverAndAuthenticate() 
	 {
		 	new Thread(new Runnable()
		 	{
		 		public void run()
		 		{
		 			bridges = HueBridge.discover();
				    for(HueBridge bridge : bridges) 
				    {
				    	Log.d("HUE", "Found " + bridge);
				        // You may need a better scheme to store your username that to just hardcode it.
				        // suggestion: Save a mapping from HueBridge.getUDN() to HueBridge.getUsername() somewhere.
				        bridge.setUsername("552627b33010930f275b72ab1c7be258");
				        if(!bridge.authenticate(false)) 
				        {
				        	Log.d("HUE", "Press the button on your Hue bridge in the next 30 seconds to grant access.");
				            if(bridge.authenticate(true)) 
				            {
				            	Log.d("HUE", "Access granted. username: " + bridge.getUsername());
				    			Collection<HueLightBulb> lights = (Collection<HueLightBulb>) bridge.getLights();
				    			Log.d("HUE", "Available LightBulbs: "+lights.size());
				    			for (HueLightBulb bulb : lights) {
				    				Log.d("HUE", bulb.toString());
				    				bulb.setBrightness(255);
				    				bulb.setHue(0);
				    			}
				    			System.out.println("");
				            } 
				            else 
				            {
				            	Log.d("HUE", "Authentication failed.");
				            }
				        } 
				        else 
				        {
				        	Log.d("HUE", "Already granted access. username: " + bridge.getUsername());
			    			Collection<HueLightBulb> lights = (Collection<HueLightBulb>) bridge.getLights();
			    			Log.d("HUE", "Available LightBulbs: "+lights.size());
			    			for (HueLightBulb bulb : lights) {
			    				Log.d("HUE", bulb.toString());
			    				bulb.setBrightness(ColorHelper.convertRGB2Hue("255255255").get("bri"));
			    				bulb.setHue(ColorHelper.convertRGB2Hue("255255255").get("hue"));
			    				bulb.setSaturation(ColorHelper.convertRGB2Hue("255255255").get("sat"));
			    			}
			    			System.out.println("");
				        }
				    }
		 		}
		 	}).start();	    
	}
}

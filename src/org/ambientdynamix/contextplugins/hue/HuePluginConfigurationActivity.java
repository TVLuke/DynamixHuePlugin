package org.ambientdynamix.contextplugins.hue;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;


import org.ambientdynamix.api.contextplugin.ContextPluginRuntime;
import org.ambientdynamix.api.contextplugin.ContextPluginSettings;
import org.ambientdynamix.api.contextplugin.IContextPluginConfigurationViewFactory;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
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
	private final static String TAG = "HUE";
	private Context ctx;
	Activity activity;
	static TextView text;
	LinearLayout listLayout;
	ProgressBar connectbar;
	Button connectbutton;
	
	@Override
	public void destroyView() throws Exception 
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public View initializeView(Context context, final ContextPluginRuntime arg1, int arg2) throws Exception 
	{
		ctx=context;
		activity=this;
		// Discover our screen size for proper formatting 
		DisplayMetrics met = context.getResources().getDisplayMetrics();
		Log.d("HUE", "xyz d");
		// Access our Locale via the incoming context's resource configuration to determine language
		String language = context.getResources().getConfiguration().locale.getDisplayLanguage();
		
		//WifiManager wm = (WifiManager) getSystemService(ctx.WIFI_SERVICE);
		//WifiManager.MulticastLock multicastLock = wm.createMulticastLock("multicastLock");
		//multicastLock.setReferenceCounted(true);
		//multicastLock.acquire();
		
        text = new TextView(ctx);
        text.setText("");
        //text.setText(HuePluginRuntime.hueID);
        final EditText ipfield = new EditText(ctx);
        connectbar = new ProgressBar(ctx, null, android.R.attr.progressBarStyleHorizontal);
        connectbar.setVisibility(View.GONE);
        connectbutton = new Button(ctx);
        connectbutton.setText("Search Hue Bridge");
        connectbutton.setOnClickListener(new View.OnClickListener() 
        {
            public void onClick(View v)
            {
            	new Countdown().execute();
	    		discoverAndAuthenticate(arg1);
            }
        });
        Log.d("HUE", "abc");
		
        listLayout = new LinearLayout(context);
		listLayout.setOrientation(LinearLayout.VERTICAL);
		
	    updateListView();
	    
		rootLayout = new LinearLayout(context);
		rootLayout.setOrientation(LinearLayout.VERTICAL);
		
	     rootLayout.addView(connectbutton,  new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
	        		FrameLayout.LayoutParams.WRAP_CONTENT));
	     
	     rootLayout.addView(text,  new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
	        		FrameLayout.LayoutParams.WRAP_CONTENT));
	     
	     rootLayout.addView(connectbar,  new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
	        		FrameLayout.LayoutParams.WRAP_CONTENT));
	     
	     rootLayout.addView(listLayout, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
	        		FrameLayout.LayoutParams.WRAP_CONTENT));

		return rootLayout;
	}

	private void updateListView()
	{
		Log.d("HUE", "update List View");
		listLayout.removeAllViews();
		List<HueBridge> bridges = HuePluginRuntime.getBridges();
		for(int i=0; i<bridges.size(); i++)
		{
			Log.d("HUE", "b");
			TextView tv = new TextView(ctx);
			if(bridges.get(i).authenticated)
			{
				tv.setText(bridges.get(i).getName());
			}
			else
			{
				tv.setText("unknown");
			}
			tv.setBackgroundColor(0xff888888);
			tv.setTextSize(35);
			
			TextView tv2 = new TextView(ctx);
			if(bridges.get(i).authenticated)
			{
				tv2.setText(""+bridges.get(i).getBaseUrl());
			}
			else
			{
				tv2.setText("unknown");
			}
			tv2.setBackgroundColor(0xff888888);
			Log.d("HUE", "c");
			listLayout.addView(tv, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
	        		FrameLayout.LayoutParams.WRAP_CONTENT));
			listLayout.addView(tv2, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
        		FrameLayout.LayoutParams.WRAP_CONTENT));
			Log.d("HUE", "d");
			if(bridges.get(i).authenticated)
			{
				Collection<HueLightBulb> lights = (Collection<HueLightBulb>) bridges.get(i).getLights();
				Iterator<HueLightBulb> it = lights.iterator();
				int counter=0;
				Log.d("HUE", "e");
				while(it.hasNext())
				{
					counter++;
					final HueLightBulb light = it.next();
					Log.d("HUE", "f");
					TextView tv3 = new TextView(ctx);
					tv3.setTextSize(30);
					String x = ""+light.getHue();
					if(counter%2==0)
					{
						tv3.setBackgroundColor(0xffcccccc);
					}
					else
					{
						tv3.setBackgroundColor(0xff888888);
					}
					tv3.setText("  "+light.getName());
					
					tv3.setOnClickListener(new View.OnClickListener() 
					{
					    public void onClick(View v) 
					    {
					    	new Thread(new Runnable()
						 	{
						 		public void run()
						 		{
						 			Integer oldhue = light.getHue();
						 			light.setHue(0);
						 			try 
						 			{
										Thread.sleep(500);
									} 
						 			catch (InterruptedException e) 
						 			{
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
						 			light.setHue(oldhue);
						 		}
						 	}).start();
					    }
					});
					listLayout.addView(tv3, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
			        		FrameLayout.LayoutParams.WRAP_CONTENT));
				}
			}
		}
	}
	 public static void discoverAndAuthenticate(final ContextPluginRuntime xyz) 
	 {
		 	new Thread(new Runnable()
		 	{
		 		public void run()
		 		{
		 			HuePluginRuntime.updateBridges();
		 			List<HueBridge> bridges = HuePluginRuntime.getBridges();
				    for(HueBridge bridge : bridges) 
				    {
				    	Log.d("HUE", "Found " + bridge);
				        // You may need a better scheme to store your username that to just hardcode it.
				        // suggestion: Save a mapping from HueBridge.getUDN() to HueBridge.getUsername() somewhere.
				    	Log.d("HUE", HuePluginRuntime.hueID);
				        bridge.setUsername(HuePluginRuntime.hueID);
				        if(!bridge.authenticate(false)) 
				        {
				        	Log.d("HUE", "Press the button on your Hue bridge in the next 30 seconds to grant access.");
				            if(bridge.authenticate(true)) 
				            {
				            	Log.d("HUE", "Access granted. username: " + bridge.getUsername());
				    			Collection<HueLightBulb> lights = (Collection<HueLightBulb>) bridge.getLights();
				    			Log.d("HUE", "Available LightBulbs: "+lights.size());
				    			for (final HueLightBulb bulb : lights) 
				    			{
				    				new Thread(new Runnable()
				    			 	{
				    			 		public void run()
				    			 		{
						    				Log.d("HUE", bulb.toString());
						    				Integer bri = bulb.brightness;
						    				Integer hu = bulb.hue;
						    				Integer sa = bulb.saturation;
						    				double cix = bulb.ciex;
						    				double ciy = bulb.ciey;
						    				int ct = bulb.colorTemperature;
						    				bulb.setOn(false);
						    				try 
						    				{
												Thread.sleep(250);
											} 
						    				catch (InterruptedException e) 
						    				{
												// TODO Auto-generated catch block
												e.printStackTrace();
											}
						    				bulb.setOn(true);
						    				bulb.setBrightness(ColorHelper.convertRGB2Hue("255255255").get("bri"));
						    				bulb.setHue(ColorHelper.convertRGB2Hue("255255255").get("hue"));
						    				bulb.setSaturation(ColorHelper.convertRGB2Hue("255255255").get("sat"));
						    				try 
						    				{
												Thread.sleep(500);
											} 
						    				catch (InterruptedException e) 
						    				{
												// TODO Auto-generated catch block
												e.printStackTrace();
											}
						    				bulb.setOn(false);
						    				try 
						    				{
												Thread.sleep(250);
											} 
						    				catch (InterruptedException e) 
						    				{
												// TODO Auto-generated catch block
												e.printStackTrace();
											}
						    				bulb.setOn(true);
						    				bulb.setBrightness(bri);
						    				bulb.setHue(hu);
						    				bulb.setSaturation(sa);		 
						    				bulb.setCieXY(cix, ciy);
						    				bulb.setColorTemperature(ct);
						    			}
				    			 	}).start();
				    			}
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
			    			for (final HueLightBulb bulb : lights) 
			    			{
			    				new Thread(new Runnable()
			    			 	{
			    			 		public void run()
			    			 		{
					    				Log.d("HUE", bulb.toString());
					    				Integer bri = bulb.brightness;
					    				Integer hu = bulb.hue;
					    				Integer sa = bulb.saturation;
					    				double cix = bulb.ciex;
					    				double ciy = bulb.ciey;
					    				int ct = bulb.colorTemperature;
					    				bulb.setOn(false);
					    				try 
					    				{
											Thread.sleep(250);
										} 
					    				catch (InterruptedException e) 
					    				{
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
					    				bulb.setOn(true);
					    				bulb.setBrightness(ColorHelper.convertRGB2Hue("255255255").get("bri"));
					    				bulb.setHue(ColorHelper.convertRGB2Hue("255255255").get("hue"));
					    				bulb.setSaturation(ColorHelper.convertRGB2Hue("255255255").get("sat"));
					    				try 
					    				{
											Thread.sleep(500);
										} 
					    				catch (InterruptedException e) 
					    				{
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
					    				bulb.setOn(false);
					    				try 
					    				{
											Thread.sleep(250);
										} 
					    				catch (InterruptedException e) 
					    				{
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
					    				bulb.setOn(true);
					    				bulb.setBrightness(bri);
					    				bulb.setHue(hu);
					    				bulb.setSaturation(sa);		 
					    				bulb.setCieXY(cix, ciy);
					    				bulb.setColorTemperature(ct);
					    			}
			    			 	}).start();
			    			}
				        }
				    }
		 		}
		 	}).start();	    
	}
	 
	 /**
	  * @author lukas
	  *
	  */
	private class Countdown extends AsyncTask<Integer, Integer, Long> 
	{
		protected Long doInBackground(Integer... urls) 
		{
		    	Log.d("HUE", "doInBackground");
		    	for(int i=0; i<30; i++)
		    	{
		    		try 
					{
		    			Log.d("HUE", "sleep");
						Thread.sleep(1000);
					} 
					catch (InterruptedException e) 
					{
							
							e.printStackTrace();
					}
		    		Log.d("HUE", "pp");
					publishProgress(i);
			    }
				return 0l;
			  }

			  protected void onProgressUpdate(Integer... progress) 
			  {
			    	 if(progress[0]==0)
			    	 {
				    	 Log.d("HUE", "set visble");
			 	    	 connectbar.setVisibility(View.VISIBLE);
			    		 Log.d("HUE", "p=0 start disc and auth");
			    	 }
			        text.setText("Press the button on your Hue bridge in the next 30 seconds to grant access.");
			    	 connectbutton.setClickable(false);
			    	  Log.d("HUE", "int p");
			    	  double px = progress[0];
			    	 int p = (int) (px/(0.3));
			    	 if(progress[0]==29)
			    	 {
			    		 p=100;
			    	 }
			    	  Log.d("HUE", "p="+p);
			    	 connectbar.setProgress(p);
			    	  Log.d("HUE", "done");
			  	}

			     protected void onPostExecute(Long result) 
			     {
			    	 Log.d("HUE", "on post execute");
			    	 connectbar.setVisibility(View.GONE);		    	
			    	 connectbutton.setClickable(true);
			    	 Log.d("Hue", "on Post Execute 2");
			 	    updateListView();
			     }
			 }
}

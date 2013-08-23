package org.ambientdynamix.contextplugins.hue;


import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.ambientdynamix.api.contextplugin.*;

import android.os.Bundle;
import android.util.Log;

public class HuePluginRuntime extends AutoReactiveContextPluginRuntime
{
	private final static String TAG = "HUE";
	public static ContextPluginSettings settings;
	public static String hueID="";
	static List<HueBridge> bridges = new ArrayList<HueBridge>();
	HuePluginRuntime context;
	 private static SecureRandom random = new SecureRandom();
	
	@Override
	public void start() 
	{
		/*
		 * Nothing to do, since this is a pull plug-in... we're now waiting for context scan requests.
		 */
		Log.i(TAG, "Started!");
	}

	@Override
	public void stop() 
	{
		/*
		 * At this point, the plug-in should cancel any ongoing context scans, if there are any.
		 */
		Log.i(TAG, "Stopped!");
	}

	@Override
	public void destroy() 
	{
		/*
		 * At this point, the plug-in should release any resources.
		 */
		stop();
		Log.i(TAG, "Destroyed!");
	}

	@Override
	public void updateSettings(ContextPluginSettings settings) 
	{
		// Not supported
	}

	@Override
	public void handleContextRequest(UUID requestId, String contextInfoType) 
	{
		context=this;
	}

	@Override
	public void handleConfiguredContextRequest(UUID requestId, String contextInfoType, Bundle scanConfig) 
	{
		Log.d(TAG, "configured context request from Hue Plugin");
		if(contextInfoType.equals("org.ambientdynamix.contextplugins.context.action.environment.light") || contextInfoType.equals("org.ambientdynamix.contextplugins.artnet"))
		{
			if(scanConfig.containsKey("action_type"))
			{
				Log.d("HUE", "contains actionType");
				String action_type = scanConfig.getString("action_type");
				if(action_type.equals("setcolor"))
				{
					Log.d("HUE", "action_type=setcolor");
					String r = scanConfig.getString("r_channel");
					
					String g = scanConfig.getString("g_channel");
					
					String b = scanConfig.getString("b_channel");
					
					Log.d("HUE", r+" "+g+" "+b);
					for(HueBridge bridge: bridges)
					{
						Log.d("HUE", "go through bridges");
						if(bridge.isAuthenticated())
						{
							if(scanConfig.containsKey("device_id"))
							{
								Log.d("HUE", "device Id found");
				    			Collection<HueLightBulb> lights = (Collection<HueLightBulb>) bridge.getLights();
				    			for (final HueLightBulb bulb : lights) 
				    			{
				    				if(bulb.getId().equals(scanConfig.getString("Device_ID")))
				    				{
										bulb.setOn(true);
										setHueColor(bulb, Double.parseDouble(r),Double.parseDouble(g),Double.parseDouble(b));
				    				}
				    			}
							}
							else
							{
								Log.d("HUE", "no device id");
				    			Collection<HueLightBulb> lights = (Collection<HueLightBulb>) bridge.getLights();
				    			for (final HueLightBulb bulb : lights) 
				    			{
									Log.d("HUE", "bulb"+bulb.id );
									bulb.setOn(true);
				    				setHueColor(bulb, Double.parseDouble(r),Double.parseDouble(g),Double.parseDouble(b));
				    			}
							}
						}
					}

				}
			}
			
		}
		if(contextInfoType.equals("org.ambientdynamix.contextplugins.context.info.environment.lightsources"))
		{
			
		}
		if(contextInfoType.equals("org.ambientdynamix.contextplugins.context.action.device.identification"))
		{
			
		}
		if(contextInfoType.equals("org.ambientdynamix.contextplugins.context.info.device.information"))
		{
			
		}
		context=this;
	}

	@Override
	public void init(PowerScheme arg0, ContextPluginSettings arg1) throws Exception 
	{
		Log.d(TAG, "init");
		if(arg1!=null)
		{
			Log.d(TAG, "settings are not null and now get stored as a static variable");
			settings=  arg1;
			Log.d(TAG, "they are also stored via dynamix");
			getPluginFacade().storeContextPluginSettings(getSessionId(), settings);
		}
		else
		{
			Log.d(TAG, "settings given to this method are null");
			settings =  getPluginFacade().getContextPluginSettings(getSessionId());
			if(settings!=null)
			{
				Log.d(TAG, "ok that worked");
			}
			else
			{
				ContextPluginSettings s = new ContextPluginSettings();
				getPluginFacade().storeContextPluginSettings(getSessionId(), s);
				settings = getPluginFacade().getContextPluginSettings(getSessionId());
				if(settings!=null)
				{
					Log.d(TAG, "ok, third one is the charm I guess...");
				}
				else
				{
					Log.d(TAG, "the settings are still null");
				}
			}
		}
		String u = settings.get("HueApplicationID");
		if(u!=null)
    	{
			hueID=u;
    	}
		else
		{
			//TODO: create ID and save into settings
			String appID = randomAppId();
			hueID=appID;
			settings.put("HueApplicationID", appID);
			getPluginFacade().storeContextPluginSettings(getSessionId(), settings);
		}
		context=this;	
		HuePluginConfigurationActivity.discoverAndAuthenticate(this);
    	getPluginFacade().setPluginConfiguredStatus(getSessionId(), true);
	}

	@Override
	public void setPowerScheme(PowerScheme arg0) throws Exception 
	{

		
	}
  
	public static String randomAppId()
	{
		Log.d(TAG, "...");
	    String x = new BigInteger(250, random).toString(32);
	    Log.d(TAG, x);
	    x = x.substring(0, 30);
	    Log.d(TAG, x);
	    return x;
	}
	  
	  @Override
	  public void doManualContextScan() 
	  {
		  
	  }

	public static void updateBridges() 
	{
		if(bridges!=null)
		{
			bridges = HueBridge.discover();		
		}
	}

	public static List<HueBridge> getBridges() 
	{
		return bridges;
	}
	
	public static void identifiy(final HueLightBulb bulb)
	{
		new Thread(new Runnable()
	 	{
	 		public void run()
	 		{
	 			try
	 			{
    				Log.d("HUE", bulb.toString());
    				boolean originalyon=false;
    				if(bulb.on)
    				{
    					originalyon=true;
    				}
    				Integer bri = null;
    				Integer hu = null;
    				Integer sa = null;
    				double cix = 0;
    				double ciy = 0;
    				int ct = 0;
    				if(originalyon)
    				{
	    				bri = bulb.brightness;
	    				hu = bulb.hue;
	    				sa = bulb.saturation;
	    				cix = bulb.ciex;
	    				ciy = bulb.ciey;
	    				ct = bulb.colorTemperature;
	    				bulb.setOn(false);
    				}
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
    				if(originalyon)
    				{
	    				bulb.setOn(true);
	    				bulb.setBrightness(bri);
	    				bulb.setHue(hu);
	    				bulb.setSaturation(sa);		 
	    				bulb.setCieXY(cix, ciy);
	    				bulb.setColorTemperature(ct);
    				}
	 			}
	 			catch(Exception e)
	 			{
	 				Log.e("HUE", "error while setting lights 2");
	 			}
			}
	 	}).start();
	}
	
	public static void setHueColor(HueLightBulb bulb, double r, double g, double b)
	{
		if(r>255)
		{
			r=255;
		}
		if(g>255)
		{
			g=255;
		}
		if(b>255)
		{
			b=255;
		}
		if(r<0)
		{
			r=0;
		}
		if(g<0)
		{
			g=0;
		}
		if(b<0)
		{
			b=0;
		}
		Log.e("HUE", r+" "+g+" "+b);		
		double x = 1.076450 * r - 0.237662 * g + 0.161212 * b;
		double y = 0.410964 * r + 0.554342 * g + 0.034694 * b;
		double z = -0.010954 * r - 0.013389 * g + 1.024343 * b;
		
		double cpx = x = x / (x + y + z);
		double cpy= y / (x + y + z);
		if(cpx>1)
		{
			cpx=1;
		}
		if(cpx<0)
		{
			cpx=0;
		}
		if(cpy>1)
		{
			cpy=1;
		}
		if(cpy>0)
		{
			cpy=0;
		}
		bulb.setCieXY(cpx, cpy);
		//bulb.setCiez(cpy);
	}
}
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
			if(scanConfig.containsKey("Action_Type"))
			{
				String action_type = scanConfig.getString("Action_Type");
				if(action_type.equals("setcolor"))
				{
					String r = scanConfig.getString("R_Channel");
					String g = scanConfig.getString("G_Channel");
					String b = scanConfig.getString("B_Channel");
					for(HueBridge bridge: bridges)
					{
						if(bridge.isAuthenticated())
						{
							if(scanConfig.containsKey("Device_ID"))
							{
				    			Collection<HueLightBulb> lights = (Collection<HueLightBulb>) bridge.getLights();
				    			for (final HueLightBulb bulb : lights) 
				    			{
				    				if(bulb.getId().equals(scanConfig.getString("Device_ID")))
				    				{
				    					bulb.setHue(ColorHelper.convertRGB2Hue(r+g+b).get("hue"));
				    				}
				    			}
							}
							else
							{
				    			Collection<HueLightBulb> lights = (Collection<HueLightBulb>) bridge.getLights();
				    			for (final HueLightBulb bulb : lights) 
				    			{
				    					bulb.setHue(ColorHelper.convertRGB2Hue(r+g+b).get("hue"));
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
		bridges = HueBridge.discover();		
	}

	public static List<HueBridge> getBridges() 
	{
		return bridges;
	}
}
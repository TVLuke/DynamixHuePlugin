package org.ambientdynamix.contextplugins.hue;


import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.UUID;

import org.ambientdynamix.api.contextplugin.*;

import android.os.Bundle;
import android.util.Log;

public class HuePluginRuntime extends AutoReactiveContextPluginRuntime
{
	private final static String TAG = "CURRENTACTIVITY";
	public static ContextPluginSettings settings;
	public static String hueID="";
	public static HueBridge bridge;
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
		Log.d(TAG, "configured context request");
		if(contextInfoType.equals("org.ambientdynamix.contextplugins.context.action.environment.light") || contextInfoType.equals("org.ambientdynamix.contextplugins.artnet"))
		{
			if(scanConfig.containsKey("Action_Type"))
			{
				String action_type = scanConfig.getString("Action_Type");
				if(action_type.equals("setcolor"))
				{
					
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
			//settings.put("HueApplicationID", appID);
		}
		context=this;		
	}

	@Override
	public void setPowerScheme(PowerScheme arg0) throws Exception 
	{

		
	}

	  public static void setBridge(HueBridge thebridge)
	  {
		  bridge = thebridge;
	  }
	  
	  public static String randomAppId()
	  {
	    return new BigInteger(250, random).toString(32);
	  }
	  
	  @Override
	  public void doManualContextScan() 
	  {
		  
	  }
}
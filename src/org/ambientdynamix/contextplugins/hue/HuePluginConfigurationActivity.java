package org.ambientdynamix.contextplugins.hue;

import java.net.InetAddress;
import java.util.StringTokenizer;

import de.jaetzold.philips.hue.HueBridge;
import de.jaetzold.philips.hue.HueLightBulb;

import org.ambientdynamix.api.contextplugin.ContextPluginRuntime;
import org.ambientdynamix.api.contextplugin.ContextPluginSettings;
import org.ambientdynamix.api.contextplugin.IContextPluginConfigurationViewFactory;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class HuePluginConfigurationActivity extends Activity implements IContextPluginConfigurationViewFactory
{

	LinearLayout rootLayout;
	private final static String TAG = "HUE PLUGIN";
	private Context ctx;
	Activity activity;
	
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
		
		// Access our Locale via the incoming context's resource configuration to determine language
		String language = context.getResources().getConfiguration().locale.getDisplayLanguage();
		
        TextView text = new TextView(ctx);
        text.setText("IP");
        text.setText(HuePluginRuntime.hueID);
        final EditText ipfield = new EditText(ctx);
        ProgressBar connectbar = new ProgressBar(ctx);
        Button connectbutton = new Button(ctx);
        connectbutton.setText("Connect To Hue Bridge");
        connectbutton.setOnClickListener(new View.OnClickListener() 
        {
            public void onClick(View v)
            {
            	String ip = ipfield.getText().toString();
            	int a=0;
            	int b=0;
            	int c=0;
            	int d=0;
            	StringTokenizer tk = new StringTokenizer(ip, ".");
            	try
            	{
	            	a = Integer.getInteger(tk.nextToken());
	            	b= Integer.getInteger(tk.nextToken());
	            	c= Integer.getInteger(tk.nextToken());;
	            	d = Integer.getInteger(tk.nextToken());
	            	InetAddress ip_of_hue = InetAddress.getByAddress(new byte[] {(byte)a, (byte)b, (byte)c, (byte)d});
	            	HueBridge bridge = new HueBridge(ip_of_hue, HuePluginRuntime.hueID);
	            	//TODO here needs to be a an asychronyou thing for the Progress Bar
	            	bridge.authenticate(true);
	            	String authenticate = bridge.getUsername();
	            	Log.d(TAG, authenticate);
	            	if(!authenticate.equals(""))
	            	{
	            		//TODO this probably means we are connected or something
	            		HuePluginRuntime.setBridge(bridge);
	            	}
	            }
            	catch(Exception e)
            	{
            		Log.e(TAG, "not a valid IP address");
            	}
            }
        });
     
		rootLayout = new LinearLayout(context);
		rootLayout.setOrientation(LinearLayout.VERTICAL);
		return rootLayout;
	}

}

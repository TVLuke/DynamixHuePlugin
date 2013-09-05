/*
 * Copyright (C) Institute of Telematics, Lukas Ruge
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.ambientdynamix.contextplugins.hue;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.ambientdynamix.api.application.IContextInfo;

import android.app.ActivityManager.RunningAppProcessInfo;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class AmbientLightContextAction implements IContextInfo
{

	private final String TAG = "SCREENSTATUS";
	
	public static Parcelable.Creator<AmbientLightContextAction> CREATOR = new Parcelable.Creator<AmbientLightContextAction>() 
			{
			public AmbientLightContextAction createFromParcel(Parcel in) 
			{
				return new AmbientLightContextAction(in);
			}

			public AmbientLightContextAction[] newArray(int size) 
			{
				return new AmbientLightContextAction[size];
			}
		};
		
	AmbientLightContextAction()
	{
		Log.d(TAG, "create new Context Info Object");

	}
	
	public AmbientLightContextAction(Parcel in) 
	{
		//in.readList(frontactivitys, getClass().getClassLoader());
	}

	@Override
	public String toString() 
	{
		return this.getClass().getSimpleName();
	}
	
	@Override
	public int describeContents() 
	{
		return 0;
	}

	@Override
	public void writeToParcel(Parcel out, int flags) 
	{
		//out.writeList(frontactivitys);
	}

	@Override
	public String getContextType() 
	{
		return "org.ambientdynamix.contextplugins.context.action.environment.light";
	}

	@Override
	public String getImplementingClassname() 
	{
		return this.getClass().getName();
	}

	@Override
	public String getStringRepresentation(String format) 
	{
		String result="";
		if (format.equalsIgnoreCase("text/plain"))
		{
			
		}
		else if (format.equalsIgnoreCase("XML"))
		{
			
		}
		else if (format.equalsIgnoreCase("JSON"))
		{
			
		}
		return result;
	}

	@Override
	public Set<String> getStringRepresentationFormats() 
	{
		Set<String> formats = new HashSet<String>();
		formats.add("text/plain");
		formats.add("XML");
		formats.add("JSON");
		return formats;
	}
}
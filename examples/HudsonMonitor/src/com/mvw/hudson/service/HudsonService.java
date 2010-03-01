package com.mvw.hudson.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.util.Log;

import com.mvw.hudson.R;
import com.mvw.hudson.activity.HudsonMonitorActivity;
import com.mvw.hudson.aidl.IHudsonAidlService;
import com.mvw.hudson.aidl.IRemoteServiceCallback;
import com.mvw.hudson.model.JobSummary;
import com.mvw.hudson.parser.HudsonJsonParser;
import com.mvw.service.BaseTimerService;

public class HudsonService extends BaseTimerService implements IHudsonService {

    private static String JOBS_URL = "/api/json";
    private static String AUTH_URL = "/j_acegi_security_check";
    private String baseurl;
    private String username;
    private String password;
    private Date lastUpdated;
	private HttpClient httpClient;
	private List<JobSummary> jobSummaryList = new ArrayList<JobSummary>();
	private RemoteCallbackList<IRemoteServiceCallback> mCallbacks = new RemoteCallbackList<IRemoteServiceCallback>();
	private boolean connected = false;
	private Object lock = new Object();
	
	/**
     * The IRemoteInterface is defined through IDL
     */
    private final IHudsonAidlService.Stub mBinder = new IHudsonAidlService.Stub() {

		@Override
		public List<JobSummary> getJobSummaryList() throws RemoteException {
			synchronized (lock) {				
				return jobSummaryList;
			}
		}   
		public void registerCallback(IRemoteServiceCallback cb) {
            if (cb != null) mCallbacks.register(cb);
        }
        public void unregisterCallback(IRemoteServiceCallback cb) {
            if (cb != null) mCallbacks.unregister(cb);
        }
		@Override
		public String getLastUpdated() throws RemoteException {
			synchronized (lock) {				
				if (lastUpdated == null) {
					return "never";
				} else {
					DateFormat df =  DateFormat.getDateTimeInstance();
					return df.format(lastUpdated);
				}
			}
		}
		@Override
		public boolean getConnected() throws RemoteException {
			synchronized (lock) {				
				return connected;
			}
		}

    };

	public HudsonService() {
		super("HudsonService");
		httpClient = new DefaultHttpClient();
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (httpClient != null && httpClient.getConnectionManager() != null) {	
			httpClient.getConnectionManager().shutdown();
		}
	}
	

	protected void runUpdate() {
		Log.i(getClass().getSimpleName(), "background task - start");
		  if (!loadPrefs()) {
			  Log.i(getClass().getSimpleName(), "background task - no baseURL skipping - end");
			  synchronized (lock) {
					connected = false;
	    		}
	    		notifyCallbacks();
			  return;
		  }

		  Hashtable<String, String> map = new Hashtable<String, String>();
		  map.put("key", "value");

		  try {

		    ByteArrayOutputStream baos = new ByteArrayOutputStream();
		    ObjectOutputStream oos = new ObjectOutputStream(baos);
		    oos.writeObject(map);
		    
		    HttpGet httpGet = new HttpGet(baseurl+JOBS_URL);
		    //HttpHost host = new HttpHost(hostname, port);
		    HttpResponse response = httpClient.execute(httpGet);
		    
		    if (response.getStatusLine().getStatusCode() == 403) {
		    	
		    	if (username == null || password == null) {
		    		Log.d(getClass().getSimpleName(), "This hudson server appears to require credentials, configure username and password");
		    		synchronized (lock) {
						connected = false;
		    		}
		    		notifyCallbacks();
		    		return;
		    	}
		    	
		    	HttpPost authPost = new HttpPost(baseurl+AUTH_URL);
		    	List<NameValuePair> formparams = new ArrayList<NameValuePair>();
		    	formparams.add(new BasicNameValuePair("j_username",username));
		    	formparams.add(new BasicNameValuePair("j_password",password));
		    	UrlEncodedFormEntity aentity = new UrlEncodedFormEntity(formparams,"UTF-8");
		    	authPost.setEntity(aentity);
		    	// this should set the cookie if successful
		    	HttpResponse authResponse = httpClient.execute(authPost);
		    	authResponse.getEntity().consumeContent();
		    	Log.d(getClass().getSimpleName(), authResponse.toString());
		    	
		    	response = httpClient.execute(httpGet);
		    }
		    
		    if (response.getStatusLine().getStatusCode() != 200) {
		    	Log.d(getClass().getSimpleName(), "Error could not access hudson server + " + response.toString());
		    	synchronized (lock) {					
		    		connected = false;
		    	}
		    	notifyCallbacks();
		    	return;
		    }
		   
		    Log.d(getClass().getSimpleName(), "data size from servlet=" + response.getEntity().getContentLength());
		    String jsonString = HudsonJsonParser.convertStreamToString(response.getEntity().getContent());
		    List<JobSummary> oldSummaryList;
		    // this thread may store, while the other thread from aidl may read, so lock briefly during our modifications.
		    synchronized (lock) {				
			    oldSummaryList = new ArrayList<JobSummary>(jobSummaryList);
			    jobSummaryList = HudsonJsonParser.parse(jsonString);
			    
			    lastUpdated = new Date();
			    connected = true;
		    }
		    
		    notifyCallbacks();
		    
		    notificationProcessing(oldSummaryList,jobSummaryList);
		    
		    } catch (UnknownHostException uh) {
		    	uh.printStackTrace();
				Log.d(getClass().getSimpleName(), "UnknownHost Exception in runUpdate" + uh.getStackTrace().toString());
				synchronized (lock) {					
		    		connected = false;
		    	}
				notifyCallbacks();
			} catch (IOException e) {
				e.printStackTrace();
				Log.d(getClass().getSimpleName(), "Exception in runUpdate" + e.getStackTrace().toString());
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace(); 
			} catch (Exception e) {
				e.printStackTrace();
			}
		    
		// http post to the service
		Log.i(getClass().getSimpleName(), "background task - end");
	}

	private boolean loadPrefs() {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		baseurl = prefs.getString("HudsonBaseURL", null);
		if (baseurl == null) {
			return false;
		}
		username = prefs.getString("HudsonUsername", null);
		password = prefs.getString("HudsonPassword", null);
		return true;
		
	}

	private void notifyCallbacks() {
		int i = mCallbacks.beginBroadcast();
		while (i > 0) {
		    i--;
		    try {
		    	mCallbacks.getBroadcastItem(i).jobsUpdated();
		    } catch (RemoteException e) {
		        // The RemoteCallbackList will take care of removing
		        // the dead object for us.
		    }
		}
		mCallbacks.finishBroadcast();
	}

	private void notificationProcessing(List<JobSummary> oldSummaryList,
			List<JobSummary> newList) {
		
			if (oldSummaryList == null || newList == null || oldSummaryList.size() == 0  || newList.size() == 0) {
				return;  
			}
			if (oldSummaryList.size() != newList.size()) {
				notification("Job List Changed");
				return;
			}
			// are the job names the same, and the color?
			for (JobSummary oldJob : oldSummaryList) {
				boolean found = false;
				for (JobSummary newJob : newList) {
					if (oldJob.getName().equals(newJob.getName())) {
						found = true;
						if (!oldJob.getColor().equals(newJob.getColor())) {
							notification(oldJob.getName() + " Job State Changed to "+ newJob.getColor());
							return;
						}
					}	
				}
				if (!found) {
					notification("Job List Changed");
					return;
				}
			}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}
	
	private void notification(String message) {
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		int icon = R.drawable.hudson_icon1;
		CharSequence tickerText = "Hudson Build Alert";
		long when = System.currentTimeMillis();
		
		Context context = getApplicationContext();      // application Context
		CharSequence contentTitle = "Hudson Notification";  // expanded message title
		//CharSequence contentText = "State changed";      // expanded message text

		Intent notificationIntent = new Intent(this, HudsonMonitorActivity.class);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

		// the next two lines initialize the Notification, using the configurations above
		Notification notification = new Notification(icon, tickerText, when);
		notification.defaults = Notification.DEFAULT_ALL;
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		notification.setLatestEventInfo(context, contentTitle, message, contentIntent);
		
		mNotificationManager.notify(1, notification);
		
	}
	
}

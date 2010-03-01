package com.mvw.hudson.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.mvw.hudson.Container;
import com.mvw.hudson.JobAdapter;
import com.mvw.hudson.R;
import com.mvw.hudson.aidl.IHudsonAidlService;
import com.mvw.hudson.aidl.IRemoteServiceCallback;
import com.mvw.hudson.model.JobSummary;
import com.mvw.hudson.service.HudsonService;

public class HudsonMonitorActivity extends ListActivity implements IHudsonMonitorActivity {
	
	//private ProgressDialog m_ProgressDialog = null;
    private ArrayList<JobSummary> m_jobSummaryList = new ArrayList<JobSummary>();
    private JobAdapter m_adapter;
    //private Runnable viewJobs;
    private IHudsonAidlService hudsonAidlService;
    private String lastUpdated;
    private TextView statusView;
    private TextView connectionView;
    private boolean lastAttempt = false;
    
    private ServiceConnection mConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			hudsonAidlService = IHudsonAidlService.Stub.asInterface(service);
			updateDataFromHudsonService();
			registerCallback();
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			try {
				Log.d(this.getClass().getSimpleName(),"unregisterCallback to HudsonService");
				hudsonAidlService.unregisterCallback(mCallback);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			hudsonAidlService = null;
			
		}
    	
    };
    
    private void updateDataFromHudsonService() {
    	if (hudsonAidlService != null) {
	    	try {
				List<JobSummary> list = hudsonAidlService.getJobSummaryList();
				lastAttempt = hudsonAidlService.getConnected();
				lastUpdated = hudsonAidlService.getLastUpdated();
				if (list != null && list.size() > 0) {
	    			m_jobSummaryList =  new ArrayList<JobSummary>(list);
	    			runOnUiThread(returnRes);
	    		}
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    }
    
    private Runnable returnRes = new Runnable() {

        @Override
        public void run() {
        	m_adapter.clear();
            if(m_jobSummaryList != null && m_jobSummaryList.size() > 0){
                m_adapter.notifyDataSetChanged();
                for(int i=0;i<m_jobSummaryList.size();i++)
                m_adapter.add(m_jobSummaryList.get(i));
            }
           // m_ProgressDialog.dismiss();
            m_adapter.notifyDataSetChanged();
            statusView.setText("Last Update: " + lastUpdated);
            if (lastAttempt) {
            	connectionView.setTextColor(Color.GREEN);
            	connectionView.setText("Connected");
            } else {
            	connectionView.setTextColor(Color.RED);
            	connectionView.setText("Not Connected");
            }
        }
      };
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Container.hudsonMonitorActivity = this;
        m_adapter = new JobAdapter(this,R.layout.job_item,m_jobSummaryList);
        setListAdapter(m_adapter);
        setContentView(R.layout.job_list);
        statusView = (TextView) this.findViewById(R.id.status);
        statusView.setText("Initializing");
        connectionView = (TextView) this.findViewById(R.id.connection);
        // starts the hudsonService
        
        Intent svc = new Intent(this, HudsonService.class);
        startService(svc);
        
        // now bind to it
        bindService(new Intent(this,HudsonService.class),
                mConnection, BIND_AUTO_CREATE);
        
        
    }
        
        @Override
	protected void onPause() {
		super.onPause();
		Log.d(this.getClass().getSimpleName(),"unregisterCallback to HudsonService");
		try {
			if (hudsonAidlService != null) {
				hudsonAidlService.unregisterCallback(mCallback);
			}
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
        
    private void registerCallback() {
    	Log.d(this.getClass().getSimpleName(),"registerCallback to HudsonService");
		try {
			if (hudsonAidlService != null) {
				hudsonAidlService.registerCallback(mCallback);
			}
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

	@Override
	protected void onResume() {
		super.onResume();
		updateDataFromHudsonService();
		registerCallback();
	}

		@Override
	protected void onDestroy() {
		super.onDestroy();
		Container.hudsonMonitorActivity = null;
		
		//Intent svc = new Intent(this, HudsonService.class);
	    //stopService(svc);
		
		unbindService(mConnection);
		Log.d(this.getClass().getSimpleName(),"onDestroy");
	}
		

	    private IRemoteServiceCallback mCallback = new IRemoteServiceCallback.Stub() {
			@Override
			public void jobsUpdated() throws RemoteException {
				mHandler.sendMessage(mHandler.obtainMessage(BUMP_MSG,0,0));
				
			}
	    };

	    private static final int BUMP_MSG = 1;

	    private Handler mHandler = new Handler() {
	        @Override public void handleMessage(Message msg) {
	            switch (msg.what) {
	                case BUMP_MSG:
	                    Log.d(this.getClass().getSimpleName(),"jobsUpdated callback hit, we should update our list");
	                    try {
	                    m_jobSummaryList =  new ArrayList<JobSummary>(hudsonAidlService.getJobSummaryList());
	                    lastUpdated = hudsonAidlService.getLastUpdated();
	                    lastAttempt = hudsonAidlService.getConnected();
	                    } catch (RemoteException re) {
	                    	Log.d(this.getClass().getSimpleName(),"Caught RemoteException in callback logic");
	                    }
						runOnUiThread(returnRes);
	                    break;
	                default:
	                    super.handleMessage(msg);
	            }
	        }

	    };
	    
	    @Override
	    public boolean onCreateOptionsMenu(Menu menu) {
	        MenuInflater inflater = getMenuInflater();
	        inflater.inflate(R.menu.menu, menu);
	        return true;
	    }
	    
	    @Override
	    public boolean onOptionsItemSelected(MenuItem item) {
	        switch (item.getItemId()) {
	        case R.id.settings:
	        	Intent settingsActivity = new Intent(getBaseContext(), HudsonPreferenceActivity.class);
	        	startActivity(settingsActivity);
	            return false;
	        case R.id.quit:
	        	 this.finish();
	            return true;
	        }
	        return false;
	    }

        
        
        
}
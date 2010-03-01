package com.mvw.hudson.aidl;

import com.mvw.hudson.model.JobSummary;
import com.mvw.hudson.model.Job;
import com.mvw.hudson.aidl.IRemoteServiceCallback;

// Declare the interface.
interface IHudsonAidlService {
    List<JobSummary> getJobSummaryList();
    void registerCallback(IRemoteServiceCallback cb);
    void unregisterCallback(IRemoteServiceCallback cb);
    String getLastUpdated();
    boolean getConnected();
}
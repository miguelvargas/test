package com.mvw.hudson.model;

import android.os.Parcel;
import android.os.Parcelable;

public class JobSummary implements Parcelable {
	private String name;
	private String color;
	private String url;
	private Job job;
	
	public static final Parcelable.Creator<JobSummary> CREATOR = new Parcelable.Creator<JobSummary>() {
        public JobSummary createFromParcel(Parcel in) {
            return new JobSummary(in);
        }

        public JobSummary[] newArray(int size) {
            return new JobSummary[size];
        }
    };
	
    public JobSummary() {
    	
    }
    
	public JobSummary(Parcel in) {
		name = in.readString();
		color = in.readString();
		url = in.readString();
		job = in.readParcelable(null);
	}
	public Job getJob() {
		return job;
	}
	public void setJob(Job job) {
		this.job = job;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getColor() {
		return color;
	}
	public void setColor(String color) {
		this.color = color;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(name);
		dest.writeString(color);
		dest.writeString(url);
		dest.writeParcelable(job, flags);
	}
}


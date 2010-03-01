package com.mvw.hudson.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Job implements Parcelable {

	private int test;
	
	public static final Parcelable.Creator<Job> CREATOR = new Parcelable.Creator<Job>() {
        public Job createFromParcel(Parcel in) {
            return new Job(in);
        }

        public Job[] newArray(int size) {
            return new Job[size];
        }
    };
	
	public Job(Parcel in) {
		test = in.readInt();
	}
	
	public Job() {
		
	}

	
	public int getTest() {
		return test;
	}

	public void setTest(int test) {
		this.test = test;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(test);	
	}

}

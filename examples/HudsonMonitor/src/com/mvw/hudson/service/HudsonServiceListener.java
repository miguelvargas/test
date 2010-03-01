package com.mvw.hudson.service;

import java.util.List;

import com.mvw.hudson.model.JobSummary;


public interface HudsonServiceListener {
	public void onJobsSummary(List<JobSummary> jobSummaryList);
}

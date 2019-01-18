package com.cbrmm.autocaddy.driver;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;


public class AC_Control extends Job {
	
	private static final int PRIORITY = 0;
	
	protected AC_Control(Params params) {
		super(new Params(PRIORITY).requireNetwork().persist());
	}
	
	@Override
	public void onAdded() {
	
	}
	
	@Override
	public void onRun() throws Throwable {
	
	}
	
	@Override
	protected void onCancel(int cancelReason, @Nullable Throwable throwable) {
	
	}
	
	@Override
	protected RetryConstraint shouldReRunOnThrowable(@NonNull Throwable throwable, int runCount, int maxRunCount) {
		return null;
	}
}

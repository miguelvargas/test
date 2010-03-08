package com.mvwsolutions.common.web.command;

public abstract class BaseCmd {
	protected String errMessage = null;
    private boolean isValid = true;

	public String getErrMessage() {
		return errMessage;
	}

	public void setErrMessage(String errMessage) {
		this.errMessage = errMessage;
	}

	public boolean isValid() {
		return isValid;
	}

	public void setIsValid(boolean userNameValid) {
		isValid = userNameValid;
	}

}

package com.roquito.web.response;

public class DeviceRegistrationResponse {
	private String deviceAppId;
	private boolean error;
	private String errorResponse;

	public DeviceRegistrationResponse(String errorResponse) {
		this(null, true, errorResponse);
	}

	public DeviceRegistrationResponse(String deviceAppId, boolean error,
			String errorResponse) {
		this.deviceAppId = deviceAppId;
		this.error = error;
		this.errorResponse = errorResponse;
	}

	public String getDeviceAppId() {
		return deviceAppId;
	}

	public void setDeviceAppId(String deviceAppId) {
		this.deviceAppId = deviceAppId;
	}

	public boolean isError() {
		return error;
	}

	public void setError(boolean error) {
		this.error = error;
	}

	public String getErrorResponse() {
		return errorResponse;
	}

	public void setErrorResponse(String errorResponse) {
		this.errorResponse = errorResponse;
	}
}

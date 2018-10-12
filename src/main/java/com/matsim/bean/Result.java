package com.matsim.bean;



import java.io.Serializable;


public class Result implements Serializable {

	private static final long	serialVersionUID	= -7134582590300986814L;
	//是否成功执行
	private boolean				isSuccess			= true;
	//错误信息
	private String				errMsg ="";

	private String info ="";

	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	//回传内容
	private Object				data;

	public boolean isSuccess() {

		return isSuccess;
	}

	public void setSuccess(boolean isSuccess) {

		this.isSuccess = isSuccess;
	}


	public String getErrMsg() {

		return errMsg;
	}

	public void setErrMsg(String errMsg) {

		this.errMsg = errMsg;
	}

	public Object getData() {

		return data;
	}

	public void setData(Object data) {

		this.data = data;
	}

	@Override
	public String toString() {
		return "'Result':{" +
				"'isSuccess':'" + isSuccess +
				"', 'errMsg':'" + errMsg +
				"', 'info':'" + info +
				"', 'data':'" + data +
				"'}";
	}
}


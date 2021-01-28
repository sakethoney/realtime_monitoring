package com.ksh.crfi.app.common.entities;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Lists;
import com.ksh.crfi.mutual.utils.Utils;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LogEntity extends EntityBase implements Comparable<LogEntity>{

	@Column(name ="Text")
	private String text;
	
	@Column(name = "Application")
	private String application;
	
	@Column(name = "LogLevel")
	private String logLevel;
	
	@Column(name ="Timestamp")
	private Long timestamp;
	
	@Column(name = "ClientId")
	private String clientId;
	
	@Override
	public String getKey() {
		return String.format("%s_%s_%s",clientId, Utils.paddingLongValue(timestamp), id);
	}
	
	@JsonIgnore
	public LogBean convertToLogBean() {
		return convertToLogBean(Utils.getISODateTimeWithTimeZoneFormat(),this);
	}
	
	private static LogBean convertToLogBean(DateFormat dateFormat, LogEntity logEntity) {
		LogBean logBean = new LogBean();
		logBean.setApplication(logEntity.getApplication());
		logBean.setClientId(logEntity.getId());
		logBean.setLogLevel(logEntity.getLogLevel());
		logBean.setText(logEntity.getText());
		logBean.setLogMessageTimeStamp(dateFormat.format(new Date(logEntity.getTimestamp())));
		return logBean;
	}
	
	public static List<LogBean> convertToLogBeans(List<LogEntity> logEntities){
		List<LogBean> result = Lists.newArrayList();
		if(logEntities == null || logEntities.isEmpty()) {
			return result;
		}
		DateFormat dateFormat = Utils.getISODateTimeWithTimeZoneFormat();
		for(LogEntity logEntity : logEntities) {
			result.add(convertToLogBean(dateFormat, logEntity));
		}
		return result;
	}
	
	@Override
	public int compareTo(LogEntity o) {
		if(timestamp == null && o.getTimestamp() == null)	{
			return 0;
		}else if (timestamp == null) {
			return -1;
		}else if (o.getTimestamp()==null) {
			return 1;
		}else {
			return timestamp.compareTo(o.getTimestamp());
		}
	}
}

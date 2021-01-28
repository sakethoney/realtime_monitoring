package kafka;

import org.mortbay.log.Log;

import com.ksh.crfi.mutual.utils.JsonObjectMapper;

import joptsimple.internal.Strings;

public class KafkaMessageProducer {

	public synchronized void sendMessage(String event, FileArrivedContext context) {
		String path = context.getFilePath();
		try {
			FileProfile profile = context.getFileProfile();
			fileMetadataS;ervice.saveFileMetaData(context);
			String hdfsPath = moveToHdfs(context);
			context.setHdfsPath(hdfsPath);
			boolean isAutoEtl = Const.FILE_BUSINESS_TYPE_AUTO_ETL.equals(profile.getFileBusinessType());
			
			if(!Strings.isNullOrEmpty(hdfsPath)&& !isAutoEtl) {
				triggerStatisticalValidation(profile, hdfsPath, path);
			}
			fileArriveMessageProducer
			.produceSync(JsonObjectMapper.writeValueAsString(convertToFileArrivalMessge(event, context)));
			log.info("Send Kafka message for arrived file:"+ path);
		}catch(Exception e) {
			String msg = String.format("Error occured while handle arrived %s", path);
			log.error(msg, e);
			sedException(context, e);
			
		}
	}
	
}

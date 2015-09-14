package com.racloop.housekeeping

import grails.transaction.Transactional

import org.apache.log4j.FileAppender
import org.apache.log4j.Logger
import org.elasticsearch.common.joda.time.DateTime

import com.amazonaws.services.s3.transfer.TransferManager
import com.amazonaws.services.s3.transfer.Upload

@Transactional
class HouseKeepingService {
	
	def journeyDataService
	def awsService
	private static final String machineName = java.net.InetAddress.getLocalHost().getHostName()
	private static final String S3_BUCKET_NAME = "racloop-analytics-logs"
	private static final String LOGS_ARCHIVE_DIR_NAME = "ArchivedLogs"

    def archiveAnalyticsLogs() {
		def results = []
		FileAppender app = Logger.getLogger("grails.app.filters.com.racloop.AnalyticsFilters").getAppender('analyticAppender')
		String filePath = app.getFile()
		String folderPath = filePath.substring(0,filePath.lastIndexOf(File.separator)+1)
		File[] files = new File(folderPath).listFiles()
		TransferManager transferManager = awsService.getTransferManager();
		
		for (File file : files) {
			if (file.isFile() && shouldArchive(file)) {
				uploadFileToS3(transferManager, file)
				archiveUploadedFile(file, folderPath)
			}
		}
    }
	
	def restoreESData() {
		DateTime journeyDateFrom = new DateTime().minusHours(6)
		journeyDataService.reloadESDataFromDynamoDB(journeyDateFrom)
	}
	
	private boolean shouldArchive(File file) {
		String fileName = file.getName()
		if(fileName.startsWith("analytics") && !fileName.endsWith(".log")){
			return true
		}
		else {
			return false
		}
	}
	
	private String getKeyForS3Bucket(File fileToUpload){
		return machineName + File.separator + fileToUpload.getName()
	}
	
	private void uploadFileToS3(TransferManager transferManager, File fileToUpload) {
		log.info "Uploading file to S3. File name is: ${fileToUpload.getName()}"
		Upload upload = transferManager.upload(S3_BUCKET_NAME,getKeyForS3Bucket(fileToUpload) , fileToUpload)
		upload.waitForCompletion();
		log.info "File uploaded successfully File name is: ${fileToUpload.getName()}"
	}
	
	private void archiveUploadedFile(File uploadedFile, String folderPath){
		File archivedLogsDir = new File(folderPath + File.separator + LOGS_ARCHIVE_DIR_NAME)
		if(!archivedLogsDir.exists()) {
			archivedLogsDir.mkdirs()
		}
		log.info"Moving file ${uploadedFile.getName()} to ${archivedLogsDir.getAbsolutePath()}"
		uploadedFile.renameTo(new File(archivedLogsDir,uploadedFile.getName()))
	}
}

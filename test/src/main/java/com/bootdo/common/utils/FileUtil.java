package com.bootdo.common.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.util.UUID;

import org.activiti.rest.editor.model.ModelEditorJsonRestResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileUtil {
	protected static final Logger LOGGER = LoggerFactory.getLogger(ModelEditorJsonRestResource.class);

	public static void uploadFile(byte[] file, String filePath, String fileName) throws Exception {
		mkdir(filePath);
		FileOutputStream out = new FileOutputStream(filePath + fileName);
		out.write(file);
		out.flush();
		out.close();
	}

	/**
	 * @Description ：删除文件夹
	 * @param fileName
	 * @return
	 */
	public static boolean deleteDirectory(String fileName) {
		File file = new File(fileName);
		// 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
		if (file.exists() && file.isDirectory()) {
			if (file.delete()) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
	
	public static boolean deleteFile(String fileName) {
		File file = new File(fileName);
		// 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
		if (file.exists() && file.isFile()) {
			if (file.delete()) {
				return true;
			} else {
			      // 将生成的服务器端文件删除
				LOGGER.error("文件："+fileName+"，删除服务端临时下载文件失败！");
				return false;
			}
		} else {
			return false;
		}
	}

	public static void mkdir(String filePath) {
		File targetFile = new File(filePath);
		if (!targetFile.exists()) {
			targetFile.mkdirs();
		}
	}
	
	
	public static String getFileName(String fileUrl){
		String[] fileNameArray = fileUrl.split("\\/");
		int length =fileNameArray.length;
		String fileName = fileNameArray[length-1];;// 文件名称
		String[] array = fileName.split("\\.");
		fileName = array[0];
		return fileName;
	}
	
	public static String renameToUUID(String fileName) {
		return UUID.randomUUID() + "." + fileName.substring(fileName.lastIndexOf(".") + 1);
	}
}

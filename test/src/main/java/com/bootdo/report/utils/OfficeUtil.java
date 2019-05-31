package com.bootdo.report.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.ConnectException;

import org.artofsolving.jodconverter.office.DefaultOfficeManagerConfiguration;
import org.artofsolving.jodconverter.office.OfficeManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.artofsolving.jodconverter.openoffice.connection.OpenOfficeConnection;
import com.artofsolving.jodconverter.openoffice.connection.SocketOpenOfficeConnection;
import com.artofsolving.jodconverter.openoffice.converter.OpenOfficeDocumentConverter;

/**
 * @author 作者:Administrator
 * 
 * @createDate 创建时间：2019年5月30日 下午3:05:36
 **/

public class OfficeUtil {

	protected static final Logger logger = LoggerFactory.getLogger(OfficeUtil.class);

	private static OfficeManager officeManager;// 在类中定义全局属性
	
	private static  String officeHome = "C:/Program Files (x86)/OpenOffice";//office安装目录

	public static int office2PDF(String sourceFile, String destFile) throws FileNotFoundException {
		OpenOfficeConnection connection = new SocketOpenOfficeConnection("127.0.0.1", 8100);
		try {
			File inputFile = new File(sourceFile);
			if (!inputFile.exists()) {
				logger.info("找不到源文件");
				return -1;// 找不到源文件, 则返回-1
			}
			// 如果目标路径不存在, 则新建该路径
			File outputFile = new File(destFile);
			if (!outputFile.getParentFile().exists()) {
				outputFile.getParentFile().mkdirs();

			}
			// 启动OpenOffice的服务
			startService();// connect to an OpenOffice.org instance running on port 8100	
			connection.connect();
			// convert
			OpenOfficeDocumentConverter converter = new OpenOfficeDocumentConverter(connection);
			converter.convert(inputFile, outputFile);
			return 0;
		} catch (ConnectException e) {
			logger.error("openOffice连接失败！请检查IP,端口",e);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// 释放连接
			connection.disconnect();// close the connection
			// 关闭OpenOffice服务的进程
			stopService();
		}
		return 1;
	}

	public static void startService() {
		DefaultOfficeManagerConfiguration configuration = new DefaultOfficeManagerConfiguration();
		try {
			// 准备启动服务
			configuration.setOfficeHome(officeHome);// 设置OpenOffice安装目录(事先把OpenOffice安装到D:/OpenOffice目录)
			// 设置转换端口，默认为8100
			configuration.setPortNumbers(8100);
			// 设置任务执行超时为5分钟
			configuration.setTaskExecutionTimeout(1000 * 60 * 5L);
			// 设置任务队列超时为24小时
			configuration.setTaskQueueTimeout(1000 * 60 * 60 * 24L);
			officeManager = configuration.buildOfficeManager();
			officeManager.start(); // 启动服务
			logger.info("office转换服务启动成功!");
		} catch (Exception ce) {
			logger.info("office转换服务启动失败!详细信息:" + ce);
		}

	}

	public static void stopService() {
		logger.info("关闭office转换服务....");
		if (officeManager != null) {
			officeManager.stop();
		}
		logger.info("关闭office转换成功!");
	}

	// 测试
	public static void main(String[] args) throws Exception {
		String filepath = "D:\\sampleTemplate\\电磁兼容概论.ppt";
		String outpath = "D:\\sampleTemplate\\电磁兼容概论.pdf";
		office2PDF(filepath, outpath);
		// docxToPDF(new File(filepath),new File(outpath));
	}

}

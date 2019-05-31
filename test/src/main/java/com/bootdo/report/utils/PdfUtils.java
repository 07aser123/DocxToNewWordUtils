package com.bootdo.report.utils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;

/**
 * @author 作者:Administrator
 * 
 * @createDate 创建时间：2019年5月9日 下午1:53:54
 **/
public class PdfUtils {

	public static String getPdfText(String inputPath) {
		String result = "";
		PdfReader reader = null;
		try {
			reader = new PdfReader(inputPath);
			int pageNum = reader.getNumberOfPages();
			for (int i = 1; i <= pageNum; i++) {
				result += PdfTextExtractor.getTextFromPage(reader, i);// 读取第i页的文档内容
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
		return result;
	}
	
	/**
	  *文档页面数量
	 * 
	 * @param fileAddress 文件地址 D://sampleTemplate//管理平台工作交接.pdf
	 */
	public static int pageNum(String fileAddress) {
		// 将pdf装图片 并且自定义图片得格式大小
		File file = new File(fileAddress);
		int pageCount = 0;
		PDDocument doc = null;
		try {
			doc = PDDocument.load(file);
			 pageCount = doc.getNumberOfPages();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (doc != null) {
				try {
					doc.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return pageCount;
	}

	/**
	 * 转换全部的pdf
	 * 
	 * @param fileAddress 文件地址 D://sampleTemplate//管理平台工作交接.pdf
	 * @param filename    PDF文件名
	 * @param type        图片类型
	 */
	public static int pdf2Image(String fileAddress, String outputPath, String filename, String type) {
		// 将pdf装图片 并且自定义图片得格式大小
		File file = new File(fileAddress);
		int i = 0;
		PDDocument doc = null;
		try {
			doc = PDDocument.load(file);
			PDFRenderer renderer = new PDFRenderer(doc);
			int pageCount = doc.getNumberOfPages();
			for (i = 0; i < pageCount; i++) {
				BufferedImage image = renderer.renderImageWithDPI(i, 144); // Windows native DPI
				// BufferedImage srcImage = resize(image, 240, 240);//产生缩略图
				ImageIO.write(image, type, new File(outputPath + "\\" + filename + "_" + (i + 1) + "." + type));

			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (doc != null) {
				try {
					doc.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return i;
	}

	/**
	 * 自由确定起始页和终止页
	 * 
	 * @param fileAddress 文件地址
	 * @param filename    pdf文件名
	 * @param startPage   开始页 开始转换的页码，从1开始
	 * @param endPage     结束页 停止转换的页码
	 * @param type        图片类型
	 */
	public static int pdf2Image(String fileAddress, String outputPath, String filename, int startPage, int endPage,
			String type) {
		// 将pdf装图片 并且自定义图片得格式大小
		File file = new File(fileAddress);
		int i = 0;
		PDDocument doc = null;
		try {
			doc = PDDocument.load(file);
			PDFRenderer renderer = new PDFRenderer(doc);
			int pageCount = doc.getNumberOfPages();// 最大页数
			if (startPage < 1) {
				return -1;
			}
			if (endPage > pageCount) {
				endPage = pageCount;
			}
			for (i = startPage - 1; i < endPage; i++) {
				BufferedImage image = renderer.renderImageWithDPI(i, 144); // Windows native DPI
				// BufferedImage srcImage = resize(image, 240, 240);//产生缩略图
				 File fileOut = new File(outputPath + "\\" + filename + "_" + (i + 1) + "." + type);
				ImageIO.write(image, type, fileOut);

			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (doc != null) {
				try {
					doc.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return i;
	}

	public static void main(String[] args) {
		// String text = getPdfText("D:\\sampleTemplate\\管理平台工作交接.pdf");
		// pdf2png("D:\\sampleTemplate","管理平台工作交接","jpg");
		pdf2Image("D:\\sampleTemplate\\管理平台工作交接.pdf", "D:\\sampleTemplate\\out", "管理平台工作交接", 1, 2, "jpg");

	}
}

package com.bootdo.report.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.io.FileUtils;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.converter.PicturesManager;
import org.apache.poi.hwpf.converter.WordToHtmlConverter;
import org.apache.poi.hwpf.usermodel.Picture;
import org.apache.poi.hwpf.usermodel.PictureType;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.w3c.dom.Document;

import fr.opensagres.poi.xwpf.converter.core.BasicURIResolver;
import fr.opensagres.poi.xwpf.converter.core.FileImageExtractor;
import fr.opensagres.poi.xwpf.converter.xhtml.XHTMLConverter;
import fr.opensagres.poi.xwpf.converter.xhtml.XHTMLOptions;

/**
 * @author 作者:Administrator
 * 
 * @createDate 创建时间：2019年4月23日 下午4:01:05
 **/
public class WordToHtmlUtils {

	private static boolean docToHtml(String filePath, String fileName,String outputPath) throws Exception {
		InputStream input = new FileInputStream(filePath);
		HWPFDocument wordDocument = new HWPFDocument(input);
		WordToHtmlConverter wordToHtmlConverter = new WordToHtmlConverter(
				DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument());
		wordToHtmlConverter.setPicturesManager(new PicturesManager() {
			public String savePicture(byte[] content, PictureType pictureType, String suggestedName, float widthInches,
					float heightInches) {
				return suggestedName;
			}
		});
		wordToHtmlConverter.processDocument(wordDocument);
		List<Picture> pics = wordDocument.getPicturesTable().getAllPictures();
		if (pics != null) {
			for (int i = 0; i < pics.size(); i++) {
				Picture pic = pics.get(i);
				try {
					pic.writeImageContent(new FileOutputStream(outputPath + pic.suggestFullFileName()));
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			}
		}
		Document htmlDocument = wordToHtmlConverter.getDocument();
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		DOMSource domSource = new DOMSource(htmlDocument);
		StreamResult streamResult = new StreamResult(outStream);
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer serializer = tf.newTransformer();
	//	serializer.setOutputProperty(OutputKeys.ENCODING, "utf-8");
		 serializer.setOutputProperty(OutputKeys.ENCODING, "utf-8");  
		serializer.setOutputProperty(OutputKeys.INDENT, "yes");
		serializer.setOutputProperty(OutputKeys.METHOD, "html");
		serializer.transform(domSource, streamResult);
		outStream.close();
		String content = new String(outStream.toByteArray());
		FileUtils.writeStringToFile(new File(outputPath,fileName), content, "utf-8");
		return true;
	}

	@SuppressWarnings("deprecation")
	private static String docxToHtml(String filepath,String fileName,String outputPath) throws Exception {
		OutputStreamWriter outputStreamWriter = null;
		try {
			XWPFDocument document = new XWPFDocument(new FileInputStream(filepath));
			XHTMLOptions options = XHTMLOptions.create();
			String imagePathStr = outputPath+"\\image\\";
			// 存放图片的文件夹
			options.setExtractor(new FileImageExtractor(new File(imagePathStr))); // html中图片的路径
			options.URIResolver(new BasicURIResolver("image"));
		    //outputStreamWriter = new OutputStreamWriter(new FileOutputStream(outputPath+fileName), "GB2312");	
			outputStreamWriter = new OutputStreamWriter(new FileOutputStream(outputPath+fileName), "utf-8");	
			
			XHTMLConverter xhtmlConverter = (XHTMLConverter) XHTMLConverter.getInstance();
			xhtmlConverter.convert(document, outputStreamWriter, options);
		} finally {
			if (outputStreamWriter != null) {
				outputStreamWriter.close();
			}
		}
		return outputPath;
	}

	
	public  static String wordToHtml(String filepath,String fileName,String outputPath) {
		try {
			if( WordUtils.checkVersionIsHigh(filepath)) {
			 	docxToHtml(filepath,  fileName,outputPath );//文件输出路径
			}else {
			 	docToHtml(filepath,  fileName,outputPath );//文件输出路径
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return outputPath;
	}

	/**测试用例**、
	 * @Description ：
	 * @param args
	 * @throws Throwable
	 */
	public static void main(String[] args) throws Throwable {
	     String path = "d:\\sampleTemplate\\1.doc";
		 String outPath = "d:\\sampleTemplate\\out\\";
		 wordToHtml(path, "1.html",outPath);
		 path = "d:\\sampleTemplate\\文件写入.docx";
		 wordToHtml(path, "11.html",outPath);
	}

}

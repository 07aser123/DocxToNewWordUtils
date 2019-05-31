package com.bootdo.report.utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.poi.xwpf.usermodel.XWPFDocument;

import com.lowagie.text.Font;
import com.lowagie.text.pdf.BaseFont;

import fr.opensagres.poi.xwpf.converter.pdf.PdfConverter;
import fr.opensagres.poi.xwpf.converter.pdf.PdfOptions;
import fr.opensagres.xdocreport.itext.extension.font.IFontProvider;
import fr.opensagres.xdocreport.itext.extension.font.ITextFontRegistry;


/**
 * @author 作者:Administrator
 * 
 * @createDate 创建时间：2019年5月7日 上午11:47:17
 **/
public class WordToPDFUtils {

	/**
	 * 将word文档， 转换成pdf, 中间替换掉变量
	 * 
	 * @param source 源为word文档， 必须为docx文档
	 * @param target 目标输出
	 * @throws Exception
	 */
	public static void docxConverterToPdf(String filepath, String outpath) throws Exception {
		if (!WordUtils.checkVersionIsHigh(filepath)) {// 不是docx不解析
			return;
		}
		InputStream source = null;
		OutputStream target = null;
		try {
			source = new FileInputStream(filepath);
			target = new FileOutputStream(outpath);
			XWPFDocument doc = new XWPFDocument(source);
			PdfOptions options = PdfOptions.create();
	        //中文字体处理
		      options.fontProvider(new IFontProvider() {		    	  
	                @Override
	                public Font getFont(String familyName, String encoding, float size, int style, java.awt.Color color) {
	                    try {
	                        BaseFont bfChinese = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
	                        Font fontChinese = new Font(bfChinese, size, style, color);
	                        if (familyName != null)
	                            fontChinese.setFamily(familyName);
	                        return fontChinese;
	                    } catch (Exception e) {
	                        e.printStackTrace();
                           return ITextFontRegistry.getRegistry().getFont(familyName, encoding, size, style, color);

	                    }
	                }
	        });

			PdfConverter.getInstance().convert(doc, target, options);
			doc.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (source != null) {
				source.close();
			}
			if (target != null) {
				target.close();
			}
		}
	}



	
	// 测试
	public static void main(String[] args) throws Exception {
		String filepath = "D:\\sampleTemplate\\坏模板.docx";
		String outpath = "D:\\sampleTemplate\\测试文件写入.pdf";
		docxConverterToPdf(filepath, outpath);
	}

}

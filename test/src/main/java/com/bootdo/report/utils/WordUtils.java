package com.bootdo.report.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.editor.language.json.converter.util.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.POIXMLDocument;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.usermodel.Range;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.poifs.filesystem.FileMagic;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.Document;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlToken;
import org.openxmlformats.schemas.drawingml.x2006.main.CTNonVisualDrawingProps;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPositiveSize2D;
import org.openxmlformats.schemas.drawingml.x2006.wordprocessingDrawing.CTInline;

import com.alibaba.fastjson.JSON;
import com.bootdo.common.utils.FileUtil;
import com.bootdo.common.utils.R;
import com.bootdo.report.domain.TestTemplateDictDO;
import com.lowagie.text.PageSize;

/**
 * @author 作者:Administrator
 * 
 * @createDate 创建时间：2019年4月23日 上午9:28:41
 **/
public class WordUtils {

	/**
	 * 为表格插入数据，行数不够添加新行
	 * 
	 * @param table     需要插入数据的表格
	 * @param tableList 插入数据集合
	 */
	@SuppressWarnings("rawtypes")
	private static void insertTable(String key, XWPFTable table, List<Object> tableValueList) {
		// 创建行,根据需要插入的数据添加新行，不处理表头
		if (CollectionUtils.isNotEmpty(tableValueList)) {
			List<XWPFTableRow> rows = table.getRows();
			int startIndex = 1;
			// 寻找开始填数据的行数，有时候表头可能是多行
			for (int i = 1; i < rows.size(); i++) {
				XWPFTableRow newRow = table.getRow(i);
				List<XWPFTableCell> cells = newRow.getTableCells();
				if (cells.get(0).getText().contains(key)) {
					// 判断单元格是否需要替换
					cells.get(0).getParagraphs().get(0).getRuns().get(0).setText("", 0);// 把模板key值换成空字符
					startIndex = i;
					break;
				}
			}
			// 表格不够，需要插入新的数据行，数据行列数和填写数据那行一样
			int dataRow = rows.size() - startIndex;// 模板里面可以填数据的行数
			if (tableValueList.size() > dataRow) {
				int addRowLen = tableValueList.size() - dataRow;// 需要新增的行
				int dateLength = table.getRow(startIndex).getTableCells().size();
				for (int i = 0; i < addRowLen; i++) {
					XWPFTableRow newRows = table.createRow(); // 创建表格
					if (newRows.getTableICells().size() < dateLength) {
						int addCellLength = dateLength - newRows.getTableICells().size();
						for (int j = 0; j < addCellLength; j++) {
							newRows.createCell();
						}
					}
				}
			}

			List<XWPFTableCell> tableInfoCellList = table.getRow(startIndex - 1).getTableCells();// 表头信息
			for (int i = 0; i < tableValueList.size(); i++) {// 填充数据
				int rowIndex = i + startIndex;
				XWPFTableRow newRow = table.getRow(rowIndex); // 填充数据行
				// [["1","1AA","1BB","1CC"],["2","2AA","2BB","2CC"],["3","3AA","3BB","3CC"],["4"
				// ,"4AA","4BB","4CC"]]
				List<XWPFTableCell> cells = newRow.getTableCells();
				Object object = tableValueList.get(i);// 获得表格对应列数据
				Map map = (Map) object;
				for (int j = 0; j < cells.size(); j++) {
					String tableKey = tableInfoCellList.get(j).getText();
					String cellValue = (String) map.get(tableKey);
					cells.get(j).setText(cellValue);
				}
			}

		}
	}

	/**
	 * 根据模板生成新word文档 判断表格是需要替换还是需要插入，判断逻辑有$为替换，表格无$为插入
	 * 
	 * @param inputUrl  模板存放地址
	 * @param outPutUrl 新文档存放地址
	 * @param textMap   需要替换的信息集合
	 * @param
	 * @return 成功返回true,失败返回false
	 */
	public static boolean changDocxByKeyList(String inputPath, String outputPath,
			Map<String, TestTemplateDictDO> textMap, List<String> keyList) {
		String[] strings = inputPath.split("\\.");
		if (!strings[1].equals("docx")) {// 不是docx不解析
			return false;
		}
		// 模板转换默认成功
		boolean changeFlag = true;
		XWPFDocument document = null;
		FileOutputStream stream = null;
		try {
			// 获取docx解析对象
			document = new XWPFDocument(POIXMLDocument.openPackage(inputPath));
			if (CollectionUtils.isNotEmpty(keyList)) {
				// 解析替换文本段落对象
				WordUtils.changeTextByKey(document, textMap, keyList);
				WordUtils.changeTableByKeyList(document, textMap, keyList);
			}
			// 生成新的word
			File file = new File(outputPath);
			stream = new FileOutputStream(file);
			document.write(stream);
		} catch (IOException e) {
			e.printStackTrace();
			changeFlag = false;
		} finally {
			if (stream != null) {
				try {
					stream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}
		return changeFlag;

	}

	/**
	 * 根据模板生成新word文档 判断表格是需要替换还是需要插入，判断逻辑有$为替换，表格无$为插入
	 * 
	 * @param inputUrl  模板存放地址
	 * @param outPutUrl 新文档存放地址
	 * @param textMap   需要替换的信息集合
	 * @param
	 * @return 成功返回true,失败返回false
	 */
	@SuppressWarnings("resource")
	public static boolean changDocByKeyList(String inputPath, String outputPath, Map<String, String> textMap,
			List<String> keyList) {

		String[] strings = inputPath.split("\\.");
		if (!strings[1].equals("doc")) {// 不是不解析
			return false;
		}
		// 模板转换默认成功
		boolean changeFlag = true;
		try {
			// 获取docx解析对象
			InputStream fis = new FileInputStream(inputPath);
			HWPFDocument document = new HWPFDocument(fis);
			Range range = document.getRange();
			String text = document.getDocumentText();
			for (String key : keyList) {
				String keyV = "[" + key + "]";
				if (text.contains(keyV)) {// 判断段落是否含有关键字
					range.replaceText(key, textMap.get(key));
				}
			}
			// 生成新的word
			FileOutputStream stream = new FileOutputStream(outputPath);
			document.write(stream);
			stream.close();
			fis.close();
		} catch (IOException e) {
			e.printStackTrace();
			changeFlag = false;
		}
		return changeFlag;

	}

	/**
	 * 替换表格对象方法
	 * 
	 * @param document  docx解析对象
	 * @param textMap   需要替换的信息集合
	 * @param tableList 需要插入的表格信息集合
	 */
	private static void changeTableByKeyList(XWPFDocument document, Map<String, TestTemplateDictDO> textMap,
			List<String> keyList) {
		// 获取表格对象集合
		List<XWPFTable> tables = document.getTables();
		for (int i = 0; i < tables.size(); i++) {
			XWPFTable table = tables.get(i);
			if (checkNeedReplace(table.getText(), keyList)) {// 需要替换的表格
				List<XWPFTableRow> rows = table.getRows();
				// 遍历表格,并替换模板
				for (XWPFTableRow row : rows) {
					List<XWPFTableCell> cells = row.getTableCells();
					for (XWPFTableCell cell : cells) {
						// 判断单元格是否需要替换
						List<XWPFParagraph> paragraphs = cell.getParagraphs();
						for (XWPFParagraph paragraph : paragraphs) {
							if (!StringUtils.isEmpty(paragraph.getText())) {
								replaceParagraphByKey(paragraph, textMap, keyList);
							}
						}
					}
				}
			} else {
				String key = checkIsInsertTable(table.getText(), keyList);
				if (StringUtils.isNotEmpty(key)) {// 插入数据的表格
					TestTemplateDictDO testTemplateDictDO = textMap.get(key);
					// [["1","1AA","1BB","1CC"],["2","2AA","2BB","2CC"],["3","3AA","3BB","3CC"],["4","4AA","4BB","4CC"]]
					if (testTemplateDictDO != null) {
						String value = testTemplateDictDO.getValue();
						if (StringUtils.isNotEmpty(value)) {
							List<Object> tableValueList = JSON.parseArray(value, Object.class);
							insertTable(key, table, tableValueList);
						}
					}
				}
			}
		}
	}

	private static boolean checkNeedReplace(String text, List<String> keyList) {
		boolean result = false;
		if (CollectionUtils.isNotEmpty(keyList)) {
			if (text.contains("[") && text.contains("]")) {
				for (String key : keyList) {
					String keyV = "[" + key + "]";
					if (text.contains(keyV)) {// 判断段落是否含有关键字
						result = true;
						break;
					}
				}
			}
		}
		return result;
	}

	private static String checkIsInsertTable(String text, List<String> keyList) {
		String result = "";
		if (CollectionUtils.isNotEmpty(keyList)) {
			if (text.contains("$") && text.contains("{")) {
				for (String key : keyList) {
					String keyV = "${" + key + "}";
					if (text.contains(keyV)) {// 判断段落是否含有关键字
						result = key;
						break;
					}
				}
			}
		}
		return result;
	}

	/**
	 * @Description ：替换段落里面匹配的key对应的value值
	 *
	 */
	@SuppressWarnings("deprecation")
	private static void replaceParagraphByKey(XWPFParagraph paragraph, Map<String, TestTemplateDictDO> textMap,
			List<String> keyList) {
		if (CollectionUtils.isNotEmpty(keyList) && MapUtils.isNotEmpty(textMap)) {
			List<XWPFRun> runs = paragraph.getRuns();
			String text = paragraph.getText();// 段落标签
			for (String key : keyList) {
				String keyV = "[" + key + "]";
				if (text.contains(keyV)) {// 判断段落是否含有关键字
					int startPostion = 0;
					int endPostion = 0;
					int runSize = runs.size();
					XWPFRun updateRun = null;
					boolean isReplace = false;
					for (int iRun = 0; iRun < runSize; iRun++) {// 一个单词可能拆分成几个run (报告时间：, [txt, T, ime])，所以先删除run,再拆入run
						XWPFRun run = runs.get(iRun);
						if (runSize==1&&run.getParagraph().getText().contains(keyV)) {
							isReplace = true;
						}
						if (run.toString().contains("[")) {
							startPostion = iRun;
						}
						if (run.toString().contains("]")) {
							endPostion = iRun;
							break;
						}
					}
					if (startPostion <= endPostion) {
						for (int pos = startPostion; pos <= endPostion; pos++) {
							paragraph.removeRun(startPostion); // 删除run
						}
						updateRun = paragraph.insertNewRun(startPostion);
					}

					TestTemplateDictDO testTemplateDictDO = (TestTemplateDictDO) textMap.get(key);
					if (testTemplateDictDO != null) {
						if (testTemplateDictDO.getDateType().equals(1)) {// 文本
							if (!isReplace) {
								updateRun.setText(testTemplateDictDO.getValue());// 插入文本
							} else {
								String textV = text.replace(keyV, testTemplateDictDO.getValue());
								updateRun.setText(textV);// 插入文本
							}
						} else if (testTemplateDictDO.getDateType().equals(2)) {// 图片
							updateRun.setText("");// 插入文本
							insertImage(testTemplateDictDO, runs.get(startPostion));
						} else if (testTemplateDictDO.getDateType().equals(3)) {// 文件
							String value = testTemplateDictDO.getValue();// D://sampleTemplate//管理平台工作交接.pdf
							String fileName = FileUtil.getFileName(value);// 文件名称 (管理平台）
							String outputFilePath = testTemplateDictDO.getOutputFilePath();
							int totalPicNum = 0;// 生成jpg图片
							if (testTemplateDictDO.getStartPage() != null && testTemplateDictDO.getEndPage() != null) {
								totalPicNum = PdfUtils.pdf2Image(value, outputFilePath, fileName,
										testTemplateDictDO.getStartPage(), testTemplateDictDO.getEndPage(), "jpg");// 生成jpg图片
							} else {
								totalPicNum = PdfUtils.pdf2Image(value, outputFilePath, fileName, "jpg");// 生成jpg图片
							}
							if (totalPicNum >= 1) {
								for (int i = 1; i <= totalPicNum; i++) {
									String picFilePath = outputFilePath + "\\" + fileName + "_" + (i) + ".jpg";
									XWPFRun run = paragraph.createRun();
									insertPdf2Image(picFilePath, fileName + "_" + (i), run);
									FileUtil.deleteFile(picFilePath);
								}
							}
						}
					}
				}
			}
		}
	}

	/**
	 * 替换段落文本
	 * 
	 * @param document docx解析对象
	 * @param textMap  需要替换的信息集合
	 */
	private static void changeTextByKey(XWPFDocument document, Map<String, TestTemplateDictDO> textMap,
			List<String> keyList) {
		if (CollectionUtils.isNotEmpty(keyList) && MapUtils.isNotEmpty(textMap)) {
			// 获取段落集合
			List<XWPFParagraph> paragraphs = document.getParagraphs();
			for (XWPFParagraph paragraph : paragraphs) {
				if (!StringUtils.isEmpty(paragraph.getText())) {
					replaceParagraphByKey(paragraph, textMap, keyList);
				}
			}
		}
	}

	public static void createPicture(String blipId, int id, int width, int height, XWPFRun run) {
		final int EMU = 9525;
		width *= EMU;
		height *= EMU;
		CTInline inline = run.getCTR().addNewDrawing().addNewInline();

		String picXml = "" + "<a:graphic xmlns:a=\"http://schemas.openxmlformats.org/drawingml/2006/main\">"
				+ "   <a:graphicData uri=\"http://schemas.openxmlformats.org/drawingml/2006/picture\">"
				+ "      <pic:pic xmlns:pic=\"http://schemas.openxmlformats.org/drawingml/2006/picture\">"
				+ "         <pic:nvPicPr>" + "            <pic:cNvPr id=\"" + id + "\" name=\"Generated\"/>"
				+ "            <pic:cNvPicPr/>" + "         </pic:nvPicPr>" + "         <pic:blipFill>"
				+ "            <a:blip r:embed=\"" + blipId
				+ "\" xmlns:r=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships\"/>"
				+ "            <a:stretch>" + "               <a:fillRect/>" + "            </a:stretch>"
				+ "         </pic:blipFill>" + "         <pic:spPr>" + "            <a:xfrm>"
				+ "               <a:off x=\"0\" y=\"0\"/>" + "               <a:ext cx=\"" + width + "\" cy=\""
				+ height + "\"/>" + "            </a:xfrm>" + "            <a:prstGeom prst=\"rect\">"
				+ "               <a:avLst/>" + "            </a:prstGeom>" + "         </pic:spPr>"
				+ "      </pic:pic>" + "   </a:graphicData>" + "</a:graphic>";

		// CTGraphicalObjectData graphicData =
		// inline.addNewGraphic().addNewGraphicData();
		XmlToken xmlToken = null;
		try {
			xmlToken = XmlToken.Factory.parse(picXml);
		} catch (XmlException xe) {
			xe.printStackTrace();
		}
		inline.set(xmlToken);
		inline.setDistT(0);
		inline.setDistB(0);
		inline.setDistL(0);
		inline.setDistR(0);

		CTPositiveSize2D extent = inline.addNewExtent();
		extent.setCx(width);
		extent.setCy(height);

		CTNonVisualDrawingProps docPr = inline.addNewDocPr();
		docPr.setId(id);
		docPr.setName("Picture " + id);
		docPr.setDescr("Generated");
	}

	@SuppressWarnings("hiding")
	public static void insertPdf2Image(String fileUrl, String fileName, XWPFRun run) {
		run.addBreak();
		InputStream inputStream = null;
		try {
			inputStream = new FileInputStream(fileUrl);
			// run.addPicture(inputStream, Document.PICTURE_TYPE_JPEG, fileName,
			// Units.toEMU(420), Units.toEMU(620));
			String picId = run.getDocument().addPictureData(inputStream, XWPFDocument.PICTURE_TYPE_PNG);
			createPicture(picId, run.getDocument().getNextPicNameNumber(XWPFDocument.PICTURE_TYPE_PNG), 580, 870, run);
		} catch (InvalidFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (inputStream != null) {
					inputStream.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		// run.addBreak(BreakType.PAGE);
	}

	public static void insertImage(TestTemplateDictDO testTemplateDictDO, XWPFRun run) {
		run.addBreak();
		String fileUrl = testTemplateDictDO.getValue();// 图片真实路径
		String[] fileNameArray = fileUrl.split("\\/");
		int length = fileNameArray.length;
		String fileName = fileNameArray[length - 1];// 文件名称
		InputStream inputStream = null;
		try {
			inputStream = new FileInputStream(fileUrl);
			run.addPicture(inputStream, Document.PICTURE_TYPE_JPEG, fileName,
					Units.toEMU(testTemplateDictDO.getPicWidth()), Units.toEMU(testTemplateDictDO.getPicHeight()));
		} catch (InvalidFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (inputStream != null) {
					inputStream.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} // 200x200 pixels
			// run.addBreak(BreakType.PAGE);
	}

	/**
	 * @Description ：查找word模板里面需要维护的word关键字
	 * @return
	 * @throws IOException
	 */
	public static List<String> serchTemplateDictKey(String inputPath, List<String> keyList) {
		List<String> resultList = new ArrayList<String>();
		if (CollectionUtils.isNotEmpty(keyList) && !StringUtils.isEmpty(inputPath)) {
			String text = "";
			try {
				if (checkVersionIsHigh(inputPath)) {
					text = contextOfDocx(inputPath);
				} else {
					text = contextOfDoc(inputPath);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// System.out.println(text);
			for (String key : keyList) {
				String keyV = "[" + key + "]";
				if (text.contains(keyV)) {// 判断段落是否含有关键字
					resultList.add(key);
				}
			}
		}
		return resultList;
	}

	/**
	 * @Description ：查找word模板里面需要维护的word关键字
	 * @return
	 * @throws IOException
	 */
	public static R serchTableInfo(String inputPath, String key) {
		List<String> resultList = null;
		if (!StringUtils.isEmpty(key) && !StringUtils.isEmpty(inputPath)) {
			String[] strings = inputPath.split("\\.");
			if (!strings[1].equals("docx")) {// 不是docx不解析
				return R.error("不是docx文件");
			}
			XWPFDocument document = null;
			try {
				// 获取docx解析对象
				document = new XWPFDocument(POIXMLDocument.openPackage(inputPath));
				List<XWPFTable> tables = document.getTables();
				if (CollectionUtils.isNotEmpty(tables)) {
					for (XWPFTable table : tables) {
						String tableKey = "${" + key;
						if (table.getText().contains(tableKey)) {
							// 查找
							List<XWPFTableRow> rows = table.getRows();// 查找表头那行
							int tableIndx = -1;
							for (int i = 0; i < rows.size(); i++) {// 查找含有key的那行，上一行就是表头
								String text = rows.get(i).getCell(0).getText();
								if (text.contains(tableKey)) {
									tableIndx = i - 1;
									break;
								}
							}
							if (tableIndx >= 0) {
								resultList = new ArrayList<String>();
								XWPFTableRow row = rows.get(tableIndx);
								List<XWPFTableCell> cells = row.getTableCells();
								for (XWPFTableCell cell : cells) {
									resultList.add(cell.getText());// 表头信息
								}
							}
							break;
						}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		Map<String, Object> map = new HashMap<>();
		map.put("resultList", resultList);
		return R.ok(map);
	}

	/**
	 * @Description ：判断是否是高版本的word
	 * @param filepath
	 * @return
	 * @throws IOException
	 */
	public static boolean checkVersionIsHigh(String filePath) {
		InputStream input = null;
		InputStream is = null;
		try {
			input = new FileInputStream(filePath);
			is = FileMagic.prepareToCheckMagic(input);
			FileMagic fm = FileMagic.valueOf(is);
			if (fm != FileMagic.OLE2) {
				return true;
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return false;
	}

	private static String contextOfDoc(String inputPath) throws IOException {
		String str = "";
		HWPFDocument doc = null;
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(new File(inputPath));
			doc = new HWPFDocument(fis);
			str = doc.getDocumentText();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			doc.close();
			fis.close();
		}
		return str;
	}

	private static String contextOfDocx(String inputPath) throws IOException {
		String str = "";
		XWPFWordExtractor extractor = null;
		FileInputStream fis = null;
		XWPFDocument xdoc = null;
		try {
			fis = new FileInputStream(new File(inputPath));
			xdoc = new XWPFDocument(fis);
			extractor = new XWPFWordExtractor(xdoc);
			str = extractor.getText();
			extractor.close();
			fis.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			extractor.close();
			fis.close();
			xdoc.close();
		}
		return str;
	}

	public static void main(String[] args) throws Exception {
		// 模板文件地址
		String inputUrl = "d:\\sampleTemplate\\坏模板.docx";
		// inputUrl = "d:\\sampleTemplate\\704报告模板.doc";
		// 新生产的模板文件
		String outputUrl = "d:\\sampleTemplate\\2.docx";

		Map<String, TestTemplateDictDO> testMap = new HashMap<String, TestTemplateDictDO>();
		TestTemplateDictDO testTemplateDictDO = new TestTemplateDictDO();
		testTemplateDictDO.setLabelKey("name");
		testTemplateDictDO.setDateType(1);
		testTemplateDictDO.setValue("2019-1-1");
		TestTemplateDictDO testTemplateDictDO1 = new TestTemplateDictDO();
		testTemplateDictDO1.setLabelKey("sex");
		testTemplateDictDO1.setDateType(1);
		testTemplateDictDO1.setValue("ss测试");
		TestTemplateDictDO testTemplateDictDO2 = new TestTemplateDictDO();
		testTemplateDictDO2.setLabelKey("txtLimitName");
		testTemplateDictDO2.setDateType(1);
		testTemplateDictDO2.setValue("哈哈哈哈");
		TestTemplateDictDO testTemplateDictDO3 = new TestTemplateDictDO();
		testTemplateDictDO3.setLabelKey("txtImage");
		testTemplateDictDO3.setValue("c:/timg.jpg");
		testTemplateDictDO3.setPicHeight(200);
		testTemplateDictDO3.setPicWidth(200);
		testTemplateDictDO3.setDateType(2);
		TestTemplateDictDO testTemplateDictDO4 = new TestTemplateDictDO();
		testTemplateDictDO4.setLabelKey("pdf");
		testTemplateDictDO4.setValue("D://sampleTemplate//管理平台工作交接.pdf");
		testTemplateDictDO4.setOutputFilePath("D://sampleTemplate");
		testTemplateDictDO4.setPicHeight(PageSize.A4.getHeight() - 2);
		testTemplateDictDO4.setPicWidth(PageSize.A4.getWidth() - 2);
		testTemplateDictDO4.setDateType(3);// 文件

		TestTemplateDictDO testTemplateDictDO5 = new TestTemplateDictDO();// 表格

		// [["1","1AA","1BB","1CC"],["2","2AA","2BB","2CC"],["3","3AA","3BB","3CC"],["4","4AA","4BB","4CC"]]
		String value = "[[\"1\",\"1AA\",\"1BB\",\"1CC\"],[\"2\",\"2AA\",\"2BB\",\"2CC\"],[\"3\",\"3AA\",\"3BB\",\"3CC\"],[\"4\",\"4AA\",\"4BB\",\"4CC\"]]";//
		value = "[{\"Test Condition\":\"dd\",\"Voltage\":\"dd\",\"Frequency\":\"dd\",\"Time\":\"dd\",\"Pass/Fail\":\"dd\"},{\"Test Condition\":\"22\",\"Voltage\":\"33\",\"Frequency\":\"44\",\"Time\":\"44\",\"Pass/Fail\":\"44\"}]";
		testTemplateDictDO5.setLabelKey("tab1");
		testTemplateDictDO5.setValue(value);
		testTemplateDictDO5.setDateType(4);// 表格

		testMap.put(testTemplateDictDO.getLabelKey(), testTemplateDictDO);
		testMap.put(testTemplateDictDO1.getLabelKey(), testTemplateDictDO1);
		testMap.put(testTemplateDictDO2.getLabelKey(), testTemplateDictDO2);
		testMap.put(testTemplateDictDO3.getLabelKey(), testTemplateDictDO3);
		testMap.put(testTemplateDictDO4.getLabelKey(), testTemplateDictDO4);
		testMap.put(testTemplateDictDO5.getLabelKey(), testTemplateDictDO5);

		List<String[]> testList = new ArrayList<String[]>();
		testList.add(new String[] { "1", "1AA", "1BB", "1CC" });
		testList.add(new String[] { "2", "2AA", "2BB", "2CC" });
		testList.add(new String[] { "3", "3AA", "3BB", "3CC" });
		testList.add(new String[] { "4", "4AA", "4BB", "4CC" });
		// System.out.print(JSON.toJSONString( testList));

		// DocxToNewWordUtils.changWord(inputUrl, outputUrl, testMap, testList);
		List<String> keyList = new ArrayList<>();
		keyList.add("txtTime");
		keyList.add("txtOperator");
		keyList.add("txtLimitName");
		keyList.add("txtMode");
		keyList.add("txtImage");
		keyList.add("pdf");
		keyList.add("tab1");
		keyList.add("name");
		keyList.add("sex");
		WordUtils.changDocxByKeyList(inputUrl, outputUrl, testMap, keyList);
		// 模板文件地址
		// inputUrl = "d:\\sampleTemplate\\704报告模板.doc";
		// 新生产的模板文件
		// outputUrl = "d:\\sampleTemplate\\22.doc";

		// WordUtils.changDocByKeyList(inputUrl, outputUrl, testMap, keyList);
		// WordUtils.serchTemplateDictKey(inputUrl, keyList);

		// [["1","1AA","1BB","1CC"],["2","2AA","2BB","2CC"],["3","3AA","3BB","3CC"],["4","4AA","4BB","4CC"]]

		/*
		 * String jsonStr =
		 * "	[{\"Test Condition\":\"dd\",\"Voltage\":\"dd\",\"Frequency\":\"dd\",\"Time\":\"dd\",\"Pass/Fail\":\"dd\"},{\"Test Condition\":\"22\",\"Voltage\":\"33\",\"Frequency\":\"44\",\"Time\":\"44\",\"Pass/Fail\":\"44\"}]"
		 * ; List<Object> objects = JSON.parseArray(jsonStr , Object.class);
		 * System.out.print(objects); Object object = objects.get(0); Map map =
		 * (Map)object; String Voltage = (String) map.get("Voltage");
		 * System.out.print(Voltage);
		 */
	}

}

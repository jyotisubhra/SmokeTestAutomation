import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.tools.ant.DirectoryScanner;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class ExcelReportGenerator {

	public static void generateExcel() throws ParserConfigurationException, SAXException, IOException {
		//String path = ExcelReportGenerator.class.getClassLoader().getResource("./").getPath();
		String path = "C:/Workspace/AlertAutomation/SmokeTestAutomation/target/classes/";
		path= path.replaceAll("target/classes", "TestSuite");
		System.out.println(path);
		
		
		DirectoryScanner scanner = new DirectoryScanner();
		scanner.setIncludes(new String[]{"TEST-*.xml"});
		scanner.setBasedir(path);
		scanner.setCaseSensitive(false);
		scanner.scan();
		String[] files = scanner.getIncludedFiles();
		
		XSSFWorkbook book = new XSSFWorkbook();
		
		for (int j = 0; j < files.length; j++) {
			boolean firstRow = true;
			
			String xmlFileName = files[j];
			System.out.println(xmlFileName);
			
			File xmlFile = new File(path + xmlFileName);
			
			DocumentBuilderFactory fact = DocumentBuilderFactory.newInstance();
			DocumentBuilder build = fact.newDocumentBuilder();
			Document doc = build.parse(xmlFile);
			doc.getDocumentElement().normalize();
			
			NodeList node_list = doc.getElementsByTagName("testsuite");
			Node testSuite = node_list.item(0);
			String testSuiteName = ((Element)testSuite).getAttribute("name");
			XSSFSheet sheet = book.createSheet(testSuiteName);
			
			NodeList testCaseList = ((Element)testSuite).getElementsByTagName("testcase");
			int r = 0;
			for (int i = 0; i < testCaseList.getLength(); i++) {
				
				
				//This is for first Row of the sheet, will be executed only once
				if (firstRow) {
					
					XSSFRow row = sheet.createRow(r++);
					
					XSSFCell first_cell_name = row.createCell(0);
					first_cell_name.setCellValue("TestCaseName");
					
					XSSFCell first_cell_time = row.createCell(1);
					first_cell_time.setCellValue("Execution Time");
					
					XSSFCell first_cell_Status = row.createCell(2);
					first_cell_Status.setCellValue("Status");
					
					XSSFCell first_cell_Reason = row.createCell(3);
					first_cell_Reason.setCellValue("Failure Reason");
				}
				firstRow = false;
				
				
				XSSFRow row = sheet.createRow(r++);
				
				Node testCase = testCaseList.item(i);
				String testCaseName = ((Element)testCase).getAttribute("name");
				String testCaseTime = ((Element)testCase).getAttribute("time");
				
				Node failure = ((Element)testCase).getElementsByTagName("failure").item(0);				
				
				XSSFCell cell_name = row.createCell(0);
				cell_name.setCellValue(testCaseName);
				
				XSSFCell cell_time = row.createCell(1);
				cell_time.setCellValue(testCaseTime);
				
				XSSFCell cell_Status = row.createCell(2);			
				XSSFCell cell_Reason = row.createCell(3);		
				
				if (null != failure) {
					String failureTypeName = ((Element)failure).getAttribute("type");
					
					Element e = (Element) failure;
					Node child = e.getFirstChild();
					
					cell_Status.setCellValue("FAILED");
					cell_Reason.setCellValue(child.getNodeValue());
				} else {
					cell_Status.setCellValue("SUCCESS");
					cell_Reason.setCellValue("");
				}
			}
		}
		
		path = path.replaceAll("TestSuite", "target/report");
		if (!new File(path).isDirectory()) {
			boolean status = new File(path).mkdir();
			System.out.println("Report Directory Not Exist!, Created: " + status);
		}
		FileOutputStream fout = new FileOutputStream(path + "/" + "report.xls");
		book.write(fout);
		fout.close();
		System.out.println("Excel File Generated");
		
	}
	public static void main(String[] args) {
		try {
			generateExcel();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
}

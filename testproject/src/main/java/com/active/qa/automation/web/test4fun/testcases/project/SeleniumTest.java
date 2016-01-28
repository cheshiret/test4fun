package com.active.qa.automation.web.test4fun.testcases.project;

/**
 * Created by tchen on 1/28/2016.
 */
public class SeleniumTest {

  public static void Test() {
//			String url = "https://orms-torqa5.dev.activenetwork.com/IndexOfLinks.do";
//			System.setProperty("webdriver.ie.driver", "C:\\AWO_QA\\Migrtest\\Migrtest_core\\java\\com\\activenetwork\\qa\\testdriver\\driver\\selenium\\IEDriverServer.exe");
////			C:\AWO_QA\Migrtest\Migrtest_core\java\com\activenetwork\qa\testdriver\driver\selenium\IEDriverServer.exe;
//			WebDriver driver = new InternetExplorerDriver();
//            driver.get(url);
////            System.out.println(MA_PageUtil.getPageSource(driver));
//            String source = MA_PageUtil.getPageSource(driver);
//            MA_PageUtil.getUrlText(source,"listView");
//			driver.close();

//			String str = "{0,number,00000000}{1}";
//			Object[] array = new Object[] { 100000253, 8 };
//			String value = MessageFormat.format(str, array);
//			System.out.println(value); // A | B | A | B

    //encrypt string
//			System.out.println(CryptoUtil.encrypt("1234567q"));

    //
    System.out.println("Test Contract".replace(" Contract", "").toUpperCase());

  }

}

//		public static boolean selectWindow(WebDriver driver, String windowTitle){
//			//Search ALL currently available windows
//			for (String handle : driver.getWindowHandles()) {
//				String newWindowTitle = driver.switchTo().window(handle).getTitle();
//				if(newWindowTitle.equalsIgnoreCase(windowTitle))
//					//if it was found break out of the wait
//					return true;
//			}
//			return false;
//
//		}

//
//		public static void switchBrowser(WebDriver driver){
//			try {
//				String currentHandler= driver.getWindowHandle();
//				Set<String> handlers = driver.getWindowHandles();
//				for(String handler: handlers) {
//					if(!handler.equals(currentHandler)) {
//						driver.switchTo().window(handler);
//					} else {
//						continue;
//				}
//				}
//			} catch (Exception e) {}
//
//		}

//		WebDriver switchTo(String... handlers) {
//			WebDriver driver=null;
//			for(String h: handlers) {
//				if(h.startsWith("frame:")) {
//					String identifier=h.substring(6);
//					if(identifier.startsWith("index=")) {
//						String index=identifier.substring(6);
//						driver= driver.switchTo().frame(Integer.parseInt(index));
//					} else {
//						int i=identifier.indexOf("=");
//						driver=driver.switchTo().frame(identifier.substring(i+1));
//					}
//
//				} else {
//					driver= driver.switchTo().window(h);
//				}
//			}
//
//			return driver;
//		}

//		public static void main(String[] args){
//			SearchBaidu();
//		}
//			read1();
//			System.exit(0);

//			SearchBaidu();
//			System.out.println(selectWindow(driver, "iquicktest_test"));
//			System.out.println(driver.getTitle());
//			System.out.println(driver.getWindowHandles());



//		//XML Parsing
//
//		public static void read1() throws IOException {
//	        try {
//	            SAXReader reader = new SAXReader();
//	            InputStream in = new FileInputStream("C://Users//QA//Documents//ClassycleAnalyse_testdriver.xml");
//	            Document doc = reader.read(in);
//	            Element root = doc.getRootElement();
//	            readNode(root, "");
//	            in.close();
//	        } catch (DocumentException e) {
//	            e.printStackTrace();
//	        }
//	    }
//
//	    @SuppressWarnings("unchecked")
//	    public static void readNode(Element root, String prefix) {
//	        if (root == null) return;
//	        // ????
//	        List<Attribute> attrs = root.attributes();
//	        if (attrs != null && attrs.size() > 0) {
//	            System.err.print(prefix);
//	            for (Attribute attr : attrs) {
//	                System.err.print(attr.getValue() + " ");
//	            }
//	            System.err.println();
//	        }
//	        // ???????
//	        List<Element> childNodes = root.elements();
//	        prefix += "\t";
//	        for (Element e : childNodes) {
//	            readNode(e, prefix);
//	        }
//	    }

//	    public static void read2() {
//	        try {
//	            SAXReader reader = new SAXReader();
//	            InputStream in = SeleniumTest.class.getClassLoader().getResourceAsStream("ClassycleAnalyse_testdriver.xml");
//	            Document doc = reader.read(in);
//	            doc.accept(new MyVistor());
//	        } catch (DocumentException e) {
//	            e.printStackTrace();
//	        }
//	    }

//	    public static void write() {
//	        try {
//	            // ????xml??
//	            Document doc = DocumentHelper.createDocument();
//	            Element university = doc.addElement("university");
//	            university.addAttribute("name", "tsu");
//	            // ??
//	            university.addComment("??????");
//	            Element college = university.addElement("college");
//	            college.addAttribute("name", "cccccc");
//	            college.setText("text");
//
//	            File file = new File("src/dom4j-modify.xml");
//	            if (file.exists()) {
//	                file.delete();
//	            }
//	            file.createNewFile();
//	            XMLWriter out = new XMLWriter(new FileWriter(file));
//	            out.write(doc);
//	            out.flush();
//	            out.close();
//	        } catch (IOException e) {
//	            e.printStackTrace();
//	        }
//	    }
//}

//class MyVistor extends VisitorSupport {
//	    public void visit(Attribute node) {
//	        System.out.println("Attibute: " + node.getName() + "="
//	                + node.getValue());
//	    }
//
//	    public void visit(Element node) {
//	        if (node.isTextOnly()) {
//	            System.out.println("Element: " + node.getName() + "="
//	                    + node.getText());
//	        } else {
//	            System.out.println(node.getName());
//	        }
//	    }
//
//	    @Override
//	    public void visit(ProcessingInstruction node) {
//	        System.out.println("PI:" + node.getTarget() + " " + node.getText());
//	    }


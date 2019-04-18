package nc.jzmobile.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.bs.framework.common.RuntimeEnv;
import nc.bs.logging.Logger;
import nc.vo.jzmobile.app.BillTypeModel;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 * xml解析管理类
 * @author liuhm
 *
 */
public class BillTypeModelTrans {
	private static BillTypeModelTrans factory;

	public static final String CONFIG_PATH = RuntimeEnv.getInstance().getNCHome()+"/resources/jzmobile/";

	private BillTypeModelTrans() {

	}

	public static BillTypeModelTrans getInstance() {
		if (factory == null) {
			factory = new BillTypeModelTrans();
		}
		return factory;

	}
	//获取home/resources/jzmobile路径下的.xml文件
	public List<String> getMobileApproveBillTypeList(){
		List<String> billTypeList = new ArrayList<String>();
		File xmlFoder = new File(CONFIG_PATH);
		if(xmlFoder!=null&&xmlFoder.exists()){
			for(File xml : xmlFoder.listFiles()){
				if(xml.getName().endsWith("xml")){
					billTypeList.add(xml.getName().substring(0, xml.getName().length() - 4));
				}
			}
		}
		return billTypeList;
	}
	//获取特定单据类型的xml文件
	public BillTypeModel getModelByBillType(String billType) throws Exception{
		File configFile = new File(CONFIG_PATH + billType + ".xml");
		if (!configFile.exists()){
			Logger.error("没有找到配置文件："+CONFIG_PATH + billType + ".xml");
			throw new Exception("没有找到配置文件：" + billType + ".xml");
		}
		return parseXml(configFile);
	}
	
	/**
	 * 解析xml
	 * @throws Exception 
	 */
	private BillTypeModel parseXml(File file) throws Exception {
		BillTypeModel model = new BillTypeModel();
		
		Document document = null;  
		FileInputStream fis = null;  
		try {
			fis = new FileInputStream(file);  
		    SAXReader reader = new SAXReader();  
		    document = reader.read(fis);  

			Element billType = document.getRootElement();
//			List<Element> transTypes = billType.element("transTypes") == null ? new ArrayList<Element>() : billType.element("transTypes").elements("transType");
//			List<Element> muiltBodyVos = billType.element("muiltBodyVos") == null ? new ArrayList<Element>() : billType.element("muiltBodyVos").elements("muiltBodyVo");
			
			model.setBillType(billType.attributeValue("id"));
			model.setTransToType(billType.attributeValue("transToType"));
			//添加自定义title读取 mxx
			model.setTitle(billType.attributeValue("title"));
//			for (Element transType : transTypes) {
//				model.getBillTypeMapping().put(transType.attributeValue("fromType"), transType.attributeValue("toType"));
//			}
//			
//			for (Element muiltBodyVo : muiltBodyVos) {
//				if (muiltBodyVo.getText() != null && muiltBodyVo.getText().length() > 0) {
//					model.getMuiltbodyVoList().add(muiltBodyVo.getText());
//				}
//			}
		} catch (Exception e) {
			e.printStackTrace();
			Logger.error("读取配置文件："+CONFIG_PATH + file.getName() + " 失败:"+e.getMessage());
			throw new Exception("读取配置文件："+CONFIG_PATH + file.getName() + " 失败:"+e.getMessage());
		} finally {  
		    if(fis != null) {  
		        try {  
		            fis.close();  
		        } catch (IOException e) {  
		            e.printStackTrace();
		        }  
		    }  
		}  

		return model;
	}

}

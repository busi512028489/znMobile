package nc.vo.jzmobile.app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.cmp.tools.StringUtil;

import com.google.zxing.common.StringUtils;

public class BillTypeModel {
	private String billType = null;  //��������
	
	private String transToType=null;  //���ݵĽ�������
	
	//����Զ���title mxx
	private String title=null;      //�Զ������
	
//	private Map<String, String> billTypeMapping = new HashMap<String, String>();
	
//	private List<String> muiltbodyVoList = new ArrayList<String>();
	
	public String getTitle(){
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	

	public String getBillType() {
		return billType;
	}

	public void setBillType(String billType) {
		this.billType = billType;
	}

	public String getTransToType() {
		return transToType;
	}

	public void setTransToType(String transToType) {
		this.transToType = transToType;
	}
	
	public String getBillTypeCode(){
		if(!StringUtil.isEmpty(transToType))
			return transToType;
		return billType;
	}

//	public Map<String, String> getBillTypeMapping() {
//		return billTypeMapping;
//	}
//
//	public void setBillTypeMapping(Map<String, String> billTypeMapping) {
//		this.billTypeMapping = billTypeMapping;
//	}
//
//	public List<String> getMuiltbodyVoList() {
//		return muiltbodyVoList;
//	}
//
//	public void setMuiltbodyVoList(List<String> muiltbodyVoList) {
//		this.muiltbodyVoList = muiltbodyVoList;
//	}
}

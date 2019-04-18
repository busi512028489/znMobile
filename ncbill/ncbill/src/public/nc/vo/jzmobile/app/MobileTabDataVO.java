package nc.vo.jzmobile.app;

import java.io.Serializable;

/**
 * 
 * @author wanghui
 * @TODO  如下示例
  "colname": "申请日期",
  "colvalue": "2016-05-10",
  "colkey": "applydate"
 * 
 */
public class MobileTabDataVO implements Serializable{

	private String colkey;
	
	private String colname;
	
	private Object colvalue;
	
	private Object colPkvalue;
	
	
	public Object getColPkvalue() {
		return colPkvalue;
	}

	public void setColPkvalue(Object colPkvalue) {
		this.colPkvalue = colPkvalue;
	}

	public String getColkey() {
		return colkey;
	}

	public void setColkey(String colkey) {
		this.colkey = colkey;
	}

	public String getColname() {
		return colname;
	}

	public void setColname(String colname) {
		this.colname = colname;
	}

	public Object getColvalue() {
		return colvalue;
	}

	public void setColvalue(Object colvalue) {
		this.colvalue = colvalue;
	}

	
}

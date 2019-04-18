package nc.impl.approvalweb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.bs.framework.common.NCLocator;
import nc.itf.uap.IUAPQueryBS;
import nc.itf.uap.pf.IPFMobileAppServiceFacade;
import nc.jdbc.framework.processor.ArrayProcessor;
import nc.jdbc.framework.processor.ColumnProcessor;
import nc.vo.jzmobile.app.BillTypeModel;
import nc.vo.pub.BusinessException;

public class Queryutils {
	BaseDAO dao = new BaseDAO();
	private IUAPQueryBS uap;
	private IUAPQueryBS getUap()
	{
		if(uap==null)
			uap=(IUAPQueryBS) NCLocator.getInstance().lookup(IUAPQueryBS.class);
		return uap;
	}
	public Object[] queryCode(String sql) throws BusinessException {
		return (Object[]) getUap().executeQuery(sql, new ArrayProcessor());
	}
	
	public String huoqu (BillTypeModel model) throws BusinessException{
//		String sqlStr = "select pk_billtemplet from pub_billtemplet where pk_billtypecode = '"+model.getBillTypeCode()+"' and bill_templetname != 'SYSTEM'";
		String sqlStr = "select pk_billtypeid from bd_billtype where pk_billtypecode = '"+model.getBillTypeCode()+"'";
		Object[] vopk = queryCode(sqlStr);
		if (vopk[0].toString() == null || "".equals(vopk[0].toString())) {
			return "";
		}
		String pk_billtemplet = vopk[0].toString();
		return pk_billtemplet;
	}
	
	public List getbillpk(String psncode) throws BusinessException{
		IPFMobileAppServiceFacade pf =NCLocator.getInstance().lookup(IPFMobileAppServiceFacade.class);
		List<Map<String, Object>> alist = pf.getTaskList("0001A110000000000AUG", getCuserid(psncode), null, null, null, "", 1, 100);
		HashMap submap = new HashMap();
		List pklist =new ArrayList();
		if(alist!=null && alist.size()>0){
			List list=(List)alist.get(0).get("taskstructlist");
			if(list!=null && list.size()>0){
				for(int i=0;i<list.size();i++){
					HashMap amap =(HashMap) list.get(i);
					pklist.add(amap.get("taskid")==null?"":amap.get("taskid").toString());
					submap.put(amap.get("taskid")==null?"":amap.get("taskid").toString(), amap.get("title")==null?"":amap.get("title").toString());
				}
			}else{
				return null;
			}
		}else{
			return null;
		}
		return pklist;
//		String insql="";
//		if(pklist!=null && pklist.size()>0){
//			insql = this.getInStr(pklist, 0, pklist.size());
//		}else{
//			return "";
//		}
//		return insql;
	}
	
	private static String getInStr( List pks, int start,
			int end) {
		int m_start = start;
		int m_end = end;
		start = Math.min(m_start, m_end);
		end = Math.max(m_start, m_end);
		StringBuffer sb = new StringBuffer();
		sb.append(" in (");
		String key = null;
		for (int i = start; i < pks.size(); i++) {
			String pk =(String) pks.get(i);
			if (i > end) {
				break;
			}
			if (pk == null)
				continue;
			key = pk.trim();
			sb.append("'");
			sb.append(key);
			sb.append("',");
		}
		String inStr = sb.substring(0, sb.length() - 1) + ") ";
		return inStr;
	}
	
	public String getCuserid(String psncode){
		String sql = " select cuserid from sm_user left join bd_psndoc on sm_user.pk_base_doc=bd_psndoc.pk_psndoc where bd_psndoc.code='"+psncode+"' ";
		try {
			Object cuserid = dao.executeQuery(sql, new ColumnProcessor());
			if(cuserid==null || "".equals(cuserid)){
				return null;
			}else{
				return cuserid.toString();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	public String telipk(List pklist) throws DAOException {
		String a = "";
		for (int i = 0; i < pklist.size(); i++) {
			String sql = "select pk_billtype from pub_workflownote where pk_checkflow = '"+pklist.get(i).toString()+"'";
			Object pk_billtype = dao.executeQuery(sql, new ColumnProcessor());
			if (pk_billtype.toString() == "2641" || "2641".equals(pk_billtype.toString())) {
				a = pklist.get(i).toString();
				break;
			}
		}
		String sql = "select billid from pub_workflownote where pk_checkflow = '"+a+"'";
		Object billid = dao.executeQuery(sql, new ColumnProcessor());
		return billid.toString();
	}
	public HashMap a(List pklist) throws DAOException {
		HashMap<String , String> map = new HashMap<String , String>();
		for (int i = 0; i < pklist.size(); i++) {
			String sql = "select pk_billtype from pub_workflownote where pk_checkflow = '"+pklist.get(i).toString()+"'";
			Object pk_billtype = dao.executeQuery(sql, new ColumnProcessor());
			if (map.get(pk_billtype) == null || "".equals(map.get(pk_billtype))) {
				map.put(pk_billtype.toString(), pklist.get(i).toString());
			}
		}
		return map;
	}
}

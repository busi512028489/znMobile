package nc.impl.approvalweb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import nc.bs.framework.common.NCLocator;
import nc.itf.approvalweb.Approvalweb;
import nc.itf.uap.pf.IPFConfig;
import nc.jzmobile.bill.data.access.NCBillAccessBillTemplate;
import nc.jzmobile.bill.data.access.NCBillTemplate;
import nc.jzmobile.bill.data.access.PubBillTempletModel;
import nc.jzmobile.bill.util.BillMetaUtil;
import nc.jzmobile.bill.util.BillTempletUtil;
import nc.jzmobile.utils.BillTypeModelTrans;
import nc.vo.jzmobile.app.BillTypeModel;
import nc.vo.jzmobile.app.MobileBillData;
import nc.vo.pub.AggregatedValueObject;

public class Approvalwebimpl implements Approvalweb{
	
	public String getCode(String billtype) {
		//加载xml解析类
		BillTypeModelTrans getbilltype = BillTypeModelTrans.getInstance();
		//自定义工具类
		Queryutils qry = new Queryutils();
		try {
			//1.获取home/resources/jzmobile路径下的.xml文件
			List<String> allbilltype = getbilltype.getMobileApproveBillTypeList();
			//2.判断是否存在
			if (!allbilltype.contains(billtype)) {
				return "该单据类型不存在";
			}
			//3.根据单据类型获取xml的有效信息
			BillTypeModel model = getbilltype.getModelByBillType(billtype);
			//4.获取单据模板pk,单据pk
			String pk_billtemplet = qry.huoqu(model);
			List pklist = qry.getbillpk("1003537");
			//5.加载获取模板类
			//NCBillTemplate ncbill = new NCBillTemplate(pk_billtemplet);
			//6.获得单据聚合VO
			AggregatedValueObject billVO = (AggregatedValueObject) ((AggregatedValueObject) Class
					.forName(BillMetaUtil.getAggVOFullClassName(billtype)).newInstance());;
			NCBillTemplate ncbill = new NCBillTemplate(pk_billtemplet,billVO);
//			IPFConfig bsConfig = (IPFConfig) NCLocator.getInstance().lookup(
//					IPFConfig.class.getName());
			//获取特例数据
		//	HashMap<String , String> map2 = new HashMap<String , String>();
		//	map2 = qry.a(pklist);
			String telipk = qry.telipk(pklist);
			//AggregatedValueObject billVo = bsConfig.queryBillDataVO(billtype,telipk);
			//7.加载单据模板方法
			//ncbill.setBillVO(billVo);
			//ncbill.loadTemplate();
			//8.获取表头表体
			//MobileBillData mbd = ncbill.billVO2Map("","");
			ncbill.billVO2Map("","");
			return null;
			//return mbd.getData().toString();
		} catch (Exception e) {
			e.printStackTrace();
			return e.getMessage();
		}
	}
	
	
}

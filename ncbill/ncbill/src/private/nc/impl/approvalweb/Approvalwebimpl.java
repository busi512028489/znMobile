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
		//����xml������
		BillTypeModelTrans getbilltype = BillTypeModelTrans.getInstance();
		//�Զ��幤����
		Queryutils qry = new Queryutils();
		try {
			//1.��ȡhome/resources/jzmobile·���µ�.xml�ļ�
			List<String> allbilltype = getbilltype.getMobileApproveBillTypeList();
			//2.�ж��Ƿ����
			if (!allbilltype.contains(billtype)) {
				return "�õ������Ͳ�����";
			}
			//3.���ݵ������ͻ�ȡxml����Ч��Ϣ
			BillTypeModel model = getbilltype.getModelByBillType(billtype);
			//4.��ȡ����ģ��pk,����pk
			String pk_billtemplet = qry.huoqu(model);
			List pklist = qry.getbillpk("1003537");
			//5.���ػ�ȡģ����
			//NCBillTemplate ncbill = new NCBillTemplate(pk_billtemplet);
			//6.��õ��ݾۺ�VO
			AggregatedValueObject billVO = (AggregatedValueObject) ((AggregatedValueObject) Class
					.forName(BillMetaUtil.getAggVOFullClassName(billtype)).newInstance());;
			NCBillTemplate ncbill = new NCBillTemplate(pk_billtemplet,billVO);
//			IPFConfig bsConfig = (IPFConfig) NCLocator.getInstance().lookup(
//					IPFConfig.class.getName());
			//��ȡ��������
		//	HashMap<String , String> map2 = new HashMap<String , String>();
		//	map2 = qry.a(pklist);
			String telipk = qry.telipk(pklist);
			//AggregatedValueObject billVo = bsConfig.queryBillDataVO(billtype,telipk);
			//7.���ص���ģ�巽��
			//ncbill.setBillVO(billVo);
			//ncbill.loadTemplate();
			//8.��ȡ��ͷ����
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

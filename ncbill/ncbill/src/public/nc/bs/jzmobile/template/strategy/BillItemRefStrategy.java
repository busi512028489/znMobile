package nc.bs.jzmobile.template.strategy;

import nc.bs.logging.Logger;
import nc.itf.jzmobile.template.strategy.IBillItemStrategy;
import nc.md.data.access.NCObject;
import nc.ui.bd.ref.AbstractRefModel;
import nc.ui.bd.ref.RefPubUtil;
import nc.ui.pub.beans.constenum.DefaultConstEnum;
import nc.vo.pmpub.common.utils.StringUtil;
import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.pub.bill.BillTempletBodyVO;

/**
 * ���մ�����
 * 
 * @author mazhyb
 * 
 */
public class BillItemRefStrategy implements IBillItemStrategy {

	public Object process(String corp,String userid,BillTempletBodyVO item, Object value, NCObject ncos) {
		String key = item.getItemkey();
		ncos.getRelatedBean().getAttributeByName(key).getRefModelName();

		String refNodeName = ncos.getRelatedBean().getAttributeByName(key).getRefModelName();
		if (refNodeName == null || null == value) {
			return value;
		}
		AbstractRefModel model = null;
		try {
			model = RefPubUtil.getRefModel(refNodeName);
		} catch (Exception e) {
			Logger.error(e);
		}
		if(model==null){
			String refModelClassName = RefPubUtil.getRefModelClassName(refNodeName);
			String refModelClassChangeName = "nc.jzmobile.refmodel"
					+ refModelClassName.substring(refModelClassName
							.lastIndexOf("."));
			try {
				Object obj = Class.forName(refModelClassChangeName)
						.newInstance();
				// ͨ������Ϊ��pk_project���ԵĲ��������pk_project���Ը�ֵ
//				setProjectByReflect(obj, refModelClassChangeName,
//						pk_project);

				model = (AbstractRefModel) obj;
			} catch (Exception ex) {
				
			}
				
		}
		if (model != null) {
			if( !StringUtil.isEmpty(corp) ){
				model.setPk_corp(corp);
			}
			if( !StringUtil.isEmpty(userid) ){
				model.setPk_user(userid);
			}
			
			model.matchPkData(value.toString());
			DefaultConstEnum refValue = new DefaultConstEnum(value, model
					.getRefNameValue());
			if (refValue != null) {
				// Object pk_value = refValue.getValue();
				return refValue.getName();
			}
			return value;// δת���ɹ�
		}
		// Ϊ�Զ������
		model = getModel(refNodeName);
		if (model == null)
			return value;// ת��ʧ��
		if( !StringUtil.isEmpty(corp) ){
			model.setPk_corp(corp);
		}
		if( !StringUtil.isEmpty(userid) ){
			model.setPk_user(userid);
		}
		Object obj = model.getValue(value.toString());
		if (obj == null)
			return value;// ת��ʧ��
		return value;
	}

	/**
	 * ��ȡ�Զ������
	 * 
	 * @param refNodeName
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private AbstractRefModel getModel(String refNodeName) {
		if (refNodeName == null)
			return null;
		refNodeName = refNodeName.substring(refNodeName.indexOf("<") + 1,
				refNodeName.indexOf(">"));
		Class refModel = null;
		try {
			refModel = Class.forName(refNodeName);
			return (AbstractRefModel) refModel.newInstance();
		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
			return null;
		}

	}

}

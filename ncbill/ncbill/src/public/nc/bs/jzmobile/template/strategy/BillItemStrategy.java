package nc.bs.jzmobile.template.strategy;

import java.util.HashMap;
import java.util.Map;

import nc.bs.logging.Logger;
import nc.itf.jzmobile.template.strategy.IBillItemStrategy;
import nc.md.data.access.NCObject;
import nc.md.model.type.impl.EnumType;
import nc.md.model.type.impl.RefType;
import nc.ui.pub.bill.IBillItem;
import nc.vo.jzmobile.app.MobileTabDataVO;
import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.pub.IAttributeMeta;
import nc.vo.pub.IVOMeta;
import nc.vo.pub.IVOMetaStatisticInfo;
import nc.vo.pub.SuperVO;
import nc.vo.pub.bill.BillTempletBodyVO;
import nc.vo.pubapp.pattern.model.meta.entity.vo.ModelAttribute;

/**
 * 模板字段取值处理策略类
 * 
 * @author mazhyb
 */
public class BillItemStrategy {
	/**
	 * 具体处理策略类
	 */
	private static final Map<Integer, IBillItemStrategy> STRATEGIES = new HashMap<Integer, IBillItemStrategy>();
	/**
	 * 数据格式化
	 */
	private static final ValueParserStrategy PARSER = new ValueParserStrategy();

	public static void register(int type, IBillItemStrategy strategy) {
		STRATEGIES.put(type, strategy);
	}

	static {
		// 下拉处理
		register(IBillItem.COMBO, new BillItemComboStrategy());
		// 参照处理
		register(IBillItem.UFREF, new BillItemRefStrategy());
		// 自定义档案
		register(IBillItem.USERDEF, new BillItemUserDefStrategy());

	}

	public MobileTabDataVO process(String corp, String userid,
			BillTempletBodyVO billitem, CircularlyAccessibleValueObject vo, NCObject ncos) {
		String key = billitem.getItemkey();
		String showname = billitem.getDefaultshowname();
		MobileTabDataVO columnVO = new MobileTabDataVO();
		if (ncos.getRelatedBean().getAttributeByName(key) == null) {
			if ( showname != null && showname.length() > 0 ) {
				columnVO.setColkey(key);
				columnVO.setColname(showname);
				columnVO.setColvalue(PARSER.parseValue(vo.getAttributeValue(key)));
				return columnVO;
			}
			return null;
		}

		if(showname==null||"".equals(showname)){
			showname = ncos.getRelatedBean().getAttributeByName(key).getDisplayName();
		}
		
		columnVO.setColkey(key);
		columnVO.setColname(showname);
		Object value = null;
		int type = billitem.getDatatype();
		value = vo.getAttributeValue(key);
		try{
			if (ncos.getRelatedBean().getAttributeByName(key).getDataType() instanceof RefType) {
				type = IBillItem.UFREF;
				columnVO.setColPkvalue(value);
			} else if (ncos.getRelatedBean().getAttributeByName(key).getDataType() instanceof EnumType) {
				columnVO.setColPkvalue(value);
				type = IBillItem.COMBO;
			}
			IBillItemStrategy strategy = STRATEGIES.get(type);
			if (strategy != null) {
				value = strategy.process(corp, userid, billitem, vo.getAttributeValue(key), ncos);
			}
			// 处理结果
		}catch (Exception e) {
			e.printStackTrace();
			Logger.error(e);
		}
		
		value = PARSER.parseValue(value);
		columnVO.setColvalue(value);

		return columnVO;
	}

}

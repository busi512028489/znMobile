package nc.jzmobile.bill.data.access;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import nc.bs.framework.common.NCLocator;
import nc.bs.jzmobile.template.mobile.NCMobileFormulaFacade;
import nc.bs.jzmobile.template.strategy.BillItemStrategy;
import nc.bs.logging.Logger;
import nc.itf.uap.billtemplate.IBillTemplateQry;
import nc.jzmobile.consts.IBillDataConst;
import nc.md.data.access.DASFacade;
import nc.md.data.access.NCObject;
import nc.md.model.IBusinessEntity;
import nc.ui.bd.ref.AbstractRefModel;
import nc.ui.pub.bill.IBillItem;
import nc.vo.am.timerule.LeaseTimeRuleVO;
import nc.vo.jzmobile.app.MobileBillData;
import nc.vo.jzmobile.app.MobileTabContentVO;
import nc.vo.jzmobile.app.MobileTabDataVO;
import nc.vo.jzmobile.template.comparator.BillTabVOComparator;
import nc.vo.jzmobile.template.comparator.BillTempletBodyVOComparator;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.pub.bill.BillTabVO;
import nc.vo.pub.bill.BillTempletBodyVO;
import nc.vo.pub.bill.BillTempletVO;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFDouble;
import nc.vo.trade.pub.IExAggVO;

/**
 * 根据【单据模板】 【完整单据数据】组装 移动端显示格式的
 * 
 * @author wanghui4
 * 
 */

public class NCBillAccessBillTemplate {

	public static final SimpleDateFormat sdf_ymd = new SimpleDateFormat(
			"yyyy-MM-dd");
	public static final SimpleDateFormat sdf_ymd_hms = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");
	// 排序
	public static final BillTabVOComparator TABVO_COMPARATOR = new BillTabVOComparator();
	public static final BillTempletBodyVOComparator BODYVO_COMPARATOR = new BillTempletBodyVOComparator();

	private BillTempletVO billTempletVO = null;
	private AggregatedValueObject billVO = null;
	private String pk_billTemplet;
	private List<BillTempletBodyVO> headTails = new ArrayList<BillTempletBodyVO>();
	private Map<String, List<BillTempletBodyVO>> bodys = new HashMap<String, List<BillTempletBodyVO>>();
	private BillItemStrategy processor = new BillItemStrategy();

	public NCBillAccessBillTemplate(String pk_billtemplet) {
		pk_billTemplet = pk_billtemplet;
		// this.billVO = billVO;
		// init();
	}

	
	//加载单据模板方法
	public void loadTemplate() {
		billTempletVO = getBillTempletVO(pk_billTemplet);
		if (billTempletVO.getBodyVO() != null
				&& billTempletVO.getBodyVO().length > 0) {
			for (int i = 0; i < billTempletVO.getBodyVO().length; i++) {
				// 只有卡片界面显示的内容才会继续
				if (!billTempletVO.getBodyVO()[i].getShowflag().booleanValue())
					continue;

				switch (billTempletVO.getBodyVO()[i].getPos()) {
				case IBillItem.BODY:
					String tablecode = billTempletVO.getBodyVO()[i]
							.getTable_code();
					if (bodys.containsKey(tablecode)) {
						bodys.get(tablecode).add(billTempletVO.getBodyVO()[i]);
					} else {
						List<BillTempletBodyVO> list = new ArrayList<BillTempletBodyVO>();
						list.add(billTempletVO.getBodyVO()[i]);
						bodys.put(tablecode, list);
					}

					break;
				case IBillItem.HEAD:
					headTails.add(billTempletVO.getBodyVO()[i]);
					break;
				// case IBillItem.TAIL:
				// billTempletVO.getBodyVO()[i].setPos(IBillItem.HEAD);
				// headTails.add(billTempletVO.getBodyVO()[i]);
				// break;
				}
			}// end of for loop
			Collections.sort(headTails, BODYVO_COMPARATOR);
		}
	}

	private BillTempletVO getBillTempletVO(String pk_billTemplet) {

		BillTempletVO vo = null;
		IBillTemplateQry qry = (IBillTemplateQry) NCLocator.getInstance()
				.lookup(IBillTemplateQry.class.getName());
		try {
			vo = qry.findTempletData(pk_billTemplet);
		} catch (BusinessException e) {
			Logger.error(e.getMessage(), e);
		}
		return vo;
	}
	//获取表头表体
	public MobileBillData billVO2Map(String corp,
			String userid) {
		if (this.billTempletVO == null || billTempletVO.getBodyVO() == null
				|| billTempletVO.getBodyVO().length == 0 || this.billVO == null) {
			return null;
		}
		Map<String, List<MobileTabContentVO>> map = new LinkedHashMap<String, List<MobileTabContentVO>>();
		
		MobileBillData billData = new MobileBillData();
		List<MobileTabContentVO> headTabList = putHeadData( corp, userid);
		map.put(IBillDataConst._HEAD, headTabList);
		// 处理表体
		List<MobileTabContentVO> bodyTabList = putBodyData(corp, userid);
		map.put(IBillDataConst._BODY, bodyTabList);
		
		billData.setData(map);
		return billData;
	}
	//获取表头
	public MobileBillData billVO2MapHead(String corp,
			String userid) {
		if (this.billTempletVO == null || billTempletVO.getBodyVO() == null
				|| billTempletVO.getBodyVO().length == 0 || this.billVO == null) {
			return null;
		}
		Map<String, List<MobileTabContentVO>> map = new LinkedHashMap<String, List<MobileTabContentVO>>();
		
		MobileBillData billData = new MobileBillData();
		List<MobileTabContentVO> headTabList = putHeadData( corp, userid);
		map.put(IBillDataConst._HEAD, headTabList);
//		// 处理表体
//		List<MobileTabContentVO> bodyTabList = putBodyData(corp, userid);
//		map.put(IBillDataConst._BODY, bodyTabList);
		
		billData.setData(map);
		return billData;
	}

	private void dealFormular(List<BillTempletBodyVO> bodys,
			CircularlyAccessibleValueObject[] vos) {
		NCMobileFormulaFacade.executeFormula(
				bodys.toArray(new BillTempletBodyVO[bodys.size()]), vos);
	}

	private void dealFormular(BillTempletBodyVO[] bodys,
			CircularlyAccessibleValueObject[] vos) {
		NCMobileFormulaFacade.executeFormula(bodys, vos);
	}
/***
	private void putHeadData(Map<String, Map<String, Object>> map, String corp,
			String userid) {
		CircularlyAccessibleValueObject headTailVO = billVO.getParentVO();
		// List<Map<String, Object>> list = new ArrayList<Map<String,
		// Object>>();

		// 处理显示公式
		dealFormular(headTails,
				new CircularlyAccessibleValueObject[] { headTailVO });

		CircularlyAccessibleValueObject[] headTailVOs = new CircularlyAccessibleValueObject[] { headTailVO };

		List<BillTabVO> tabvos = getHeadTabVO();
		Map<String, Object> tabMap = new LinkedHashMap<String, Object>();
		List<Map> contList = new ArrayList<Map>();
		for (BillTabVO tabvo : tabvos) {
			// tabMap.put(IBillDataConst.TABCODE, tabvo.getTabcode());
			// tabMap.put(IBillDataConst.TABTITLE, tabvo.getTabname());
			List<BillTempletBodyVO> bvos = getHeadBodyVOs(headTails, tabvo);
			List<Map> data = getTabDataMap(headTailVOs,
					bvos.toArray(new BillTempletBodyVO[0]),
					IBillDataConst.HEAD, corp, userid, tabvo);
			if (data.size() == 0)
				continue;
			contList.addAll(data);
		}
		tabMap.put(IBillDataConst.TABCONTENT, contList);
		// list.add(tabMap);
		String key = IBillDataConst._HEAD;
		map.put(key, tabMap);

	}
***/	
	private List<MobileTabContentVO> putHeadData(String corp,String userid) {
		CircularlyAccessibleValueObject headTailVO = billVO.getParentVO();
		
		// 处理显示公式
		dealFormular(headTails, new CircularlyAccessibleValueObject[] { headTailVO });

		CircularlyAccessibleValueObject[] headTailVOs = new CircularlyAccessibleValueObject[] { headTailVO };

		List<BillTabVO> tabvos = getHeadTabVO();
		List<MobileTabContentVO> contList = new ArrayList<MobileTabContentVO>();
		for (BillTabVO tabvo : tabvos) {
			List<BillTempletBodyVO> bvos = getHeadBodyVOs(headTails, tabvo);
			if(bvos==null||bvos.size()==0)
				continue;
			MobileTabContentVO tabContentVO = getTabDataMap(headTailVOs,bvos.toArray(new BillTempletBodyVO[0]),IBillDataConst.HEAD, corp, userid, tabvo);
			if (tabContentVO != null) {
				contList.add(tabContentVO);
			}
		}
		return contList;

	}

	private List<MobileTabContentVO> putBodyData(String corp, String userid) {
		List<MobileTabContentVO> contList = new ArrayList<MobileTabContentVO>();
		// 支持多子表
		if (billVO instanceof IExAggVO) {
			IExAggVO aggvo = (IExAggVO) billVO;
			List<BillTabVO> tabvos = getBodyTabVO();
			for (BillTabVO tabvo : tabvos) {
				String tablecode = tabvo.getBasetab() != null && tabvo.getBasetab().length() > 0 ? tabvo.getBasetab() : tabvo.getTabcode();
				
				//tabvo.getBasetab() != null && tabvo.getBasetab().length() > 0 ? tabvo.getBasetab() : tabvo.getTabcode();
				List<BillTempletBodyVO> tplvos = bodys.get(tablecode);
				if(tplvos==null)
					continue;
				Collections.sort(tplvos, BODYVO_COMPARATOR);// 排序
				
				tablecode = tabvo.getMetadatapath();
				if(tablecode==null)
					continue;
				CircularlyAccessibleValueObject[] bodyVOs = aggvo.getTableVO(tablecode);
				if (bodyVOs == null)
				{
					bodyVOs = new CircularlyAccessibleValueObject[0];
				}else{
					//转换计租方式字段的显示值
					String rent_type = (String) bodyVOs[0].getAttributeValue("rent_type");
					if (StringUtils.isNotBlank(rent_type)){
						LeaseTimeRuleVO rent_type_name = LeaseTimeRuleVO.fromXml(rent_type);
						bodyVOs[0].setAttributeValue("rent_type", rent_type_name);
					}
				}
				dealFormular(tplvos, bodyVOs);
				MobileTabContentVO tabContentVO = getBodyTabDataMap(bodyVOs,tplvos.toArray(new BillTempletBodyVO[0]), IBillDataConst.BODY, corp, userid, tabvo);
				if (tabContentVO != null) {
					contList.add(tabContentVO);
				}
			}
		} else {
			CircularlyAccessibleValueObject[] bodyVOs = billVO.getChildrenVO();
			if(bodyVOs==null)
				bodyVOs = new CircularlyAccessibleValueObject[0];
			dealFormular(billTempletVO.getBodyVO(), bodyVOs);
			
			MobileTabContentVO tabContentVO = getTabDataMap(bodyVOs, billTempletVO.getBodyVO(),IBillDataConst.BODY, corp, userid, new BillTabVO());
			if (tabContentVO != null) {
				contList.add(tabContentVO);
			}
		}
		return contList;
	}
	/****
	private void putBodyData(Map<String, Map<String, Object>> map, String corp,
			String userid) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		List<Map> contList = new ArrayList<Map>();
		Map<String, Object> tabMap = new LinkedHashMap<String, Object>();
		// 支持多子表
		if (billVO instanceof IExAggVO) {
			IExAggVO aggvo = (IExAggVO) billVO;
			List<BillTabVO> tabvos = getBodyTabVO();
			for (BillTabVO tabvo : tabvos) {
				String tablecode = tabvo.getTabcode();
				List<BillTempletBodyVO> tplvos = bodys.get(tablecode);
				Collections.sort(tplvos, BODYVO_COMPARATOR);// 排序
				CircularlyAccessibleValueObject[] bodyVOs = aggvo
						.getTableVO(tablecode);
				if (bodyVOs == null)
					bodyVOs = new CircularlyAccessibleValueObject[0];
				// if(tabvo!=null){
				// tabMap.put(IBillDataConst.TABCODE, tablecode);
				// tabMap.put(IBillDataConst.TABTITLE, tabvo.getTabname());
				// }
				dealFormular(tplvos, bodyVOs);
				List<Map> data = getBodyTabDataMap(bodyVOs,
						tplvos.toArray(new BillTempletBodyVO[0]),
						IBillDataConst.BODY, corp, userid, tabvo);
				if (data.size() == 0)
					continue;
				contList.addAll(data);

			}// end of for loop
			tabMap.put(IBillDataConst.TABCONTENT, contList);
		} else {
			CircularlyAccessibleValueObject[] bodyVOs = billVO.getChildrenVO();
			dealFormular(billTempletVO.getBodyVO(), bodyVOs);
			tabMap.put(
					IBillDataConst.TABCONTENT,
					getTabDataMap(bodyVOs, billTempletVO.getBodyVO(),
							IBillDataConst.BODY, corp, userid, new BillTabVO()));
		}
		map.put(IBillDataConst._BODY, tabMap);
	}
	

	// 页签下的一条记录 ，为一个Map
	// 为key的数据
	// 每个BillItem为一个Map，map中为 billItem 的显示名称，value,如果是参照类型还有PK值。
	@SuppressWarnings("rawtypes")
	public List<Map> getTabDataMap(CircularlyAccessibleValueObject[] bodyVOs,
			BillTempletBodyVO[] itemVOs, int pos, String corp, String userid,
			BillTabVO tabvo) {
		List<Map> recordList = new ArrayList<Map>();
		Map<String, Object> recordMap = new LinkedHashMap<String, Object>();
		for (int bvo = 0; bvo < bodyVOs.length; bvo++) {
			Map<String, Object> map = new LinkedHashMap<String, Object>();
			CircularlyAccessibleValueObject vo = bodyVOs[bvo];
			List<MobileTabDataVO> columnList = new ArrayList<MobileTabDataVO>();
			for (int i = 0; i < itemVOs.length; i++) {
				if (itemVOs[i].getPos() != pos) {
					continue;
				}
				MobileTabDataVO column = processor.process(corp, userid,
						itemVOs[i], bodyVOs[bvo]);
				columnList.add(column);
			}
			map.put(IBillDataConst.TABDATA, columnList);
			map.put(IBillDataConst.TABCODE, tabvo.getTabcode());
			map.put(IBillDataConst.TABTITLE, tabvo.getTabname());
			recordList.add(map);
		}
		return recordList;
	}
***/	
	@SuppressWarnings("rawtypes")
	public MobileTabContentVO getTabDataMap(CircularlyAccessibleValueObject[] bodyVOs,
			BillTempletBodyVO[] itemVOs, int pos, String corp, String userid,
			BillTabVO tabvo) {
		
		MobileTabContentVO tabContentVO = new MobileTabContentVO();
		tabContentVO.setCode(tabvo.getTabcode());
		tabContentVO.setPos(IBillItem.HEAD);
		tabContentVO.setTabTitle(tabvo.getTabname());
		
		List<List<MobileTabDataVO>> tabData = new ArrayList<List<MobileTabDataVO>>();
		for (int bvo = 0; bvo < bodyVOs.length; bvo++) {
			IBusinessEntity be = tabvo.getBillMetaDataBusinessEntity();
			if(be==null)
				continue;
			List<MobileTabDataVO> columnList = new ArrayList<MobileTabDataVO>();
			NCObject ncos = DASFacade.newInstanceWithContainedObject(be, bodyVOs[bvo]);
			
			for (int i = 0; i < itemVOs.length; i++) {
				if (itemVOs[i].getPos() != pos) {
					continue;
				}
				
				MobileTabDataVO column = processor.process(corp, userid,itemVOs[i], bodyVOs[bvo], ncos);
				if (column != null) {
					columnList.add(column);
				}
			}
			if(columnList.size()>0)
				tabData.add(columnList);
		}
		if(tabData.size()==0)
			return null;
		tabContentVO.setTabdata(tabData);
		return tabContentVO;
	}
	
	
	
	/**
	 * 20160815-zhangwxe
	 * 表体数据拼装和表体不一样
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public MobileTabContentVO getBodyTabDataMap(CircularlyAccessibleValueObject[] bodyVOs,
				BillTempletBodyVO[] itemVOs, int pos, String corp, String userid,
				BillTabVO tabvo) {
		
		MobileTabContentVO tabContentVO = new MobileTabContentVO();
		tabContentVO.setCode(tabvo.getTabcode());
		tabContentVO.setPos(IBillItem.BODY);
		tabContentVO.setTabTitle(tabvo.getTabname());
		tabContentVO.setDatacount(bodyVOs.length+"");
		
		List<List<MobileTabDataVO>> tabData = new ArrayList<List<MobileTabDataVO>>();
		for (int bvo = 0; bvo < bodyVOs.length; bvo++) {
			List<MobileTabDataVO> columnList = new ArrayList<MobileTabDataVO>();
			IBusinessEntity be = tabvo.getBillMetaDataBusinessEntity();
			if(be==null)
				continue;
			NCObject ncos = DASFacade.newInstanceWithContainedObject(be, bodyVOs);
			
			for (int i = 0; i < itemVOs.length; i++) {
				if (itemVOs[i].getPos() != pos) {
					continue;
				}
				MobileTabDataVO column = processor.process(corp, userid,itemVOs[i], bodyVOs[bvo], ncos);
				if (column != null) {
					columnList.add(column);
				}
			}
			tabData.add(columnList);
		}
		tabContentVO.setTabdata(tabData);
		return tabContentVO;
	}
	
	/***
	 * 
	@SuppressWarnings("rawtypes")
	public List<Map> getBodyTabDataMap(CircularlyAccessibleValueObject[] bodyVOs,
				BillTempletBodyVO[] itemVOs, int pos, String corp, String userid,
				BillTabVO tabvo) {
		List<Map> recordList = new ArrayList<Map>();
		List<List<MobileTabDataVO> > mopList = new ArrayList<List<MobileTabDataVO>>();
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		for (int bvo = 0; bvo < bodyVOs.length; bvo++) {
			CircularlyAccessibleValueObject vo = bodyVOs[bvo];
			List<MobileTabDataVO> columnList = new ArrayList<MobileTabDataVO>();
			for (int i = 0; i < itemVOs.length; i++) {
				if (itemVOs[i].getPos() != pos) {
					continue;
				}
				MobileTabDataVO column = processor.process(corp, userid,
						itemVOs[i], bodyVOs[bvo]);
				columnList.add(column);
			}
			mopList.add(columnList);
		}
		map.put(IBillDataConst.TABDATA, mopList);
		map.put(IBillDataConst.DATACOUNT, bodyVOs.length);
		map.put(IBillDataConst.TABCODE, tabvo.getTabcode());
		map.put(IBillDataConst.TABTITLE, tabvo.getTabname());
		recordList.add(map);
		return recordList;
	}
	 */

	private AbstractRefModel getModel(String refNodeName) {
		if (null != refNodeName) {
			refNodeName = refNodeName.substring(refNodeName.indexOf("<") + 1,
					refNodeName.indexOf(">"));
		}
		Class refModel = null;
		try {
			refModel = Class.forName(refNodeName);
			return (AbstractRefModel) refModel.newInstance();
		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
			return null;
		}

	}

	private BillTabVO getTabVO(String tablecode) {
		BillTabVO[] bvos = this.billTempletVO.getHeadVO().getStructvo()
				.getBillTabVOs();
		for (BillTabVO bvo : bvos) {
			if (tablecode != null && tablecode.length() > 0
					&& tablecode.equals(bvo.getTabcode())) {
				return bvo;
			}
		}
		return null;
	}

	private List<BillTabVO> getBodyTabVO() {
		List<BillTabVO> list = new ArrayList<BillTabVO>();
		BillTabVO[] bvos = this.billTempletVO.getHeadVO().getStructvo()
				.getBillTabVOs();
		for (BillTabVO bvo : bvos) {
			if (bvo.getPos() == IBillDataConst.BODY) {
				list.add(bvo);
			}
		}
		Collections.sort(list, TABVO_COMPARATOR);
		return list;
	}

	private List<BillTabVO> getHeadTabVO() {
		List<BillTabVO> list = new ArrayList<BillTabVO>();
		BillTabVO[] bvos = this.billTempletVO.getHeadVO().getStructvo()
				.getBillTabVOs();
		for (BillTabVO bvo : bvos) {
			if (bvo.getPos() == IBillDataConst.HEAD) {
				list.add(bvo);
			}
		}
		Collections.sort(list, TABVO_COMPARATOR);
		return list;
	}

	private List<BillTempletBodyVO> getHeadBodyVOs(
			List<BillTempletBodyVO> head, BillTabVO tabvo) {
		List<BillTempletBodyVO> list = new ArrayList<BillTempletBodyVO>();
		for (BillTempletBodyVO vo : head) {
			if (!vo.getShowflag().booleanValue())
				continue;
			if (vo.getTable_code().equals(tabvo.getTabcode())) {
				list.add(vo);
			}
		}
		Collections.sort(list, BODYVO_COMPARATOR);// 排序
		return list;
	}

	// 格式转换
	private Object parseValue(Object val) {
		if (val == null)
			return null;
		if (val instanceof UFDouble) {
			return ((UFDouble) val).getDouble();
		}
		if (val instanceof UFDate) {
			return sdf_ymd.format(((UFDate) val).toDate());
		}
		if (val instanceof UFDateTime) {
			return ((UFDateTime) val).toString();
		}
		return val;
	}

	public AggregatedValueObject getBillVO() {
		return billVO;
	}

	public void setBillVO(AggregatedValueObject billVO) {
		this.billVO = billVO;
	}

	public BillTempletVO getBillTempletVO() {
		return this.billTempletVO;
	}

}

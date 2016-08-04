package com.zlz.bug.data;

import java.net.URL;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author zhailz
 *
 * 时间：2016年8月4日 ### 上午10:44:00
 */
public class SimpleTextFilter {

	private Logger logger = LoggerFactory.getLogger(SimpleTextFilter.class);
	
	// 文本过滤
	public String filter(String firtext, String secondText, DataModel dataModel) {
		if(StringUtils.isNotBlank(firtext) && StringUtils.isNotBlank(secondText)){
			//利用缓存
			URL base = dataModel.getBaseurl();
			String host = base.getHost();
			String sfix = dataModel.getUrlType().get(host+ConfigCenter.BodyAsTextStartFix);
			String efix = dataModel.getUrlType().get(host+ConfigCenter.BodyAsTextEndFix);
			if(StringUtils.isNotBlank(sfix) || StringUtils.isNotBlank(efix)){
				firtext = firtext.replaceAll(sfix, "");
				firtext = firtext.replaceAll(efix, "");
				return firtext;
			}
			return compareValue(firtext, secondText, dataModel);
		}
		return null;
	}

	private String compareValue(String firtext, String secondText, DataModel dataModel) {
		String[] farray = firtext.split("\n");
		String[] sarray = secondText.split("\n");
		int first = 0; 
		for (int i = 0; i < farray.length; i++) {
			String temp = farray[i];
			String sarrayt = sarray[i];
			if(!temp.contentEquals(sarrayt)){
				first = i;
				break;
			}
		}
		
		int end1 = farray.length -1; int end2 = sarray.length -1;
		for (; end1 > 0 && end2 >0 ;end1--,end2--) {
			String temp = farray[end1];
			String sarrayt = sarray[end2];
			if(!temp.contentEquals(sarrayt)){
				break;
			}
		}
		
		if(first > 0 || end1 != farray.length -1){
			String but = StringUtils.join(farray, "\n", first, end1+1);
			iniDataValue(first,end1,farray,dataModel);
			return but;
		}
		return null;
	}

	private void iniDataValue(int first, int end1, String[] farray, DataModel dataModel) {
		URL base = dataModel.getBaseurl();
		String host = base.getHost();
		String sfix = StringUtils.join(farray, "\n", 0, first);
		String efix = StringUtils.join(farray, "\n", end1, farray.length-1);
		dataModel.getUrlType().put(host+ConfigCenter.BodyAsTextStartFix, sfix);
		dataModel.getUrlType().put(host+ConfigCenter.BodyAsTextEndFix, efix);
		
	}

}

package com.zlz.bug.data;

import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.zlz.bug.ContentsData.HtmlContentPage;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author zhailz
 *
 * 时间：2016年8月4日 ### 上午9:32:58
 */
public class NovalRegularExpression implements RegularExpression {

	private String regularExpression = "<[^>]+>"; 
	// 定义HTML标签的正则表达式 
	private Pattern regularPattern =  Pattern.compile(regularExpression, Pattern.CASE_INSENSITIVE);  
	
	// 定义
	//定义script的正则表达式{或<script[^>]*?>[\\s\\S]*?<\\/script>  
    String regEx_script = "<[\\s]*?script[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?script[\\s]*?>"; 
    // 定义HTML标签的正则表达式 
 	private Pattern regEx_scriptPattern =  Pattern.compile(regEx_script, Pattern.CASE_INSENSITIVE);  
 	
    
    //定义style的正则表达式{或<style[^>]*?>[\\s\\S]*?<\\/style>  
    String regEx_style = "<[\\s]*?style[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?style[\\s]*?>";   
    // 定义HTML标签的正则表达式 
 	private Pattern regEx_stylePattern =  Pattern.compile(regEx_style, Pattern.CASE_INSENSITIVE);  
 	
	public String execute(String vbefore) {
		if(vbefore == null || vbefore.trim().length() < 1){
			return null;
		}
		
		// 过滤html标签 
		Matcher matcher = regularPattern.matcher(vbefore);  
        String after = matcher.replaceAll(""); 
        
        //过滤了script标签
        Matcher scriptmatcher = regEx_scriptPattern.matcher(after);
        after = scriptmatcher.replaceAll("");
        
        //过滤了script标签
        Matcher typematcher = regEx_stylePattern.matcher(after);
        after = typematcher.replaceAll("");
        
		return after;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String value = "";
		NovalRegularExpression expre = new NovalRegularExpression();
		String valueAfter = expre.execute(value);
		System.out.println(valueAfter);
	}

	public HtmlContentPage execute(HtmlPage firstPage) {
		HtmlContentPage page = new HtmlContentPage(firstPage.getBaseURI());
		String value = firstPage.asText();
		String filter = execute(value);
		page.setFilterContent(filter);
		return page;
	}
}

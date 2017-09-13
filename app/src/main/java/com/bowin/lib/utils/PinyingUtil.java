package com.bowin.lib.utils;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import java.util.regex.Pattern;

/**
 * 
 * @author Terence
 *
 */
public class PinyingUtil {
	
	public static final String[] pinyin = new String[]{"#","A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};
	
	public static String getAlpha(String str1) {
		String msg[] = str1.split("\\(");
		String str = msg[0];
		if (str == null) {
			return pinyin[0];
		}

		if (str.trim().length() == 0) {
			return pinyin[0];
		}
		char c = str.trim().substring(0, 1).charAt(0);
		Pattern pattern = Pattern.compile("^[A-Za-z]");
		if (pattern.matcher(c + "").matches()) {
			return (c + "").toUpperCase(); 
		} else {
			return pinyin[0];
		}
	}

    public static String[] stringToPinyin(String src) {
        return stringToPinyin(src, false, null);
    }
    
    public static String[] stringToPinyin(String src,String separator) {
        return stringToPinyin(src, true, separator);
    }

    public static String[] stringToPinyin(String src, boolean isPolyphone,
            String separator) {
        if ("".equals(src) || null == src) {
            return null;
        }
        char[] srcChar = src.toCharArray();
        int srcCount = srcChar.length;
        String[] srcStr = new String[srcCount];

        for (int i = 0; i < srcCount; i++) {
            srcStr[i] = charToPinyin(srcChar[i], isPolyphone, separator);
        }
        return srcStr;
    }

    public static String charToPinyin(char src, boolean isPolyphone, String separator) {
        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
        defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);

        StringBuffer tempPinying = new StringBuffer();
        if (src > 128) {
            try {
                String[] strs = PinyinHelper.toHanyuPinyinStringArray(src, defaultFormat);
                if (isPolyphone && null != separator) {
                    for (int i = 0; i < strs.length; i++) {
                        tempPinying.append(strs[i]);
                        if (strs.length != (i + 1)) 
                            tempPinying.append(separator);
                    }
                } else {
                    tempPinying.append(strs[0]);
                }

            } catch (BadHanyuPinyinOutputFormatCombination e) {
                e.printStackTrace();
            }
        } else {
            tempPinying.append(src);
        }
        return tempPinying.toString();

    }

    public static String hanziToPinyin(String hanzi){
        return hanziToPinyin(hanzi," ");
    }
    
    public static String hanziToPinyin(String hanzi,String separator){
    	HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
        defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        
        String pinyingStr="";
        try {
        	pinyingStr=PinyinHelper.toHanyuPinyinString(hanzi, defaultFormat, separator);
        } catch (BadHanyuPinyinOutputFormatCombination e) {
            e.printStackTrace();
        }    
        return pinyingStr;
    }

    public static String stringArrayToString(String[] str, String separator) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < str.length; i++) {
            sb.append(str[i]);
            if (str.length != (i + 1)) 
                sb.append(separator);
        }
        return sb.toString();
    }
    
    public  static String stringArrayToString(String[] str){
        return stringArrayToString(str,"");
    }
    
    public static String charArrayToString(char[] ch, String separator) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < ch.length; i++) {
            sb.append(ch[i]);
            if (ch.length != (i + 1)) {
                sb.append(separator);
            }
        }
        return sb.toString();
    }
    
    public static String charArrayToString(char[] ch) {
        return charArrayToString(ch," ");
    }

    public static char[]  getHeadByChar(char src,boolean isCapital){
        if (src <= 128) {
            return new char[]{src};
        }
        String []pinyingStr=PinyinHelper.toHanyuPinyinStringArray(src);
        int polyphoneSize=pinyingStr.length;
        char [] headChars=new char[polyphoneSize];
        int i=0;
        for(String s:pinyingStr){
            char headChar=s.charAt(0);
            if(isCapital){
                headChars[i]=Character.toUpperCase(headChar);
             }else{
                headChars[i]=headChar;
             }
            i++;
        }
        return headChars;
    }
    
    public static char[]  getHeadByChar(char src){
        return getHeadByChar(src,true);
    }
    
    public  static String[] getHeadByString(String src){
        return getHeadByString( src, true);
    }
    
    public  static String[] getHeadByString(String src,boolean isCapital){
        return getHeadByString( src, isCapital,null);
    }
    
    public  static String[] getHeadByString(String src,boolean isCapital,String separator){
        char[]chars=src.toCharArray();
        String[] headString=new String[chars.length];
        int i=0;
        for(char ch:chars){
            char[]chs=getHeadByChar(ch,isCapital);
            StringBuffer sb=new StringBuffer();
            if(null!=separator){
                int j=1;
                
                for(char ch1:chs){
                    sb.append(ch1);
                    if(j!=chs.length){
                        sb.append(separator);
                    }
                    j++;
                }
            }else{
                sb.append(chs[0]);
            }
            headString[i]=sb.toString();
            i++;
        }
        return headString;
    }
}
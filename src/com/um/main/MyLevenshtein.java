package com.um.main;

import java.util.List;

import com.um.classify.CWRelationMapping;
import com.um.model.EHealthRecord;

public class MyLevenshtein {

	
	
	 /** 
     * 　　DNA分析 　　拼字检查 　　语音辨识 　　抄袭侦测 
     *  
     * @createTime 2012-1-12 
     */  
    public static void levenshtein(String str1,String str2) {  
        //计算两个字符串的长度。  
        int len1 = str1.length();  
        int len2 = str2.length();  
        //建立上面说的数组，比字符长度大一个空间  
        int[][] dif = new int[len1 + 1][len2 + 1];  
        //赋初值，步骤B。  
        for (int a = 0; a <= len1; a++) {  
            dif[a][0] = a;  
        }  
        for (int a = 0; a <= len2; a++) {  
            dif[0][a] = a;  
        }  
        //计算两个字符是否一样，计算左上的值  
        int temp;  
        for (int i = 1; i <= len1; i++) {  
            for (int j = 1; j <= len2; j++) {  
                if (str1.charAt(i - 1) == str2.charAt(j - 1)) {  
                    temp = 0;  
                } else {  
                    temp = 1;  
                }  
                //取三个值中最小的  
                dif[i][j] = min(dif[i - 1][j - 1] + temp, dif[i][j - 1] + 1,  
                        dif[i - 1][j] + 1);  
            }  
        }  
//        System.out.println("字符串\""+str1+"\"与\""+str2+"\"的比较");  
        //取数组右下角的值，同样不同位置代表不同字符串的比较  
//        System.out.println("差异步骤："+dif[len1][len2]);  
        //计算相似度  
        float similarity =1 - (float) dif[len1][len2] / Math.max(str1.length(), str2.length());  
        System.out.println("相似度："+similarity);  
    }  
  
    //得到最小值  
    private static int min(int... is) {  
        int min = Integer.MAX_VALUE;  
        for (int i : is) {  
            if (min > i) {  
                min = i;  
            }  
        }  
        return min;  
    }  
    
    public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		 String str1 = "2012年7月24日于广东省人民医院行左上肺癌手术，病理：侵润性腺癌，pT1N0M0，ERCC1（+），VEGF（+-），ALK（-），EGFR（++）。现少咳，胃脘不适，纳眠可，二便调。舌质淡，舌苔微黄，脉滑";  
	     String str2 = "左肺恶性肿瘤（cT4N3M1，中分化腺癌）双肺转移，纵隔及左肺门多发淋巴结转移；埃克替尼治疗中；右乳腺恶性肿瘤术后。20111112于顺德第一人民医院纤支镜：左肺癌，病理：中分化腺癌。20111110陆总PET-CT：右肺上叶周围型癌并双肺弥漫转移，纵隔及左肺门多发淋巴结转移，右侧乳腺小结节，不除外恶性病变。2011－12－21开始口服埃克替尼。2010105我院乳腺钼靶：右乳外下象限病灶，考虑乳腺癌。20120216CT：对比旧片，肺左舌叶病灶、双肺转移灶、纵隔淋巴结及心包积液明显吸收减小。CEA:33.6，CA125:206.3。2012-3－18外院行右乳肿物切除术，病理：右侧乳腺浸润性导管癌，ER、PR（－），CerbB-2（＋＋＋）。2012-5-12复查CEA:21.68，CA125:134.7，CT提示原发灶及转移灶较前缩小。201200802复查CEA:16.3，CA125:88.6，CT提示原发灶稳定，转移灶较前缩小。20120922CT评价NC，CA125：74.6。现右侧肢体麻木，下肢抽搐缓解，纳眠可，二便调。舌质淡，舌苔微黄，脉弦细";  
	     levenshtein(str1,str2);
	     
	     CWRelationMapping cwRelationMapping = new CWRelationMapping();
			/**
			 * 1.病历信息
			 */
		List<EHealthRecord> eHealthList = cwRelationMapping.queryEhealthData();
		
		for(EHealthRecord eHealthRecord : eHealthList){
			levenshtein(str1, eHealthRecord.getConditionsdescribed());
		}
	     
	}

}

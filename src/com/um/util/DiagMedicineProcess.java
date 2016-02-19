package com.um.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.Map.Entry;
import java.util.Set;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import com.um.classify.CMDescriptionClassify;
import com.um.classify.CWRelationMapping;
import com.um.classify.DiagnosticsClassify;
import com.um.data.DiagClassifyData;
import com.um.model.ChineseMedicine;
import com.um.model.EHealthRecord;
import com.um.mongodb.converter.MedicineStatics;

public class DiagMedicineProcess {
	
	
	/**
	 * 构建关键字参照表
	 * @return
	 */
	public static Map<String, HashMap<String,ArrayList<String>>> creatrReference(String[] keywords){
		// 1.构造参照表
		Map<String, HashMap<String,ArrayList<String>>> keyMap = new HashMap<String, HashMap<String,ArrayList<String>>>();
		for(String key : keywords){
			String[] projects = key.split("%");// 0:部位 1:描述
			String[] descriptions = projects[1].split("#"); //不同描述
			HashMap<String, ArrayList<String>> descMap = new HashMap<String, ArrayList<String>>();
			for(String s : descriptions){
				String[] desc = s.split(":");
				String[] descKey = desc[1].split("\\|");
				ArrayList<String> descList = (ArrayList<String>) DiagMedicineProcess.arrayToList(descKey);
				descMap.put(desc[0], descList);
			}
			keyMap.put(projects[0], descMap);
		}
		return keyMap;
	}
	
	/**
	 *  统计一组出现概率的并集 大于percent的中药
	 * @param medicineMap   ----- 中药统计
	 * @param length    --------- 病历数量
	 * @param percent   --------- 出现概率
	 * @return： 多个中药名称        
	 */
	public static List<String> accumulateMedicines(Map<String, Integer> medicineMap,int length,double percent){
		if(medicineMap == null || medicineMap.isEmpty()){
			return null;
		}
		List<String> medicineList = new ArrayList<String>(); // 中药名称
		Set<String> keys = medicineMap.keySet();
		System.out.println("[keys] :" + keys);
		int accumulate = 0;
		for(String s : keys){
			if((1.0 * accumulate / length) >= percent){
				break;
			}
			accumulate += (Integer)medicineMap.get(s);
			medicineList.add(s);
		}
		return medicineList;
	}
	
	/**
	 *  统计一组出现概率的并集 大于percent的中药
	 * @param medicineMap   ----- 中药统计
	 * @param length    --------- 病历数量
	 * @param percent   --------- 出现概率
	 * @return： 多个中药名称        
	 */
	public static List<String> accumulateMedicines(Map<String, Integer> medicineMap,List<EHealthRecord> allList,double percent){
		if(medicineMap == null || medicineMap.isEmpty() || allList == null || allList.isEmpty()){
			return null;
		}
		List<String> results = new ArrayList<String>(); // 结果
		Set<String> medicineNameSet = medicineMap.keySet(); // 当前全部的中药名称
		
		// 根据某一组中药的出现概率的并集是否大于100%来判断这组中药是否可以作为结果输出
		
		List<String> accumuMedicineList = new ArrayList<String>(); //中药分组
		
		Iterator<String> iterator = medicineNameSet.iterator();
		while(iterator.hasNext()){
//			System.out.println(iterator.next());
			// 1. 构建新的分组
			accumuMedicineList.add(iterator.next()); // 构建分组
			// 2. 判断该分组是否符合条件 : 并集的数量 >= allList.size()
			// 		2.1 计算并集的数量
			int union = EhealthRecordMath.getUnion(accumuMedicineList, allList); // 求中药分组的并集（符合条件的病例数）
			// 		2.2 判断 ：并集的数量 >= allList.size()
			if(union >= allList.size() * percent){
				results = accumuMedicineList;
				break;
			}
			// 3. 若符合，则结束循环，输出
			// 4. 不符合，继续循环，构建分组
		}
		System.out.println(results);
		return results;
	}
	
	
    /**
     *  计算分组后的中药之间的相关性
     *    输入：       1） 分组中药； 2） 描述
     *    输出： 选中的中药
     * 
     *  原理：
     *    1、完成对中药分组后，该组中药的出现概率的并集大于 90%，说明该组中药中至少有一种中药出现在最终结果中；
     *    2、在根据中药处方统计数据（交集）以及中医病症描述（基于对中医症状的描述的统计），进行分析；
     *    3、根据分析结果，确定从该组中药中需要输出哪几种中药；
     * @param medicineMap
     * @param allList
     * @param description
     * @return
     */
    public static List<String> accuMedicineRelation(Map<String, Integer> medicineMap,List<EHealthRecord> allList ,String description ){
           if( medicineMap == null || medicineMap.isEmpty() || allList == null || allList .isEmpty() || description =="" ){
                 return null;
          }
          List<String> resultsList = null; // 中药名称
          // 1. 分析中药之间的相关性
          
          // 2. 根据中医描述进行分析
          
          
          return resultsList;
    }
	
	
	/**
	 * 	统计出现概率大约 percent的中药
	 * 
	 * @param medicines----- 中药处方统计结果<名称，数量>
	 * @param length-------- 全部病历数量
	 * @param percent------- 百分比
	 * @return
	 */
	public static List<String> statisMedicineNinePercent(Map<String, Integer> medicines,int length,double percent){
		if(medicines == null || medicines.isEmpty()){
			return null;
		}
		List<String> medicineList = new ArrayList<String>(); // 中药名称
		
		Set<String> keys = medicines.keySet();
		
		for( String s : keys ){
			int count = (Integer)medicines.get(s);
			if((count * 1.0 / length) >= percent){
				// 大于percent
				medicineList.add(s);
			}
		}
		return medicineList;
	}
	
	
	/**
	 *  统计中药出现的概率
	 * @param medicines
	 * @return
	 */
	public static String statisMedicProbability(String medicines){
		if(medicines == ""){
			return null;
		}
		
		// 1. 拆分中药名称
		
		String[] names = medicines.split(" ");
		if(names == null || names.length == 0){
			return null;
		}
		
		// 2、统计中药
		
		List<EHealthRecord> results = null;
		
		// 2.1、获取全部病历数据
		CWRelationMapping cMapping = new CWRelationMapping();		
		List<EHealthRecord> allRecrods = cMapping.queryEhealthData();
		
		if(allRecrods == null || allRecrods.size() == 0){
			return null;
		}
		
		results = new ArrayList<EHealthRecord>();
		
		// 2.2、判断是否同时出现在同一病历中
		for(EHealthRecord e : allRecrods){
			if(hasThisMedicine(e, names)){
				//同时出现在同一病历中
				results.add(e);
			}
		}
		
		// 3. 整理统计结果
		
		if(results == null || results.size() == 0){
			return null;
		}
		
		int count = results.size();
		double percent = 1.0 * count / allRecrods.size();
		System.out.println("[all record]: " + allRecrods.size());
		return count + "|" + percent;
	}
	
	/**
	 *  判断是否同时出现所有的中药
	 * @param e
	 * @param names
	 * @return
	 */
	public static boolean hasThisMedicine(EHealthRecord e,String[] names){
		if(e == null || e.getChineseMedicines() == null || 
				e.getChineseMedicines().size() == 0 || names == null || names.length == 0){
			return false;
		}
		
		List<ChineseMedicine> allMedicines = e.getChineseMedicines(); // 中药处方
		boolean hasMedicine = true;
		boolean flag = false;
		int length = allMedicines.size();
		for(String s : names){
			flag = false;
			for(int i = 0; i < length; i++){
				if(s.equals(allMedicines.get(i).getNameString())){
					flag = true;// 同时出现则为true = 只要有一个不出现就为false 
				}
				if(!flag && i == length - 1){
					flag = false;
				}
			}
			hasMedicine = hasMedicine && flag;
		}
		
		return hasMedicine;
	}
	
	/**
	 *  统计
	 * @param cmd
	 * @return
	 */
	public static Map<String, Integer> statisMedicineWithCMDescription(CMDescriptionClassify cmd){
		if(cmd == null || cmd.geteHealthRecords() == null || cmd.geteHealthRecords().size() == 0){
			return null;
		}
		
		Map<String, Integer> cnmedicineMap = new HashMap<String, Integer>(); // 中药处方统计
		Set<String> medicineSet = new TreeSet<String>();
		// 属于该类型的病历
		List<EHealthRecord> ehealthList = cmd.geteHealthRecords();
		for(EHealthRecord e : ehealthList){
			if(e.getChineseMedicines() != null && e.getChineseMedicines().size() > 0 ){
				for(ChineseMedicine c : e.getChineseMedicines()){
					if(medicineSet.add(c.getNameString())){
						// 新的中药
						cnmedicineMap.put(c.getNameString(), 1);
					}else{
						// set 已经有了中药
						int count = (Integer)cnmedicineMap.get(c.getNameString());
						count++;
						cnmedicineMap.remove(c.getNameString());
						cnmedicineMap.put(c.getNameString(), count);
					}
				}
			}
		}
		System.out.println(cnmedicineMap);
		return cnmedicineMap;
	}
	
	
	/**
	 *  根据描述，匹配类型
	 * @param desc
	 * @param cmdList
	 * @return
	 */
	public static CMDescriptionClassify matchDescriptionClassify(String desc,List<CMDescriptionClassify> cmdList){
		if(desc == "" || cmdList == null || cmdList.size() == 0){
			return null;
		}
		System.out.println("desc :" + desc);
		// 匹配
		int maxcount = 0; // 最大匹配长度		
		int index = -1;    // cmdList 索引		
		int length = cmdList.size();
		
		for(int i = 0; i < length; i++ ){
			
			int count = 0;    // 匹配长度			
			String[] keywords = cmdList.get(i).getKeywords(); // 关键字
			
			for(String s : keywords ){
				if(desc.matches(".*" + s + ".*")){
					count++;
				}				
			}
			if(count > maxcount){
				maxcount = count;
				index = i;
			}
		}
		System.out.println("[index] :" + index);
		if(index != -1){
			return cmdList.get(index);
		}else{
			return null;
		}
	}
	
	/**
	 *  根据病症判断病症类型
	 *  1、根据输入病症描述，与数据库中的病历数据进行文本相似度计算；
	 * 	2、 确定最为相似的病历；
	 * 	3、确定该病历的诊断类型；
	 * 	4、返回诊断类型；
	 * @param diag：诊断
	 * @param diagnosticsClassifies：诊断分类
	 * @return
	 */
	public static DiagnosticsClassify matchDiagnosticsClassify(String diag, List<DiagnosticsClassify> diagnosticsClassifies){
		if (diag == "" || diag == null || diagnosticsClassifies == null || diagnosticsClassifies.size() == 0) {
			return null;
		}
		System.out.println("[diags] :" + diag);
		DiagnosticsClassify result = null;
		
		DiagnosticsClassify tmp;
		int length = diagnosticsClassifies.size();
		int maxMatchNum = 0; // 关键字最大匹配数
		for(int i = 0; i < length; i++){
			tmp = diagnosticsClassifies.get(i);
			if(maxDiagMatchNum(diag,tmp) > maxMatchNum){
				result = tmp;
				maxMatchNum = maxDiagMatchNum(diag,tmp);
			}
		}
		System.out.println("[max match num] :" + maxMatchNum);
		return result;
	}
	
	/**
	 *  根据病症判断病症类型
	 *  1、根据输入病症描述，与数据库中的病历数据进行文本相似度计算；
	 * 	2、 确定最为相似的病历；
	 * 	3、确定该病历的诊断类型；
	 * 	4、返回诊断类型；
	 * @param diag：诊断
	 * @param diagnosticsClassifies：诊断分类
	 * @return
	 * @throws IOException 
	 */
	public static DiagnosticsClassify matchDiagnostics(String diag) throws IOException{
		if(diag == "" || diag == null){
			return null;
		}
		
		CWRelationMapping cwRelationMapping = new CWRelationMapping();
		// 1.   读取病历信息
		List<EHealthRecord> eHealthList = cwRelationMapping.queryEhealthData();
		// 2. 诊断类型构建
		List<DiagnosticsClassify> chineseDiagnostics = cwRelationMapping.createDiagnostics(DiagClassifyData.cnDiagClassify); // 中医诊断分类
		// 3. 中医诊断分类
		cwRelationMapping.chineseDiagnosticsClassify(eHealthList,chineseDiagnostics);//中医诊断分类
		// 4. 病症描述相似度计算
		double maxSimilarity = -1.0; //最大相似度
		String maxRegNo = ""; // 最大相似度病历的挂号号
		
		for(EHealthRecord eHealthRecord : eHealthList){
			if(eHealthRecord.getConditionsdescribed() != ""){
				double similarity = levenshtein(diag, eHealthRecord.getConditionsdescribed());
				if(similarity > maxSimilarity){
					maxSimilarity = similarity;
					maxRegNo = eHealthRecord.getRegistrationno();
				}
			}
		}
		
		// 5. 得到最相似的病历后，确定该病历的诊断类型
		
		DiagnosticsClassify resultClassify = null;
		
		if(maxRegNo != ""){
			for(DiagnosticsClassify d:chineseDiagnostics){
				if(d.geteHealthRecords() != null && d.geteHealthRecords().size() > 0){
					for(EHealthRecord e : d.geteHealthRecords()){
						if(e.getRegistrationno().equals(maxRegNo)){
							resultClassify = d;
						}
					}
				}
			}
		}
		return resultClassify;
	}
	
	/**
	 *  病症匹配
	 * @param diagString
	 * @param diagnosticsClassify
	 * @return 
	 */
	public static int maxDiagMatchNum(String diagString,DiagnosticsClassify diagnosticsClassify){
		
		if(diagString == "" || diagString == null || diagnosticsClassify == null){
			return 0;
		}
		
		String[] keywords = diagnosticsClassify.getKeywrods();
		int length = keywords.length;
		int count = 0; // 关键字匹配次数
		for(int i = 0; i < length; i++){
			if(diagString.matches(".*" + keywords[i] + ".*")){
				//
				count++;
			}
		}
		return count;
	}
	
	/**
	 *  统计病历list中的中药处方的数据
	 * @param eHealthRecords
	 * @return Map<中药名称，数量>
	 */
	public static Map<String, Integer> statisEhealthMedicine(List<EHealthRecord> eHealthRecords){
		if(eHealthRecords == null || eHealthRecords.size() == 0){
			return null;
		}
		//1、统计list中所有的中药名称
		List<String> allCnMedicines = new ArrayList<String>(); // 所有的中药名称（包含重复的相）
		for(EHealthRecord eRecord : eHealthRecords){
			if(eRecord.getChineseMedicines() != null && eRecord.getChineseMedicines().size() > 0){
				for(ChineseMedicine c : eRecord.getChineseMedicines()){
					allCnMedicines.add(c.getNameString());
				}
			}
		}
		
		//2、依次统计重复的名称
		Map<String, Integer> statisMedicines = MedicineStatics.staticsChineseMedicine(allCnMedicines);
		// 3. 修正统计结果
		statisMedicines = DiagMedicineProcess.correctMap(statisMedicines);
		// 4. 排序
		statisMedicines = DiagMedicineProcess.sortMapByValue(statisMedicines);
		// 5. 返回结果
		return statisMedicines;
	}
	 /**
     *  修正中药统计结果-----防止多味中药重复统计
     * @param oldMap
     * @return
     */
    public static Map<String, Integer> correctMap(Map<String, Integer> oldMap){
     if(oldMap == null || oldMap.size() == 0){
      return null;
     }
     Map<String, Integer> newMap = new HashMap<String, Integer>();
     
     Set<String> keySet = oldMap.keySet();
     
     List<String> keyList = new ArrayList<String>();
     
     for(String string : keySet){
      keyList.add(string);
     }
     
     for(int i = 0; i < keyList.size(); i++ ){
      boolean hasCorrect = false;
      for(int j = i + 1; j < keyList.size(); j++ ){
       if(keyList.get(i).matches(".*" + keyList.get(j) +".*") || keyList.get(j).matches(".*" + keyList.get(i) +".*")){
        int pre = oldMap.get(keyList.get(i));
        int post = oldMap.get(keyList.get(j));
//     System.out.println(pre + " : " + post);
        newMap.put(keyList.get(i), pre + post);
        keyList.remove(j);
        j--;
        hasCorrect = true;
       }else if( j == keyList.size() - 1 && !hasCorrect){
        newMap.put(keyList.get(i), oldMap.get(keyList.get(i)));
       }
      }
     }
     newMap = DiagMedicineProcess.sortMapByValue(newMap);
     return newMap;
    }

	/**
	 *  对病历数据进行分类，并对类型进行编码
	 * @param keywords
	 * @param eHealthRecords
	 * @return
	 */
	public static Map<EHealthRecord, String> classifyEhealthMap(List<String> keywords,List<EHealthRecord> eHealthRecords){
		if(keywords == null || keywords.size() == 0 || eHealthRecords == null || eHealthRecords.size() == 0){
			return null;
		}
		Map<EHealthRecord, String> resultMap = new HashMap<EHealthRecord, String>();
		
		for(EHealthRecord e : eHealthRecords){
			String descString = "";
			String codeString = "";
			for(String k : keywords){
				// xx|k1
				String[] strings = k.split("\\|");
				if(e.getConditionsdescribed().matches(".*" + strings[0] + ".*")){
					descString += strings[0] + " ";
					codeString += strings[1] + " ";
				}
			}
			if(descString != "" && codeString != ""){
				resultMap.put(e, descString + "|" + codeString);
			}
		}
		
		return resultMap;
	}
	
	 /** 
     * 　　DNA分析 　　拼字检查 　　语音辨识 　　抄袭侦测 
     *  
     * @createTime 2012-1-12 
     */  
    public static double levenshtein(String str1,String str2) {  
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
        
        return similarity;  
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
    /**
     *  根据list 去掉map中的某些数据
     * @param maps
     * @param list
     * @return
     */
    public static Map<String, Integer> removeMapOfList(Map<String,Integer> maps,List<String> list){
    	if(maps == null || maps.isEmpty()){
    		return null;
    	}
    	if(list == null || list.isEmpty()){
    		return maps;
    	}
    	
    	for(String s : list){
    		maps.remove(s);
    	}
    	return maps;
    }
    
    /**
     *  按值对map进行排序
     * @param orimap
     * @return
     */
    public static Map<String, Integer> sortMapByValue(Map<String, Integer> orimap){
    	if(orimap == null || orimap.isEmpty()){
    		return null;
    	}
    	
    	Map<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();
    	
    	List<Map.Entry<String, Integer>> entryList = new ArrayList<Map.Entry<String,Integer>>(orimap.entrySet());
    	
    	Collections.sort(entryList,
    			new Comparator<Map.Entry<String,Integer>>(){

					@Override
					public int compare(Entry<String, Integer> o1,
							Entry<String, Integer> o2) {
						// TODO Auto-generated method stub
						int value1 = 0,value2 = 0;
						try {
							value1 = o1.getValue();
							value2 = o2.getValue();
						} catch (NumberFormatException e) {
							// TODO: handle exception
							value1 = 0;
							value2 = 0;
						}
						return value2 - value1;
					}
    	});
    	Iterator<Map.Entry<String, Integer>> iterator = entryList.iterator();
    	
    	Map.Entry<String, Integer> tmpEntry = null;
    	while (iterator.hasNext()) {

    		tmpEntry = iterator.next();
    		sortedMap.put(tmpEntry.getKey(), tmpEntry.getValue());
		}
    	return sortedMap;
    }
    
    /**
     *  汉字转拼音
     * @param string
     * @return
     * @throws BadHanyuPinyinOutputFormatCombination
     */
    public static String getPinyinString(String string) throws BadHanyuPinyinOutputFormatCombination{
		if(string == ""){
			return "";
		}
		String pinyin = "";
		HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
		format.setCaseType(HanyuPinyinCaseType.LOWERCASE); //小写字符
		format.setToneType(HanyuPinyinToneType.WITHOUT_TONE); // 无音标
		format.setVCharType(HanyuPinyinVCharType.WITH_V); // nv
		
		char[] chars = string.toCharArray();
		
		for(char c : chars){
			pinyin += PinyinHelper.toHanyuPinyinStringArray(c, format)[0];
		}
		return pinyin ;
	}
	
    /**
     *  convert array to list
     * @param arrays
     * @return
     */
    public static List<String> arrayToList(String[] arrays){
    	if(arrays == null || arrays.length == 0){
    		return null;
    	}
    	int length = arrays.length;
    	List<String> results = new ArrayList<String>(length);
    	
    	for( int i = 0; i < length; i++ ){
    		results.add(arrays[i].trim());
    	}
    	
    	return results;
    }
	
    /**
	 *  string to array （split with 空格）
	 * @param string
	 * @return
	 */
	public static String[]  stringToArray(String string){
		if(string == ""){
			return null;
		}
		if(string.split(" ") != null){
			return string.split(" ");
		}
		return null;
	}
	
	public static void main(String[] args){
	}
}

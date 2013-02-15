import java.io.*;
import java.util.*;
import java.util.Map.Entry;


// this code actually generates more than just 5 top keys and also the values themselves 
public class Apriori {
	private final static int freq = 100;
	
	public static void calcSingles(String[] input, HashMap<String, Integer> map) {
		int length = input.length;
		String key;
		Integer value;
		for (int i = 0; i < length; i++) {
			key = input[i];
			if (map.containsKey(key)) {
				value = map.get(key);
				value++;
				map.put(key, value);
			}
			else map.put(key, 1);
		}
	}
	
	public static void pruneSingles(HashMap<String, Integer> map) {
		Integer value;
		Iterator<String> it = map.keySet().iterator();

		while (it.hasNext())
		{
		  value = map.get(it.next());
		  if (value < freq)
		    it.remove();
		 }
		
	}
	
	public static void makePairs(HashMap<String, Integer> map1, HashMap<Set<String>, Integer> map2) {

		String key1, key2;
		String[] mapArray = map1.keySet().toArray(new String[0]);
		int length = mapArray.length;
		for (int i = 0; i < length; i++) {
			for (int j = i+1; j < length; j++) {
				Set<String> set = new HashSet<String>();
				key1 = mapArray[i];
				key2 = mapArray[j];
				set.add(key1);
				set.add(key2);
				map2.put(set, 0);
			}
		}
	}
	
	public static void calcPairs(String[] input, HashMap<Set<String>, Integer> map2) {
		String key1, key2;
		Integer value;
		int length = input.length;
		for (int i = 0; i < length; i++) {
			for (int j = i+1; j < length; j++) {
				Set<String> set = new HashSet<String>();
				key1 = input[i];
				key2 = input[j];
				set.add(key1);
				set.add(key2);
				if (map2.containsKey(set)) {
					value = map2.get(set);
					value++;
					map2.put(set, value);
				}
			}
		}
	}
	
	public static void pruneSets(HashMap<Set<String>, Integer> map) {
		Integer value;
		Iterator<Set<String>> it = map.keySet().iterator();

		while (it.hasNext())
		{
		  value = map.get(it.next());
		  if (value < freq)
		    it.remove();
		 }
		
	}
	
	public static void makeTriples(HashMap<Set<String>, Integer> map2, HashMap<Set<String>, Integer> map3) {
		Set set1, set2;
		Set[] mapArray2 = map2.keySet().toArray(new Set[0]);
		int length = mapArray2.length;
		
		for (int i = 0; i < length; i++) {
			for (int j = i+1; j < length; j++) {
				set1 = mapArray2[i];
				set2 = mapArray2[j];
				Set<String> set3 = new HashSet<String>();
				set3.addAll(set1);
				set3.addAll(set2);
				if (set3.size() == 3) {
					map3.put(set3, 0);
				}
			}
		}		
	}
	
	public static void calcTriples(String[] input, HashMap<Set<String>, Integer> map3) {
		String key1, key2, key3;
		Integer value;
		int length = input.length;
		for (int i = 0; i < length; i++) {
			for (int j = i+1; j < length; j++) {
				for (int k = j+1; k < length; k++) {
					Set<String> set = new HashSet<String>();
					key1 = input[i];
					key2 = input[j];
					key3 = input[k];
					set.add(key1);
					set.add(key2);
					set.add(key3);
					if (map3.containsKey(set)) {
						value = map3.get(set);
						value++;
						map3.put(set, value);
					}
				}
			}
		}
	}
	
	public static String makePair(String a, String b) {
		return (a + " -> " + b);
	}
	
	public static String make2to1triple(String a, String b, String c) {
		return ("(" + a + ", " + b + ")" + " -> " + c);
	}
	
	public static String make1to2triple(String a, String b, String c) {
		return (a + " -> " + "(" + b + ", " + c + ")");
	}
	
	// add (A -> B) keys to the confMap and to the liftMap and convMap
	public static void confLiftPairs (HashMap<String, Integer> map1, HashMap<Set<String>, Integer> map2, HashMap<String, Double> confMap, HashMap<String, Double> liftMap, HashMap<String, Double> convMap, int numLines) {
		Iterator<Set<String>> it = map2.keySet().iterator();
		String x, y, pair_xy, pair_yx; 
		Set<String> key;
		int value_x, value_y, value_xy;
		double supp_x, supp_y, conf_xy, conf_yx, lift_xy, conv_xy, conv_yx;
		String[] setArray;
		
		while (it.hasNext()) {
			key = it.next();
			setArray = key.toArray(new String[0]);
			x = setArray[0];
			y = setArray[1];
			value_x = map1.get(x);
			value_y = map1.get(y);
			value_xy = map2.get(key);
			conf_xy = (double)value_xy/(double)value_x;
			conf_yx = (double)value_xy/(double)value_y;
			supp_x = (double)value_x/(double)numLines;
			supp_y = (double)value_y/(double)numLines;
		

			lift_xy = (double)conf_xy/(double)supp_y;
			
			conv_xy = (1.0 - supp_y)/(1.0 - conf_xy);
			conv_yx = (1.0 - supp_x)/(1.0 - conf_yx);
			

			
			pair_xy = makePair(x, y);
			pair_yx = makePair(y, x);
			
			confMap.put(pair_xy, conf_xy);
			confMap.put(pair_yx, conf_yx);
			liftMap.put(pair_xy, lift_xy);
			convMap.put(pair_xy, conv_xy);
			convMap.put(pair_yx, conv_yx);
		}
	}
	
	// add (A -> B,C) and (A -> B,C) keys to the confMap and liftMap	
	public static void confLiftTriples (HashMap<String, Integer> map1, HashMap<Set<String>, Integer> map2, HashMap<Set<String>, Integer> map3, HashMap<String, Double> confMap, HashMap<String, Double> liftMap, HashMap<String, Double> convMap, int numLines) {
		Iterator<Set<String>> it = map3.keySet().iterator();
		String a, b, c, triple_ab_c, triple_bc_a, triple_ac_b, triple_a_bc, triple_b_ac, triple_c_ab; 
		Set<String> key;
		int value_a, value_b, value_c, value_ab, value_bc, value_ac, value_abc;
		double conf_a_bc, conf_b_ac, conf_c_ab, conf_ab_c, conf_bc_a, conf_ac_b;
		double lift_a_bc, lift_b_ac, lift_c_ab, conv_ab_c, conv_bc_a, conv_ac_b, conv_a_bc, conv_b_ac, conv_c_ab;
		
		while (it.hasNext()) {
			key = it.next();
			String[] setArray = key.toArray(new String[0]);
			a = setArray[0];
			b = setArray[1];
			c = setArray[2];
			
			value_a = map1.get(a);
			value_b = map1.get(b);
			value_c = map1.get(c);
			
			Set<String> set_ab = new HashSet<String>();
			Set<String> set_bc = new HashSet<String>();
			Set<String> set_ac = new HashSet<String>();
			
			set_ab.add(a);set_ab.add(b);
			set_bc.add(b);set_bc.add(c);
			set_ac.add(a);set_ac.add(c);
			
			value_ab = map2.get(set_ab);
			value_bc = map2.get(set_bc);
			value_ac = map2.get(set_ac);
			
			value_abc = map3.get(key);
			
			conf_a_bc = (double)value_abc/(double)value_a;
			conf_b_ac = (double)value_abc/(double)value_b;
			conf_c_ab = (double)value_abc/(double)value_c;
			conf_ab_c = (double)value_abc/(double)value_ab;
			conf_bc_a = (double)value_abc/(double)value_bc;
			conf_ac_b = (double)value_abc/(double)value_ac;
			
			lift_a_bc = conf_a_bc/(double)value_bc*(double)numLines;
			lift_b_ac = conf_b_ac/(double)value_ac*(double)numLines;
			lift_c_ab = conf_c_ab/(double)value_ab*(double)numLines;
			
			conv_ab_c = (1.0 - ((double)value_c)/(double)numLines)/(1.0 - conf_ab_c);
			conv_bc_a = (1.0 - ((double)value_a)/(double)numLines)/(1.0 - conf_bc_a);
			conv_ac_b = (1.0 - ((double)value_b)/(double)numLines)/(1.0 - conf_ac_b);
			
			conv_a_bc = (1.0 - ((double)value_bc)/(double)numLines)/(1.0 - conf_a_bc);
			conv_b_ac = (1.0 - ((double)value_ac)/(double)numLines)/(1.0 - conf_b_ac);
			conv_c_ab = (1.0 - ((double)value_ab)/(double)numLines)/(1.0 - conf_c_ab);
			
			triple_ab_c = make2to1triple(a,b,c);
			triple_bc_a = make2to1triple(b,c,a);
			triple_ac_b = make2to1triple(a,c,b);
			
			triple_a_bc = make1to2triple(a,b,c);
			triple_b_ac = make1to2triple(b,c,a);
			triple_c_ab = make1to2triple(c,a,b);
			
			confMap.put(triple_a_bc, conf_a_bc);confMap.put(triple_b_ac, conf_b_ac);
			confMap.put(triple_c_ab, conf_c_ab);confMap.put(triple_ab_c, conf_ab_c);
			confMap.put(triple_bc_a, conf_bc_a);confMap.put(triple_ac_b, conf_ac_b);
			
			liftMap.put(triple_a_bc, lift_a_bc);
			liftMap.put(triple_b_ac, lift_b_ac);
			liftMap.put(triple_c_ab, lift_c_ab);
			
			convMap.put(triple_a_bc, conv_a_bc);
			convMap.put(triple_b_ac, conv_b_ac);
			convMap.put(triple_c_ab, conv_c_ab);
			
			convMap.put(triple_ab_c, conv_ab_c);
			convMap.put(triple_bc_a, conv_bc_a);
			convMap.put(triple_ac_b, conv_ac_b);
		}
	}
	
	public static String mapOutput(HashMap<String, Double> map1) {
		
		HashMap<Object, Object> map = new HashMap<Object, Object>();
		map.putAll(map1);
		Set<Entry<Object, Object>> mapSet = map.entrySet();
		
        LinkedList<LinkedList<Object>> grandList = new LinkedList<LinkedList<Object>>();
        LinkedList<Object> list;
        Object key1;
        Double value1;
        int index;
        for (Entry<Object, Object> entry : mapSet) {
            key1 = entry.getKey();
            value1 = (Double)entry.getValue();
            index = 0;
            // go through linked list until finding a list whose second element is smaller than this value and insert this user as the tail of this list, the head of the list is the userCount
            while (index < grandList.size() && (Double)((grandList.get(index)).get(0)) > value1) {
                index++;
            }
            // if you have reached end of list or next element is strictly smaller, add a new list at this index of the grandList with userCount as head and userID as tail
            if (index == grandList.size() || (Double)((grandList.get(index)).get(0)) < value1) {
                list = new LinkedList<Object>();
                list.add(value1);
                list.add(key1);
                grandList.add(index, list);
            }
            // if it is not end of list and the next element has head value equal to the userCount of this user, append this userID to that list and sort the linked list
            else {
                list = grandList.get(index);
                list.add(key1);
            }
        }
        
        String finalList = "";
        
        int count = 0;
        for (LinkedList<Object> l : grandList) {
            for (int j = 0; count < 10 && j < l.size(); j++) {    
                finalList += l.get(j) + "\n";
                count++;
            }
        }
     
        return finalList;
	}
	
	// 
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try{
			FileInputStream fstream = new FileInputStream(args[0]);
			// Get the object of DataInputStream
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			String[] input;
			
			HashMap<String, Integer> map1 = new HashMap<String, Integer>();

			
			int numLines = 0;
			//Read File Line By Line
			while ((strLine = br.readLine()) != null) {
				// Print the content on the console
				input = strLine.split("[ ]");
				calcSingles(input, map1);
				numLines++;
			}
			pruneSingles(map1);
			
			HashMap<Set<String>, Integer> map2 = new HashMap<Set<String>, Integer>(numLines);
			HashMap<Set<String>, Integer> map3 = new HashMap<Set<String>, Integer>(numLines);
			HashMap<String, Double> confMap2 = new HashMap<String, Double>(numLines);
			HashMap<String, Double> confMap3 = new HashMap<String, Double>(numLines);
			HashMap<String, Double> liftMap2 = new HashMap<String, Double>(numLines);
			HashMap<String, Double> liftMap3 = new HashMap<String, Double>(numLines);
			HashMap<String, Double> convMap2 = new HashMap<String, Double>(numLines);
			HashMap<String, Double> convMap3 = new HashMap<String, Double>(numLines);
			
			makePairs(map1, map2);	
			
			fstream.close();

			fstream = new FileInputStream(args[0]);
			in = new DataInputStream(fstream);
			br = new BufferedReader(new InputStreamReader(in));
			
			while ((strLine = br.readLine()) != null) {
				// Print the content on the console
				input = strLine.split("[ ]");
				calcPairs(input, map2);
			}
			pruneSets(map2);
			makeTriples(map2, map3);
			
			fstream.close();

			fstream = new FileInputStream(args[0]);
			in = new DataInputStream(fstream);
			br = new BufferedReader(new InputStreamReader(in));
			
			while ((strLine = br.readLine()) != null) {
				// Print the content on the console
				input = strLine.split("[ ]");
				calcTriples(input, map3);
			}
			pruneSets(map3);
			//Close the input stream

			fstream.close();

			in.close();
			
			confLiftPairs(map1, map2, confMap2, liftMap2, convMap2, numLines);
			confLiftTriples(map1, map2, map3, confMap3, liftMap3, convMap3, numLines);
			
			System.out.println("Top 2-Item Confidence Rules");
			System.out.println(mapOutput(confMap2));
			System.out.println();
			System.out.println("Top 3-Item Confidence Rules");
			System.out.println(mapOutput(confMap3));
			System.out.println();
			System.out.println("Top 2-Item Lift Rules");
			System.out.println(mapOutput(liftMap2));
			System.out.println();
			System.out.println("Top 3-Item Lift Rules");
			System.out.println(mapOutput(liftMap3));
			System.out.println();
			System.out.println("Top 2-Item Conviction Rules");
			System.out.println(mapOutput(convMap2));
			System.out.println();
			System.out.println("Top 3-Item Conviction Rules");
			System.out.println(mapOutput(convMap3));
			
		}
		catch (Exception e){//Catch exception if any
			  System.err.println("Error: " + e.getMessage());
		}
	}
}

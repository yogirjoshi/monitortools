package rithm.driver;


import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import org.antlr.runtime.EarlyExitException;



class MonResult{
	protected ArrayList<String> ObjectIDs;
	int count;
	public MonResult(){
		ObjectIDs = new ArrayList<String>();
		count = 1;
	}
}
class FOFinisher implements Function<Hashtable<ArrayList<String>, MonResult>, Hashtable<ArrayList<String>, MonResult>>{

	@Override
	public Hashtable<ArrayList<String>, MonResult> apply(
			Hashtable<ArrayList<String>, MonResult> t) {
		// TODO Auto-generated method stub
		return t;
	}

}	
class FOCCollector<T> implements Collector<MonResult, Hashtable<ArrayList<String>, MonResult>, Hashtable<ArrayList<String>, MonResult>> {
	
	protected ArrayList<Integer> keylist;
	
	public FOCCollector(ArrayList<Integer> keyList){
		keylist = keyList;
	}
	protected void merge(Hashtable<ArrayList<String>, MonResult> myMap, MonResult mResult){
		ArrayList<String> currList = new ArrayList<>();
		MonResult m ;
		for(int key: keylist){
			currList.add(key, mResult.ObjectIDs.get(key));
//			System.out.println(mResult.ObjectIDs.get(key));
		}
//		System.out.println("---------------------------------------------");
		if(myMap.containsKey(currList)){
			m = myMap.get(currList);
			m.count = m.count + 1;
			myMap.put(currList, m);
		}else{
			myMap.put(currList, mResult);
		}
	}
    @Override
    public Supplier<Hashtable<ArrayList<String>, MonResult>> supplier() {
        return Hashtable<ArrayList<String>, MonResult>::new;
    }

    @Override
    public BiConsumer<Hashtable<ArrayList<String>, MonResult>, MonResult> accumulator() {
        
    	return (builder, t1) -> {
    		merge(builder, t1);
    	};
    }

    @Override
    public BinaryOperator<Hashtable<ArrayList<String>, MonResult>> combiner() {
        return (b1, b2) -> {
        	b1.putAll(b2);
        	return b1;
        };
    }

    @Override
    public Function<Hashtable<ArrayList<String>, MonResult>, Hashtable<ArrayList<String>, MonResult>> finisher() {
        return new FOFinisher();
    }

    @Override
    public Set<Characteristics> characteristics() {
        return EnumSet.of(Characteristics.CONCURRENT);
    }
}
public class Hypothesis2{
//	public static MonResult createIDs(){
//		MonResult m = new MonResult();
//		m.ObjectIDs.add("1");
//		m.ObjectIDs.add("1");
//	}
	public static void main(String[] args) {
		ArrayList<MonResult> monResultList = new ArrayList<MonResult>();
		ArrayList<MonResult> monResultList1 = new ArrayList<MonResult>();

//		MonResult m1 = new MonResult();
//		m1.ObjectIDs.add("10");
//		m1.ObjectIDs.add("1");
//		m1.ObjectIDs.add("a");
//		monResultList.add(m1);
//		MonResult m2 = new MonResult();
//		m2.ObjectIDs.add("10");
//		m2.ObjectIDs.add("1");
//		m2.ObjectIDs.add("b");
//		monResultList.add(m2);
//		MonResult m3 = new MonResult();
//		m3.ObjectIDs.add("10");
//		m3.ObjectIDs.add("2");
//		m3.ObjectIDs.add("c");
//		monResultList.add(m3);
//		MonResult m4 = new MonResult();
//		m4.ObjectIDs.add("11");
//		m4.ObjectIDs.add("1");
//		m4.ObjectIDs.add("a");
//		monResultList.add(m4);
		ArrayList<Integer> collectList = new ArrayList<>();
		collectList.add(0);
		collectList.add(1);

		Hashtable<ArrayList<String>, MonResult> res;
		for(int t = 0; t < Integer.parseInt(args[4]);t++){
			for(int i = 0; i < Integer.parseInt(args[0]); i++ )
				for(int j = 0; j < Integer.parseInt(args[1]); j++)
					for(int k = 0; k < Integer.parseInt(args[2]);k++){
						MonResult m1 = new MonResult();
						m1.ObjectIDs.add(Integer.toString(i));
						m1.ObjectIDs.add(Integer.toString(100+j));
						m1.ObjectIDs.add(Integer.toString(10000+k));
						monResultList.add(m1);
					}
			
			long beg = System.nanoTime();
			if(Boolean.parseBoolean(args[3]))
				res = monResultList.stream().collect(new FOCCollector<>(collectList));
			else
				res = monResultList.parallelStream().collect(new FOCCollector<>(collectList));	
			
			monResultList1.addAll(res.values());
			collectList.remove(1);
			if(Boolean.parseBoolean(args[3]))
				res = monResultList1.stream().collect(new FOCCollector<>(collectList));
			else
				res = monResultList1.parallelStream().collect(new FOCCollector<>(collectList));	
			
			for(MonResult m : res.values()){
				System.out.println(m.count);
				break;
			}
			long end = System.nanoTime();
			res.clear();;monResultList.clear();
			System.out.println(end-beg);
		}
		System.out.println("Threads:" +  Thread.activeCount());
	}
}
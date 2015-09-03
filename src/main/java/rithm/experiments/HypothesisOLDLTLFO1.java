package rithm.experiments;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.lang.management.*;

import com.sun.management.OperatingSystemMXBean;

class MonResult{
	protected ArrayList<String> ObjectIDs;
	int count;
	public MonResult(){
		ObjectIDs = new ArrayList<String>();
		count = 1;
	}
}
class RedOperator implements BinaryOperator<MonResult>{

	@Override
	public MonResult apply(MonResult t, MonResult u) {
		// TODO Auto-generated method stub
		if(t != null && u != null)
			t.count = t.count + u.count;
		return t;
	}
	
}
class FOFinisher implements Function<ConcurrentHashMap<ArrayList<String>, ArrayList<MonResult>>, ArrayList<MonResult>>{


	protected MonResult id;
	protected boolean isParallel;
	public FOFinisher(boolean isparallel){
		id = new MonResult();
		id.count = 0;
		this.isParallel = isparallel;
	}
	public long getJVMCpuTime() {
		long lastProcessCpuTime = 0;
		try {
			if (ManagementFactory.getOperatingSystemMXBean() instanceof OperatingSystemMXBean) {
				lastProcessCpuTime=((com.sun.management.OperatingSystemMXBean)ManagementFactory.getOperatingSystemMXBean()).getProcessCpuTime();
			}
		}
		catch (  ClassCastException e) {
			System.out.println(e.getMessage());
		}finally{
			return lastProcessCpuTime;
		}
	}
	@Override
	public ArrayList<MonResult> apply(
			ConcurrentHashMap<ArrayList<String>, ArrayList<MonResult>> t) {
		// TODO Auto-generated method stub

		
		long beg = System.nanoTime();
		long begCPU = getJVMCpuTime();
		ArrayList<MonResult> ret;
		RedOperator x = new RedOperator();
		if(isParallel){
			ret = new ArrayList<MonResult>(t.values().stream().map((alist)->{
				return alist.parallelStream().reduce(id,x);
			}).collect(Collectors.toList()));
		}
		else{
			ret = new ArrayList<MonResult>(t.values().stream().map((alist)->{
				return alist.stream().reduce(id,x);
			}).collect(Collectors.toList()));
		}
			
		long end = System.nanoTime();
		long endCPU = getJVMCpuTime();
		System.out.println("Exec Time:" + TimeUnit.MILLISECONDS.convert((end-beg),TimeUnit.NANOSECONDS));
		System.out.println("CPU Time : " + TimeUnit.MILLISECONDS.convert((endCPU-begCPU),TimeUnit.NANOSECONDS));
		return ret;
	}

}	

class FOCCollector<T> implements Collector<MonResult, ConcurrentHashMap<ArrayList<String>, ArrayList<MonResult>>, ArrayList<MonResult>> {
	
	protected ArrayList<Integer> keylist;
	protected boolean isParallel;
	public FOCCollector(ArrayList<Integer> keyList, boolean isParallel){
		keylist = keyList;
		this.isParallel = isParallel;
	}
	protected void updateHT(ConcurrentHashMap<ArrayList<String>, ArrayList<MonResult>> myMap, MonResult mResult, ArrayList<String> currList){

		ArrayList<MonResult> temp = new ArrayList<MonResult>();
		if(myMap.containsKey(currList)){
			Collections.synchronizedList(myMap.get(currList)).add(mResult);
		}else{
			myMap.putIfAbsent(currList, temp);
			Collections.synchronizedList(myMap.get(currList)).add(mResult);
		}
	}
	protected void merge(ConcurrentHashMap<ArrayList<String>, ArrayList<MonResult>> myMap, MonResult mResult){
		ArrayList<String> currList = new ArrayList<>();
		MonResult m ;
		for(int key: keylist){
			currList.add(key, mResult.ObjectIDs.get(key));
		}
		updateHT(myMap, mResult,currList);

	}
    @Override
    public Supplier<ConcurrentHashMap<ArrayList<String>, ArrayList<MonResult>>> supplier() {
        return ConcurrentHashMap<ArrayList<String>, ArrayList<MonResult>>::new;
    }

    @Override
    public BiConsumer<ConcurrentHashMap<ArrayList<String>, ArrayList<MonResult>>, MonResult> accumulator() {
        
    	return (builder, t1) -> {
    		merge(builder, t1);
    	};
    }

    @Override
    public BinaryOperator<ConcurrentHashMap<ArrayList<String>, ArrayList<MonResult>>> combiner() {
        return (b1, b2) -> {
        	b1.putAll(b2);
        	return b1;
        };
    }

    @Override
    public Function<ConcurrentHashMap<ArrayList<String>, ArrayList<MonResult>>, ArrayList<MonResult>> finisher() {
        return new FOFinisher(isParallel);
    }

    @Override
    public Set<Characteristics> characteristics() {
        return EnumSet.of(Characteristics.CONCURRENT,Characteristics.UNORDERED);
    }
}
public class HypothesisOLDLTLFO1{
//	public static MonResult createIDs(){
//		MonResult m = new MonResult();
//		m.ObjectIDs.add("1");
//		m.ObjectIDs.add("1");
//	}
	
	public static void main(String[] args) {
		ArrayList<MonResult> monResultList = new ArrayList<MonResult>();

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
		System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism", args[4]);
		ArrayList<MonResult> res;
		for(int i = 0; i < Integer.parseInt(args[0]); i++ )
			for(int j = 0; j < Integer.parseInt(args[1]); j++)
				for(int k = 0; k < Integer.parseInt(args[2]);k++){
					MonResult m1 = new MonResult();
					m1.ObjectIDs.add(Integer.toString(i));
					m1.ObjectIDs.add(Integer.toString(j));
					m1.ObjectIDs.add(Integer.toString(k));
					monResultList.add(m1);
				}

		long beg = System.nanoTime();
		if(Boolean.parseBoolean(args[3])){
			res = monResultList.stream().collect(new FOCCollector<>(collectList, false));
		}
		else{
			res = monResultList.stream().collect(new FOCCollector<>(collectList, true));

		}
		//			for(MonResult m : res.values()){
		//				System.out.println(m.count);
		//			}
		//			System.out.println("--------------------------");

		//			for(MonResult m : monResultList1){
		//				System.out.println(m.count);
		//			}


		//			for(MonResult m : res){
		//				System.out.println(m.count);
		//				break;
		//			}
		long end = System.nanoTime();

//			System.out.println(end-beg);
		System.out.println("Threads:" +  Thread.activeCount());
	}
}
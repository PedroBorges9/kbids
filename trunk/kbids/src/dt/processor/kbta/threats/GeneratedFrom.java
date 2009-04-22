package dt.processor.kbta.threats;

import java.util.ArrayList;

public class GeneratedFrom {
	private final String _type;
	private final String _name;
	private final ArrayList<String> _symbolicValueCondition;
	private final ArrayList<Duration> _durationCondition;
	
	
	public GeneratedFrom(String type,String name,ArrayList<String> symbolicValueCondition,ArrayList<Duration> durationCondition) {
		_type = type;
		_name = name;
		_symbolicValueCondition = symbolicValueCondition;
		_durationCondition = durationCondition;
		
	}
	
	
	
	public ArrayList<String> getSymbolicValueCondition() {
		return _symbolicValueCondition;
	}

	public ArrayList<Duration> getDurationCondition() {
		return _durationCondition;
	}
	
	public boolean symbolicValueConditionExist(String st){
		if (st==null){
			return false;
		}
		for(String s : _symbolicValueCondition){
			if (st.equalsIgnoreCase(s)){
				return true;
			}
			
		}
		return false;
	}
	
//	public boolean durationConditionExist(Duration du){
//		if(du==null){
//			return false;
//		}
//		
//		String min=du.getMin();
//		String max=du.getMax();
//		
//		
//		for (Duratin d : _durationCondition){
//			if()
//		}
//		
//		
//		
//		
//	}



	public String getType() {
		return _type;
	}



	public String getName() {
		return _name;
	}



	@Override
	public String toString() {
		String st="GeneratedFrom\n";
		st+="<"+_type+" name="+_name+">\n";
		
		st+="symbolicValueCondition ";
		for(String s : _symbolicValueCondition){
			st+=s+" ";
			
		}
		st+="\n";
		st+="durationCondition ";
		for(Duration d : _durationCondition){
			st+=d.toString()+"\n";
			
		}
		return st;
	}
	
	
	
	
	
	
	

}

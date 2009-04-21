package dt.processor.kbta.threats;

import java.util.ArrayList;

public class GeneratedFrom {
	private final String _type;
	private final String _name;
	private ArrayList<String> _symbolicValueCondition;
	private ArrayList<Duration> _durationCondition;
	
	
	public GeneratedFrom(String type,String name,ArrayList<String> symbolicValueCondition,ArrayList<Duration> durationCondition) {
		_symbolicValueCondition = new ArrayList<String>();
		_durationCondition =  new ArrayList<Duration>();
		_type = type;
		_name = name;
		_symbolicValueCondition = symbolicValueCondition;
		_durationCondition = durationCondition;
		
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

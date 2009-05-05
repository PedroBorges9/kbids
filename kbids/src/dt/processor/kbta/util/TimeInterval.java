package dt.processor.kbta.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import dt.processor.kbta.ontology.instances.Element;

public class TimeInterval{
	private final long _startTime;

	private long _endTime;

	public TimeInterval(long startTime, long endTime){
		if ((startTime < 0) || (endTime < 0) || (endTime < startTime))
			throw new IllegalArgumentException("TimeInterval can't be created: " + "["
					+ startTime + ", " + endTime + "]");

		_startTime = startTime;
		_endTime = endTime;
	}
	
	public boolean equals(Object o){
		if (o == this){
			return true;
		}
		if (!(o instanceof TimeInterval)){
			return false;
		}
		TimeInterval other = (TimeInterval)o;

		return other.getStartTime() == getStartTime()
				&& other.getEndTime() == getEndTime();
	}

	
	/**
	 * Returns the start time
	 * 
	 * @return The start time
	 */
	public long getStartTime(){
		return _startTime;
	}
	

	/**
	 * Returns the end time
	 * 
	 * @return The end time
	 */
	public long getEndTime(){
		return _endTime;
	}
	

	public long getDuration(){
		return getEndTime() - getStartTime();
	}

	/**
	 * Whether this interval ends before the other interval starts
	 * 
	 * @param another The other interval
	 * @return Whether this interval ends before the other interval starts
	 */
	public boolean isBefore(TimeInterval another){
		checkIsNotNull(another);
		return getEndTime() < another.getStartTime();
	}

	/**
	 * Whether this interval starts after the other interval ends
	 * 
	 * @param another The other interval
	 * @return Whether this interval starts after the other interval ends
	 */
	public boolean isAfter(TimeInterval another){
		checkIsNotNull(another);
		return getStartTime() > another.getEndTime();
	}
	
	/**
	 * Checks that the given time interval isn't null, throws an IllegalArgumentException
	 * otherwise
	 * 
	 * @param timeInterval The interval to be checked
	 * @throws IllegalArgumentException If timeInterval is null
	 */
	private void checkIsNotNull(TimeInterval timeInterval){
		if (timeInterval == null)
			throw new IllegalArgumentException("TimeInterval can't be null");
	}
	
	/**
	 * Compares this interval with another according to start time
	 * 
	 * @param o The time interval to be compared
	 * @return A negative integer, zero, or a positive integer as this interval's start
	 *         time is earlier than, equal to, or later than that of the other time
	 *         interval.
	 */
	public int compareTo(TimeInterval o){
		if (getStartTime() > o.getStartTime()){
			return 1;
		}

		if (getStartTime() < o.getStartTime()){
			return -1;
		}

		return 0;
	}
	
	/**
	 * Whether this interval contains (open range) the other interval
	 * 
	 * @param another The other interval
	 * @return Whether this interval contains (open range) the other interval
	 */
	public boolean isDuring(TimeInterval another){
		checkIsNotNull(another);
		return ((getStartTime() < another.getStartTime()) && (getEndTime() > another
				.getEndTime()));
	}

	/**
	 * Whether this interval contains (closed range) the other interval
	 * 
	 * @param another The other interval
	 * @return Whether this interval contains (closed range) the other interval
	 */
	public boolean isContainedIn(TimeInterval another){
		checkIsNotNull(another);
		return ((getStartTime() <= another.getStartTime()) && (getEndTime() >= another
				.getEndTime()));
	}

	/**
	 * Returns the overlap of this interval and the other interval
	 * 
	 * @param other The other interval
	 * @return The overlap of this and other
	 */
	public TimeInterval getOverlap(TimeInterval other){
		long s1 = getStartTime();
		long e1 = getEndTime();
		long s2 = other.getStartTime();
		long e2 = other.getEndTime();

		if (e2 >= s2 && e2 >= e1){
			return new TimeInterval(Math.max(s1, s2), e1);
		}else if (e2 >= s1 && e1 >= e2){
			return new TimeInterval(Math.max(s1, s2), e2);
		}else{
			return null;
		}
	}

	public void setEndTime(long end){
		_endTime=end;		
	}
	
	@Override
	public String toString(){
		SimpleDateFormat d=new SimpleDateFormat("HH:mm:ss");
		String s=d.format(new Date(_startTime));
		String e=d.format(new Date(_endTime));
		return ("["+s+","+e+"]");
	}

	public static TimeInterval intersection(Element[] elements, TimeInterval initialInterval){
		long endMin = (initialInterval == null) ? Long.MAX_VALUE : initialInterval
				.getEndTime();
		long startMax = (initialInterval == null) ? 0 : initialInterval.getStartTime();
		for (Element element : elements){
			TimeInterval timeInterval = element.getTimeInterval();
			long startTime = timeInterval.getStartTime();
			long endTime = timeInterval.getEndTime();
			startMax = (startTime > startMax) ? startTime : startMax;
			endMin = (endTime < endMin) ? endTime : endMin;
		}
		if (startMax <= endMin){
			return new TimeInterval(startMax, endMin);
		}
		return null;
	}

}

package dt.processor.kbta;

public interface ElementContainer{
	public void shiftBack();
	public void discardOlderThen(long time);
}

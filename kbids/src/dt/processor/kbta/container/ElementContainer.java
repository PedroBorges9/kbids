package dt.processor.kbta.container;

public interface ElementContainer{
	public void shiftBack();
	public void discardOlderThan(long time);
}

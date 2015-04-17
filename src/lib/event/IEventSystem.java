package lib.event;


public interface IEventSystem {
	public void trigger(String name, Object data);
	public void register(String name, Object executable);
	public boolean remove(String name);
	public void clear();
	public void destory();
	
}

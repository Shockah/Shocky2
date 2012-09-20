package pl.shockah.shocky2;

public class ModuleSource<T> {
	private final T source;
	
	public ModuleSource(T source) {
		this.source = source;
	}
	
	public T getSource() {return source;}
}
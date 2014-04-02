package puzzle;
public class Crate {
	int height;
	boolean start;
	boolean end;
	
	Crate()
	{
		height = -1;
		start = false;
		end = false;
	}
	
	Crate(int h, boolean s, boolean e)
	{
		this.height = h;
		this.start = s;
		this.end = e;
	}

}

package puzzle;
public class pos {
	int i;
	int j;
	pos prev;
	int direction;
	
	
	pos(int i, int j)
	{
		prev = null;
		this.i = i;
		this.j = j;
		this.direction = -1;
	}
	
	public void right()
	{
		this.j = this.j + 1;
	}
	
	public void left()
	{
		this.j = this.j - 1;
	}
	
	public void up()
	{
		this.i = this.i - 1;
	}
	
	public void down()
	{
		
		this.i = this.i + 1;
	}

	public boolean equals(pos temp)
	{
		if((this.i == temp.i) && (this.i == temp.j))
		{
			return true;
		}
		else return false;
	}
	
	public String toString()
	{
		return this.i + "," + this.j + "," + this.direction;
	}

}

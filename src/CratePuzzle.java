import java.util.ArrayList;


class Crate {
	
	
	public int height;
	public boolean start;
	public boolean end;
	
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

class pos {
	
	int i;
	int j;
	public pos prev;
	pos(int i, int j)
	{
		prev = null;
		this.i = i;
		this.j = j;
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
		return this.i + "," + this.j;
	}
}



public class CratePuzzle {

	
	ArrayList<pos> posList = new ArrayList<pos>();
	ArrayList<pos> endList = new ArrayList<pos>();
	Crate[][] board;
	
	CratePuzzle()
	{
		board = new Crate[5][5];
		
		for(int i=0; i<5; i++)
		{
			for(int j=0; j<5; j++)
			{
				board[i][j] = new Crate();
			}
		}
		
		
		pos sz = new pos(5, 5);
		pos start = new pos(3, 4);
		pos end = new pos(0,4);
		
		endList.add(end);
		
		board[3][4].start = true;
		board[0][4].end = true;
		board[0][1].height = 2;
		posList.add(new pos(0,1));
		board[1][3].height = 2;
		posList.add(new pos(1,3));
		board[3][0].height = 3;
		posList.add(new pos(3,0));
		board[3][4].height = 2;
		posList.add(new pos(3,4));
		
		start = new pos(3,4);
		end = new pos(0,4);
		
		//toppled crates
		board[4][3].height = 0;
		board[4][4].height = 0;
		posList.add(new pos(4,3));
		posList.add(new pos(4,4));
		
		
		
		solve(board, start, endList, posList);
		
	}
	
	
	
	
	public boolean reachable(pos start, pos end)
	{
		boolean canTopple = false;
		if(start.j == end.j)
		{
			if((Math.abs(start.i-end.i)-1) == board[start.i][start.j].height)
			{
				canTopple = true;
				for(int i= Math.min(start.i, end.i) + 1; i<Math.max(start.i, end.i); i++)
				{
					if(board[i][end.j].height != -1)
					{
						canTopple = false;
						break;
					}
				}
			}
		}
		else if(start.i == end.i)
		{
			if((Math.abs(start.j-end.j)-1) == board[start.i][start.j].height)
			{
				canTopple = true;
				for(int j= Math.min(start.j, end.j) + 1; j<Math.max(start.j, end.j); j++)
				{
					if(board[end.i][j].height != -1)
					{
						canTopple = false;
						break;
					}
				}
			}
			
		}
		else if(Math.abs(start.i-end.i) == 1)
		{
		
			if((Math.abs(start.j-end.j)) <= board[start.i][start.j].height)
			{
				canTopple = true;
				int dir = 1;
				if(start.j > end.j)
				{
					dir = -1;
				}
				
				int h = board[start.i][start.j].height;
				int j = start.j + dir;
				while(h > 0)
				{
					if( (j < 0) || (j >= 5) || (board[start.i][j].height != -1))
					{
						canTopple = false;
						break;
					}
					j = j + dir;
					h--;
				}
				
			}
		}
		else if(Math.abs(start.j-end.j) == 1)
		{
			System.out.println("here");
			if((Math.abs(start.i-end.i)) <= board[start.i][start.j].height)
			{
				canTopple = true;
				int dir = 1;
				if(start.i > end.i)
				{
					dir = -1;
				}
				
				int h = board[start.i][start.j].height;
				int i = start.i + dir;
				while(h > 0)
				{
					if((i < 0) || (i >= 5) || (board[i][start.j].height != -1))
					{
						canTopple = false;
						break;
					}
					i = i + dir;
					h--;
				}
				
			}
		}
		else return false;
		
		System.out.println("Start:" + start);
		System.out.println("End:" + end);
		System.out.println("cantopple" +  canTopple);
		
		return canTopple;
	}
	
	public void printPath(pos crtPos)
	{
		pos temp = crtPos;
		System.out.println("path");
		while(temp!=null)
		{
			System.out.println(temp.i + ", " + temp.j);
			temp = temp.prev;
		}
		
	}
	
	public void solve(Crate[][] board, pos start, ArrayList<pos> ends, ArrayList<pos> posList)
	{
		System.out.println("ends");
		for(pos end: ends)
		{
			System.out.println(end);
		}
		
		
		
		for(pos end: ends)
		{
			ArrayList<pos> newEnds = new ArrayList<pos>();
			ArrayList<Integer> indicesToRemove = new ArrayList<Integer>();
			ArrayList<pos> temp = new ArrayList<pos>();
			
			for(int ind =0; ind < posList.size(); ind++)
			{
				pos cratePos = posList.get(ind);
				if(reachable(cratePos, end))
				{
					//System.out.println("checking");
					//System.out.println(cratePos.i + ", " + cratePos.j);
					pos crtPos = new pos(cratePos.i,cratePos.j);
					crtPos.prev = end;
					if((crtPos.i == 3) && (crtPos.j == 0))
					{
						printPath(crtPos);
					}
					newEnds.add(crtPos);
					indicesToRemove.add(ind);
					temp.add(cratePos);
					//System.out.println(newEnds);
				}
				//ind++;
			}
			
			
			for(Integer e: indicesToRemove)
			{
				posList.remove(e.intValue());
			}
			solve(board, start, newEnds, posList);
			posList.addAll(temp);
			
		}
		
	}
	
	
	public static void main(String args[])
	{
		CratePuzzle cp = new CratePuzzle();
	}
	
	
	public Crate[][] topple(Crate[][] board, pos cur, int direction)
	{
		Crate[][] temp = new Crate[5][5];
		
		for(int i=0; i<5; i++)
		{
			for(int j=0; j<5; j++)
			{
				temp[i][j] = board[i][j];
			}
		}
		
		temp[cur.i][cur.j].height = -1;
		switch(direction)
		{
			case 0:for(int j=cur.j+1; j<5; j++)
				   {
						temp[cur.i][j].height = 0;
					}
					break;
				
			case 1:for(int j=cur.j-1; j> -1; j--)
				   {
						temp[cur.i][j].height = 0;
				   }
				   break;

			case 2:for(int i=cur.i+1; i<5; i++)
			   	   {
						temp[i][cur.j].height = 0;
			   	   }
			   	   break;
			   	   
			case 3:for(int i=cur.i-1; i > -1; i--)
		   	   	   {
					 	temp[i][cur.j].height = 0;
		   	   	   }
		   	   	   break;
		}
		
		return temp;
		
	}

}

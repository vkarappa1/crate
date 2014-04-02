package puzzle;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;

public class CratePuzzle {

	
	ArrayList<pos> posList = new ArrayList<pos>();  // list of positions where crates are located
	ArrayList<pos> endList = new ArrayList<pos>();
	JSONArray outputArr = new JSONArray();
	JSONArray toppled_crates;
	JSONArray standing_crates;
	Crate[][] board;
	pos start,end; 
	
	CratePuzzle(JSONObject boardData, String filePath)
	{
		
		JSONArray boardSize = (JSONArray)boardData.get("board");
		board = new Crate[Integer.parseInt(boardSize.get(0).toString())][Integer.parseInt(boardSize.get(1).toString())];
		
		for(int i=0; i<board.length; i++)
		{
			for(int j=0; j<board[0].length; j++)
			{
				board[i][j] = new Crate();
			}
		}
		
		JSONArray st = (JSONArray)boardData.get("start");
		start = new pos(Integer.parseInt(st.get(0).toString()), Integer.parseInt(st.get(1).toString()));
		
		
		JSONArray ed = (JSONArray)boardData.get("end");
		end = new pos(Integer.parseInt(ed.get(0).toString()),Integer.parseInt(ed.get(1).toString()));
		
		endList.add(end);
		
		
		standing_crates = (JSONArray)boardData.get("standing_crates");
		for(int i=0; i<standing_crates.size(); i++)
		{
			JSONArray sCrate = (JSONArray)standing_crates.get(i);
			pos p = new pos(Integer.parseInt(sCrate.get(0).toString()),Integer.parseInt(sCrate.get(1).toString()));
			board[p.i][p.j].height = Integer.parseInt(sCrate.get(2).toString());
			posList.add(p);
		}
		
		
		toppled_crates  = (JSONArray)boardData.get("toppled_crates");
		for(int k=0; k<toppled_crates.size(); k++)
		{
			JSONArray tCrate = (JSONArray)toppled_crates.get(k);
			pos s = new pos(Integer.parseInt(tCrate.get(0).toString()),Integer.parseInt(tCrate.get(1).toString())); 
			pos e = new pos(Integer.parseInt(tCrate.get(2).toString()),Integer.parseInt(tCrate.get(3).toString()));
			
			// running through all the toppled crates and saving their positions
			if(s.i == e.i)
			{
				for(int j=Math.min(s.j, e.j); j<=Math.max(s.j, e.j); j++)
				{
					board[s.i][j].height = 1;
					posList.add(new pos(s.i,j));
				}
			}
			else if(s.j == e.j)
			{
				for(int i=Math.min(s.i, e.i); i<=Math.max(s.i, e.i); i++)
				{
					board[i][s.j].height = 1;
					posList.add(new pos(i,s.j));
				}
			}
		}
	
		//This array keeps track of the visited crates
		boolean[] visited = new boolean[posList.size()];
		
		for(int i=0; i<posList.size(); i++)
		{
			visited[i] = false;
		}
		
		//Find all paths from start to end
		solve(board, start, endList, posList, visited);
		
		try{
			//FileWriter file = new FileWriter("C:\\workspace2\\crate\\bin\\test.json");
			FileWriter file = new FileWriter(filePath);
			file.write(outputArr.toJSONString());
			file.flush();
			file.close();
	 
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	/*
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 */
	
	public boolean reachable(Crate[][] board, pos start, pos end, Integer direction)
	{
		boolean canTopple = false;
		
		if(start.j == end.j)
		{
			if (((start.i == end.i + 1) || (start.i + 1 == end.i)) && (board[start.i][start.j].height > 0))
					return true;
			
			if(((Math.abs(start.i-end.i)-1) == board[start.i][start.j].height) && (board[start.i][start.j].height > 1))
			{
				canTopple = true;
				direction = start.i > end.i? 2: 3;
				
				start.direction = direction;
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
			
			if (((start.j == end.j + 1) || (start.j + 1 == end.j)) && (board[start.i][start.j].height > 0))
				return true;
			
			if(((Math.abs(start.j-end.j)-1) == board[start.i][start.j].height) && (board[start.i][start.j].height > 1))
			{
				canTopple = true;
				direction = start.j > end.j? 0: 1;
				start.direction = direction;
				
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
		
			if(((Math.abs(start.j-end.j)) <= board[start.i][start.j].height) && (board[start.i][start.j].height > 1))
			{
				canTopple = true;
				int dir = 1;
				if(start.j > end.j)
				{
					dir = -1;
					direction = 0;
				}
				else direction = 1;
				
				start.direction = direction;
				int h = board[start.i][start.j].height;
				int j = start.j + dir;
				while(h > 0)
				{
					if( (j < 0) || (j >= board[0].length) || (board[start.i][j].height != -1))
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
			if(((Math.abs(start.i-end.i)) <= board[start.i][start.j].height) && (board[start.i][start.j].height > 1))
			{
				canTopple = true;
				int dir = 1;
				if(start.i > end.i)
				{
					dir = -1;
					direction = 2;
				}
				else
				{
					direction = 3;
				}
				
				start.direction = direction;
				int h = board[start.i][start.j].height;
				int i = start.i + dir;
				while(h > 0)
				{
					if((i < 0) || (i >= board[0].length) || (board[i][start.j].height != -1))
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
		
	
		return canTopple;
	}
	
	
	/*
	 * As we start from the end and find all reachable crate positions. Then these positions become the new ends.
	 * That's why this funciton helps to find the path when we reach the start. Every pos object contains a reference to the previous 
	 * end.
	 *  
	 */
	public void printPath(pos crtPos, boolean[] visited)
	{
		pos temp = crtPos;
		System.out.println("path");
		JSONObject obj = new JSONObject();
		JSONArray size = new JSONArray();
		size.add(board.length);
		size.add(board[0].length);
		obj.put("board", size);
		
		JSONArray standing_crates_now = new JSONArray();
		standing_crates_now.addAll(standing_crates);
		JSONArray toppled = new JSONArray();
		
		while(temp.prev!=null) //find the path till we reach end
		{
			System.out.println(temp.i + ", " + temp.j + ", " +  temp.direction);
			JSONArray list = new JSONArray();
			int start_row, start_column, end_row, end_column;
			
			int toremove= -1;
			
			/// find out which of the crates are toppled.
			for(int i=0; i<standing_crates_now.size(); i++)
			{
				JSONArray st = (JSONArray)standing_crates_now.get(i);
				if((Integer.parseInt(st.get(0).toString()) == temp.i) && (Integer.parseInt(st.get(1).toString()) == temp.j))
				{
					System.out.println(st.toJSONString());
					toremove = i;
					break;
				}
				
			}
			if(toremove >= 0) standing_crates_now.remove(toremove);
			
			start_row = temp.i;
			start_column = temp.j;
			end_row = temp.i;
			end_column = temp.j;
			
			///figuring out which way the crates are toppled.
			switch(temp.direction)
			{
				case 0: end_column = start_column - 1;
						start_column = start_column - board[start_row][start_column].height;
						break;
				case 1:	end_column = start_column + board[start_row][start_column].height; 
						start_column = start_column + 1;
						break;
				case 2: end_row = start_row - 1;
						start_row = start_row -  board[start_row][start_column].height;
						break;
				case 3: end_row = start_row + board[start_row][start_column].height;
						start_row = start_row + 1;
			}
			
			list.add(start_row);
			list.add(start_column);
			list.add(end_row);
			list.add(end_column);
			
			
			toppled.add(list);
			temp = temp.prev;
		}
		
		obj.put("standing_crates", standing_crates_now);
		toppled.addAll(toppled_crates);
		obj.put("toppled_crates", toppled);
		outputArr.add(obj);

	}
	
	
	/*
	 * 
	 * We solve the path problem by starting from the end. We find reachable nodes from the end and those become new ends.
	 * Then again we do the same for new ends again until we find a reachable start position. Once we hit the start we would print out the path.
	 * 
	 * 
	 * 
	 * 
	 */
	public void solve(Crate[][] board, pos start, ArrayList<pos> ends, ArrayList<pos> posList, boolean[] visited)
	{
		
		/*
		 * for each end, find reachable crate piles 
		 */
		for(pos end: ends)
		{
			ArrayList<pos> newEnds = new ArrayList<pos>();
			ArrayList<Integer> indicesToRemove = new ArrayList<Integer>();
			
			
			for(int ind =0; ind < posList.size(); ind++)
			{
				if(visited[ind]) continue;
				pos cratePos = posList.get(ind);
				Integer dir = new Integer(-1);
				
				/*
				 * 
				 * find if the crate pile can be toppled in such way that we reach current end.
				 * If yes add it to new ends.
				 * 
				 */
				
				if(reachable(board, cratePos, end, dir))
				{
					
					
					pos crtPos = new pos(cratePos.i,cratePos.j);
					
					crtPos.direction = cratePos.direction;
					crtPos.prev = end;
					
					// if reach start position we have got a solution
					if((crtPos.i == start.i) && (crtPos.j == start.j))
					{
						printPath(crtPos, visited);
					}
					newEnds.add(crtPos);
					indicesToRemove.add(ind);
					visited[ind] = true;
				}
			}
			
			// solve with new ends which are reachable from the current end.
			solve(board, start, newEnds, posList, visited);
			
			for(Integer e: indicesToRemove)
			{
				visited[e.intValue()] = false; 
			}
	
		}
		
	}
	
	public static String readFile(String path, Charset encoding) throws IOException 
	{
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return encoding.decode(ByteBuffer.wrap(encoded)).toString();
	}
	
	public Crate[][] topple(Crate[][] board, pos cur, int direction)
	{
		Crate[][] temp = new Crate[board.length][board[0].length];
		
		for(int i=0; i<board.length; i++)
		{
			for(int j=0; j<board[0].length; j++)
			{
				temp[i][j] = board[i][j];
			}
		}
		
		temp[cur.i][cur.j].height = -1;
		switch(direction)
		{
			case 1:for(int j=cur.j+1; j<board[0].length; j++)
				   {
						temp[cur.i][j].height = 1;
					}
					break;
				
			case 0:for(int j=cur.j-1; j> -1; j--)
				   {
						temp[cur.i][j].height = 1;
				   }
				   break;

			case 3:for(int i=cur.i+1; i<board[0].length; i++)
			   	   {
						temp[i][cur.j].height = 1;
			   	   }
			   	   break;
			   	   
			case 2:for(int i=cur.i-1; i > -1; i--)
		   	   	   {
					 	temp[i][cur.j].height = 1;
		   	   	   }
		   	   	   break;
		}
		
		return temp;
		
	}

	public static void main(String args[]) throws IOException
	{
		
		String str = readFile("C:\\workspace2\\crate\\bin\\questions.json", Charset.defaultCharset());
		System.out.println(str);
		
		Object obj=JSONValue.parse(str);
		JSONArray array=(JSONArray)obj;
		
		//System.out.println(array.size());
		
		for(int i=0; i<array.size(); i++)
		{
			JSONObject o = (JSONObject)array.get(i);
			CratePuzzle cp = new CratePuzzle(o, "C:\\workspace2\\crate\\bin\\board" + i + "_solution.json");
		}

	}

}

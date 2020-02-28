package packy;

import java.util.ArrayList;
import java.util.List;

public class Mkdir implements Command {
	
	
		Director dir;
		String target;
		List <String> errors;
		public Mkdir(Director d, String target, List <String> err)
		{
			dir = d;
			this.target = target;
			errors = err;
		}
		
		@Override
		public void execute() 
		{	
			if(target.contains("*"))
			{	//bonus2
				int lastappar = target.lastIndexOf("/");
				String create = target.substring(lastappar, target.length());
				List<String> cai = new ArrayList<String>();
				target = target.substring(0, lastappar);
				if(dir.allPaths(target, cai) == 0)
				{
					//eroare
					dir.newDir(target   + create, errors);
				}
				else
				{
					int i;
					for(i = 0; i < cai.size();i++)
					{
						dir.newDir(cai.get(i)   + create, errors);
					}
				}
					
			}
			else
				dir.newDir(target, errors);//mkdir cu cale normala
		}
	

}

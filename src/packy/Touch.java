package packy;

import java.util.ArrayList;
import java.util.List;

public class Touch implements Command {
	
	Director dir;
	String target;
	List <String> errors;
	public Touch(Director d, String target, List <String> err)
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
				dir.newFis(target   + create, errors);
			}
			else
			{
				int i;
				for(i = 0; i < cai.size();i++)
				{
					dir.newFis(cai.get(i)   + create, errors);
				}
			}
				
		}
		else
			dir.newFis(target, errors);//touch cu cale fara *
	}

}

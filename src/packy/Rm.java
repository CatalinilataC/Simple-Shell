package packy;

import java.util.ArrayList;
import java.util.List;

public class Rm implements Command
{
	String target;
	Director dir;
	List <String> errors;
	public Rm(String s, Director d, List <String> err)
	{
		errors = err;
		dir = d;
		target = s;
	}
	
	@Override
	public void execute() 
	{
		
		if(target.contains("*"))
		{	
			int lastappar = target.lastIndexOf("/");
			List<String> cai = new ArrayList<String>();
			String toberem;
			if(lastappar == -1 || !target.substring(0, lastappar).contains("*"))
			{
				//e o cale nu complicata
				if(lastappar == -1)
				{
					toberem = target;
					cai.add(".");
				}
				else
				{
					if(lastappar == 0)
						cai.add("/");
					else
						cai.add(target.substring(0, lastappar));
					toberem = target.substring(lastappar, target.length());
				}
			
				
			}
			else
			{
				//cale cu *, poate si restu' are vreo *
				toberem = target.substring(lastappar, target.length());
				target = target.substring(0, lastappar);
				if(target.contains("*"))
					dir.allPaths(target, cai);
				else cai.add(target);	
			}
			 
			if(toberem.contains("*"))
			{
				//de verificat fiecare director din cai daca are ce se cauta
				int i, j;
				String []spart = toberem.split("\\*");
				String a ="";
				String b = "";
				if(spart.length == 1)
					a = spart[0];
				else 
					if(spart.length == 2)
					{
						a = spart[0];
						b = spart[1];
					}
				if(a.length() >= 1 && a.charAt(0) == '/')
					a = a.substring(1, a.length());
				Director old = dir.getWorkingDirectory();
				for(i = 0; i < cai.size(); i++)
				{
					if(dir.changePosition(cai.get(i)) == 0)
					{
						dir.setWD(old);
						dir.elimina(cai.get(i)+toberem, errors);
						return;//maybe not
					}
					Director aux = dir.getWorkingDirectory();
					dir.setWD(old);
					
					for(j = 0; j < aux.listd.size(); j++)
					{
						
						int dim = aux.listd.size();
						int val1 = aux.listd.get(j).nume.indexOf(a);
						int val2 = aux.listd.get(j).nume.lastIndexOf(b);
						if(val1 != -1 && val2 != -1 && val1 <= val2)
						{
							dir.elimina(aux.listd.get(j).path, errors);
							if(dim > aux.listd.size())
								j--;
						}
					}
				
					for(j = 0; j < aux.listf.size(); j++)
					{
						
						int dim = aux.listf.size();
						int val1 = aux.listf.get(j).nume.indexOf(a);
						int val2 = aux.listf.get(j).nume.lastIndexOf(b);
						if(val1 != -1 && val2 != -1 && val1 <= val2)
						{
							dir.elimina(aux.listf.get(j).path, errors);
							if(dim > aux.listf.size())
								j--;
						}
					}
				}
				dir.setWD(old);
			}
			else
			{
				//rm pe toate caile din cai stiind sigur ce se vrea sters
				int i;
				Director old = dir.getWorkingDirectory();
				for(i = 0; i < cai.size(); i++)
				{
					if(dir.changePosition(cai.get(i)) == 1)
					{
						int ok = 0, j;
						for(j = 0;j < dir.getWorkingDirectory().listd.size(); j++)
							if(toberem.equals("/" + dir.getWorkingDirectory().listd.get(j).nume))
							{
								ok = 1;
								break;
							}
						for(j = 0;j < dir.getWorkingDirectory().listf.size(); j++)
							if(toberem.equals("/" + dir.getWorkingDirectory().listf.get(j).nume))
							{
								ok = 1;
								break;
							}
						if(ok == 1)	
							dir.elimina(cai.get(i) + toberem, errors);
					}
					dir.setWD(old);
				}
				dir.setWD(old);
			}
		}
		else
		 dir.elimina(target, errors);//rm pe cale simpla
	}
}

package packy;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;




public class Director implements Nod {
	
	private static Director root = null;
	static Director work_dir = null;
	
	String path;
	String nume;
	LinkedList<Director> listd = new LinkedList<Director>();
	LinkedList<Fisier> listf = new LinkedList<Fisier>();
	LinkedList<Nod> listnod = new LinkedList<Nod>();
	public Director tata;
	
	public static Director getRoot()
	{
		if(root == null)
		{
			work_dir = root = (Director) new Director();
			root.nume = "/";
			root.path = "/";
		}
		return root;
	}
	
	public Director getWorkingDirectory()
	{
		return work_dir;
	}
	public void setWD(Director d)
	{
		work_dir = d; 
	}
	
	public Director(String s, Director father)
	{
		tata = father;
		if(!father.path.equals("/"))
			path = father.path + "/" + s;
		else path = father.path + s;
		nume = s;
	}
	
	public Director()
	{
		tata = null;
		path = "/";
		nume = "/";
	}
	
	public void showPath(List<String> lines)
	{
		lines.add(work_dir.path);
	}
	
	public int searchDir(Director dir, String name)
	{
		int i, nr = -1;
		for(i = 0; i < dir.listd.size(); i++)
			if(dir.listd.get(i).nume.equals(name))
			{
				nr = i;
				break;
			}
		return nr;
	}
	
	public int changePosition(String target)
	{
		Director oldwd = work_dir;
		if(target.equals("/"))
		{
			work_dir = root;
			return 1;
		}
		else if(target.equals("."))
			return 1;
			
		String []directoare = target.split("/");
		if(target.charAt(0) == '/')
			work_dir = root;
		for(String word : directoare )
		{
			if(word.length() == 0)
				continue;
			int ok = searchDir(work_dir, word);
			if(ok != -1)
				work_dir = work_dir.listd.get(ok);
			else if(word.equals(".."))
				work_dir = work_dir.tata;
			 if(work_dir == null ||((ok == -1) && (!word.equals(".")) && (!word.equals("..")) ))
			{
				//eroare
				work_dir = oldwd;
				return 0;
			}
		}
		return 1;
	}
	
	
	public Director newDir(String cale, List<String> errors)
	{
		Director aux = work_dir;
		int len = cale.length(), i;
		String num = "";
		for(i = len ;i > 0; i--)//last index of facut cu for
			if(cale.charAt(i-1) == '/')
			{
				int j;
				for(j = i; j < len; j++)
					num += "" + cale.charAt(j);
				if(i==1)i++;
					cale = cale.substring(0, i - 1);
				//de cand nu stiam ca exista niste functii pt stringuri
				break;
			}
		if(i == 0)
			num = cale;
		else if(0 == changePosition(cale))
			{
				errors.add("mkdir: "+ cale + ": No such directory");
				work_dir = aux;
				return null;
			}
		for(i = 0; i < work_dir.listd.size(); i++)
			if(num.equals(work_dir.listd.get(i).nume))
			{
				errors.add("mkdir: cannot create directory "+ work_dir.listd.get(i).path + ": Node exists");
				work_dir = aux;
				return null;
			}
		for(i = 0; i < work_dir.listf.size(); i++)
			if(num.equals(work_dir.listf.get(i).nume))
			{
				errors.add("mkdir: cannot create directory " + work_dir.listf.get(i).path + ": Node exists");
				work_dir = aux;
				return null;
			}
		Director nou = new Director(num, work_dir);
		
		for(i = 0; i < work_dir.listd.size(); i++)
			if(nou.nume.compareTo(work_dir.listd.get(i).nume) <= 0)
			{
				break;
			}
	
		work_dir.listd.add(i, nou);
		work_dir = aux;
		return nou;
	}
	
	public void ls_simplu(Director dir, List<String> lines)
	{
		 
		lines.add(dir.path + ":");
		int j = 0, k = 0;
		String cai = "";
		//interclasare
		
		while(j < dir.listd.size() && k < dir.listf.size())
		{
	 
			if(dir.listd.get(j).nume.compareTo(dir.listf.get(k).nume) < 0)
			{
				cai += (dir.listd.get(j).path) + " ";
				j++;
			}
			else
			{
				cai += (dir.listf.get(k).path) + " ";
				k++;
			}
		}
		
	
		
		while(k < dir.listf.size())
		{
			cai += (dir.listf.get(k).path) + " ";
			k++;
		}
		
		while(j < dir.listd.size())
		{
			cai += (dir.listd.get(j).path) + " ";
			j++;
		}
		lines.add(cai.trim());
		lines.add("");
	}
	
	public void ls_recursiv(Director dir, List<String> lines)
	{
		ls_simplu(dir, lines);
		int i;
		for (i = 0; i< dir.listd.size(); i++)
		{
			ls_recursiv(dir.listd.get(i), lines);
		}
	}
	
	public void listare(String arg, List<String> lines, List<String> errors)
	{
		String []aux = arg.split(" ");
		if(aux.length == 1)
			ls_simplu(work_dir, lines);//ls simplus
		else
		{
			if(aux.length == 2)
			{
				if(aux[1].equals("-R"))
				{
					//ls recursiv
					ls_recursiv(work_dir, lines);
				}
				else
				{
					//cd + ls simplu
					Director auxiliar = work_dir;
					if( 0 == changePosition(aux[1]))
					{
						errors.add("ls: " + aux[1] + ": No such directory");
						return;
					}
					ls_simplu(work_dir, lines);
					work_dir = auxiliar;
				}
			}
			else
			{
				if(aux.length == 3)
				{
					//cd + ls rec
					String cale;
					if(aux[1].equals("-R"))
						cale = aux[2];
					else cale = aux[1];
					Director auxiliar = work_dir;
					if( 0 == changePosition(cale))
					{
						errors.add("ls: " + cale + ": No such directory");
						return;
					}
					ls_recursiv(work_dir, lines);
					work_dir = auxiliar;
				}
				else
				{
					String cale;
					if(aux[1].equals("-R"))
						cale = aux[2];
					else cale = aux[1];
					Director auxiliar = work_dir;
					if(changePosition(cale) == 0)
					{
						errors.add("ls: " + cale + ": No such directory");
						return;
					}
					List <String> rezls = new ArrayList<String>();
					int nr = 4;
					if(aux[1].equals("-R"))
					{
						nr = 5;
						ls_recursiv(work_dir, rezls);
					}
					else ls_simplu(work_dir, rezls);
					work_dir = auxiliar;
					aux[nr] = aux[nr].substring(1, aux[nr].length() - 1);
					int i, j;
					for(i = 0; i < rezls.size();i++)
					{
						String linie = "";
						String []cuvinte = rezls.get(i).split(" ");
						for(j = 0; j < cuvinte.length; j++)
						{	
							if(cuvinte[j].length() == 0)
								continue;
							int k;
							for(k = cuvinte[j].length() - 1; k > 0; k--)
								if(cuvinte[j].charAt(k) == '/')
									break;
							if(k < 0)
								k  = 0;
							if(k + 1 < Math.max(0, cuvinte[j].length()))
								k++;
							String word = cuvinte[j].substring(k, Math.max(0, cuvinte[j].length() ));
							if(word.matches(aux[nr]))
							{
								if(word.charAt(word.length() - 1) == ':')
									continue;
								linie += cuvinte[j] + " ";
							}
						}
						if(cuvinte[0].length() != 0 && cuvinte[0].charAt(Math.max(0, cuvinte[0].length() - 1)) == ':')
							lines.add(cuvinte[0]);
						else
						{
							linie = linie.trim();
							if(linie.length() != 0)
								lines.add(linie);
							else lines.add("");
						}
					}//for
				} //else
			}//alt else
		}//else ceva
	}//fct listare
	
	
	public void newFis(String cale, List <String> errors)
	{
		Director aux = work_dir;
		//work_dir = root;
		int i;
		String num = "";
		String err;
		for(i = cale.length() - 1 ;i >= 0; i--)
			if(cale.charAt(i) == '/')
				break;
		if(i != -1)
		{
			int j;
			for(j = i + 1; j < cale.length(); j++)
				num += "" + cale.charAt(j);
			if(i <= 0)i++;
			cale = cale.substring(0, i);
			if(cale.equals("/"))
				err = cale + num;
			else err = cale ;
			
		}
		else//cale simpla
		{
			err ="/" + cale;
			num  = cale;
			cale = ".";		
		}
		
		
		if(0 == changePosition(cale))
			{
			errors.add("touch: "+ err + ": No such directory");
			work_dir = aux;
			return;
			}
		if(work_dir.path.equals("/"))
			err = "/" + num;
		else err = work_dir.path + "/" + num;
		Fisier nou = new Fisier(num, work_dir.path);
		for(i = 0; i < work_dir.listf.size(); i++)
			if(num.equals(work_dir.listf.get(i).nume))
			{
				errors.add("touch: cannot create file "+ err + ": Node exists");
				work_dir = aux;
				return;
			}
		else if(nou.nume.compareTo(work_dir.listf.get(i).nume) <= 0)
				break;
		work_dir.listf.add(i, nou);
		work_dir = aux;
	}
	
	public int copy(String surs, String dest, List<String>errors, String text)
	{
		Director oldwd = work_dir;
		String err;
		int i;
		String num = "";
		for(i = surs.length() - 1; i >= 0; i--)
			if(surs.charAt(i) == '/')
				break;
		if(i != - 1)
		{
			int j;
			for(j = i + 1; j < surs.length(); j++)
				num += ""+ surs.charAt(j);
			if(i <= 0)i++;
			surs = surs.substring(0, i);
			if(surs.equals("/"))
				err = surs + num;
			else err = surs + "/" + num;
		}
		else//cale simpla
		{
			err = surs;
			if(work_dir.nume.equals("/"))
			{
				num = surs;
				surs = "/";
			}
			else
			{
				surs = "..";
				num = work_dir.nume;
			}
		}
		if(changePosition(surs) == 0)
		{
			errors.add(text + err + ": No such file or directory");
			work_dir = oldwd;
			return 0;
		}
		int ok = -1;
		for(i = 0 ; i < work_dir.listd.size(); i++)
			if(work_dir.listd.get(i).nume.equals(num))
			{
				ok = i;
				break;
			}
		if(ok >= 0)
		{
			Director dup = work_dir.listd.get(ok);
			work_dir = oldwd;
			if(changePosition(dest) == 0)
			{
				errors.add(text + "into " + dest + ": No such directory");
				work_dir = oldwd;
				return 0;
			}
			for(i = 0; i < work_dir.listd.size();i++)//verificare ca nu exista alt director numit lfl, poate ar trb si pt fisier
				if(num.equals(work_dir.listd.get(i).nume))
						{
							errors.add(text + err + ": Node exists at destination");
							work_dir = oldwd;
							return 0;
						}
			Director aux = work_dir;
			work_dir = oldwd;
			Duplica(dup, aux, oldwd);
			return 1;
		}
		else
		{//							PT FISIER
		
			for(i = 0 ; i < work_dir.listf.size(); i++)
				if(work_dir.listf.get(i).nume.equals(num))
				{
					ok = i;
					break;
				}
			if(ok >= 0)
			{
				Fisier dup = work_dir.listf.get(ok);
				if(changePosition(dest) == 0)
				{
					errors.add(text + "into " + dest + ": No such directory");
					work_dir = oldwd;
					return 0;
				}
				ok = 0;
				for(i = 0; i < work_dir.listf.size();i++)
					if(num.equals(work_dir.listf.get(i).nume))
					{
						errors.add(text + err + ": Node exists at destination");
						work_dir = oldwd;
						return 0;
					}
					else if(num.compareTo(work_dir.listf.get(i).nume) >= 0)//pt ordine
						ok = i + 1;
				Fisier nou = new Fisier(dup.nume, dest);
				work_dir.listf.add(ok, nou);
				work_dir = oldwd;
				return 1;
			}
			else
			{
				errors.add(text + err + ": No such file or directory");
				work_dir = oldwd;
				return 0;
			}	
		}
	}
	
	void Duplica(Director surs, Director dest, Director oldwd)
	{
		List<String>errors = null;
		String cale = "";
		if(!dest.path.equals("/"))
				cale = dest.path + "/" + surs.nume;
		else
			cale += "/" + surs.nume;
		Director nou = newDir(cale, errors);
		int i;
		for(i = 0; i < surs.listd.size();i++)
			Duplica(surs.listd.get(i), nou, oldwd);
		
		for(i = 0 ; i < surs.listf.size(); i++)
			newFis(nou.path +"/"+ surs.listf.get(i).nume, errors);
		if(surs == oldwd)
			work_dir = nou;
	}
	
	public void move(String surs, String dest, List<String>errors)
	{
		Director oldest = work_dir;
		int ok = copy(surs, dest, errors, "mv: cannot move ");
		if(ok == 1)
		{
			Director goodwd = work_dir;
			work_dir = oldest;
			int i;
			String num = "";
			for(i = surs.length() - 1; i >= 0; i--)
				if(surs.charAt(i) == '/')
					break;
			if(i != - 1)
			{
				int j;
				for(j = i + 1; j < surs.length(); j++)
					num += ""+ surs.charAt(j);
				if(i <= 0)i++;
				surs = surs.substring(0, i);
			}
			else//cale simpla
			{
				if(work_dir.nume.equals("/"))
				{
					num = surs;
					surs = "/";
				}
				else
				{
					surs = "..";
					num = work_dir.nume;
				}	
			}
			changePosition(surs);
			for(i = 0; i < work_dir.listd.size();i++)
				if(num.equals(work_dir.listd.get(i).nume))
				{
					work_dir.listd.remove(i);
					break;
				}
			for(i = 0; i < work_dir.listf.size();i++)
				if(num.equals(work_dir.listf.get(i).nume))
				{
					work_dir.listf.remove(i);
					break;
				}
			work_dir = goodwd;
			//mai e de taiat legatura sursei cu fath
		}
	}
	
	public void elimina(String target, List<String> errors)
	{
		if(target.equals("."))
			return;
		int i;
		String err;
		String num = "";
		for(i = target.length() - 1; i >= 0; i--)
			if(target.charAt(i) == '/')
				break;
		if(i != - 1)
		{
			int j;
			for(j = i + 1; j < target.length(); j++)
				num += ""+ target.charAt(j);
			if(i <= 0)i++;
			target = target.substring(0, i);
			if(target.equals("/"))
				err = target + num;
			else err = target + "/" + num;
			
		}
		else//cale simpla
		{
			err = target;
			num  = target;
			target = ".";		
		}
		Director old = work_dir;
		if(changePosition(target) == 0)
		{
			errors.add("rm: cannot remove " + err + ": No such file or directory");
			work_dir = old;
			return;
		}
		int ok = -1;
		for(i = 0 ; i < work_dir.listd.size(); i++)
			if(work_dir.listd.get(i).nume.equals(num))
			{
				ok = i;
				break;
			}
		if(ok >= 0)
		{
			//gasit fisier de sters
			//daca e gasita calea aluia de sters in calea work_dir
			//if(work_dir.listd.get(ok).path.contains(work_dir.path))
			if(old.path.contains(work_dir.listd.get(ok).path ))
				{work_dir = old;return;}
			work_dir.listd.get(ok).tata = null;
			work_dir.listd.remove(ok);
			work_dir = old;
			return;
		}
		else
		{
			for(i = 0 ; i < work_dir.listf.size(); i++)
				if(work_dir.listf.get(i).nume.equals(num))
				{
					ok = i;
					break;
				}
			if(ok >= 0)
			{
				work_dir.listf.remove(ok);
				work_dir = old;
				return;
			}
			else
			{
				//eroare nu e gasit nimic
				if(old.path.contains(work_dir.path + "/" + num))
				{
					work_dir = old;
					return;
				}
				errors.add("rm: cannot remove " + err + ": No such file or directory");
				work_dir = old;
				return;
			}
		}
	}
	
	public int allPaths(String cale, List<String>cai)
	{
		/*
		 * Idee:
		 * se fragmenteaza calea dupa se verifica daca exista * in token daca nu se adauga la o noua cale catre care se face cd
		 * urmeaza tokenu cu * deci se apeleaza DFS cu restu de cale(din care a fost inlaturat ce a s a folosit pt cd)
		 * se reia procesu ca s a apelat functia recursuva dfs 
		 */
		//cale = cale.substring(2, cale.length());
		Director old = work_dir;
		DFS(work_dir, cale, cai);
		work_dir = old;
		return cai.size();
	}
	
	public void DFS(Director d, String cale, List<String>cai)
	{
		if(cale.length() == 0)
		{
			cai.add(d.path);
			return;
		}
		else if(!cale.contains("*"))
		{
			cai.add(cale);
			return;
		}
		int i, j;
		String nouacale = "";
		String []cuvinte = cale.split("/");
		for(i = 0; i < cuvinte.length; i++)
		{
			if(cuvinte[i].contains("*"))
			{	
				Director old = work_dir;
				int ok = 1;
				if(nouacale.length() != 0)
					ok = changePosition(nouacale);
				if(ok!=1)
					return;
				cale = cale.substring(cuvinte[i].length() , cale.length());
				if(cale.length() > 0 && cale.charAt(0)=='/')
					cale = cale.substring(1 , cale.length());
				String []spart = cuvinte[i].split("\\*");
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
				for(j = 0;j < work_dir.listd.size(); j++)
				{
					//verificare ca face match pe cuvinte[i] si apoi apel dfs pe directoarele care fac match
					int val1 = work_dir.listd.get(j).nume.indexOf(a);
					int val2 = work_dir.listd.get(j).nume.lastIndexOf(b);
					if(val1 != -1 && val2 != -1 && val1 <= val2)
					{
						String toadd = "/" + cale;
						if(!work_dir.listd.get(j).path.equals("/"))
							toadd = work_dir.listd.get(j).path + toadd;
						if (cale.length() == 0)
							toadd = "";
						DFS(work_dir.listd.get(j), toadd, cai);
					}
				}
				work_dir = old;
				return;
			}
			else
			{
				nouacale += cuvinte[i]+"/";
				cale = cale.substring(cuvinte[i].length() + 1, cale.length());
			}
		}
	}
}//clasa





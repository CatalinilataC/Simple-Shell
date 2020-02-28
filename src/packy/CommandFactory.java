package packy;

import java.util.ArrayList;
import java.util.List;

public class CommandFactory {

	public Command createCommand(String s, Director r, List<String> lines, List<String> errors)
	{
		String []aux = s.split(" ", 2);
		Command com = null;
		if(aux[0].equals("pwd"))
			com = new Pwd(r.getWorkingDirectory(), lines);
		else if(aux[0].equals("mkdir"))
			com = new Mkdir(r.getWorkingDirectory(), aux[1], errors);
		else if(aux[0].equals("touch"))
			com = new Touch(r.getWorkingDirectory(), aux[1], errors);
		else if(aux[0].equals("cd"))
			com = new Cd(r.getWorkingDirectory(), aux[1], errors);
		else if(aux[0].equals("ls"))
			com = new Ls(s, r.getWorkingDirectory(), lines, errors);
		else if(aux[0].equals("cp"))
			com = new Cp(aux[1], r.getWorkingDirectory(), errors);
		else if(aux[0].equals("mv"))
			com = new Mv(aux[1], r.getWorkingDirectory(), errors);
		else if(aux[0].equals("rm"))
			com = new Rm(aux[1], r.getWorkingDirectory(), errors);
		return com;
	}
	
}

class Cd implements Command
{

	Director dir;
	String target;
	List <String> errors;
	public Cd(Director d, String target, List <String> err)
	{
		dir = d;
		this.target = target;
		errors = err;
	}
	
	@Override
	public void execute() 
	{
		if(0 == dir.changePosition(target))
			errors.add("cd: " + target +": No such directory");
	}
}

class Ls implements Command
{
	String arg;
	Director dir;//e ceva gresit aici
	List<String> lines;
	List<String> errors;
	public Ls(String s, Director d, List<String> lines, List<String> errors)
	{
		arg = s;
		dir = d;
		this.lines = lines;
		this.errors = errors;
	}
	@Override
	public void execute()
	{
		if(arg.contains("*"))
		{
			Director old = dir.getWorkingDirectory();
			List<String> cai = new ArrayList<String>();
			arg = arg.substring(3, arg.length());
			if(dir.allPaths(arg, cai) == 0)
			{
				//eroare
				arg = "ls " + arg;
				dir.listare(arg, lines, errors);
			}
			else
			{
				int i;
				for(i = 0; i < cai.size();i++)
				{
					dir.listare("ls "+cai.get(i), lines, errors);
				}
			}
				dir.setWD(old);
		}
		else
		dir.listare(arg, lines, errors);
	}
}


class Cp implements Command
{
	String sur;
	String des;
	Director dir;
	List <String> errors;
	public Cp(String sursa, Director d, List <String> err)
	{	
		String []aux = sursa.split(" ", 2);
		sur = aux[0];
		des = aux[1];
		dir = d;
		errors = err;
	}
	@Override
	public void execute() 
	{
		Director old = dir.getWorkingDirectory();
		dir.copy(sur, des, errors, "cp: cannot copy ");
		dir.setWD(old);
	}
}

class Mv implements Command
{
	String sur;
	String des;
	Director dir;
	List <String> errors;
	public Mv(String sursa, Director d, List <String> err)
	{
		String []aux = sursa.split(" ", 2);
		sur = aux[0];
		des = aux[1];
		dir = d;
		errors = err;
	}
	@Override
	public void execute()
	{
		dir.move(sur, des, errors);
	}
}

class Pwd implements Command{
	Director dir;
	List<String> lines;
	public Pwd(Director d, List<String> lines)
	{
		dir = d;
		this.lines = lines;
	}
	public void execute()
	{
		dir.showPath(lines);
	}
}


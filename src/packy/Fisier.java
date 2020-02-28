package packy;

public class Fisier implements Nod {
	String nume;
	String path;
	
	public Fisier(String s, String cale)
	{
		nume = s;
		if(cale.equals("/"))
			path = cale + s;
		else
			path = cale + "/" + s;
	}
}

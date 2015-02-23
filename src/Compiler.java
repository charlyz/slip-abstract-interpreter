import slip.ConcreteToInternal;
import slip.internal.Prog;




public class Compiler 
{
	
	public static Prog compile(String path) 
	{
		ConcreteToInternal translator = new ConcreteToInternal (path);
		Prog prog = null;
		try
		{
			prog = translator . translate ();
		}
		catch ( Exception e)
		{
			System .err .println (" Translation error : " + e.getMessage ());
			System .exit (0) ;
		}
		System.out.println(prog);
		//slip.interpreter.Interpreter.interprete(prog);
		return prog;
	}
	
	
}

package slip.internal; 

public class Prog extends AbstractNode // Program
{  
   private Method[] meths; // Declaration of all methods
   
   
   public Prog(Method[] meths)
   { this.setMeths(meths); 
   }

   public String toString()
   {
     String res = "" ;
     
     int i = 0 ;
     while (i!=getMeths().length)
     { res += getMeths()[i] ; i++ ; }

     return res ;
   }

public void setMeths(Method[] meths) {
	this.meths = meths;
}

public Method[] getMeths() {
	return meths;
}
}


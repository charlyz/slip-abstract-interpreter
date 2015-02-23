package abstractI.domain;

import java.util.LinkedList;

/**
 * Class that computes arithmetic operations (+,-,*,/,%) and less equals operator on abstract integer values.
 * 
 * @author Jian Lian
 * @author Cedric Vanderperren
 */
public abstract class PZa
{
	public static final int EMPTY = 0;
	public static final int MINUS = 1;
	public static final int MZ = 2;
	public static final int ZERO = 3;
	public static final int ZP = 4;
	public static final int PLUS = 5;
	public static final int MP = 6;
	public static final int MZP = 7;
	public static final int TOP = 8;
	public static final boolean T = true;
	public static final boolean F = false;
	
	private static final boolean[] N1 = {F,T,T,F,F,F,T,T};
	private static final boolean[] Z1 = {F,F,T,T,T,F,F,T};
	private static final boolean[] P1 = {F,F,F,F,T,T,T,T};
	
	private static final int X=-1;
	
	private static final int[][] cPrime = {	{X,X,X,X,X,X,X,X},
											{X,X,X,1,X,1,X,X},
											{X,X,X,X,X,X,X,X},
											{X,0,X,0,X,1,X,X},
											{X,X,X,X,X,X,X,X},
											{X,0,X,0,X,X,X,X},
											{X,X,X,X,X,X,X,X},
											{X,X,X,X,X,X,X,X}};
	
	private static final int[][] ub = {	{0,1,2,3,4,5,6,7},
										{1,1,2,2,7,6,6,7},
										{2,2,2,2,7,7,7,7},
										{3,2,2,3,4,4,7,7},
										{4,7,7,4,4,4,7,7},
										{5,6,7,4,4,5,6,7},
										{6,6,7,7,7,6,6,7},
										{7,7,7,7,7,7,7,7}};
	
	private static final int[][] inter= {{0,0,0,0,0,0,0,0},
										{0,1,1,0,0,0,1,1},
										{0,1,2,3,3,0,1,2},
										{0,0,3,3,3,0,0,3},
										{0,0,3,3,4,5,5,4},
										{0,0,0,0,5,5,5,5},
										{0,1,1,0,5,5,6,6},
										{0,1,2,3,4,5,6,7}};
	
	private static final int [][] except = {	{0,0,0,0,0,0,0,0},
											{1,0,0,1,1,1,0,0},
											{2,3,0,1,1,2,3,0},
											{3,3,0,0,0,3,3,0},
											{4,4,5,5,0,3,3,0},
											{5,5,5,5,0,0,0,0},
											{6,5,5,6,1,1,0,0},
											{7,4,5,6,1,2,3,0}};

	
	private static final int [][] plus = {	{X,X,X,X,X,X,X,X},
											{X,1,1,1,7,7,7,7},
											{X,1,2,2,7,7,7,7},
											{X,1,2,3,4,5,6,7},
											{X,7,7,4,4,5,7,7},
											{X,7,7,5,5,5,7,7},
											{X,7,7,6,7,7,7,7},
											{X,7,7,7,7,7,7,7}};
	
	private static final int [][] minus = {	{X,X,X,X,X,X,X,X},
											{X,7,7,1,1,1,7,7},
											{X,7,7,2,2,1,7,7},
											{X,5,4,3,2,1,6,7},
											{X,5,4,4,7,7,7,7},
											{X,5,5,5,7,7,7,7},
											{X,7,7,6,7,7,7,7},
											{X,7,7,7,7,7,7,7}};
	
	
	private static final int [][] times = {	{X,X,X,X,X,X,X,X},
											{X,5,4,3,2,1,6,7},
											{X,4,4,3,2,2,7,7},
											{X,3,3,3,3,3,3,3},
											{X,2,2,3,4,4,7,7},
											{X,1,2,3,4,5,6,7},
											{X,6,7,3,7,6,6,7},
											{X,7,7,3,7,7,7,7}};
	
	private static final int [][] div = {	{X,X,X,X,X,X,X,X},
											{X,5,5,X,1,1,6,6},
											{X,4,4,X,2,2,7,7},
											{X,3,3,X,3,3,3,3},
											{X,2,2,X,4,4,7,7},
											{X,1,1,X,5,5,6,6},
											{X,6,6,X,6,6,6,6},
											{X,7,7,X,7,7,7,7}};
	
	private static final boolean[][] leq	 = {{T,T,T,T,T,T,T,T},
											{F,T,T,F,F,F,T,T},
											{F,F,T,F,F,F,F,T},
											{F,F,T,T,T,F,F,T},
											{F,F,F,F,T,F,F,T},
											{F,F,F,F,T,T,T,T},
											{F,F,F,F,F,F,T,T},
											{F,F,F,F,F,F,F,T}};
	
	private static final boolean contains[][] = {{T,F,F,F,F,F,F,F},
												{T,T,F,F,F,F,F,F},
												{T,T,T,T,F,F,F,F},
												{T,F,F,T,F,F,F,F},
												{T,F,F,T,T,T,F,F},
												{T,F,F,F,F,T,F,F},
												{T,T,F,F,F,T,T,F},
												{T,T,T,T,T,T,T,T}};


	
	public static boolean hasN(int i){return i>=0 && N1[i];}
	public static boolean hasZ(int i){return i>=0 && Z1[i];}
	public static boolean hasP(int i){return i>=0 && P1[i];}
	
	private static int protectedAccess(int[][] a, int i, int j)
	{
		if(i<0 || j<0) return -1;
		else return a[i][j];
	}
	
	public static int cPrime(int i, int j) {return protectedAccess(cPrime, i, j);}
	public static int ub(int i, int j){return protectedAccess(ub, i, j);}
	public static int intersection(int i, int j){
		return protectedAccess(inter, i, j);}
	public static int except(int i, int j){return protectedAccess(except, i, j);}
	public static int plus(int i, int j){
		return protectedAccess(plus, i, j);}
	public static int minus(int i, int j){
		return protectedAccess(minus, i, j);}
	public static int times(int i, int j){return protectedAccess(times, i, j);}
	public static int div(int i, int j){return protectedAccess(div, i, j);}
	public static int mod(int i, int j){return protectedAccess(div, i, j);}
	public static boolean plusErr(int i,int j){ return i==0|j==0;}
	public static boolean minusErr(int i, int j){return plusErr(i,j);}
	public static boolean timesErr(int i, int j){return plusErr(i,j);}
	public static boolean divErr(int i, int j){return i==0|j==0|(j>=2&j<=4)|j==7;}
	public static boolean modErr(int i, int j){return divErr(i,j);}
	public static boolean leq(int i, int j){ 
		if(i<0 || j<0) return true;
		else return leq[i][j];}
	public static boolean contains(int i, int j){return contains[i][j];}
	public static boolean containsVals(int i){return i>0;}
	
	public static String toString(int i){
		switch(i){
			case 0: return "Int:{ }";
			case 1: return "Int:{-}";
			case 2: return "Int:{- 0}";
			case 3: return "Int:{0}";
			case 4: return "Int:{0 +}";
			case 5: return "Int:{+}";
			case 6: return "Int:{- +}";
			case 7: return "Int:{- 0 +}";
			default: return "ERROR"; 
		}
	}
	
	public static char[] getElements(int i){
		char[] t=null;		
		switch(i){
			case 0: char[] t0 = null; 			t = t0; break;
			case 1: char[] t1 = {'-'}; 			t = t1; break;
			case 2: char[] t2 = {'-','0'}; 		t = t2; break;
			case 3: char[] t3 = {'0'}; 			t = t3; break;
			case 4: char[] t4 = {'0','+'}; 		t = t4; break;
			case 5: char[] t5 = {'+'}; 			t = t5; break;
			case 6: char[] t6 = {'-','+'}; 		t = t6; break;
			case 7: char[] t7 = {'-','0','+'}; 	t = t7; break;
		}
		return t;
	}
	
	public static LinkedList<Integer> zaList(int pza)
	{
		LinkedList<Integer> res = new LinkedList<Integer>();
		if(PZa.hasN(pza)) res.add(PZa.MINUS);
		if(PZa.hasZ(pza)) res.add(PZa.ZERO);
		if(PZa.hasP(pza)) res.add(PZa.PLUS);
		return res;
	}
	
}

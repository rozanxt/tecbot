package zan.game.object;

public class Pair {
	
	public BaseObject pairA, pairB;
	
	public Pair(BaseObject pa, BaseObject pb) {
		pairA = pa; pairB = pb;
	}
	
	public static boolean equalPair(Pair pa, Pair pb) {
		if (pa.pairA == pb.pairB && pa.pairB == pb.pairA) return true;
		return false;
	}
	
}

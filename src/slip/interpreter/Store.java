package slip.interpreter;

import java.util.WeakHashMap;

public class Store {

	private static WeakHashMap<Ref, Objet> store = new WeakHashMap<Ref, Objet>();

	public static Objet getObjet(Ref ref) {
		return store.get(ref);
	}

	public static Ref setObjet(Objet objet) {
		Ref ref = new Ref();
		store.put(ref, objet);
		return ref;
	}
}

package root;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GraphPlan {

	private List<Capa> capas = new ArrayList<>();
	private Set<Literal> literales;
	private Set<Accion> acciones;
	private int fixedPointLevel = -1;

	public GraphPlan(Set<Literal> literales, Set<Accion> acciones) {
		super();
		this.literales = literales;
		this.acciones = acciones;

		Capa capaInicial = new Capa();
		/*
		 * Hay que copiar los literales que se reciben a la nueva capa pero SIN
		 * SUS PROPIEDADES, s髄o el nombre
		 */
		Map<Literal, Literal> hechos = new HashMap<>();
		for (Literal l : literales) {
			Literal aux = new Literal();
			aux.setNombre(l.getNombre());
			hechos.put(aux, aux);
		}
		capaInicial.setLiterales(hechos);

		capas.add(capaInicial);
	}

	public void expandirGrafo() {
		Capa nuevaCapa = new Capa();
		Capa capaPrevia = capas.get(capas.size() - 1);

		// Construimos las acciones y los literales
		Set<Accion> accionesNuevas = nuevaCapa.getAcciones();
		Set<Accion> accionesSinPersistencia = nuevaCapa
				.getAccionesSinPersistencia();

		Map<Literal, Literal> literales = nuevaCapa.getLiterales();

		// Crear NO-OP
		crearPersistencia(capaPrevia);

		// Comprobar acciones aplicables
		for (Accion accion : acciones) {
			System.out.println("Comprobando si la acci髇 es aplicable: "
					+ accion.getNombre());
			if (accion.esAplicable(capaPrevia.getLiterales())
					&& !accion.tienePrecondicionesMutex(capaPrevia
							.getMutexLiterales())) {

				accionesNuevas.add(accion);

				if (!accion.getEsPersistencia()) {
					accionesSinPersistencia.add(accion);
				}

				crearRelacionesNegativas(literales, accion);
				crearRelacionesPositivas(literales, accion);
				crearRelacionesSalida(capaPrevia.getLiterales(), accion);
			}
		}

		// Construir Mutex

		crearMutexAcciones(nuevaCapa, capaPrevia, accionesNuevas);
		crearMutexLiterales(nuevaCapa, literales);

		// A馻dir capa al grafo

		capas.add(nuevaCapa);

		System.out.println("\n\n" + nuevaCapa + "\n\n");

		if (isFixedPointLevel()) {
			fixedPointLevel = getCapas().size() - 1;
		}

	}

	/**
	 * Crea las acciones de persistencia para la siguiente capa.
	 * 
	 * @param capaPrevia
	 */
	private void crearPersistencia(Capa capaPrevia) {
		Iterator<Literal> iterator = capaPrevia.getLiterales().values()
				.iterator();
		while (iterator.hasNext()) {
			Literal l = iterator.next();

			Accion nuevaAccion = new Accion();
			nuevaAccion.setNombre(l.getNombre());
			nuevaAccion.setEsPersistencia(true);

			if (!acciones.contains(nuevaAccion)) {
				acciones.add(nuevaAccion);
				nuevaAccion.getEfectosPositivos().add(l);
				nuevaAccion.getPrecondiciones().add(l);
			}
		}
	}

	/**
	 * A馻de a cada literal una uni髇 con la acci髇 que lo tiene como efecto
	 * negativo.
	 * 
	 * @param literales
	 * @param accion
	 */
	private void crearRelacionesNegativas(Map<Literal, Literal> li,
			Accion accion) {
		System.out.println("Recibiendo acci髇: " + accion.getNombre());

		for (Literal efectoNegativo : accion.getEfectosNegativos()) {

			Literal key = efectoNegativo;
			Literal literalLi = li.get(key);
			// Si el literal no existe en la capa, se a馻de SIN PROPIEDADES
			if (literalLi == null) {
				literalLi = new Literal();
				literalLi.setNombre(key.getNombre());
				li.put(literalLi, literalLi);
			}
			literalLi.getNegIn().add(accion);
		}

	}

	/**
	 * Comprueba si las dos 鷏timas capas son iguales (fixed point)
	 * 
	 * @return
	 */
	public boolean isFixedPointLevel() {
		if (capas.size() < 2)
			return false;
		Capa nuevaCapa = capas.get(capas.size() - 1);
		Capa capaAnterior = capas.get(capas.size() - 2);
		boolean res = nuevaCapa.getLiterales().size() == capaAnterior
				.getLiterales().size()
				&& getNumParesMutex(nuevaCapa.getMutexLiterales()) == getNumParesMutex(capaAnterior
						.getMutexLiterales());
		return res;
	}

	/**
	 * Devuelve el n鷐ero de pares Mutex entre literales.
	 * 
	 * @param mutexLiterales
	 * @return
	 */
	public int getNumParesMutex(Map<Literal, MutexLiteral> mutexLiterales) {
		int num = 0;
		for (MutexLiteral l : mutexLiterales.values()) {
			num += l.getEnlacesMutex().size();
		}
		return num;
	}

	/**
	 * A馻de a cada literal una uni髇 con la acci髇 que lo tiene como efecto
	 * positivo.
	 * 
	 * @param literales
	 * @param accion
	 */
	private void crearRelacionesPositivas(Map<Literal, Literal> li,
			Accion accion) {

		for (Literal efectoPositivo : accion.getEfectosPositivos()) {

			Literal key = efectoPositivo;
			Literal literalLi = li.get(key);
			// Si el literal no existe en la capa, se a馻de SIN PROPIEDADES
			if (literalLi == null) {
				literalLi = new Literal();
				literalLi.setNombre(key.getNombre());
				li.put(literalLi, literalLi);
			}
			literalLi.getPosIn().add(accion);
		}
	}

	/**
	 * A馻de a cada literal la acci髇 que lo tiene como precondici髇.
	 * 
	 * @param literalesAnteriores
	 * @param accion
	 */
	private void crearRelacionesSalida(
			Map<Literal, Literal> literalesAnteriores, Accion accion) {

		Iterator<Literal> iterator = accion.getPrecondiciones().iterator();

		while (iterator.hasNext()) {

			Literal aux = (Literal) iterator.next();
			literalesAnteriores.get(aux).getSalida().add(accion);

		}
	}

	/**
	 * Crea MUTEX de los literales.
	 * 
	 * @param nuevaCapa
	 * @param literales
	 */
	private void crearMutexLiterales(Capa nuevaCapa,
			Map<Literal, Literal> literales) {
		List<Literal> listaLiterales = new ArrayList<>(literales.values());
		System.out
				.println("创创创创创创创创创创创创创创创创创创创创创创创创CREANDO MUTEX LITERALES: "
						+ Arrays.toString(listaLiterales.toArray()));
		/*
		 * Busca si cualquier par de acciones son excluyentes. Para ello recorre
		 * la lista de literales cogiendo un elemento y su siguiente
		 */
		for (int i = 0; i < listaLiterales.size(); i++) {
			Literal p = listaLiterales.get(i);
			Collection<Accion> accionesP = p.getPosIn();

			for (int j = i + 1; j < listaLiterales.size(); j++) {
				Literal q = listaLiterales.get(j);
				Collection<Accion> accionesQ = q.getPosIn();
				/*
				 * Recorremos todas las acciones que llevan a P comprobando si
				 * tambi閚 conducen a Q (accionesComunes = true)
				 */
				boolean accionesComunes = false;
				System.out.println("BUSCANDO ACCIONES COMUNES ENTRE " + p
						+ " [" + Arrays.toString(accionesP.toArray()) + "]"
						+ " y " + q + " ["
						+ Arrays.toString(accionesQ.toArray()) + "]");
				Iterator<Accion> iteratorP = accionesP.iterator();
				while (iteratorP.hasNext() && !accionesComunes) {
					accionesComunes = accionesQ.contains(iteratorP.next());
				}

				/*
				 * Si no hay ninguna accion com鷑 (creo que si hay comunes no
				 * pueden ser MUTEX los literales), comprobamos si TODAS las
				 * acciones comunes son mutex entre ellas
				 */

				// /////////////////////////////////////////////////////////////////////////
				// /////////////////////////////////////////////////////////////////////////
				// /////////////////////////////////////////////////////////////////////////
				// /////////////////////////////////////////////////////////////////////////

				if (!accionesComunes) {
					boolean esMutex = true;
					iteratorP = accionesP.iterator();
					while (iteratorP.hasNext() && esMutex) {
						/*
						 * Miramos si a cada acci髇 hay asociada una relaci髇
						 * Mutex
						 */
						MutexAccion mutex = nuevaCapa.getMutexAcciones().get(
								iteratorP.next());
						/*
						 * Si no hay mutex, o hay pero no contiene TODAS las
						 * acciones que llevan a Q, no se a馻dir� el enlace
						 * mutex entre los literales
						 */
						if (mutex == null
								|| mutex != null
								&& !mutex.getEnlacesMutex().containsAll(
										accionesQ)) {
							esMutex = false;
						}

					}
					/*
					 * Si s� que hay mutex entre TODAS, creamos la relaci髇 de
					 * exclusi髇 entre los literales
					 */
					if (esMutex) {
						System.out.println("MUTEXLITERAL: " + p + ", " + q);
						nuevaCapa.a馻dirNodoMutex(
								nuevaCapa.getMutexLiterales(), p, q);
					}

				}

			}
		}

	}

	/**
	 * Crea MUTEX de las acciones.
	 * 
	 * @param nuevaCapa
	 * @param capaPrevia
	 * @param accionesNuevas
	 */
	private void crearMutexAcciones(Capa nuevaCapa, Capa capaPrevia,
			Set<Accion> accionesNuevas) {

		List<Accion> listaAcciones = new ArrayList<>(accionesNuevas);

		for (int i = 0; i < listaAcciones.size(); i++) {
			Accion accionA = listaAcciones.get(i);
			for (int j = i + 1; j < listaAcciones.size(); j++) {
				Accion accionB = listaAcciones.get(j);
				/*
				 * Comprobamos si A y B son dependientes (hay interferencia o
				 * efectos inconsistentes). De serlo, ser醤 MUTEX
				 */
				if (!accionA.esIndependienteDe(accionB)) {
					nuevaCapa.a馻dirNodoMutex(nuevaCapa.getMutexAcciones(),
							accionA, accionB);
				} else {
					/*
					 * Comprobamos si en las precondiciones de A hay alguna que
					 * sea MUTEX con las de B (necesidades que compiten)
					 */
					Iterator<Literal> itA = accionA.getPrecondiciones()
							.iterator();
					while (itA.hasNext()) {
						Literal precondA = itA.next();
						MutexLiteral mutex = capaPrevia.getMutexLiterales()
								.get(precondA);
						if (mutex != null
								&& mutex.existeAlMenosUno(accionB
										.getPrecondiciones().iterator())) {
							nuevaCapa.a馻dirNodoMutex(
									nuevaCapa.getMutexAcciones(), accionA,
									accionB);
							System.out.println("MUTEX: " + accionA + ", "
									+ accionB);

							break; // Con una nos basta
						}
					}

				}
			}
		}

	}

	/**
	 * Comprueba que todos los literales del objetivo se encuentran presentes en
	 * la capa actual y que no hay mutex entre ellas.
	 * 
	 * @param objetivo
	 * @return
	 */
	public boolean cumpleObjetivo(List<Literal> objetivo) {
		Capa capaActual = capas.get(capas.size() - 1);
		for (Literal l : objetivo) {
			if (!capaActual.getLiterales().containsKey(l)) {
				return false;
			}
			MutexLiteral mutex = capaActual.getMutexLiterales().get(l);
			if (mutex != null && mutex.existeAlMenosUno(objetivo.iterator())) {
				return false;
			}
		}
		return true;
	}

	public Map<List<Literal>, List<Literal>> getNoGoodTableAt(int index) {

		return capas.get(index).getNoGoodTable();
	}

	public int getFixedPointLevel() {
		return fixedPointLevel;
	}

	public void setFixedPointLevel(int fixedPointLevel) {
		this.fixedPointLevel = fixedPointLevel;
	}

	public List<Capa> getCapas() {
		return capas;
	}

	public void setCapas(List<Capa> capas) {
		this.capas = capas;
	}

	public Set<Literal> getLiterales() {
		return literales;
	}

	public void setLiterales(Set<Literal> literales) {
		this.literales = literales;
	}

	public Set<Accion> getAcciones() {
		return acciones;
	}

	public void setAcciones(Set<Accion> acciones) {
		this.acciones = acciones;
	}

}

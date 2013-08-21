package root;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class GraphPlan {

	private List<Capa> capas = new ArrayList<>();
	private Set<Literal> literales;
	private Set<Accion> acciones;

	public GraphPlan(Set<Literal> literales, Set<Accion> acciones) {
		super();
		this.literales = literales;
		this.acciones = acciones;

		Capa capaInicial = new Capa();

		capaInicial.setLiterales(literales);

		capas.add(capaInicial);
	}

	public void expandirGrafo() {
		Capa nuevaCapa = new Capa();
		Capa capaPrevia = capas.get(capas.size() - 1);

		// Construimos las acciones y los literales
		Set<Accion> accionesNuevas = nuevaCapa.getAcciones();
		Set<Accion> accionesSinPersistencia = nuevaCapa
				.getAccionesSinPersistencia();

		Set<Literal> literales = nuevaCapa.getLiterales();

		// Crear NO-OP
		crearPersistencia(capaPrevia);

		// Comprobar acciones aplicables
		for (Accion accion : acciones) {
			System.out.println("Comprobando si la acci�n es aplicable: "
					+ accion.getNombre());
			if (accion.esAplicable(capaPrevia.getLiterales())
					&& !accion.tienePrecondicionesMutex(capaPrevia
							.getMutexLiterales())) {
				// FIXME A�ade demasiadas persistencias
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

		// A�adir capa al grafo

		capas.add(nuevaCapa);

		System.out.println("\n\n" + nuevaCapa + "\n\n");

	}

	private void crearPersistencia(Capa capaPrevia) {
		Iterator<Literal> iterator = capaPrevia.getLiterales().iterator();
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
	 * A�ade a cada literal una uni�n con la acci�n que lo tiene como efecto
	 * negativo.
	 * 
	 * @param literales
	 * @param accion
	 */
	private void crearRelacionesNegativas(Set<Literal> literales, Accion accion) {
		System.out.println("Recibiendo acci�n: " + accion.getNombre());

		// FIXME Las acciones no est�n bien asignadas

		for (Literal efectoNegativo : accion.getEfectosNegativos()) {
			Literal aux = new Literal();

			if (!literales.contains(efectoNegativo)) {
				aux.setNombre(efectoNegativo.getNombre());

			} else {
				aux = efectoNegativo;
			}
			aux.getNegIn().add(accion);

			// El problema del Set es que si ya existe, no modifica los datos,
			// por lo que hay que borrarlo y volverlo a meter. Con un Map se
			// solucionar�a
			if (literales.contains(aux)) {
				literales.remove(aux);
			}
			literales.add(aux);
		}

	}

	/**
	 * A�ade a cada literal una uni�n con la acci�n que lo tiene como efecto
	 * positivo.
	 * 
	 * @param literales
	 * @param accion
	 */
	private void crearRelacionesPositivas(Set<Literal> literales, Accion accion) {
		// FIXME Las acciones no est�n bien asignadas

		//List<Literal> listaLiterales = new ArrayList<Literal>(literales);

		for (Literal efectoPositivo : accion.getEfectosPositivos()) {
			Literal aux = new Literal();

			if (!literales.contains(efectoPositivo)) {
				System.out.println("A�ADIENDO EFECTO POSITIVO "
						+ efectoPositivo);
				// Para que no se a�adan otros par�metros

				aux.setNombre(efectoPositivo.getNombre());
				// literales.add(aux);
			} else {
				aux = efectoPositivo;
			}
			/*
			 * int index = listaLiterales.indexOf(efectoPositivo); if (index >=
			 * 0) { listaLiterales.get(index).getPosIn().add(accion); }
			 */
			System.out.println("A�ADIENDO ACCION A EFECTO POSITIVO "
					+ efectoPositivo + " ---- " + accion);

			aux.getPosIn().add(accion);

			// El problema del Set es que si ya existe, no modifica los datos,
			// por lo que hay que borrarlo y volverlo a meter. Con un Map se
			// solucionar�a
			if (literales.contains(aux)) {
				literales.remove(aux);
			}
			literales.add(aux);

			System.out
					.println(" %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% RESULTADO EFECTO "
							+ aux.getPosIn());

		}
	}

	/**
	 * A�ade a cada literal la acci�n que lo tiene como precondici�n.
	 * 
	 * @param literalesAnteriores
	 * @param accion
	 */
	private void crearRelacionesSalida(Set<Literal> literalesAnteriores,
			Accion accion) {
		// FIXME Las acciones no est�n bien asignadas

		Iterator<Literal> iterator = accion.getPrecondiciones().iterator();
		List<Literal> listaLiterales = new ArrayList<Literal>(
				literalesAnteriores);

		while (iterator.hasNext()) {
			/*
			 * Podr�a hacerse con un Map con K=V, pero por no usar memoria
			 * innecesaria...
			 */
			Literal aux = (Literal) iterator.next();

			int index = listaLiterales.indexOf(aux);
			listaLiterales.get(index).getSalida().add(accion);
			System.out.println("�?�? Arco salida: " + listaLiterales.get(index)
					+ " ��� " + accion);

		}
	}

	/**
	 * Crea MUTEX de los literales.
	 * 
	 * @param nuevaCapa
	 * @param literales
	 */
	private void crearMutexLiterales(Capa nuevaCapa, Set<Literal> literales) {
		List<Literal> listaLiterales = new ArrayList<>(literales);
		System.out
				.println("������������������������������������������������CREANDO MUTEX LITERALES: "
						+ Arrays.toString(listaLiterales.toArray()));
		// FIXME NO LOS CREA BIEN
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
				 * tambi�n conducen a Q (accionesComunes = true)
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

				// FIXME DEBE CREAR MUTEX[PatSpare,Ground :
				// {PatSpare,Trunk=PatSpare,Trunk}]
				/*
				 * Si no hay ninguna accion com�n (creo que si hay comunes no
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
						 * Miramos si a cada acci�n hay asociada una relaci�n
						 * Mutex
						 */
						MutexAccion mutex = nuevaCapa.getMutexAcciones().get(
								iteratorP.next());
						/*
						 * Si no hay mutex, o hay pero no contiene TODAS las
						 * acciones que llevan a Q, no se a�adir� el enlace
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
					 * Si s� que hay mutex entre TODAS, creamos la relaci�n de
					 * exclusi�n entre los literales
					 */
					if (esMutex) {
						System.out.println("MUTEXLITERAL: " + p + ", " + q);
						nuevaCapa.a�adirNodoMutex(
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

		// FIXME
		List<Accion> listaAcciones = new ArrayList<>(accionesNuevas);

		for (int i = 0; i < listaAcciones.size(); i++) {
			Accion accionA = listaAcciones.get(i);
			for (int j = i + 1; j < listaAcciones.size(); j++) {
				Accion accionB = listaAcciones.get(j);
				/*
				 * Comprobamos si A y B son dependientes (hay interferencia o
				 * efectos inconsistentes). De serlo, ser�n MUTEX
				 */
				if (!accionA.esIndependienteDe(accionB)) {
					nuevaCapa.a�adirNodoMutex(nuevaCapa.getMutexAcciones(),
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
							nuevaCapa.a�adirNodoMutex(
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
	// FIXME
	public boolean cumpleObjetivo(List<Literal> objetivo) {
		Capa capaActual = capas.get(capas.size() - 1);
		for (Literal l : objetivo) {
			if (!capaActual.getLiterales().contains(l)) {
				return false;
			}
			MutexLiteral mutex = capaActual.getMutexLiterales().get(l);
			if (mutex != null && mutex.existeAlMenosUno(objetivo.iterator())) {
				return false;
			}
		}
		return true;
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

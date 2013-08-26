package root;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AlgoritmoGraphPlan {

	private GraphPlan graphPlan;
	private String salida;
	private Set<Accion> acciones;

	public GraphPlan getGraphPlan() {
		return graphPlan;
	}

	public void setGraphPlan(GraphPlan graphPlan) {
		this.graphPlan = graphPlan;
	}

	public String getSalida() {
		return salida;
	}

	public void setSalida(String salida) {
		this.salida = salida;
	}

	public Set<Accion> getAcciones() {
		return acciones;
	}

	public void setAcciones(Set<Accion> acciones) {
		this.acciones = acciones;
	}

	public Set<Literal> getHechos() {
		return hechos;
	}

	public void setHechos(Set<Literal> hechos) {
		this.hechos = hechos;
	}

	public List<Literal> getObjetivo() {
		return objetivo;
	}

	public void setObjetivo(List<Literal> objetivo) {
		this.objetivo = objetivo;
	}

	private Set<Literal> hechos;
	private List<Literal> objetivo;

	public AlgoritmoGraphPlan(Set<Accion> acciones, Set<Literal> hechos,
			List<Literal> objetivo) {
		super();
		graphPlan = new GraphPlan(hechos, acciones);
		this.acciones = acciones;
		this.hechos = hechos;
		this.objetivo = objetivo;
	}

	/**
	 * Indica si hay posibilidad de encontrar solución.
	 * 
	 * @return
	 */
	private boolean crearGrafoPlanificacion() {
		while (!graphPlan.cumpleObjetivo(objetivo)) {

			if (graphPlan.isFixedPointLevel()) {
				return false;
			} else {
				graphPlan.expandirGrafo();
			}

		}
		return true;
	}

	public List<List<Accion>> ejecutar() {
		boolean existePlan = crearGrafoPlanificacion();
		if (!existePlan) {
			salida = "No se encontró solución =(";
			return null;
		}

		mensajeInicioAlgoritmo();
		List<List<Accion>> plan = ejecutarAlgoritmo();
		mensajeFinalAlgoritmo(plan);

		return plan;

	}

	private void mensajeFinalAlgoritmo(List<List<Accion>> plan) {
		// Añadir tiempo de ejecución
		StringBuilder sB = new StringBuilder();
		sB.append(" ··········· Resultado ··········· \n");
		if (plan != null) {
			sB.append("Solución: " + plan.toString() + "\n");
		} else {
			sB.append("NO EXISTE SOLUCIÓN");
		}
	}

	private void mensajeInicioAlgoritmo() {

		StringBuilder sB = new StringBuilder();
		sB.append("Estado inicial: " + hechos.toString() + "\n");
		sB.append("Objetivo: " + objetivo.toString() + "\n");

		salida = sB.toString();

	}

	private List<List<Accion>> ejecutarAlgoritmo() {
		List<List<Accion>> plan = extraerSolucion(graphPlan, objetivo,
				graphPlan.getCapas().size() - 1);

		int n = 0;

		while (plan == null) {
			graphPlan.expandirGrafo();
			plan = extraerSolucion(graphPlan, objetivo, graphPlan.getCapas()
					.size() - 1);
			/*
			 * 
			 */
			if (plan == null && graphPlan.isFixedPointLevel()) {
				int numNoGood = graphPlan.getNoGoodTableAt(
						graphPlan.getFixedPointLevel()).size();

				if (n == numNoGood) {
					return null;
				} else {
					n = numNoGood;
				}
			}
		}

		return eliminarPersistencias(plan);
	}

	/**
	 * Extrae soluciones parciales para un nivel dado.
	 * 
	 * @param graphPlan
	 * @param objetivo
	 * @param i
	 * @return
	 */
	private List<List<Accion>> extraerSolucion(GraphPlan graphPlan,
			List<Literal> objetivo, int i) {

		System.out.println("EXTRAER SOLUCIÓN CON OBJETIVO: "
				+ Arrays.toString(objetivo.toArray()));
		/*
		 * Caoa inicial
		 */
		if (i == 0) {
			return new ArrayList<List<Accion>>();
		}
		/*
		 * Ya analizado con resultado negativo.
		 */
		if (graphPlan.getNoGoodTableAt(i).containsKey(objetivo)) {
			return null;
		}
		/*
		 * Si ni ha fallado anteriormente ni es la capa inicial, buscamos un
		 * plan.
		 */
		List<List<Accion>> planParcial = buscar(graphPlan, objetivo,
				new ArrayList<Accion>(), i);

		if (planParcial != null) {
			System.out.println("PLAN PARCIAL: "
					+ Arrays.toString(planParcial.toArray()));
			return planParcial;
		}
		/*
		 * No se consigue plan, por tanto se mete en la tabla noGood
		 */
		graphPlan.getNoGoodTableAt(i).put(objetivo, objetivo);

		return null;
	}

	/**
	 * Búsqueda hacia atrás.
	 * 
	 * @param grafo
	 * @param objetivo
	 * @param planParcial
	 * @param i
	 * @return
	 */
	public List<List<Accion>> buscar(GraphPlan grafo, List<Literal> objetivo,
			List<Accion> planParcial, int i) {
		if (objetivo.size() == 0) {

			List<Literal> precondiciones = getPrecondiciones(planParcial);
			List<List<Accion>> p = extraerSolucion(grafo, precondiciones, i - 1);

			if (p == null) {
				return null;
			}

			p.add(planParcial);

			return p;
		} else {
			// Para cada literal, buscamos las acciones que los tienen como
			// efectos
			for (int j = objetivo.size() - 1; j >= 0; j--) {
				Literal l = objetivo.get(j);
				System.out.println("OBJETIVO RESOLVERS: " + objetivo);
				List<Accion> resolvers = getResolvers(grafo.getCapas().get(i),
						l, planParcial, i);
				if (resolvers.size() == 0) {
					return null;
				}
				/*
				 * Recorremos todas las posibles acciones a aplicar para
				 * comprobar si son aplicables, intentando buscar una solución
				 * con ellas.
				 */
				for (Accion a : resolvers) {

					List<Literal> nuevoObjetivo = subconjuntoObjetivos(
							objetivo, a.getEfectosPositivos());
					System.out.println("OBJETIVO: " + objetivo
							+ " NUEVO OBJETIVO: " + nuevoObjetivo);
					planParcial.add(a);
					List<List<Accion>> plan = buscar(grafo, nuevoObjetivo,
							planParcial, i);
					if (plan == null) {
						planParcial.remove(a);
					} else {
						return plan;
					}
				}

			}
		}
		return null;
	}

	/**
	 * Quita a los objetivos que recibe los que son consecuencia de la acción
	 * recibida como parámetro.
	 * 
	 * @param objetivo2
	 * @param efectosPositivos
	 * @return
	 */
	private List<Literal> subconjuntoObjetivos(List<Literal> objetivo,
			Set<Literal> efectosPositivos) {

		List<Literal> nuevoObjetivo = new ArrayList<Literal>();

		for (int j = objetivo.size() - 1; j >= 0; j--) {
			Literal l = objetivo.get(j);
			if (!efectosPositivos.contains(l)) {
				nuevoObjetivo.add(l);
			}
		}

		return nuevoObjetivo;
	}

	/**
	 * Devuelve todas las precondiciones del plan parcial actual.
	 * 
	 * @param planParcial
	 * @return
	 */

	private List<Literal> getPrecondiciones(List<Accion> planParcial) {
		List<Literal> precondiciones = new ArrayList<Literal>();
		for (Accion a : planParcial) {
			precondiciones.addAll(a.getPrecondiciones());
		}
		return precondiciones;
	}

	/**
	 * Devuelve las acciones que tienen al literal l como consecuencia
	 * 
	 * @param capa
	 * @param l
	 * @param planParcial
	 * @param index
	 * @return
	 */
	private List<Accion> getResolvers(Capa capa, Literal l,
			List<Accion> planParcial, int index) {
		List<Accion> resolvers = new ArrayList<Accion>();

		Literal l2 = capa.getLiterales().get(l);

		Map<Accion, MutexAccion> mutexAcciones = capa.getMutexAcciones();

		Iterator<Accion> iterator = l2.getPosIn().iterator();
		/*
		 * Recorremos todas las acciones que llevan al literal l2 y comprobamos
		 * si cada una de estas acciones es mutex con alguna de las existentes
		 * en el plan parcial.
		 */
		System.out
				.println("------------------------------------------------- POSIN: "
						+ l2.getPosIn());
		for (int i = l2.getPosIn().size(); i > 0; i--) {
			Accion a = iterator.next();
			System.out.println("Accion resolver: " + a);

			boolean esMutex = false;
			Iterator<Accion> iterator2 = planParcial.iterator();

			while (iterator2.hasNext() && !esMutex) {
				MutexAccion mutex = mutexAcciones.get(iterator2.next());
				esMutex = mutex != null && mutex.getEnlacesMutex().contains(a);
			}
			if (!esMutex) {
				if (a.getEsPersistencia()) {
					resolvers.add(0, a);
				} else {
					resolvers.add(resolvers.size(), a);
				}
			}
		}
		System.out.println("---- Resolvers: " + resolvers + "PLAN: "
				+ planParcial + "LITERAL: " + l + " INDEX: " + index);

		return resolvers;
	}

	/**
	 * Elimina las acciones de persistencias del plan para mostrar el resultado final.
	 * 
	 * @param plan
	 * @return
	 */
	private List<List<Accion>> eliminarPersistencias(List<List<Accion>> plan) {
		List<List<Accion>> nuevoPlan = new ArrayList<List<Accion>>(plan.size());
		for (List<Accion> lista : plan) {
			List<Accion> nuevaLista = new ArrayList<Accion>(lista.size());
			nuevoPlan.add(nuevaLista);
			for (Accion a : lista) {
				if (!a.getEsPersistencia()) {
					nuevaLista.add(a);
				}
			}
		}
		return nuevoPlan;
	}
}

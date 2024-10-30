package com.aluracursos.screenmatch.principal;

import com.aluracursos.screenmatch.model.*;
import com.aluracursos.screenmatch.repository.SerieRepository;
import com.aluracursos.screenmatch.service.ConsumoAPI;
import com.aluracursos.screenmatch.service.ConvierteDatos;

import java.util.*;
import java.util.stream.Collectors;

public class Principal {
    private Scanner teclado = new Scanner(System.in);
    private ConsumoAPI consumoApi = new ConsumoAPI();
    private final String URL_BASE = "https://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=YOUR_KEY";
    private ConvierteDatos conversor = new ConvierteDatos();
    private List<DatosSerie> datosSeries = new ArrayList<>();
    private SerieRepository repository;

    private List<Serie> series;

    private Optional<Serie> serieBuscada;

    public Principal(SerieRepository repository) { //inyectamos la dependencia en el constructor
        this.repository = repository;
    }

    public void muestraElMenu() {
        var opcion = -1;
        while (opcion != 0) {
            var menu = """
                    1 - Buscar series 
                    2 - Buscar episodios
                    3 - Mostrar series buscadas
                    4 - Buscar series por titulo
                    5 - Top 5 series
                    6 - Busqueda de series por genero
                    7 - Busqueda de series por cantidad de temporadas y por evaluacion
                    8 . Buscar episodios por nombre
                    9 - Top 5 episodios por serie
                                  
                    0 - Salir
                    """;
            System.out.println(menu);
            opcion = teclado.nextInt();
            teclado.nextLine();

            switch (opcion) {
                case 1:
                    buscarSerieWeb();
                    break;
                case 2:
                    buscarEpisodioPorSerie();
                    break;
                case 3:
                    mostrarSeriesBuscadas();
                    break;
                case 4:
                    buscarSeriesPorTitulo();
                    break;
                case 5:
                    buscarTop5Series();
                    break;
                case 6:
                    buscarSeriesPorCategoria();
                    break;
                case 7:
                    buscarPorTotalTemporadasYEvaluacion();
                    break;
                case 8:
                    buscarEpisodiosPorTitulo();
                    break;
                case 9:
                    buscar5Episodios();
                    break;
                case 0:
                    System.out.println("Cerrando la aplicación...");
                    break;
                default:
                    System.out.println("Opción inválida");
            }
        }

    }

    private void buscar5Episodios() {
        //primero buscamos por series
        buscarSeriesPorTitulo();

        if(serieBuscada.isPresent()){
            Serie serie = serieBuscada.get();
            List<Episodio> episodios = repository.top5Episodios(serie);
            episodios.forEach(e -> System.out.printf("Serie: %s Temporada: %s Episodio: %s Evaluacion: %s\n",
                    e.getSerie().getTitulo(), e.getTemporada(), e.getTitulo(), e.getEvaluacion()));
        }
    }

    private void buscarEpisodiosPorTitulo() {
        System.out.println("Escribe el nombre del episodio que deseas buscar: ");
        var nombreEpisodio = teclado.nextLine();

        List<Episodio> episodios = repository.episodiosPorNombre(nombreEpisodio);
        episodios.forEach(e -> System.out.printf("Serie: %s Temporada: %s Episodio: %s Evaluacion: %s\n",
                e.getSerie().getTitulo(), e.getTemporada(), e.getTitulo(), e.getEvaluacion()));
    }

    private void buscarPorTotalTemporadasYEvaluacion() {
        System.out.println("Escribe el número de temporadas minimo: ");
        var totalTemporadas = teclado.nextInt();
        System.out.println("Escribe la evaluación minima: ");
        var evaluacion = teclado.nextDouble();

        List<Serie> series = repository.seriesPorTemporadaYEvaluacion(totalTemporadas, evaluacion);
        series.forEach(s -> System.out.println(s.getTitulo()));
    }

    private void buscarSeriesPorCategoria() {
        System.out.println("Escribe el genero/categoria de la serie que quieres buscar: ");
        var genero = teclado.nextLine();

        //transformamos el string a un enum de Categoria
        var categoria = Categoria.fromEspaniol(genero);
        List<Serie> seriesPorCategoria = repository.findByGenero(categoria);
        System.out.println("las series de la categoria " + genero );
        seriesPorCategoria.forEach(s -> System.out.println(s.getTitulo()));

    }

    private void buscarTop5Series() {
        List<Serie> top5 = repository.findTop5ByOrderByEvaluacionDesc();
        top5.forEach(s -> System.out.println(s.getTitulo() + " - " + s.getEvaluacion()));
    }

    private void buscarSeriesPorTitulo() {
        System.out.println("Escribe el nombre de la serie que deseas buscas: ");
        var nombreSerie = teclado.nextLine();

        serieBuscada = repository.findByTituloContainsIgnoreCase(nombreSerie);

        if (serieBuscada.isPresent()) {
            System.out.println("La serie buscada es: " + serieBuscada.get().getTitulo());
        } else {
            System.out.println("No se encontró la serie");
        }
    }

    private DatosSerie getDatosSerie() {
        System.out.println("Escribe el nombre de la serie que deseas buscar");
        var nombreSerie = teclado.nextLine();
        var json = consumoApi.obtenerDatos(URL_BASE + nombreSerie.replace(" ", "+") + API_KEY);
        System.out.println(json);
        DatosSerie datos = conversor.obtenerDatos(json, DatosSerie.class);
        return datos;
    }
    private void buscarEpisodioPorSerie() {
        mostrarSeriesBuscadas();
        System.out.println("Escribe el nombre de la serie de la que deseas buscar episodios: ");
        var nombreSerie = teclado.nextLine();

        //realizamos una busqueda que puede o no tener resultados
        Optional<Serie> serie = series.stream()
                .filter(s -> s.getTitulo().toLowerCase().contains(nombreSerie.toLowerCase()))
                .findFirst();

        if(serie.isPresent()){
            var serieEncontrada = serie.get();//con el get() obtenemos el contenido del Optional
            List<DatosTemporadas> temporadas = new ArrayList<>();

            for (int i = 1; i <= serieEncontrada.getTotalTemporadas(); i++) {
                var json = consumoApi.obtenerDatos(URL_BASE + serieEncontrada.getTitulo()
                        .replace(" ", "+") + "&season=" + i + API_KEY);
                DatosTemporadas datosTemporada = conversor.obtenerDatos(json, DatosTemporadas.class);
                temporadas.add(datosTemporada);
            }
            temporadas.forEach(System.out::println);

            //convertomos la lista de temporadas a una de episodios
            List<Episodio> episodios = temporadas.stream()
                    .flatMap(d -> d.episodios().stream() //dado que temporadas es una lista de listas, se convierte a una sola lista
                            .map(e -> new Episodio(d.numero(), e)))
                    .collect(Collectors.toList());

            serieEncontrada.setEpisodios(episodios);
            repository.save(serieEncontrada); //si bien save() es para guardar, en este caso se actualiza la serie
            //sus episodios
        }

    }
    private void buscarSerieWeb() {
        DatosSerie datos = getDatosSerie();
        Serie serie = new Serie(datos);
        repository.save(serie);
//        datosSeries.add(datos);
        System.out.println(datos);
    }

    private void mostrarSeriesBuscadas() {
        series = repository.findAll(); //obtenemos todas las series de la base de datos

        //ordenar por genero
        series.stream()
                .sorted(Comparator.comparing(Serie::getGenero).reversed())
                .forEach(System.out::println);

    }
}


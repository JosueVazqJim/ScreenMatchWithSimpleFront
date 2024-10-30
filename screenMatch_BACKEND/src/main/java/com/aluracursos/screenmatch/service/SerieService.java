package com.aluracursos.screenmatch.service;

import com.aluracursos.screenmatch.controller.dto.EpisodioDTO;
import com.aluracursos.screenmatch.controller.dto.SerieDTO;
import com.aluracursos.screenmatch.model.Categoria;
import com.aluracursos.screenmatch.model.Serie;
import com.aluracursos.screenmatch.repository.SerieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service //anotación que indica que esta clase es un servicio
public class SerieService {

    //se inyecta la dependencia
    @Autowired
    private SerieRepository repository;

    public List<SerieDTO> obtenerTodasLasSeries() {
        return convierteDatos(repository.findAll());
    }

    public List<SerieDTO> obtenerTop5() {
        return convierteDatos(repository.findTop5ByOrderByEvaluacionDesc());
    }

    public List<SerieDTO> obtenerLanzamientosRecientes() {
        return convierteDatos(repository.lanzamientosRecientes());
    }

    public SerieDTO obtenerPorId(Long id) {
        Optional<Serie> serie = repository.findById(id);

        if (serie.isPresent()) {
            Serie s = serie.get();
            return new SerieDTO(s.getId(), s.getTitulo(), s.getTotalTemporadas(), s.getEvaluacion(), s.getPoster()
                    , s.getGenero(), s.getActores(), s.getSinopsis());
        }
        return null;
    }

    public List<EpisodioDTO> obtetenerTodasLasTemporadas(Long id) {
        Optional<Serie> serie = repository.findById(id);

        if (serie.isPresent()) {
            Serie s = serie.get();
            return s.getEpisodios().stream().map(e -> new EpisodioDTO(e.getTemporada(), e.getTitulo(), e.getNumeroEpisodio()))
                    .collect(Collectors.toList());
        }
        return null;
    }

    public List<EpisodioDTO> obtenerEpisodiosPorTemporada(Long id, Long temporada) {
//        Optional<Serie> serie = repository.findById(id);
//
//        if (serie.isPresent()) {
//            Serie s = serie.get();
//            return s.getEpisodios().stream()
//                    .filter(e -> e.getTemporada().equals(temporada))
//                    .map(e -> new EpisodioDTO(e.getTemporada(), e.getTitulo(), e.getNumeroEpisodio()))
//                    .collect(Collectors.toList());
//        }
//        return null;
        return repository.obtenerTemporadasPorNumero(id, temporada).stream()
                .map(e -> new EpisodioDTO(e.getTemporada(), e.getTitulo(), e.getNumeroEpisodio()))
                .collect(Collectors.toList());
    }

    public List<SerieDTO> obtenerSeriesPorCategoria(String categoria) {
        Categoria cat = Categoria.fromEspaniol(categoria);
        return convierteDatos(repository.findByGenero(cat));
    }

    public List<SerieDTO> convierteDatos(List<Serie> serie) {
        return serie.stream()
                .map(s -> new SerieDTO(s.getId(), s.getTitulo(), s.getTotalTemporadas(), s.getEvaluacion(), s.getPoster()
                        , s.getGenero(), s.getActores(), s.getSinopsis()))
                .collect(Collectors.toList());
    }

}

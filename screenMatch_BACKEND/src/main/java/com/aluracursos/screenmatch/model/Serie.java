package com.aluracursos.screenmatch.model;

import jakarta.persistence.*;
import java.util.List;
import java.util.Optional;

@Entity //se indica que es una entidad, es decir, una tabla
@Table(name = "series") //se indica el nombre de la tabla

public class Serie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //se indica que es autoincremental
    private long id;
    @Column(unique = true) //se indica que el titulo es unico
    private String titulo;
    private int totalTemporadas;
    private double evaluacion;
    private String poster;
    @Enumerated(EnumType.STRING) //se indica que el genero es un string
    private Categoria genero;
    private String actores;
    private String sinopsis;
    @OneToMany(mappedBy = "serie", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    //se indica que es una relacion uno a muchos
    //serie es el campo que representa la Serie en la entidad Episodio
    //el poner EAGER indica que se cargan todos los episodios de la serie al cargar la serie
    //el cascade indica cambios en cascada en la entidad Serie. el CascadeType.ALL indica que se aplican todos los cambios
    //en este caso al usar repository.save(serie) se guardan los episodios de la serie pues hubo un cambio en la entidad Serie
    private List<Episodio> episodios;

    public Serie() {
    }

    public Serie(DatosSerie datosSerie) {
        this.titulo = datosSerie.titulo();
        this.totalTemporadas = datosSerie.totalTemporadas();
        //dado que evaluacion es un string, se intenta convertir a double
        this.evaluacion = Optional.of(Double.valueOf(datosSerie.evaluacion())).orElse(0.0);
        this.poster = datosSerie.poster();
        this.genero = Categoria.fromString(datosSerie.genero().split(",")[1].trim()); //se convierte el string a Categoria o en nunll
        this.actores = datosSerie.actores();
        this.sinopsis = datosSerie.sinopsis();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public int getTotalTemporadas() {
        return totalTemporadas;
    }

    public void setTotalTemporadas(int totalTemporadas) {
        this.totalTemporadas = totalTemporadas;
    }

    public double getEvaluacion() {
        return evaluacion;
    }

    public void setEvaluacion(double evaluacion) {
        this.evaluacion = evaluacion;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public Categoria getGenero() {
        return genero;
    }

    public void setGenero(Categoria genero) {
        this.genero = genero;
    }

    public String getActores() {
        return actores;
    }

    public void setActores(String actores) {
        this.actores = actores;
    }

    public String getSinopsis() {
        return sinopsis;
    }

    public void setSinopsis(String sinopsis) {
        this.sinopsis = sinopsis;
    }

    public List<Episodio> getEpisodios() {
        return episodios;
    }

    public void setEpisodios(List<Episodio> episodios) {
        episodios.forEach(e -> e.setSerie(this)); //se asigna la serie a cada episodio dado que se usa una perspectiva bidireccional
        this.episodios = episodios;
    }

    @Override
    public String toString() {
        return
                ", genero=" + genero + '\'' +
                "titulo='" + titulo + '\'' +
                ", totalTemporadas=" + totalTemporadas +
                ", evaluacion=" + evaluacion +
                ", poster='" + poster + '\'' +
                ", actores='" + actores + '\'' +
                ", sinopsis='" + sinopsis + '\'' +
                ", Epidosios='" + episodios + '\n' + '\n';
    }
}

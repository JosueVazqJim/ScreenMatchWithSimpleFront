package com.aluracursos.screenmatch.model;

public enum Categoria {
    ACCION("Action", "Acción"),
    ANIMACION("Animation", "Animación"),
    AVENTURA("Adventure" , "Aventura"),
    CIENCIA_FICCION("Sci-Fi" , "Ciencia Ficción"),
    COMEDIA("Comedy" , "Comedia"),
    DOCUMENTAL("Documentary" , "Documental"),
    DRAMA("Drama" , "Drama"),
    FANTASIA("Fantasy", "Fantasía"),
    INFANTIL("Children", "Infantil"),
    MISTERIO("Mystery", "Misterio"),
    SUSPENSO("Thriller", "Suspense"),
    TERROR("Horror", "Terror"),
    ROMANCE("Romance", "Romance"),
    CRIMEN("Crime", "Crimen"),;

    private String categoriaOMDB;
    private String categoriaEspaniol;

    Categoria(String categoriaOMDB, String categoriaEspaniol) {
        this.categoriaOMDB = categoriaOMDB;
        this.categoriaEspaniol = categoriaEspaniol;
    }

    //metodo para obtener la categoria de OMDB.
    //lo que hace es recorrer el enum y si la categoria de OMDB es igual a la categoria que se le pasa como parametro
    //devuelve la categoria en forma de Categoria y no de String
    public static Categoria fromString(String categoria){
        for (Categoria c : Categoria.values()){
            if (c.categoriaOMDB.equals(categoria)){
                return c;
            }
        }
        throw new IllegalArgumentException("Categoria no encontrada" + categoria);
    }

    //metodo para obtener la categoria en español
    public static Categoria fromEspaniol(String categoria){
        for (Categoria c : Categoria.values()){
            if (c.categoriaEspaniol.equalsIgnoreCase(categoria)){
                return c;
            }
        }
        throw new IllegalArgumentException("Categoria no encontrada" + categoria);
    }
}

package com.aluracursos.LiterAlura.principal;

import com.aluracursos.LiterAlura.model.dto.DatosLibro;
import com.aluracursos.LiterAlura.model.dto.Datos;
import com.aluracursos.LiterAlura.model.entity.Libro;
import com.aluracursos.LiterAlura.model.entity.Autor;
import com.aluracursos.LiterAlura.repository.AutorRepository;
import com.aluracursos.LiterAlura.repository.LibroRepository;
import com.aluracursos.LiterAlura.service.ConsumoAPI;
import com.aluracursos.LiterAlura.service.ConvierteDatos;

import java.util.*;
import java.util.stream.Collectors;


public class Principal {
    private Scanner teclado = new Scanner(System.in);
    private  ConsumoAPI consumoApi = new ConsumoAPI();
    private final String URL_BASE = "http://gutendex.com/books/?search=";
    private ConvierteDatos conversor = new ConvierteDatos();
    public LibroRepository libroRepository;
    public AutorRepository autorRepository;


    public Principal(LibroRepository libroRepository, AutorRepository autorRepository) {
        this.libroRepository = libroRepository;
        this.autorRepository = autorRepository;
    }

    private void leerLibro(Libro libro) {
        System.out.printf("""
                        ----- LIBRO -----
                        Titulo: %s
                        Autor: %s
                        Idioma: %s
                        Numero de descargas: %d
                        -------------------- \n
                        """,
                libro.getTitulo(),
                libro.getAutor().getNombre(),
                libro.getIdioma(),
                libro.getNumeroDeDescargas());
    }

    private void leerAutor(Autor autor) {
        System.out.printf("""
                        Autor: %s
                        Fecha de nacimiento: %s
                        Fecha de fallecimiento: %s
                        """,
                autor.getNombre(),
                autor.getFechaDeNacimiento(),
                autor.getFechaDeFallecimiento());

        var libros = autor.getLibros().stream()
                .map(a -> a.getTitulo())
                .collect(Collectors.toList());
        System.out.println("Libros: " + libros + "\n");
    }

    private void listarLibrosRegistrados() {
        List<Libro> libros = libroRepository.findAll();
        libros.stream()
                .forEach(this::leerLibro);
    }


    private void listarAutoresRegistrados() {
        List<Autor> autores = autorRepository.findAll();
        autores.stream()
                .forEach(this::leerAutor);
    }

    private void listarAutoresPorAño() {
        System.out.println("Ingresa el año vivo de autor(es) que desea buscar");
        Integer año = teclado.nextInt();
        List<Autor> autores = autorRepository.findByFechaDeFallecimientoGreaterThan(año);
        if (autores.isEmpty()){
            System.out.println("No se encontraron autores en esa fecha");
        }else
            {
            autores.stream()
                .forEach(this::leerAutor);
            }
    }

    private void listarLibrosPorIdioma() {
        System.out.println("""
                Ingrese el idioma para buscar los libros
                es - español
                en - ingles
                fr - frances
                pt - portugues
                """);
        String idioma = teclado.next();
        List<Libro> libros = libroRepository.findByIdioma(idioma);
        libros.stream()
                .forEach(this::leerLibro);
    }


    public void muestraElMenu() {
        var opcion = -1;
        while (opcion != 0) {
            var menu = """ 
                    ********** CHALLENGE LITERALURA **********
                    1 - Buscar libro por titulo
                    2 - Listar libros registrados
                    3 - Listar autores registrados
                    4 - Listar autores vivos en determinado año
                    5 - Listar libros por idioma
                    0 - Salir
                    ********** ********** ********** **********
                    """;
            System.out.println(menu);
            opcion = teclado.nextInt();
            teclado.nextLine();

            switch (opcion) {
                case 1:
                    buscarLibroPorTitulo();
                    break;
                case 2:
                    listarLibrosRegistrados();
                    break;
                case 3:
                    listarAutoresRegistrados();
                    break;
                case 4:
                    listarAutoresPorAño();
                    break;
                case 5:
                    listarLibrosPorIdioma();
                    break;
                case 0:
                    System.out.println("Cerrando la aplicación...");
                    break;
                default:
                    System.out.println("Opción inválida");
            }
        }
    }

    private void buscarLibroPorTitulo() {
        System.out.println("Escribe el nombre del libro que deseas buscar");
        var nombreLibro = teclado.nextLine();
        var json = consumoApi.obtenerDatos(URL_BASE + nombreLibro.replace(" ", "+"));
        List<DatosLibro> libros = conversor.obtenerDatos(json, Datos.class).resultado();
        Optional<DatosLibro> libroOptional = libros.stream()
                .filter(l -> l.titulo().toLowerCase().contains(nombreLibro.toLowerCase()))
                .findFirst();
        if (libroOptional.isPresent()){
            var libro = new Libro(libroOptional.get());
            libroRepository.save(libro);
            leerLibro(libro);
        } else {
                System.out.println("No se encontro el libro");
            }
        }


    }




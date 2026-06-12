package br.ucalc.calculadora_pcs.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "processo")
public class Processo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String autor;
    private String reu;
    private String autos;
    private String comarca;
    private String vara;
    private String pgenet;

    @OneToMany(mappedBy = "processo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Calculo> calculos = new ArrayList<>();

    // Construtores
    public Processo() {}

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getAutor() { return autor; }
    public void setAutor(String autor) { this.autor = autor; }

    public String getReu() { return reu; }
    public void setReu(String reu) { this.reu = reu; }

    public String getAutos() { return autos; }
    public void setAutos(String autos) { this.autos = autos; }

    public String getComarca() { return comarca; }
    public void setComarca(String comarca) { this.comarca = comarca; }

    public String getVara() { return vara; }
    public void setVara(String vara) { this.vara = vara; }

    public String getPgenet() { return pgenet; }
    public void setPgenet(String pgenet) { this.pgenet = pgenet; }

    public List<Calculo> getCalculos() { return calculos; }
    public void setCalculos(List<Calculo> calculos) { this.calculos = calculos; }
}

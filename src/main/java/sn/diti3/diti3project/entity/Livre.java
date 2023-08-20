package sn.diti3.diti3project.entity;

public class Livre {
    private int id, etatEmprunt, nbPages;
    private String titre, auteur, isbn;

    public Livre() {
    }

    public Livre(int id, int etatEmprunt, int nbPages, String titre, String auteur, String isbn) {
        this.id = id;
        this.etatEmprunt = etatEmprunt;
        this.nbPages = nbPages;
        this.titre = titre;
        this.auteur = auteur;
        this.isbn = isbn;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getEtatEmprunt() {
        return etatEmprunt;
    }

    public void setEtatEmprunt(int etatEmprunt) {
        this.etatEmprunt = etatEmprunt;
    }

    public int getNbPages() {
        return nbPages;
    }

    public void setNbPages(int nbPages) {
        this.nbPages = nbPages;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getAuteur() {
        return auteur;
    }

    public void setAuteur(String auteur) {
        this.auteur = auteur;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }
}

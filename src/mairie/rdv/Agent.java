package mairie.rdv;

public class Agent {

    // ATTRIBUT AGENT DREFLA
    private String matricule;
    private String nom;
    private int age;
    private String poste;
    private double salaire;
    private boolean enService;

    // CONSTRUCTEUR AGENT
    public Agent(String matricule, String nom, int age, String poste, double salaire) {
        this.matricule = matricule;
        this.nom = nom;
        this.age = age;
        this.poste = poste;
        this.salaire = salaire;
        this.enService = false;
    }

    // METHODES DE L'AGENT
    public void PosteOccuper() {
        System.out.println(nom + " occupe le poste de : " + poste);
    }

    public void CommencerService() {
        this.enService = true;
        System.out.println(nom + " a commencé son service.");
    }

    public void TerminerService() {
        this.enService = false;
        System.out.println(nom + " a terminé son service.");
    }

    public void AugmenterSalaire(double montant) {
        this.salaire += montant;
        System.out.println(nom + " a reçu une augmentation de " + montant + ". Nouveau salaire: " + salaire);
    }

    // AFFICHAGE DES INFOS
    public void afficherInfos() {
        System.out.println("Matricule : " + matricule);
        System.out.println("NOM : " + nom);
        System.out.println("Age : " + age);
        System.out.println("Poste : " + poste);
        System.out.println("Salaire : " + salaire);
        System.out.println("En Service : " + enService);
    }

    // MES ACCESSEURS/GETTERS

    public String getMatricule() {
        return matricule;
    }

    public String getNom() {
        return nom;
    }

    public int getAge() {
        return age;
    }

    public String getPoste() {
        return poste;
    }

    public double getSalaire() {
        return salaire;
    }

    public boolean getEtatService() {
        return enService;
    }

    // MES MUTATEURS/SETTERS
    public void setMatricule(String matricule) {
        this.matricule = matricule;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setPoste(String poste) {
        this.poste = poste;
    }

    public void setSalaire(double salaire) {
        this.salaire = salaire;
    }

    public void setEtatService(boolean enService) {
        this.enService = enService;
    }

}

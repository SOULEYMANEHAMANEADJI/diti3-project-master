# Projet Java avec JavaFX, MySQL et contrôles de saisie

Ce projet Java est conçu pour mettre en œuvre les concepts de JavaFX,
MySQL et les contrôles de saisie pour une application de gestion de livres.

## Prérequis

- Java 19
- JavaFX (inclus avec Java 19)
- MySQL
- Maven

## Configuration de la base de données

1. Créez une base de données MySQL appelée "diti3_project_db".
2. Exécutez le script SQL suivant pour créer la table "livre" :

--- sql
CREATE TABLE livre (
id INT PRIMARY KEY AUTO_INCREMENT,
titre VARCHAR(255) NOT NULL,
auteur VARCHAR(255) NOT NULL,
isbn VARCHAR(20) NOT NULL,
etat_emprunt INT DEFAULT 0,
nb_pages INT NOT NULL
);
Exécution de l'application
Ouvrez une console dans le dossier du projet.
Compilez et exécutez l'application en utilisant la commande suivante :
mvn javafx:run
Fonctionnalités
Ajout d'un livre avec contrôles de saisie pour les champs obligatoires.
Affichage de la liste des livres dans une table.
Sélection d'un livre pour afficher ses détails et permettre la modification ou la suppression.
Gestion de l'emprunt et du retour de livres avec mise à jour automatique de l'état.
Recherche de livres par titre, auteur, ISBN ou nombre de pages.

Prochaines étapes
Intégration de la persistance de données avec MySQL pour stocker les livres.
Mise en place d'une interface utilisateur plus conviviale avec JavaFX.
Ajout de la validation des données côté back-end pour garantir l'intégrité des données.
Finalisation de l'intégration de RMI pour la communication distante entre les composants.





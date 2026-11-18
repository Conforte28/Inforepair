# INFOREPAIR — Système de suivi de réparation de matériel informatique

Application web de gestion et de suivi des réparations de matériel informatique (suivi des dossiers, devis en FCFA, réparations et interventions, plaintes, paiements et administration), conçue avec Java 17, Spring Boot 3, Thymeleaf, Spring Security, Spring Data JPA et PostgreSQL.

## 1. Logiciels à installer en local

- **JDK 17** (Eclipse Temurin / OpenJDK 17)
- **Apache Maven 3.9+** (ou le Maven intégré à NetBeans)
- **Apache NetBeans** (version 17 ou supérieure)
- **PostgreSQL** (version 14 ou supérieure) et pgAdmin ou `psql`
- **Git**

## 2. Ouvrir le projet Maven dans NetBeans

1. Ouvrir Apache NetBeans.
2. Cliquer sur **File > Open Project...**
3. Sélectionner le dossier `inforepair` contenant le fichier `pom.xml`.
4. Cliquer sur **Open Project**. NetBeans télécharge automatiquement les dépendances Maven si votre connexion Internet est active.

## 3. Créer et configurer PostgreSQL en local

Connectez-vous à PostgreSQL (`psql` ou pgAdmin) et exécutez :

```sql
CREATE USER inforepair WITH PASSWORD 'votre_mot_de_passe_local';
CREATE DATABASE inforepair OWNER inforepair;
GRANT ALL PRIVILEGES ON DATABASE inforepair TO inforepair;
```

## 4. Définir les variables d'environnement

L'application lit ses paramètres depuis les variables d'environnement suivantes :

- `PORT` : port d'écoute HTTP (`8080` par défaut en local, fourni automatiquement par Render en production)
- `DB_URL` : URL JDBC PostgreSQL au format `jdbc:postgresql://HOTE:PORT/NOM_DE_LA_BASE` (ex. : `jdbc:postgresql://localhost:5432/inforepair`)
- `DB_USERNAME` : nom d'utilisateur PostgreSQL
- `DB_PASSWORD` : mot de passe PostgreSQL
- `DEMO_MODE` : `true` pour initialiser les comptes et données de démonstration lors d'une première installation sur base vide, sinon `false`
- `DEMO_PASSWORD` : mot de passe initial attribué aux comptes de démonstration lorsque `DEMO_MODE=true`

### Exemple sous Linux / macOS

```bash
export PORT=8080
export DB_URL=jdbc:postgresql://localhost:5432/inforepair
export DB_USERNAME=inforepair
export DB_PASSWORD=votre_mot_de_passe_local
export DEMO_MODE=true
export DEMO_PASSWORD=demo1234
```

### Exemple sous Windows (Invite de commandes `cmd`)

```bat
set PORT=8080
set DB_URL=jdbc:postgresql://localhost:5432/inforepair
set DB_USERNAME=inforepair
set DB_PASSWORD=votre_mot_de_passe_local
set DEMO_MODE=true
set DEMO_PASSWORD=demo1234
```

Dans NetBeans : clic droit sur le projet > **Properties** > **Actions** > **Run project**, puis renseignez ces variables dans **Set Properties / Env**.

## 5. Lancer le projet en local

Depuis le dossier `inforepair` :

```bash
mvn spring-boot:run
```

Ou depuis NetBeans avec le bouton **Run Project**.
Ouvrez ensuite votre navigateur à l'adresse : `http://localhost:8080`.

## Sécurité et comptes de démonstration

Lorsque `DEMO_MODE=true` et `DEMO_PASSWORD=demo1234` sont définis et que la table des utilisateurs est vide, l'application crée une seule fois :

- Administrateur : `admin@inforepair.bj` / `demo1234`
- Agent : `agent@inforepair.bj` / `demo1234`
- Technicien : `technicien@inforepair.bj` / `demo1234`

**Attention importante :**

- Ces comptes et données sont strictement réservés à une démonstration.
- Après la démonstration, vous devez **impérativement changer les mots de passe** de ces comptes ou les **désactiver** depuis la page **Utilisateurs**.
- Passer `DEMO_MODE` à `false` empêche uniquement une nouvelle création initiale ; **cela ne supprime pas les comptes ni les données déjà enregistrés dans PostgreSQL**.

## 6. Créer un dépôt GitHub et envoyer le projet

Important : les fichiers `pom.xml`, `Dockerfile` et `render.yaml` doivent se trouver **à la racine** de votre dépôt GitHub.

1. Créez un nouveau dépôt vide sur GitHub (sans l'initialiser avec un README).
2. Placez-vous **à l'intérieur** du dossier `inforepair` et exécutez :

```bash
git init
git add .
git commit -m "Initialisation du projet INFOREPAIR"
git branch -M main
git remote add origin https://github.com/VOTRE_COMPTE/inforepair.git
git push -u origin main
```

Vérifiez que `.env`, `target/` et vos fichiers personnels ne sont pas envoyés (le fichier `.gitignore` les exclut).

## 7. Déployer directement sur Render depuis GitHub (Option A : Blueprint `render.yaml`)

Le fichier `render.yaml` inclus à la racine du projet permet à Render de créer automatiquement la base PostgreSQL et le Web Service Docker reliés ensemble :

1. Connectez-vous sur Render puis cliquez sur **New + > Blueprint**.
2. Sélectionnez votre dépôt GitHub `inforepair`.
3. Render détecte `render.yaml` et affiche les ressources à créer (`inforepair-db` et `inforepair`). *(Vous pouvez adapter le champ `plan` dans `render.yaml` selon l'offre choisie.)*
4. Lorsque Render demande la variable `DEMO_PASSWORD`, saisissez par exemple `demo1234` (uniquement pour la démonstration initiale).
5. Cliquez sur **Apply**. Render crée la base PostgreSQL, injecte automatiquement `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USERNAME` et `DB_PASSWORD`, puis lance la compilation Docker.

## 8. Déployer manuellement sur Render (Option B : PostgreSQL + Web Service Docker)

Si vous préférez configurer chaque service séparément :

1. Sur Render, cliquez sur **New + > PostgreSQL** (ou utilisez une base PostgreSQL externe). Relevez **Host**, **Port**, **Database**, **Username** et **Password**.
2. Construisez l'URL JDBC au format obligatoire :
   `jdbc:postgresql://HOTE:PORT/NOM_DE_LA_BASE`
   *(Ne collez pas directement une URL commençant par `postgres://` dans `DB_URL`.)*
3. Cliquez ensuite sur **New + > Web Service** et connectez votre dépôt GitHub.
4. Choisissez **Language / Runtime : Docker** (si `inforepair` est un sous-dossier du dépôt, indiquez `inforepair` dans **Root Directory**).

## 9. Renseigner les variables d'environnement sur Render (Option B)

Dans l'onglet **Environment** du Web Service Render, ajoutez :

- `DB_URL` : `jdbc:postgresql://HOTE:5432/NOM_DE_LA_BASE`
- `DB_USERNAME` : utilisateur fourni par la base PostgreSQL
- `DB_PASSWORD` : mot de passe fourni par la base PostgreSQL
- `DEMO_MODE` : `true` (uniquement pour la démonstration initiale)
- `DEMO_PASSWORD` : mot de passe initial choisi pour la démonstration

Render injecte automatiquement la variable `PORT`, utilisée par `application.properties` et le `Dockerfile`.

## 10. Obtenir l'URL réelle de l'application

Une fois le déploiement Docker terminé avec le statut **Live**, l'URL publique réelle apparaît en haut de la page du service sur Render (sous la forme `https://nom-du-service.onrender.com`).

## 11. Vérifier la persistance des données après redémarrage

1. Connectez-vous à l'application déployée et créez un nouveau client ainsi qu'un nouveau dossier matériel.
2. Sur Render, cliquez sur **Manual Deploy > Restart service** (ou redémarrez le service).
3. Reconnectez-vous une fois le service redémarré : le client et le matériel créés doivent toujours être présents grâce au stockage PostgreSQL externe au conteneur.

## 12. Limites éventuelles du forfait Render choisi

Selon le forfait sélectionné sur Render (en particulier les offres d'essai ou gratuites, qui ne sont ni garanties permanentes ni sans contrainte) :

- **Mise en veille** : une instance peut se mettre en veille après une période d'inactivité ; le premier chargement suivant prend alors quelques dizaines de secondes.
- **Disponibilité et ressources** : la mémoire vive et le temps CPU sont limités selon l'offre.
- **Conservation de la base PostgreSQL** : certaines offres de base de données d'essai expirent après une durée limitée (par exemple 30 jours) si elles ne sont pas passées sur une offre payante ; vérifiez les conditions exactes de votre forfait dans le tableau de bord Render et sauvegardez régulièrement vos données.

## Tests manuels recommandés

1. Connexion avec chacun des trois comptes (`ADMIN`, `AGENT`, `TECHNICIEN`) et déconnexion.
2. Vérification des restrictions d'accès par rôle (un `AGENT` et un `TECHNICIEN` ne peuvent pas ouvrir `/utilisateurs`).
3. Ajout, recherche et modification d'un client ; vérification de l'impossibilité de supprimer un client ayant des opérations liées.
4. Enregistrement d'un matériel avec génération du numéro de dossier et recherche par numéro de dossier / filtrage par type et état.
5. Création d'un devis pour un matériel, vérification du calcul automatique du total en FCFA, acceptation et refus d'un devis, impression propre.
6. Démarrage d'une réparation depuis un devis accepté, affectation d'un technicien, enregistrement d'interventions techniques, mise à jour des statuts et clôture.
7. Enregistrement d'une plainte, affectation, traitement, passage au statut clôturée et ajout d'une nouvelle action de suivi.
8. Enregistrement de plusieurs paiements partiels pour une réparation, vérification du calcul du reste à payer et blocage si le montant dépasse le solde dû.
9. Mise à jour des indicateurs du tableau de bord après ces opérations.
10. Vérification de la persistance des données après redémarrage du serveur.

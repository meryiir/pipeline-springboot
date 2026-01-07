# Pipeline CI/CD avec Jenkins, SonarQube et Docker

Ce projet contient 4 microservices Spring Boot avec un pipeline CI/CD complet utilisant Jenkins, SonarQube et Docker Compose.

## Architecture

- **server_eureka** : Serveur de découverte Eureka (port 8761)
- **gateway** : API Gateway Spring Cloud (port 8080)
- **car** : Microservice Car (port 8081)
- **client** : Microservice Client (port 8082)

## Prérequis

- Java 17
- Maven 3.6+
- Docker et Docker Compose
- Jenkins (avec plugins : Pipeline, GitHub, SonarQube Scanner)
- Ngrok (pour exposer Jenkins)
- Compte GitHub

## Configuration

### 1. Configuration Jenkins

#### Installation des outils dans Jenkins

1. **JDK** :
   - Aller dans `Manage Jenkins` > `Tools`
   - Ajouter JDK 17 (chemin ou installation automatique)

2. **Maven** :
   - Dans `Manage Jenkins` > `Tools`
   - Ajouter Maven 3.8+ (chemin ou installation automatique)

3. **SonarScanner** :
   - Installer le plugin "SonarQube Scanner"
   - Dans `Manage Jenkins` > `Tools`
   - Ajouter SonarQube Scanner
   - Dans `Manage Jenkins` > `Configure System`
   - Ajouter un serveur SonarQube (nom: `SonarQube`, URL: `http://sonarqube:9000`)

#### Configuration des credentials

1. **SonarQube Token** :
   - Créer un token dans SonarQube (voir section SonarQube)
   - Dans Jenkins : `Manage Jenkins` > `Credentials`
   - Ajouter une credential de type "Secret text"
   - ID: `sonar-token`
   - Secret: votre token SonarQube

2. **GitHub** (optionnel pour webhooks) :
   - Ajouter credentials GitHub si nécessaire

### 2. Déploiement SonarQube

#### Démarrer SonarQube avec Docker Compose

```bash
docker-compose up -d sonarqube
```

Attendre que SonarQube soit prêt (environ 1-2 minutes) :
```bash
docker-compose logs -f sonarqube
```

Accéder à SonarQube : http://localhost:9000
- Login par défaut : `admin` / `admin`
- Changer le mot de passe au premier login

#### Créer les projets et tokens dans SonarQube

1. **Projet Car Service** :
   - Aller dans `Projects` > `Create Project`
   - Project Key: `car-service`
   - Display Name: `Car Service`
   - Créer un token : `My Account` > `Security` > `Generate Token`
   - Nom: `car-service-token`
   - Copier le token

2. **Projet Client Service** :
   - Répéter pour `client-service`
   - Créer un token : `client-service-token`

3. **Token global pour Jenkins** :
   - Créer un token avec permissions globales pour Jenkins
   - Utiliser ce token dans les credentials Jenkins (`sonar-token`)

### 3. Exposer Jenkins avec Ngrok

1. Installer Ngrok : https://ngrok.com/download

2. Démarrer Jenkins (si pas déjà fait)

3. Exposer Jenkins :
```bash
ngrok http 8080
```

4. Copier l'URL HTTPS fournie par Ngrok (ex: `https://abc123.ngrok.io`)

### 4. Configuration GitHub Webhook

1. Aller dans votre dépôt GitHub
2. `Settings` > `Webhooks` > `Add webhook`
3. Configuration :
   - Payload URL: `https://votre-url-ngrok.ngrok.io/github-webhook/`
   - Content type: `application/json`
   - Events: `Just the push event`
   - Active: ✓

### 5. Créer le Job Pipeline Jenkins

1. Dans Jenkins : `New Item`
2. Nom: `microservices-pipeline`
3. Type: `Pipeline`
4. Configuration :
   - **Pipeline Definition** : `Pipeline script from SCM`
   - **SCM** : `Git`
   - **Repository URL** : URL de votre dépôt GitHub
   - **Credentials** : Ajouter si nécessaire
   - **Branches** : `*/main` ou `*/master`
   - **Script Path** : `Jenkinsfile`

5. Sauvegarder

### 6. Exécution manuelle

Pour tester le pipeline manuellement :
1. Aller dans le job `microservices-pipeline`
2. Cliquer sur `Build Now`

### 7. Test automatique via Webhook

1. Faire un commit et push sur GitHub :
```bash
git add .
git commit -m "Test pipeline CI/CD"
git push origin main
```

2. Le webhook déclenchera automatiquement le pipeline Jenkins

## Structure du Pipeline

Le pipeline Jenkins (`Jenkinsfile`) exécute les étapes suivantes :

1. **Checkout** : Clone le dépôt GitHub
2. **Build** : Compilation Maven de tous les microservices
3. **SonarQube Analysis - Car** : Analyse de qualité du code pour le service Car
4. **SonarQube Analysis - Client** : Analyse de qualité du code pour le service Client
5. **Build Docker Images** : Construction des images Docker
6. **Deploy** : Déploiement avec Docker Compose
7. **Health Check** : Vérification que les services sont opérationnels

## Commandes utiles

### Démarrer tous les services
```bash
docker-compose up -d
```

### Voir les logs
```bash
docker-compose logs -f
```

### Arrêter tous les services
```bash
docker-compose down
```

### Rebuild et redémarrer
```bash
docker-compose up -d --build
```

### Accéder aux services

- Eureka Dashboard : http://localhost:8761
- Gateway : http://localhost:8080
- Car Service : http://localhost:8081/api/cars
- Client Service : http://localhost:8082/api/clients
- SonarQube : http://localhost:9000

## Dépannage

### Jenkins ne peut pas accéder à SonarQube
- Vérifier que SonarQube est démarré : `docker-compose ps`
- Vérifier l'URL dans la configuration Jenkins
- Si Jenkins est dans Docker, utiliser `http://sonarqube:9000` au lieu de `http://localhost:9000`

### Les services ne démarrent pas
- Vérifier les logs : `docker-compose logs [service-name]`
- Vérifier que les JAR sont bien générés dans `target/`
- Vérifier que les ports ne sont pas déjà utilisés

### Webhook ne fonctionne pas
- Vérifier que Ngrok est toujours actif
- Vérifier l'URL du webhook dans GitHub
- Vérifier les logs Jenkins pour les erreurs

## Notes importantes

- Le pipeline utilise les credentials Jenkins pour SonarQube
- Les tokens SonarQube doivent être configurés dans Jenkins
- Ngrok génère une nouvelle URL à chaque redémarrage (mettre à jour le webhook GitHub)
- Pour un environnement de production, utiliser un tunnel stable ou un reverse proxy

## Auteurs

Projet pédagogique pour l'apprentissage du CI/CD avec Jenkins, SonarQube et Docker.
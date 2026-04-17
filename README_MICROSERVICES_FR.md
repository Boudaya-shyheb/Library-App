# Architecture Microservices - Library

Ce document explique clairement le fonctionnement de votre architecture microservices avec:

- Eureka Server (Service Discovery)
- API Gateway (point d'entree unique)
- Config Server (configuration centralisee)
- Communication inter-services (HTTP/OpenFeign)
- MySQL, RabbitMQ, Keycloak

## 1. Vue d'ensemble

Dans ce projet, le client (frontend ou outils API) n'appelle pas directement chaque microservice.
Toutes les requetes passent d'abord par l'API Gateway, qui redirige ensuite vers le bon service.

Flux global:

1. Le service demarre et charge sa configuration depuis Config Server.
2. Le service s'enregistre dans Eureka.
3. Le client appelle API Gateway.
4. API Gateway cherche le service cible dans Eureka.
5. La requete est routée vers l'instance du microservice.
6. Le service peut appeler un autre service via son nom Eureka (OpenFeign/HTTP).

## 2. Roles des composants

### API Gateway

- Port expose: `8080`
- Role:
  - point d'entree unique des APIs
  - routage vers les microservices
  - centralisation de la securite (JWT Keycloak cote gateway)
  - logs et observabilite du trafic

Exemple:

- Requete client: `GET /api/spaces`
- Gateway route vers `space-service`

### Eureka Server

- Port: `8761`
- Role:
  - registre des services (service registry)
  - discovery dynamique (evite les URLs en dur)
  - suivi de l'etat des instances (UP/DOWN)

Dashboard:

- URL: `http://localhost:8761`

### Config Server

- Port: `8888`
- Role:
  - centraliser les proprietes de configuration
  - fournir la configuration aux services au demarrage
  - eviter de dupliquer `application.properties` partout

Source des configs dans votre projet:

- `config-server/config-repo`

### Keycloak

- Port hote: `8180`
- Role:
  - authentification/autorisation OAuth2/JWT
  - emission des tokens utilises par les requetes API

### MySQL

- Port expose: `3306`
- Conteneur: `library-mysql`
- Credentials par defaut (compose):
  - user: `root`
  - password: `root`

Chaque microservice peut utiliser sa propre base/schema (ex: `library_spaces`, `library_reservations`, etc.).

### RabbitMQ

- Ports: `5672` (AMQP), `15672` (UI)
- Role:
  - communication asynchrone (events/messages) entre services si necessaire

## 3. Communication entre microservices

### A. Communication synchrone (HTTP)

Un service peut appeler un autre service:

- soit via API Gateway (externe)
- soit directement service-to-service (interne)

En pratique Spring Cloud:

- `space-service` peut appeler `reservation-service` via OpenFeign
- Le nom logique `reservation-service` est resolu par Eureka

Avantage:

- pas d'IP/port en dur
- support naturel du load balancing

### B. Communication asynchrone (messagerie)

Avec RabbitMQ, un service publie un message sans bloquer la requete.
Un autre service consomme ce message plus tard.

Utile pour:

- notifications
- traitements differes
- decouplage fort entre services

## 4. Sequence de demarrage (Docker Compose)

Ordre logique dans votre stack:

1. `eureka-server`
2. `config-server` (depend de eureka)
3. `api-gateway` (depend de config-server)
4. microservices metier (space, reservation, fournisseur, emprunt, reclamation, etc.)
5. `frontend`

Etapes:

1. `docker compose up -d --build`
2. Verifier les conteneurs: `docker compose ps`
3. Verifier Eureka: `http://localhost:8761`
4. Verifier Config Server: `http://localhost:8888/actuator/health`
5. Verifier Gateway: `http://localhost:8080/actuator/health`

## 5. Ports importants (selon votre docker-compose)

- API Gateway: `8080`
- Eureka: `8761`
- Config Server: `8888`
- Keycloak: `8180`
- Frontend: `4200`
- MySQL: `3306`
- RabbitMQ: `5672`
- RabbitMQ UI: `15672`

## 6. Exemple de flux complet

Cas: recuperer des reservations d'un espace.

1. Le frontend appelle `GET /api/spaces/{id}/reservations` sur Gateway.
2. Gateway route vers `space-service` (via Eureka).
3. `space-service` appelle `reservation-service` (OpenFeign + Eureka).
4. `reservation-service` lit la base MySQL.
5. Reponse retour: reservation-service -> space-service -> gateway -> frontend.

## 7. Pourquoi cette architecture est utile

- Scalabilite: chaque service peut etre scale independamment.
- Resilience: un service en panne n'arrete pas tout le systeme.
- Maintenabilite: code metier separe par domaine.
- Evolutivite: ajout de nouveaux services plus simple.
- Gouvernance: securite et routage centralises via Gateway.

## 8. Commandes utiles

Demarrer:

```bash
docker compose up -d --build
```

Voir les logs d'un service:

```bash
docker compose logs -f api-gateway
docker compose logs -f eureka-server
docker compose logs -f config-server
```

Lister les services actifs:

```bash
docker compose ps
```

Arreter:

```bash
docker compose down
```

## 9. Points d'attention

- Verifier que chaque service a bien:
  - `SPRING_CONFIG_IMPORT=configserver:http://config-server:8888`
  - `EUREKA_CLIENT_SERVICEURL_DEFAULTZONE=http://...@eureka-server:8761/eureka/`
- Eviter les URLs locales en dur entre services.
- Surveiller les `healthchecks` pour les dependances critiques (MySQL, Config Server, Gateway).

---

Si vous voulez, je peux aussi vous generer un schema Mermaid dans un second fichier pour visualiser les flux (client -> gateway -> services -> DB) directement dans VS Code.
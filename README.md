# osa-back
API REST Projet Open Street Art


Installation des dépendances
```
mvn clean install
```

Lancement du projet
```
mvn spring-boot:run
```

Lancement des tests unitaires
```
mvn test
```

Lancement du projet en arrière plan
```
mvn spring-boot:start
```

Lancement du projet en arrière plan en mode production
```
mvn spring-boot:start -Pprod
```

Accès à la documentation Swagger
```
http://localhost:8080/swagger-ui.html
```

Compte administrateur
```
username: root
password: OsaAdmin1234
```

Lancement d'une nouvelle migration de schéma Flyway
```
mvn flyway:migrate -Dflyway.url=... -Dflyway.user=... -Dflyway.password=...
```

Nettoyage de la base de données
```
mvn flyway:clean -Dflyway.url=... -Dflyway.user=... -Dflyway.password=...
```

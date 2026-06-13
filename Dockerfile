FROM mysql:8.0

ENV MYSQL_ROOT_PASSWORD=root
ENV MYSQL_DATABASE=expediciones_db
ENV MYSQL_USER=usuario_app
ENV MYSQL_PASSWORD=pass123

COPY init.sql /docker-entrypoint-initdb.d/

EXPOSE 3306

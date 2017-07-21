FROM mysql:latest

ENV MYSQL_ROOT_PASSWORD mysqladmin
COPY config/* /etc/mysql/conf.d/
COPY data/* /docker-entrypoint-initdb.d/
FROM mysql:latest

EXPOSE 3306

ENV MYSQL_PASSWORD='chapter-backend'
ENV MYSQL_USER=chapter.user
ENV MYSQL_DATABASE=chapter_db
ENV MYSQL_ROOT_PASSWORD=root
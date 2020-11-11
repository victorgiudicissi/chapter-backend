FROM mysql:latest

RUN make /app
CMD python /app/app.py
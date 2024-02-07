#FROM nginx:alpine
FROM proxy-docker-io.insee.fr/nginx:latest

WORKDIR /etc/nginx
COPY ./nginx.conf ./conf.d/default.conf
EXPOSE 80
ENTRYPOINT [ "nginx" ]
CMD [ "-g", "daemon off;" ]
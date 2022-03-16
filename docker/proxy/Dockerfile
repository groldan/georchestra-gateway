FROM debian:buster

RUN apt update && apt install -y polipo
EXPOSE 8123
RUN echo "proxyAddress=0.0.0.0" >> /etc/polipo/options
RUN echo "allowedPorts=80-65535" >> /etc/polipo/options
RUN echo "tunnelAllowedPorts=80-65535" >> /etc/polipo/options
CMD ["polipo", "-c", "/etc/polipo/options"]

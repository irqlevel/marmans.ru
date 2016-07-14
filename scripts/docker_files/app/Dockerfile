FROM fedora

MAINTAINER Maintaner Name

RUN dnf -y install wget unzip git
RUN dnf -y install openssl
RUN mkdir /distr
RUN cd /distr && wget https://s3.eu-central-1.amazonaws.com/ru.marmans.distr/typesafe-activator-1.3.10-minimal.zip
RUN cd /distr && wget https://s3.eu-central-1.amazonaws.com/ru.marmans.distr/jdk-8u91-linux-x64.rpm
RUN cd /distr && dnf -y install jdk-8u91-linux-x64.rpm
RUN cd /distr && unzip typesafe-activator-1.3.10-minimal.zip
RUN cd /distr && mv activator-1.3.10-minimal activator
RUN mkdir /build
RUN git clone https://github.com/irqlevel/marmans.ru.git /build
RUN cd build && /distr/activator/bin/activator clean stage
COPY run_app.sh /distr/run_app.sh

EXPOSE 8080

ENTRYPOINT /distr/run_app.sh

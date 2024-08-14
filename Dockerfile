FROM registry.access.redhat.com/ubi9/ubi-minimal:9.4-1194

RUN microdnf install -y shadow-utils && microdnf clean all
RUN groupadd -g 1000 tmpusrgp
RUN useradd -u 1000 -g tmpusrgp -d /home/tmpusr -m tmpusr
USER tmpusr
RUN mkdir -p /home/tmpusr/app
WORKDIR /home/tmpusr/app

COPY --chown=tmpusr:tmpusrgp *-runner ./app

EXPOSE 8080
CMD [ "./app", "-Dquarkus.http.host=0.0.0.0" ]

HEALTHCHECK --interval=30s --timeout=30s --retries=3 \
    CMD curl -f http://localhost:8080/q/health || exit 1
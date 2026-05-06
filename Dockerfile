FROM python:3.14-slim

RUN apt-get update -qq && \
    apt-get install -y -qq --no-install-recommends \
        net-tools \
        iputils-ping \
        traceroute \
        netcat-openbsd && \
    rm -rf /var/lib/apt/lists/*

WORKDIR /app
COPY . .
RUN pip install -e . -q

CMD ["bash"]

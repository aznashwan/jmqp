#!/usr/bin/env sh

./client --ip 127.0.0.1 --port 8989 --app-path bajetii/jmqp \
    --send --topic --name sometopic --timeout 10 --message "SALUT BAJETII!!!"

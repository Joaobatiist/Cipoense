#!/bin/sh
# wait-for-db.sh

set -e

host="$1"
shift
cmd="$@"

# Loop até que o host e a porta estejam aceitando conexões TCP
until nc -z "$host" 3306; do
  >&2 echo "MySQL is unavailable - sleeping"
  sleep 1
done

>&2 echo "MySQL is up - executing command"
exec $cmd
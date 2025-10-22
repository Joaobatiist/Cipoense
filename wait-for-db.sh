#!/bin/sh
# wait-for-db.sh

# Não precisamos mais do sleep inicial, pois o depends_on e o healthcheck já cuidaram do tempo
set -e

# Extrai as informações de ambiente diretamente da sua configuração do Spring
DB_HOST="db"
DB_USER="root"
DB_PASS="@Joaosurf1"

# Ignora o $1 e o $2, e pega o comando principal
shift 1
cmd="$@"

>&2 echo "Waiting for MySQL at $DB_HOST:3306 to be truly ready..."

# Loop: tenta logar no banco de dados até que seja bem-sucedido
until mysql -h "$DB_HOST" -u "$DB_USER" -p"$DB_PASS" -e 'select 1' > /dev/null 2>&1; do
  >&2 echo "MySQL is not accepting connections yet (or healthcheck is unreliable) - sleeping"
  sleep 1
done

>&2 echo "MySQL is fully up and accepting connections - executing command: $cmd"
exec $cmd
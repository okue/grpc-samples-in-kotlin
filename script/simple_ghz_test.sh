
cd "$(dirname "$0")" || exit

# https://ghz.sh/docs/options
ghz --config ./ghz_config.json \
  --format html \
  --output "result_$(date "+%Y%m%d%H%M").html"

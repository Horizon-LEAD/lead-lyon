set -e

for seed in 1000; do
  for scaling in 1.0 1.3 1.6 2.0; do
    papermill -p seed ${seed} -p suffix _seed${seed}_scaling${scaling} -p scaling ${scaling} "Generate Parcels.ipynb" \
      "output/Generate Parcels Seed ${seed}.ipynb"

    papermill -p seed ${seed} -p suffix _seed${seed}_scaling${scaling} "Generate VRP.ipynb" \
      "output/Generate VRP Seed ${seed}.ipynb"
  done
done

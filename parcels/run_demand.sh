set -e

for seed in 1000 2000 3000 4000 5000 6000 7000 8000 9000 10000; do
  for scaling in 1.0 1.3 1.6 2.0; do
    papermill -p seed ${seed} -p suffix _seed${seed}_scaling${scaling} -p scaling ${scaling} "Generate Parcels.ipynb" \
      "output/Generate Parcels Seed ${seed}.ipynb"

    papermill -p seed ${seed} -p suffix _seed${seed}_scaling${scaling} "Generate VRP.ipynb" \
      "output/Generate VRP Seed ${seed}.ipynb"

    java -cp lead-java/target/lead-0.0.1-SNAPSHOT.jar lead.RunOptimization \
      --deliveries-path output/vrp_deliveries_seed${seed}_scaling${scaling}.csv --costs-path output/vrp_distances.csv --depot-node 6068627214 --output-path output/vrp_solution_seed${seed}_scaling${scaling}.csv --vehicle-capacity 4

    java -cp lead-java/target/lead-0.0.1-SNAPSHOT.jar lead.RunMovements \
      --network-path input/lyon_network.xml.gz --nodes-path output/vrp_nodes.csv --activities-path output/vrp_solution_seed${seed}_scaling${scaling}.csv --output-path output/vrp_movements_seed${seed}_scaling${scaling}
  done
done

set -e

for seed in 1000 2000 3000 4000 5000 6000 7000 8000 9000 10000; do
  papermill -p seed ${seed} -p suffix _seed${seed} "Generate Parcels.ipynb" \
    "output/Generate Parcels Seed ${seed}.ipynb"

  papermill -p seed ${seed} -p suffix _seed${seed} "Generate VRP.ipynb" \
    "output/Generate VRP Seed ${seed}.ipynb"

  for capacity in 4 5 6; do
    java -cp lead-java/target/lead-0.0.1-SNAPSHOT.jar lead.RunOptimization \
      --deliveries-path output/vrp_deliveries_seed${seed}.csv --costs-path output/vrp_distances.csv --depot-node 6068627214 --output-path output/vrp_solution_seed${seed}_cap${capacity}.csv --vehicle-capacity ${capacity}

    java -cp lead-java/target/lead-0.0.1-SNAPSHOT.jar lead.RunMovements \
      --network-path input/lyon_network.xml.gz --nodes-path output/vrp_nodes.csv --activities-path output/vrp_solution_seed${seed}_cap${capacity}.csv --output-path output/vrp_movements_seed${seed}_cap${capacity}
  done

  for service in ups tnt_fedex poste dpd dhl; do
    if [[ "${service}" == "ups" || "${service}" == "dhl" ]]; then
      entry="26340341"
    elif [[ "${service}" == "dpd" || "${service}" == "tnt_fedex" ]]; then
      entry="25632869"
    elif [[ "${service}" == "poste" ]]; then
      entry="3246517575"
    else
      echo "Error, unknown service"
      exit
    fi

    java -cp lead-java/target/lead-0.0.1-SNAPSHOT.jar lead.RunOptimization \
      --deliveries-path output/deliveries_for_${service}_seed${seed}.csv --costs-path output/vrp_distances.csv --depot-node ${entry} --output-path output/vrp_solution_for_${service}_seed${seed}.csv --vehicle-speed 20 --vehicle-capacity 20

    java -cp lead-java/target/lead-0.0.1-SNAPSHOT.jar lead.RunMovements \
      --network-path input/lyon_network.xml.gz --nodes-path output/vrp_nodes.csv --activities-path output/vrp_solution_for_${service}_seed${seed}.csv --output-path output/vrp_movements_for_${service}_seed${seed}
  done
done

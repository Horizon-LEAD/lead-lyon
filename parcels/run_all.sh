## Check for input files

INPUT_FILES="
  input/activities.csv
  input/homes.gpkg
  input/persons.csv
  input/confluence_areas.gpkg
  input/rhone-alpes-latest.osm.pbf
  input/lyon_network.xml.gz
"

for f in $INPUT_FILES; do
  if [ ! -f "$f" ]; then
    echo "Input file missing: $f"
    exit 1
  fi
done

## Check osmosis
if [ ! -x "$(which osmosis)" ]; then
  echo "Osmosis is not executable"
  exit 1
fi

## Check Maven
if [ ! -x "$(which mvn)" ]; then
  echo "Maven does not exist (mvn is not executable)"
  exit 1
fi

## Initialize output directory
mkdir -p output

## Generate poly file from GPKG for cutting the OSM data
jupyter nbconvert "Make Polygon.ipynb" --execute --output-dir output --to html

## Extract OSM data for the area
osmosis --read-pbf input/rhone-alpes-latest.osm.pbf --tf accept-ways highway=* --bounding-polygon file=output/confluence_areas.poly --used-node --write-pbf output/confluence.osm.pbf

## Generate parcels based on synthetic travel demand and ADM survey
jupyter nbconvert "Generate Parcels.ipynb" --execute --ExecutePreprocessor.timeout=-1 --output-dir output --to html

## Generate the VRP network
jupyter nbconvert "Generate VRP Network.ipynb" --execute --ExecutePreprocessor.timeout=-1 --output-dir output --to html

## Generate service data
jupyter nbconvert "Generate Service Data.ipynb" --execute --ExecutePreprocessor.timeout=-1 --output-dir output --to html

## Generate the VRP problem to solve
jupyter nbconvert "Generate VRP.ipynb" --execute --ExecutePreprocessor.timeout=-1 --output-dir output --to html

## Build the Java part
sh -c "cd lead-java && mvn clean package"

## Solve the VRP problem using JSprit
java -cp lead-java/target/lead-0.0.1-SNAPSHOT.jar lead.RunOptimization \
  --deliveries-path output/vrp_deliveries.csv --costs-path output/vrp_distances.csv --depot-node 6068627214 --output-path output/vrp_solution.csv

## Simulate vehicles in MATSim to create visualisations
java -cp lead-java/target/lead-0.0.1-SNAPSHOT.jar lead.RunMovements \
  --network-path input/lyon_network.xml.gz --nodes-path output/vrp_nodes.csv --activities-path output/vrp_solution.csv --output-path output/vrp_movements

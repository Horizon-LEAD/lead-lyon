set -e

# Algorithm options
depot_node=6068627214
vehicle_speed=5
vehicle_capacity=5
vehicle_type="robo"

# Enter the environment
source environment/miniconda/etc/profile.d/conda.sh
conda activate lead_lyon
PATH=$(realpath environment/java/bin):$(realpath environment/osmosis/bin):$PATH
JAVA_HOME=$(realpath environment/java)

#rm -rf temp
#mkdir temp

# Create a polygon file from a shapefile / GeoPackage
#papermill \
#  -p output_path temp \
#  "Make Polygon.ipynb" "output/Temporary.ipynb"
#rm output/Temporary.ipynb

# Extract OSM data for the area
#osmosis --read-pbf input/region.osm.pbf \
#  --tf accept-ways highway=* --bounding-polygon file=temp/area.poly \
#  --used-node --write-pbf temp/filtered_area.osm.pbf

# Generate a VRP network
#papermill \
#  -p input_path temp \
#  -p output_path temp \
#  "Generate VRP Network.ipynb" "output/Temporary.ipynb"
#rm output/Temporary.ipynb

# Generate the VRP problem to solve
#ln -s ../input/slots.csv temp/slots.csv
#ln -s ../input/homes.gpkg temp/homes.gpkg
#ln -s ../input/area.gpkg temp/area.gpkg

#papermill \
#  -p input_path temp \
#  -p output_path temp \
#  "Generate VRP Instance.ipynb" "output/Temporary.ipynb"

# Solve the VRP problem using JSprit
java -cp lead-0.0.1-SNAPSHOT.jar lead.RunOptimization \
  --deliveries-path temp/vrp_deliveries.csv \
  --costs-path temp/vrp_distances.csv  \
  --depot-node ${depot_node} \
  --vehicle-speed ${vehicle_speed} \
  --vehicle-capacity ${vehicle_capacity} \
  --vehicle-type ${vehicle_type} \
  --output-path output/vrp_solution.csv

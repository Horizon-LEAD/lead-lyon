## Attention: Instance needs build tools, e.g. for Ubuntu:
# sudo apt update && sudo apt upgrade -y && sudo apt install -y build-essential

set -e

# Reset the environments
rm -rf 01_synthetic_population/environment
rm -rf 01_synthetic_population/pipeline

rm -rf 02_parcel_generation/environment
rm -rf 02_parcel_generation/code

rm -rf 03_vrp/environment
rm -rf 03_vrp/code

# Copy the raw data sets to the population synthesis environment
cp -r raw_data/* 01_synthetic_population/data
cp raw_data/osm/rhone-alpes-latest.osm.pbf 03_vrp/input/region.osm.pbf

# Copy perimeter file for the study area
cp raw_data/study_area.gpkg 03_vrp/input/area.gpkg

# 1) Population synthesis
cp raw_data/* 01_synthetic_population/data # Move the raw input data
cd 01_synthetic_population
bash 00_setup.sh
bash 01_run.sh
cd ..

# 2) Parcel generation
cp -r 01_synthetic_population/output/* 02_parcel_generation/input # Copy popualtion input data
cd 02_parcel_generation
bash 00_setup.sh
bash 01_run.sh
cd ..

# 3) Solve VRP
cp 02_parcel_generation/output/* 03_vrp/input # Copy demand input data
cp 01_synthetic_population/output/homes.gpkg 03_vrp/input # Copy some population input
cd 03_vrp
bash 00_setup.sh
bash 01_run.sh
cd ..

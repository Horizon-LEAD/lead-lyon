# Reset the environments
rm -rf 01_synthetic_population/environment
rm -rf 01_synthetic_population/pipeline

rm -rf 02_parcel_generation/environment
rm -rf 02_parcel_generation/code

rm -rf 03_vrp/environment
rm -rf 03_vrp/code

# Copy the raw data sets to the population synthesis environment
cp raw_data/* 01_synthetic_population/data
cp raw_data/osm/rhones-alpes.osm.pbf 03_vrp/input/region.osm.pbf

# Copy perimeter file for the study area
cp raw_data/study_area.gpkg 03_vrp/input/area.gpkg

# 1) Population synthesis
cp raw_data/* 01_synthetic_population/data # Move the raw input data
bash 01_synthetic_population/00_setup.sh
bash 01_synthetic_population/01_run.sh

# 2) Parcel generation
cp 01_synthetic_population/output/* 02_parcel_generation/input # Copy popualtion input data
bash 02_parcel_generation/00_setup.sh
bash 02_parcel_generation/01_run.sh

# 3) Solve VRP
cp 02_parcel_generation/output/* 03_vrp/input # Copy demand input data
cp 01_synthetic_population/output/homes.gpkg 03_vrp/input # Copy some population input
bash 03_vrp/00_setup.sh
bash 03_vrp/01_run.sh

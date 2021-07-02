# Parcel delivery pipeline for LEAD Lyon

This repository generates and simulates the parcel demand for the Confluence
area in Lyon as part of the LEAD project.

## Requirements

- The synthetic travel demand for *Lyon* needs to be created from [eqasim-org/ile-de-france](https://github.com/eqasim-org/ile-de-france):
  - First, [generic data needs to be collected](https://github.com/eqasim-org/ile-de-france/blob/develop/docs/population.md) for Île-de-France which creates a synthetic population for the area around Paris
  - Second, [instructions can be followed](https://github.com/eqasim-org/ile-de-france/blob/develop/docs/cases/lyon.md) for Lyon where several files need to be replaced, afterwards the synthetic travel demand is generated anew
- `osmosis` needs to be installed
- `nbconvert` for Juypter needs to be installed
- `pyrosm` needs to be installed (pip or conda)

### Environment

Since 2nd July we have prepared a conda environment file. It is available in `environment.yml`. To install the environment, call:

```sh
conda env create -n lead_lyon -f environment.yml
```

It should resolve all the dependencies properly. After, you can enter the environment by:

```sh
conda activate lead_lyon
```

## How to run

- From the synthetic travel demand output for Lyon *(should be 100pct)* some files should be placed or symlinked in `input/`:
  - `activities.csv`
  - `homes.gpkg`
  - `persons.csv`
  - `lyon_network.xml.gz`
- From the input data of the synthetic travel demand (including the simulation part), the OSM input file should be copied or symlinked to `input/`:
  - `rhone-alpes-latest.osm.pbf`

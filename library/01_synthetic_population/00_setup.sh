set -e

# Clone the repository with the pipeline code
git clone -b v1.2.0 https://github.com/eqasim-org/ile-de-france.git pipeline

# Prepare environment
mkdir environment

# Set up Anaconda environment
wget https://repo.continuum.io/miniconda/Miniconda3-latest-Linux-x86_64.sh -O environment/miniconda.sh
bash environment/miniconda.sh -b -p environment/miniconda
source environment/miniconda/etc/profile.d/conda.sh
conda config --set always_yes yes --set changeps1 no
conda update -q conda
conda env create -f pipeline/environment.yml

# Set up osmosis
wget https://github.com/openstreetmap/osmosis/releases/download/0.48.2/osmosis-0.48.2.tgz -O environment/osmosis.tgz
mkdir -p environment/osmosis
tar xf environment/osmosis.tgz -C environment/osmosis
PATH=$(realpath environment/osmosis/bin):$PATH

# Test environment
conda activate ile-de-france
python3 --version
osmosis -v
git --version

# Cleanup
rm environment/miniconda.sh
rm environment/osmosis.tgz

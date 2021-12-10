set -e

# Clone the repository with the code
git clone  https://github.com/Horizon-LEAD/lead-lyon code
cd code && git checkout ba19f4f && cd ..

# Prepare environment
mkdir environment

# Set up Anaconda environment
wget https://repo.continuum.io/miniconda/Miniconda3-latest-Linux-x86_64.sh -O environment/miniconda.sh
bash environment/miniconda.sh -b -p environment/miniconda
source environment/miniconda/etc/profile.d/conda.sh
conda config --set always_yes yes --set changeps1 no
conda update -q conda
conda env create -f code/parcels/environment.yml

# Test environment
conda activate lead_lyon
python3 --version
git --version

# Prepare notebook
cp "code/parcels/Generate Parcels.ipynb" .

# Cleanup
rm -rf code
rm environment/miniconda.sh

set -e
mkdir -p output

# Enter the environment
source environment/miniconda/etc/profile.d/conda.sh
conda activate lead_lyon

# Algorithm options
seed=1234
scaling=1.0
earliest_delivery_time=7
latest_delivery_time=23

# Generate parcels based on synthetic travel demand and ADM survey
papermill \
  -p seed ${seed} \
  -p scaling ${scaling} \
  -p earliest_delivery_time ${earliest_delivery_time} \
  -p latest_delivery_time ${latest_delivery_time} \
  "Generate Parcels.ipynb" "Temporary.ipynb"

rm Temporary.ipynb

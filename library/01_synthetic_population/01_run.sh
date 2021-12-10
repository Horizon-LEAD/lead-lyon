set -e

# Prepare folder structure
mkdir -p cache

# Options
sampling_rate=0.01
random_seed=1234

# Prepare the configuration
cp pipeline/config.yml pipeline/lead_config.yml

sed -i -E "s/data_path: .+/data_path: ..\/data/" pipeline/lead_config.yml
sed -i -E "s/output_path: .+/output_path: ..\/ouput/" pipeline/lead_config.yml
sed -i -E "s/working_directory: .+/working_directory: ..\/cache/" pipeline/lead_config.yml
sed -i -E "s/osmosis_path: .+/osmosis_path: ..\/environment\/osmosis\/bin\/osmosis/" pipeline/lead_config.yml

sed -i -E "s/random_seed: .+/random_seed: ${random_seed}/g" pipeline/lead_config.yml
sed -i -E "s/sampling_rate: .+/sampling_rate: ${sampling_rate}/" pipeline/lead_config.yml
sed -i -E "s/random_seed: .+/random_seed: ${random_seed}/" pipeline/lead_config.yml

sed -i -E "s/random_seed: .+/&\n\toutput_prefix: lead_/" pipeline/lead_config.yml
sed -i -E "s/random_seed: .+/&\n\tcensus_path: rp_2015\/FD_INDCVIZE_2015.dbf/" pipeline/lead_config.yml
sed -i -E "s/random_seed: .+/&\n\regions: []/" pipeline/lead_config.yml
sed -i -E "s/random_seed: .+/&\n\departments: [\"01\", 38, 42, 69, 69M]/" pipeline/lead_config.yml

# Enter the environment
source environment/miniconda/etc/profile.d/conda.sh
conda activate ile-de-france

# Run the pipeline
cd pipeline
python3 -m synpp lead_config.yml

set -e

# Clone the repository with the pipeline code
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

# Set up osmosis
wget https://github.com/openstreetmap/osmosis/releases/download/0.48.2/osmosis-0.48.2.tgz -O environment/osmosis.tgz
mkdir -p environment/osmosis
tar xf environment/osmosis.tgz -C environment/osmosis
PATH=$(realpath environment/osmosis/bin):$PATH

# Set up Java
wget https://github.com/adoptium/temurin11-binaries/releases/download/jdk-11.0.13%2B8/OpenJDK11U-jdk_x64_linux_hotspot_11.0.13_8.tar.gz -O environment/java.tar.gz
tar xf environment/java.tar.gz -C environment
mv environment/jdk-11.0.13+8 environment/java
PATH=$(realpath environment/java/bin):$PATH
JAVA_HOME=$(realpath environment/java)

# Set up Maven
wget http://mirror.easyname.ch/apache/maven/maven-3/3.6.3/binaries/apache-maven-3.6.3-bin.tar.gz -O environment/maven.tar.gz
tar xf environment/maven.tar.gz -C environment
mv environment/apache-maven-3.6.3 environment/maven
PATH=$(realpath environment/maven/bin):$PATH

# Test environment
conda activate lead_lyon
python3 --version
osmosis -v
git --version
mvn -v
java -version

# Prepare notebooks
cp "code/parcels/Generate VRP Network.ipynb" .
cp "code/parcels/Generate VRP Instance.ipynb" .
cp "code/parcels/Make Polygon.ipynb" .

# Build the Java part of lyon-lead
cd code/parcels/lead-java && mvn clean package
cd ../../..

# Copy JAR executable
cp code/parcels/lead-java/target/lead-0.0.1-SNAPSHOT.jar .

# Cleanup
rm environment/miniconda.sh
rm environment/osmosis.tgz
rm environment/java.tar.gz
rm environment/maven.tar.gz
rm -rf environment/maven
rm -rf code

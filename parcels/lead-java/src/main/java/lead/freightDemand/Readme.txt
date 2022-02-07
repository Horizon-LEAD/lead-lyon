to execute use the class RunFreightDemand and create a FreightDemand class and call runTest()
input - sirene-file (https://www.data.gouv.fr/fr/datasets/base-sirene-des-entreprises-et-de-leurs-etablissements-siren-siret/)
      - area-file (idf_coords.csv, lyon_coords.csv, nantes_coords.csv, toulouse_coords.csv)
            - needs "siret" column
            - needs "law_status" column
            - needs "x" collum
            - needs "y" collum
      - correct coordinates selected in class RunFreightDemand
      -
output - txt file ({region_name}directRounds.txt) with
            - Coord of start point
            - Coord of endpoint
            - score
            - distance (not euclidean)
            - linestring of the trip
       - txt file ({region_name}roundRounds.txt) with
                   - Coord of start point
                   - score
                   - distance (not euclidean)
                   - linestring of the trip
       - matsim plans-file {region_name}_population.xml
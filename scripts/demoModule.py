import argparse
import osmnx as ox

parser = argparse.ArgumentParser()

# generation distance for the graph
parser.add_argument("--dist", type=int, required=True)
# middle coordinates of the two points
parser.add_argument("--coords", type=lambda x: tuple(map(float, x.split(','))), required=True)
# Parse the command-line arguments
args = parser.parse_args()

dist = args.dist
coords = args.coords

#create the graph
G = ox.graph_from_point(coords, dist)

nodes, edges = ox.graph_to_gdfs(G,nodes=False,edges=True)
print(edges.to_json())

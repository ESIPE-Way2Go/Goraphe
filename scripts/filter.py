import sys

import osmnx as ox
import networkx as nx
import geopandas as gpd
import matplotlib.pyplot as plt
import pandas as pd
import math
import time
import argparse
import compute

#################SELECTION OF THE ROADNETWORK#######################################################

parser = argparse.ArgumentParser()
# coordinates which will be the center point of the graph generated
parser.add_argument("--coords", type=lambda x: tuple(map(float, x.split(','))), required=True)
# Type of roads accepted in the graph
parser.add_argument("--roads", type=lambda x: x.split(','), required=True)
# generation distance for the graph
parser.add_argument("--dist", type=int, required=True)
# Parse the command-line arguments
args = parser.parse_args()

# Access the values of the command-line arguments
location = args.coords
roads = args.roads
dist = args.dist

# motorway,trunk,primary,secondary,tertiary,residential,service
cf = '["highway"~"' + '|'.join(roads) + '"]'

print(f"Location: {location}")
print(f"Roads accepted : {roads}")
print(f"Generation distance : {dist}")
time_start = time.perf_counter()

g = ox.graph_from_point(location, dist, network_type='drive', simplify=True, custom_filter=cf)
# g = ox.graph_from_point(location, dist, network_type='drive', simplify=True)

g = ox.project_graph(g)

road_types = set(map(str, nx.get_edge_attributes(g, 'highway').values()))
print(road_types)

# Plot the graph
# ox.plot_graph(g)
# highlighted_roads = [1097986493, 1097986493, 1097781920, 1117815309, 1117815308]
print(f"Nodes of graph :{g.number_of_nodes()}")
print(f"Edges of graph :{g.number_of_edges()}")
# remove nodes with degree=0 (no edges) from the network
solitary = [n for n, d in g.degree() if d == 0]
g.remove_nodes_from(solitary)
print(f"Nodes of graph after removing solitary :{g.number_of_nodes()}")
print(f"Edges of graph after removing solitary :{g.number_of_edges()}")
# Generate connected components and select the largest:
largest_scc = max(nx.strongly_connected_components(g), key=len)
g = g.subgraph(largest_scc)

print(f"Nodes of graph after scc :{g.number_of_nodes()}")
print(f"Edges of graph after scc :{g.number_of_edges()}")

no_speed_roads = [data for u, v, data in g.edges(data=True) if 'maxspeed' not in data]
nodes_proj, edges_proj = ox.graph_to_gdfs(g, nodes=True, edges=True)

speed_map = {
    'motorway': 120,
    'trunk': 100,
    'primary': 90,
    'secondary': 90,
    'tertiary': 90,
    'unclassified': 50,
    'motorway_link': 120,
    'trunk_link': 50,
    'primary_link': 40,
    'secondary_link': 40,
    'tertiary_link': 40,
    'road': 50
}
# new attribute list
traveltimes = dict([])
fixedmaxspeed = dict([])

for k in edges_proj.index:
    u = k[0]
    v = k[1]
    key = k[2]
    if math.isnan(float(edges_proj.at[k, 'maxspeed'])):
        maxspeed = speed_map.get(edges_proj.at[k, 'highway'], 0)
    else:
        maxspeed = float(edges_proj.at[k, 'maxspeed'])
    fixedmaxspeed[(u, v, key)] = maxspeed
    traveltimes[(u, v, key)] = ((float(edges_proj.at[k, 'length']) / fixedmaxspeed[(u, v, key)]) / 3.6) if \
        fixedmaxspeed[(u, v, key)] != 0 else sys.maxsize#99999TODO"NaN"

nx.set_edge_attributes(g, fixedmaxspeed, 'fixedmaxspeed')
nx.set_edge_attributes(g, traveltimes, 'traveltimes')

# highlighted_roads = []

# Create a new column "highlight" in the edges GeoDataFrame
# edges['highlight'] = edges['osmid'].isin(highlighted_roads)

# Create a new column "color" in the edges GeoDataFrame
edges_proj['color'] = 'grey'
edges_proj.loc[pd.isna(edges_proj['maxspeed']), 'color'] = 'green'
# edges.loc[edges['highlight']==True, 'color'] = 'red'


geojson = edges_proj.to_json()
with open('highlighted_roads.geojson', 'w') as f:
    f.write(geojson)
nodes_proj = nodes_proj.to_crs(epsg=4326)
nodes_geojson = nodes_proj.to_json()

with open('highlighted_nodes.geojson', 'w') as f:
    f.write(nodes_geojson)

time_elapsed = (time.perf_counter() - time_start)
print("Filtering time : "+str(time_elapsed))
compute.compute(graph_proj=g,nodes_proj=nodes_proj,edges_proj=edges_proj)
print("Total time : "+str((time.perf_counter() - time_start)))

import logging
import os
import sys
import numpy as np
import osmnx as ox
import networkx as nx
import geopandas as gpd
import math
import time
import argparse
from shapely import LineString
import compute
import random_nodes

def setup_logger(name, log_file, level=logging.DEBUG):
    """To setup as many loggers as you want"""
    formatter = logging.Formatter('%(asctime)s %(levelname)s %(message)s')
    handler = logging.FileHandler(log_file)
    handler.setFormatter(formatter)

    logger = logging.getLogger(name)
    logger.setLevel(level)
    logger.addHandler(handler)

    return logger

def get_nodes_geojson(graph, osmid_list):
    # Create a GeoDataFrame of the nodes in the graph that are in osmid_list
    nodes_gdf = ox.graph_to_gdfs(graph, edges=False)
    nodes_gdf = nodes_gdf[nodes_gdf.index.isin(osmid_list)]
    # Convert the GeoDataFrame to a GeoJSON
    nodes_geojson = nodes_gdf.to_crs("EPSG:4326").to_json()
    return nodes_geojson


def shortest_path_geojson(G, point1, point2, weight):
    route = nx.shortest_path(G, point1, point2, weight=weight)
    nodes = set(route)
    edges = G.subgraph(nodes)
    features = gpd.GeoDataFrame(edges.edges(keys=True))
    print(features.index)
    features['geometry'] = features.apply(
        lambda x: LineString([(G.nodes[x[0]]['x'], G.nodes[x[0]]['y']), (G.nodes[x[1]]['x'], G.nodes[x[1]]['y'])]),
        axis=1)
    features['osmid'] = features.apply(lambda x: G.edges[(x[0], x[1], x[2])].get('osmid', ''), axis=1)
    features['name'] = features.apply(lambda x: G.edges[(x[0], x[1], x[2])].get('name', ''), axis=1)
    features['maxspeed'] = features.apply(lambda x: G.edges[(x[0], x[1], x[2])].get('fixedmaxspeed', ''), axis=1)
    features['timetravel'] = features.apply(lambda x: G.edges[(x[0], x[1], x[2])].get('traveltimes', ''), axis=1)
    features['evi_local'] = features.apply(lambda x: G.edges[(x[0], x[1], x[2])].get('evi_local', ''), axis=1)

    return features.to_json()

#################SELECTION OF THE ROADNETWORK#######################################################
parser = argparse.ArgumentParser()
# user name
parser.add_argument("--user", type=str, required=True)
# simulation
parser.add_argument("--sim", type=str, required=True)
# coordinates which will be the center point of the graph generated
parser.add_argument("--coords", type=lambda x: tuple(map(float, x.split(','))), required=True)
# coordinates point 1
parser.add_argument("--point1", type=lambda x: tuple(map(float, x.split(','))), required=True)
# coordinates point 2
parser.add_argument("--point2", type=lambda x: tuple(map(float, x.split(','))), required=True)

# Type of roads accepted in the graph
parser.add_argument("--roads", type=lambda x: x.split(','), required=True)
# generation distance for the graph
parser.add_argument("--dist", type=int,required=True)
# Parse the command-line arguments
args = parser.parse_args()

# Access the values of the command-line arguments
location = args.coords
point1 = args.point1
point2 = args.point2
roads = args.roads
dist = args.dist
user = args.user
sim = args.sim

#Creation of logger
os.makedirs("scripts/" + user, exist_ok=True)
LOG_FILENAME = os.getcwd() + "/scripts/" + user + "/" + sim + "_1.log"
logger = setup_logger(LOG_FILENAME,LOG_FILENAME)
logger.info("Init of filter")

# motorway,trunk,primary,secondary,tertiary,residential,service
cf = '["highway"~"' + '|'.join(roads) + '"]'

logger.info(f"Location: {location}")
logger.info(f"Roads accepted : {roads}")
logger.info(f"Generation distance : {dist}")
time_start = time.perf_counter()

g_not_proj = ox.graph_from_point(location, dist, simplify=False, network_type='drive', custom_filter=cf)
g = ox.project_graph(g_not_proj)

road_types = set(map(str, nx.get_edge_attributes(g, 'highway').values()))
logger.info(road_types)

# Plot the graph
# ox.plot_graph(g)
logger.info(f"Nodes of graph :{g.number_of_nodes()}")
logger.info(f"Edges of graph :{g.number_of_edges()}")
# remove nodes with degree=0 (no edges) from the network
solitary = [n for n, d in g.degree() if d == 0]
g.remove_nodes_from(solitary)
logger.info(f"Nodes of graph after removing solitary :{g.number_of_nodes()}")
logger.info(f"Edges of graph after removing solitary :{g.number_of_edges()}")
# Generate connected components and select the largest:
largest_scc = max(nx.strongly_connected_components(g), key=len)
g = g.subgraph(largest_scc)

logger.info(f"Nodes of graph after scc :{g.number_of_nodes()}")
logger.info(f"Edges of graph after scc :{g.number_of_edges()}")

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
    edges_proj = edges_proj.fillna(value=np.nan)
    if math.isnan(float(edges_proj.at[k, 'maxspeed'])):
        maxspeed = float(speed_map.get(edges_proj.at[k, 'highway'], 0))
        edges_proj.at[k, 'maxspeed'] = maxspeed
    else:
        maxspeed = float(edges_proj.at[k, 'maxspeed'])
    fixedmaxspeed[(u, v, key)] = maxspeed
    traveltimes[(u, v, key)] = ((float(edges_proj.at[k, 'length']) / fixedmaxspeed[(u, v, key)]) / 3.6) if \
        fixedmaxspeed[(u, v, key)] != 0 else sys.maxsize

nx.set_edge_attributes(g, fixedmaxspeed, "fixedmaxspeed")
nx.set_edge_attributes(g, traveltimes, "traveltimes")

time_elapsed = (time.perf_counter() - time_start)
logger.info("Filtering time : " + str(time_elapsed))
logger.info("End of filter")
rand_nodes = random_nodes.random_nodes(g, g_not_proj,point1[0],point1[1],point2[0],point2[1],user,sim,dist)
rand_nodes_geojson = get_nodes_geojson(g,rand_nodes)
# with open('rand_nodes.geojson', 'w') as f:
#     f.write(rand_nodes_geojson)
print(rand_nodes_geojson)

source_node = ox.distance.nearest_nodes(g_not_proj, point1[0], point1[1])
dest_node = ox.distance.nearest_nodes(g_not_proj, point2[0], point2[1])

compute.compute(g, rand_nodes,source_node,dest_node,user,sim)
selected_route_geojson = shortest_path_geojson(g, source_node, dest_node, 'traveltimes')
# with open('selected_route.geojson', 'w') as f:
#     f.write(selected_route_geojson)
print(selected_route_geojson)

logger.info("Total time : " + str((time.perf_counter() - time_start)))

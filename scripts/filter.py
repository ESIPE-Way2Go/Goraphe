import sys
import json
import geopandas
import numpy as np
import requests
import osmnx as ox
import osmnet as oxnet
import networkx as nx
import geopandas as gpd
import matplotlib.pyplot as plt
import pandas as pd
import math
import time
import argparse

from networkx import nodes
from shapely import LineString

import compute
import random_nodes


def meanspeed(speeds):
    if isinstance(speeds, float):
        return speeds
    speeds = [float(speed) for speed in speeds]
    return sum(speeds) / len(speeds)


# Define a function to convert a way element to a GeoDataFrame row
def way_to_row(way, edges_proj_consistent):
    osmid = way['id']
    tags = way['tags']

    geometry = LineString([(n['lon'], n['lat']) for n in way['geometry']])
    #TODO key est égale à 0 quand on décompose mais à vérifier quand même
    oneway = 'oneway' in tags and tags['oneway'] == 'yes'
    reversed = False
    highway = tags.get('highway')
    length = geometry.length
    maxspeed = tags.get('maxspeed')
    junction = tags.get('junction')
    lanes = tags.get('lanes')
    ref = tags.get('ref')
    bridge = tags.get('bridge')
    name = tags.get('name')
    service = tags.get('service')
    area = tags.get('area')
    width = tags.get('width')

    u = way['nodes'][0]
    v = way['nodes'][-1]
    key = 0

    data = {
        'osmid': osmid,
        'oneway': oneway,
        'highway': highway,
        'reversed': reversed,
        'length': length,
        'maxspeed': maxspeed,
        'geometry': geometry,
        'junction': junction,
        'lanes': lanes,
        'ref': ref,
        'bridge': bridge,
        'name': name,
        'service': service,
        'area': area,
        'width': width
    }
    panda_serie = pd.Series(data=data, index=['osmid', 'oneway', 'highway', 'reversed', 'length', 'maxspeed',
                                              'geometry', 'junction', 'lanes', 'ref', 'bridge', 'name', 'service',
                                              'area', 'width'])
    edges_proj_consistent.loc[(u, v, key)] = panda_serie
    return edges_proj_consistent


#################SELECTION OF THE ROADNETWORK#######################################################

parser = argparse.ArgumentParser()
# coordinates which will be the center point of the graph generated
parser.add_argument("--coords", type=lambda x: tuple(map(float, x.split(','))), required=True)
# coordinates point 1
parser.add_argument("--point1", type=lambda x: tuple(map(float, x.split(','))), required=True)
# coordinates point 2
parser.add_argument("--point2", type=lambda x: tuple(map(float, x.split(','))), required=True)

# Type of roads accepted in the graph
parser.add_argument("--roads", type=lambda x: x.split(','), required=True)
# generation distance for the graph
parser.add_argument("--dist", type=int,
                    required=True)  # TODO tester si la distance inclut bien les points posés par l'utilisateur
# Parse the command-line arguments
args = parser.parse_args()

# Access the values of the command-line arguments
location = args.coords
point1 = args.point1
point2 = args.point2
roads = args.roads
dist = args.dist

# motorway,trunk,primary,secondary,tertiary,residential,service
cf = '["highway"~"' + '|'.join(roads) + '"]'

print(f"Location: {location}")
print(f"Roads accepted : {roads}")
print(f"Generation distance : {dist}")
time_start = time.perf_counter()

g_not_proj = ox.graph_from_point(location, dist, network_type='drive', custom_filter=cf)
g = ox.project_graph(g_not_proj)

road_types = set(map(str, nx.get_edge_attributes(g, 'highway').values()))
print(road_types)

# Plot the graph
# ox.plot_graph(g)
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

nodes_proj, edges_proj = ox.graph_to_gdfs(g, nodes=True, edges=True)

overpass_url = "http://overpass-api.de/api/interpreter"
idsToRequest = []
index = pd.MultiIndex(levels=[[], [], []], codes=[[], [], []], names=['u', 'v', 'key'])
edges_proj_consistent = gpd.GeoDataFrame(columns=['osmid', 'oneway', 'highway', 'reversed', 'length', 'maxspeed',
                                                  'geometry', 'junction', 'lanes', 'ref', 'bridge', 'name'], index=index)

for index, entry in edges_proj.iterrows():
    osmid = entry.get('osmid')
    if (isinstance(osmid, list)):
        for id in osmid:
            idsToRequest.append(id)
    else:
        edges_proj_consistent.loc[(index[0], index[1], index[2])] = entry

nbSubList = len(idsToRequest) / 100
print(edges_proj_consistent)
for i in range(int(nbSubList) + 1):
    subList = idsToRequest[i * 100:len(idsToRequest)] if i + 1 == nbSubList else idsToRequest[i * 100:(i + 1) * 100]
    query = "[out:json];(" + "".join(["way({});".format(osmid) for osmid in subList]) + ");out body geom;"
    response = oxnet.overpass_request(data=query)
    elements = response['elements']
    for element in elements:
        edges_proj_consistent = way_to_row(element, edges_proj_consistent)

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

print("edges_proj_consistent.index : " + str(edges_proj_consistent.index))
print("edges_proj_consistent : " + str(edges_proj_consistent))
for k in edges_proj_consistent.index:
    u = k[0]
    v = k[1]
    key = k[2]
    edges_proj_consistent = edges_proj_consistent.fillna(value=np.nan)
    if math.isnan(float(edges_proj_consistent.at[k, 'maxspeed'])):
        maxspeed = speed_map.get(edges_proj_consistent.at[k, 'highway'], 0)
    else:
        maxspeed = meanspeed(edges_proj_consistent.at[k, 'maxspeed'])
    fixedmaxspeed[(u, v, key)] = maxspeed
    traveltimes[(u, v, key)] = ((float(edges_proj_consistent.at[k, 'length']) / fixedmaxspeed[(u, v, key)]) / 3.6) if \
        fixedmaxspeed[(u, v, key)] != 0 else sys.maxsize  # 99999TODO"NaN"


# highlighted_roads = []

# Create a new column "highlight" in the edges GeoDataFrame
# edges['highlight'] = edges['osmid'].isin(highlighted_roads)

# Create a new column "color" in the edges GeoDataFrame
edges_proj_consistent['color'] = 'grey'
edges_proj_consistent.loc[pd.isna(edges_proj_consistent['maxspeed']), 'color'] = 'green'
# edges.loc[edges['highlight']==True, 'color'] = 'red'

edges_proj_consistent.crs = "EPSG:4326"
g_consistent = ox.graph_from_gdfs(nodes_proj, edges_proj_consistent)
nx.set_edge_attributes(g_consistent, fixedmaxspeed, "fixedmaxspeed")
nx.set_edge_attributes(g_consistent, traveltimes, "traveltimes")

geojson = edges_proj_consistent.to_json()
with open('highlighted_roads.geojson', 'w') as f:
    f.write(geojson)
nodes_proj = nodes_proj.to_crs(epsg=4326)
nodes_geojson = nodes_proj.to_json()

with open('highlighted_nodes.geojson', 'w') as f:
    f.write(nodes_geojson)

time_elapsed = (time.perf_counter() - time_start)
print("Filtering time : " + str(time_elapsed))
#x1 et x2 longitude et y1 et y2 lattitude
rand_nodes = random_nodes.random_nodes(g_consistent,g_not_proj,point1[0],point1[1],point2[0],point2[0])

compute.compute(g_not_proj,g_consistent, rand_nodes,point1,point2)
print("Total time : " + str((time.perf_counter() - time_start)))

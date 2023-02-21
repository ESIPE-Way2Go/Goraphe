import sys

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


def meanspeed(speeds):
    if isinstance(speeds, float):
        return speeds
    speeds = [float(speed) for speed in speeds]
    return sum(speeds) / len(speeds)


# Define a function to convert a way element to a GeoDataFrame row
def way_to_row(way, edges_proj_consistent):
    osmid = way['id']
    tags = way['tags']

    # nodeslist = way['nodes']
    # query = f'[out:json];({"".join("node({});".format(osmid) for osmid in nodeslist)}); out;'
    # response = requests.get(overpass_url, params={'data': query})
    # elements=[]
    # if response.status_code == 200:
    #     data = response.json()
    #     elements = data['elements']
    #     # for element in elements:
    #     #     print(element)
    #
    #
    # else:
    #     print("Request to overpass for nodes failed with status code", response.status_code)
    # print(str(len(subList)))

    geometry = LineString([(n['lon'], n['lat']) for n in way['geometry']])
    # print("Geometry 0 et 1 : "+str(way['nodes'][0])+"!!!"+str(way['nodes'][-1]))
    # print("Key in tags !!! : "+str(tags.get('id')))#TODO key est égale à 0 quand on décompose mais à vérifier quand même
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
    # panda_serie = pd.Series(data=data, index=pd.MultiIndex.from_tuples([(u, v, key)]))
    panda_serie = pd.Series(data=data, index=['osmid', 'oneway', 'highway', 'reversed', 'length', 'maxspeed',
                                              'geometry', 'junction', 'lanes', 'ref', 'bridge', 'name', 'service',
                                              'area', 'width'])
    # print("Avant WARNING")
    # edges_proj_consistent = pd.concat([edges_proj_consistent, panda_serie], ignore_index=True)
    edges_proj_consistent.loc[(u, v, key)] = panda_serie
    return edges_proj_consistent


#################SELECTION OF THE ROADNETWORK#######################################################

parser = argparse.ArgumentParser()
# coordinates which will be the center point of the graph generated
parser.add_argument("--coords", type=lambda x: tuple(map(float, x.split(','))), required=True)
# Type of roads accepted in the graph
parser.add_argument("--roads", type=lambda x: x.split(','), required=True)
# generation distance for the graph
parser.add_argument("--dist", type=int,
                    required=True)  # TODO tester si la distance inclut bien les points posés par l'utilisateur
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

# no_speed_roads = [data for u, v, data in g.edges(data=True) if 'maxspeed' not in data]
nodes_proj, edges_proj = ox.graph_to_gdfs(g, nodes=True, edges=True)

# TODO essayer de faire une liste et de faire une seule grande requête d'un coup
# requestcounter = 0
# overpass_url = "http://overpass-api.de/api/interpreter"
# for index,entry in edges_proj.iterrows() :
#      osmid = entry.get('osmid')
#      print(entry)
#      if(isinstance(osmid,list)):
#          for id in osmid :
#              query = f'[out:json];way({id});out body;'
#              response = oxnet.overpass_request(data=query)
#              edge = response['elements'][0]
#              print(edge)
#              requestcounter = requestcounter+1
# print("\nNumber of requests sent to overpass-api :\n"+str(requestcounter)) #536 request for args : --coords 40.869397098029644,-2.8448171296830687 --dist 10000 --roads motorway,trunk,primary,secondary,tertiary,residential,service

# TODO
overpass_url = "http://overpass-api.de/api/interpreter"
idsToRequest = []
index = pd.MultiIndex(levels=[[], [], []], codes=[[], [], []], names=['u', 'v', 'key'])
edges_proj_consistent = gpd.GeoDataFrame(columns=['osmid', 'oneway', 'highway', 'reversed', 'length', 'maxspeed',
                                                  'geometry', 'junction', 'lanes', 'ref', 'bridge', 'name', 'service',
                                                  'area', 'width'], index=index)

for index, entry in edges_proj.iterrows():
    osmid = entry.get('osmid')
    if (isinstance(osmid, list)):
        for id in osmid:
            idsToRequest.append(id)
    else:
        edges_proj_consistent.loc[(index[0], index[1], index[2])] = entry
        # edges_proj_consistent = pd.concat([edges_proj_consistent, entry], ignore_index=True)

nbSubList = len(idsToRequest) / 100
print(edges_proj_consistent)
for i in range(int(nbSubList) + 1):
    subList = idsToRequest[i * 100:len(idsToRequest)] if i + 1 == nbSubList else idsToRequest[i * 100:(i + 1) * 100]
    # query = "[out:json];(" + "".join(["way({});(._;>;);".format(osmid) for osmid in subList]) + ");out ids geom bb;"
    query = "[out:json];(" + "".join(["way({});".format(osmid) for osmid in subList]) + ");out body geom;"
    # query = f'[out:json];({",".join("node({});".format(osmid) for osmid in subList)}) out;'
    # query = (
    #     "[out:json];(".join(["way({});".format(osmid) for osmid in subList])+")->.allways;way(allways)->.ways;way(allways);node(w.ways);way(w.ways);out meta geom;"
    # )
    response = oxnet.overpass_request(data=query)
    # response = requests.get(overpass_url, params={'data': query})
    # if response.status_code == 200:
    #data = response.json()
    #elements = data['elements']
    elements = response['elements']
    for element in elements:
        # print(element)
        edges_proj_consistent = way_to_row(element, edges_proj_consistent)
        # new_row = way_to_row(element)
        # print(str(new_row))
        # edges_proj_consistent = pd.concat([edges_proj_consistent,new_row], ignore_index=True)

# else:
#     print("Request to overpass failed with status code", response.status_code)
# print(str(len(subList)))
# print(subList)
# print(str(edges_proj_consistent))
# print(str(edges_proj.columns))


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
    # print("K type : " + str(type(k)))
    # print("K : " + str(k))
    # print("K[0] : " + str(k[0]))
    # print("K[1] : " + str(k[1]))
    # print("K[2] : " + str(k[2]))

    u = k[0]
    v = k[1]
    key = k[2]
    edges_proj_consistent = edges_proj_consistent.fillna(value=np.nan)
    # print(edges_proj_consistent.at[k, 'osmid'])
    if math.isnan(float(edges_proj_consistent.at[k, 'maxspeed'])):
        # print(edges_proj_consistent.at[k, 'highway'])
        # print(edges_proj_consistent.at[k, 'highway'])
        # print(edges_proj_consistent.at[k, 'osmid'])
        maxspeed = speed_map.get(edges_proj_consistent.at[k, 'highway'], 0)
    else:
        maxspeed = meanspeed(edges_proj_consistent.at[k, 'maxspeed'])
    fixedmaxspeed[(u, v, key)] = maxspeed
    traveltimes[(u, v, key)] = ((float(edges_proj_consistent.at[k, 'length']) / fixedmaxspeed[(u, v, key)]) / 3.6) if \
        fixedmaxspeed[(u, v, key)] != 0 else sys.maxsize  # 99999TODO"NaN"
#TODO TODO TODO TODO
g_consistent = ox.graph_from_gdfs(nodes_proj, edges_proj_consistent)
nx.set_edge_attributes(g_consistent, fixedmaxspeed, 'fixedmaxspeed')
nx.set_edge_attributes(g_consistent, traveltimes, 'traveltimes')

# highlighted_roads = []

# Create a new column "highlight" in the edges GeoDataFrame
# edges['highlight'] = edges['osmid'].isin(highlighted_roads)

# Create a new column "color" in the edges GeoDataFrame
edges_proj_consistent['color'] = 'grey'
edges_proj_consistent.loc[pd.isna(edges_proj_consistent['maxspeed']), 'color'] = 'green'
# edges.loc[edges['highlight']==True, 'color'] = 'red'


geojson = edges_proj_consistent.to_json()
with open('highlighted_roads.geojson', 'w') as f:
    f.write(geojson)
nodes_proj = nodes_proj.to_crs(epsg=4326)
nodes_geojson = nodes_proj.to_json()

with open('highlighted_nodes.geojson', 'w') as f:
    f.write(nodes_geojson)

time_elapsed = (time.perf_counter() - time_start)
print("Filtering time : " + str(time_elapsed))
#g_consistent = ox.graph_from_gdfs(nodes_proj, edges_proj_consistent)
compute.compute(graph_proj=g, nodes_proj=nodes_proj, edges_proj=edges_proj_consistent)
print("Total time : " + str((time.perf_counter() - time_start)))

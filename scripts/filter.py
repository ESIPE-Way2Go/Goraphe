import logging
import os
import sys
import numpy as np
import osmnx as ox
import networkx as nx
import math
import time
import argparse
import compute

def setup_logger(name, log_file, level=logging.DEBUG):
    """To setup as many loggers as you want"""
    formatter = logging.Formatter('%(asctime)s %(levelname)s %(message)s')
    handler = logging.FileHandler(log_file)
    handler.setFormatter(formatter)

    logger = logging.getLogger(name)
    logger.setLevel(level)
    logger.addHandler(handler)

    return logger

#################SELECTION OF THE ROADNETWORK#######################################################
time_start = time.perf_counter()

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
parser.add_argument("--dist", type=int, required=True)
# Number of random points generated for each iteration
parser.add_argument("--random", type=int, required=True)
# start osm_id
parser.add_argument("--start_id", type=int, required=True)
# end osm_id
parser.add_argument("--end_id", type=int, required=True)

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
random = args.random
source_node=args.start_id
dest_node=args.end_id

# Creation of logger
os.makedirs("scripts/" + user, exist_ok=True)
os.makedirs("scripts/" + user + "/" + sim, exist_ok=True)
os.makedirs("scripts/" + user + "/" + sim + "/json", exist_ok=True)
LOG_FILENAME = os.getcwd() + "/scripts/" + user + "/" + sim + "/filter.log"
logger = setup_logger(LOG_FILENAME, LOG_FILENAME)

logger.info("Init of filter")

#check if the number of random nodes is enough or to much to run a simulation
if random < 2 or random > 100:
    logger.error("Number of random points cannot be less than 2 or more than 100")
    exit(0)

# motorway,trunk,primary,secondary,tertiary,residential,service
cf = '["highway"~"' + '|'.join(roads) + '"]'

logger.info(f"Center point used for generation : {location}")
logger.info(f"Roads accepted : {roads}")
logger.info(f"Generation distance : {dist}")
logger.info(f"Starting point : {point1}")
logger.info(f"Destination point : {point2}")

#Create the graph using center coordinates and a radius distance
g_not_proj = ox.graph_from_point(location, dist, simplify=False, network_type='drive', custom_filter=cf)
g = ox.project_graph(g_not_proj)

logger.info(f"Nodes of graph :{g.number_of_nodes()}")
logger.info(f"Edges of graph :{g.number_of_edges()}")

logger.info("get source node")

#Find the source and destination node selected from the graphical user interface
test = [(u,v,k,d) for u,v,k,d in g.edges(keys=True, data=True) if  d['osmid'] == int(source_node)]
if (len(test) < 1):
    logger.error('No source node found near the osmid selected')
    exit(1)
else:
    logger.info("Source node used : " + str(test[0][0]))
    source_node = test[0][0]

logger.info("get destination node")
test = [(u,v,k,d) for u,v,k,d in g.edges(keys=True, data=True) if  d['osmid']==int(dest_node)]
if (len(test) < 1):
    logger.error('No dest node found near the osmid selected')
    exit(1)
else:
    logger.info("Dest node used : " + str(test[0][0]))
    dest_node=test[0][0]

nodes_proj, edges_proj = ox.graph_to_gdfs(g, nodes=True, edges=True)

#dictionary setting default values for speeds using type of roads
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
    'road': 50,
    'service' : 40,
    'residential' : 40
}

# new attribute list
traveltimes = dict([])
fixedmaxspeed = dict([])

#Check if all edges have a speed or set it using the dictionary of default speeds
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
    traveltimes[(u, v, key)] = ((float(edges_proj.at[k, 'length']) / fixedmaxspeed[(u, v, key)])) if \
        fixedmaxspeed[(u, v, key)] != 0 else sys.maxsize

#Adds attributes to the graph
nx.set_edge_attributes(g, fixedmaxspeed, "fixedmaxspeed")
nx.set_edge_attributes(g, traveltimes, "traveltimes")

time_elapsed = (time.perf_counter() - time_start)
logger.info("Filtering time : " + str(time_elapsed))
logger.info("Number of edges in graph : " + str(len(edges_proj)))
logger.info("End")
compute.compute(g,point1,point2,dist,user,sim,random,source_node,dest_node)
logger.info("Total time : " + str((time.perf_counter() - time_start)))

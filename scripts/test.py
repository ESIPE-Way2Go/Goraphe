import argparse
import sys
import os
import osmnx as ox
import logging
import time

sys.path.append("/mnt/d/Cours/S5/LastProject/GoRaphe/frontend")
parser = argparse.ArgumentParser()

# generation distance for the graph
parser.add_argument("--dist", type=int, required=True)
# middle coordinates of the two points
parser.add_argument("--coords", type=lambda x: tuple(map(float, x.split(','))), required=True)
# user name
parser.add_argument("--user", type=str, required=True)
# simulation
parser.add_argument("--sim", type=str, required=True)
# description
parser.add_argument("--desc", type=str, required=True)
# Parse the command-line arguments
args = parser.parse_args()

dist = args.dist
coords = args.coords
user = args.user
sim = args.sim
desc = args.desc

# Build the directory of log
os.makedirs("scripts/" + user, exist_ok=True)

LOG_FILENAME = os.getcwd() + "/scripts/" + user + "/" + sim + "_1.log";
logging.basicConfig(level=logging.DEBUG,
                    filename=LOG_FILENAME,
                    filemode="a+",
                    format='%(asctime)s - %(levelname)s - %(message)s')

logging.info("Start")
logging.debug(f"User : {user}")
logging.debug(f"Simulation name : {sim}")
logging.debug(f"Description : {desc}")
logging.debug(f"Coordinates used : {coords}")
logging.debug(f"Generation distance used : {dist}")
logging.debug("\nStarting timer...")
time_start = time.perf_counter()

# create the graph
G = ox.graph_from_point(coords, dist, network_type='drive', simplify=True)

edges = ox.graph_to_gdfs(G, nodes=False, edges=True)

time_elapsed = (time.perf_counter() - time_start)
logging.info("Filtering time : " + str(time_elapsed))

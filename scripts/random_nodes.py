import logging
import os

import osmnx as ox
import random

#Need to be duplicated from filter3 because of error "circular import"
def setup_logger(name, log_file, level=logging.DEBUG):
    """To setup as many loggers as you want"""
    formatter = logging.Formatter('%(asctime)s %(levelname)s %(message)s')
    handler = logging.FileHandler(log_file)
    handler.setFormatter(formatter)

    logger = logging.getLogger(name)
    logger.setLevel(level)
    logger.addHandler(handler)

    return logger

def test(user ,sim):
    #os.makedirs("scripts/" + user, exist_ok=True)
    #os.makedirs("scripts/" + user + "/" + sim, exist_ok=True)
    LOG_FILENAME = os.getcwd() + "/scripts/" + user + "/" + sim + "/random.log"
    logger = setup_logger(LOG_FILENAME, LOG_FILENAME)
    logger.info("Init of random_nodes")

    logger.info("random")


def random_nodes(G_proj, G_not_proj, x1, y1, x2, y2, user ,sim):
    # Creation of logger
    os.makedirs("scripts/" + user, exist_ok=True)
    LOG_FILENAME = os.getcwd() + "/scripts/" + user + "/" + sim + "_2.log"
    logger = setup_logger(LOG_FILENAME, LOG_FILENAME)
    logger.info("Init of random_nodes")

    # define the number of random points that will be used (this number will be around the first chosen node AND around
    # the second chosen node ; so there will be random points equals to 2 times the number chosen below in total)
    n_random_points = 3
    # define a distance around each one of the 2 chosen nodes to create random points
    distance = 0.0125

    # create the random points list, consisting of 2 "n_random_points" size lists in which will be stored our random
    # points ("n_random_points" in the first list and "n_random_points" in the second list ; used to separate our points
    # that will be around the 2 different chosen nodes)

    random_points = []
    # define 2 x "n_random_points" at random positions in the graph (10 around each chosen node at a
    # distance <= "distance")
    for i in range(n_random_points):
        # TODO à perfectionner
        rx1 = random.uniform(-distance, distance)
        ry1 = random.uniform(-distance, distance)
        rx2 = random.uniform(-distance, distance)
        ry2 = random.uniform(-distance, distance)

        # find the first random point
        first_random_node = ox.distance.nearest_nodes(G_not_proj, x1 + rx1, y1 + ry1)
        while(not G_proj.has_node(first_random_node)):
            logger.info("RANDOM NODE NOT IN GRAPH :" + str(first_random_node))
            rx1 = random.uniform(-distance, distance)
            ry1 = random.uniform(-distance, distance)
            first_random_node = ox.distance.nearest_nodes(G_not_proj, x1 + rx1, y1 + ry1)
        random_points.append(first_random_node)

        # find the second random point
        second_random_node = ox.distance.nearest_nodes(G_not_proj, x2 + rx2, y2 + ry2)
        while(not G_proj.has_node(second_random_node)):
            logger.info("RANDOM NODE NOT IN GRAPH 2:" + str(second_random_node))
            rx2 = random.uniform(-distance, distance)
            ry2 = random.uniform(-distance, distance)
            second_random_node = ox.distance.nearest_nodes(G_not_proj, x2 + rx2, y2 + ry2)
        random_points.append(second_random_node)
    # create a list of colors to highlight the random points
    node_colors = ['blue' if node in random_points else 'gray' for node in G_proj.nodes()]
    # plot the graph with highlighted random points
    #ox.plot_graph(G_proj,node_color=node_colors ,node_size=50 ,show=True)
    logger.info("End of random_nodes")
    return random_points


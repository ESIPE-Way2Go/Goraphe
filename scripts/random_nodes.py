import argparse
import logging

import osmnx as ox
import networkx as nx
import random


def random_nodes(G_proj,G, x1, y1, x2, y2):
    # define the number of random points that will be used (this number will be around the first chosen node AND around
    # the second chosen node ; so there will be random points equals to 2 times the number chosen below in total)
    n_random_points = 10
    # define a distance around each one of the 2 chosen nodes to create random points
    distance = 0.0125

    # create the random points list, consisting of 2 "n_random_points" size lists in which will be stored our random
    # points ("n_random_points" in the first list and "n_random_points" in the second list ; used to separate our points
    # that will be around the 2 different chosen nodes)
    #random_points = [0 for _ in range(n_random_points * 2)]
    random_points = []
    # define 2 x "n_random_points" at random positions in the graph (10 around each chosen node at a
    # distance <= "distance")
    for i in range(n_random_points):
        rx1 = random.uniform(-distance, distance)
        ry1 = random.uniform(-distance, distance)
        rx2 = random.uniform(-distance, distance)
        ry2 = random.uniform(-distance, distance)
        # find the first random point

        first_random_node = ox.distance.nearest_nodes(G, x1 + rx1, y1 + ry1)
        #TODO à perfectionner
        if(G_proj.has_node(first_random_node)):
            # first_random_node = ox.distance.nearest_nodes(G, x1 + rx1, y1 + ry1)
            logging.info("IN GRAPH :"+str(first_random_node))
            random_points.append(first_random_node)
        # find the second random point
        second_random_node = ox.distance.nearest_nodes(G, x2 + rx2, y2 + ry2)
        if(G_proj.has_node(second_random_node)):
            # second_random_node = ox.distance.nearest_nodes(G, x2 + rx2, y2 + ry2)
            logging.info("IN GRAPH :"+str(second_random_node))
            random_points.append(second_random_node)
        # add the first random point

        # add the second random point


    return random_points


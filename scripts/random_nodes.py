import logging
import os

import osmnx as ox
import networkx as nx
import random


# Need to be duplicated from filter3 because of error "circular import"
def setup_logger(name, log_file, level=logging.DEBUG):
    """To setup as many loggers as you want"""
    formatter = logging.Formatter('%(asctime)s %(levelname)s %(message)s')
    handler = logging.FileHandler(log_file)
    handler.setFormatter(formatter)

    logger = logging.getLogger(name)
    logger.setLevel(level)
    logger.addHandler(handler)

    return logger


def random_node_is_too_close(random_node, path_nodes, G_not_proj, min_distance):
    for node in path_nodes:
        distance_calculated = ox.distance.euclidean_dist_vec(G_not_proj.nodes[node]['x'], G_not_proj.nodes[node]['y'],
                                                             G_not_proj.nodes[random_node]['x'],
                                                             G_not_proj.nodes[random_node]['y'])
        if distance_calculated < min_distance:
            return True
    return False


def random_nodes(G_proj, G_not_proj, x1, y1, x2, y2, user, sim, dist, nb_nodes):
    # Creation of logger
    os.makedirs("scripts/" + user, exist_ok=True)
    LOG_FILENAME = os.getcwd() + "/scripts/" + user + "/" + sim + "_2.log"
    logger = setup_logger(LOG_FILENAME, LOG_FILENAME)
    logger.info("Init of random_nodes")

    # define the number of random points that will be used
    n_random_nodes = nb_nodes
    logger.info("Number of random points used : " + str(n_random_nodes))
    # define 2 distances around each one of the nodes to create random points (min and max distances)
    min_distance = dist / 500000
    max_distance = dist / 250000
    # create the random nodes list
    random_nodes = []

    # define the source node, the destination node and all the nodes that are on the shortest path between those 2 nodes
    source_node = ox.distance.nearest_nodes(G_not_proj, x1, y1)
    destination_node = ox.distance.nearest_nodes(G_not_proj, x2, y2)
    path_nodes_src_to_dst = nx.shortest_path(G_proj, source=source_node, target=destination_node)
    path_nodes_dst_to_src = nx.shortest_path(G_proj, source=destination_node, target=source_node)
    # set the number of random nodes to be taken to the number of the nodes in the shortest path if the number of random
    # nodes is to much
    if n_random_nodes > len(path_nodes_src_to_dst):
        n_random_nodes = len(path_nodes_src_to_dst)

    shortest_path_nodes_src_to_dst = random.sample(path_nodes_src_to_dst, n_random_nodes // 2)
    shortest_path_nodes_dst_to_src = random.sample(path_nodes_dst_to_src, n_random_nodes // 2 + n_random_nodes % 2)
    shortest_path_nodes = shortest_path_nodes_src_to_dst + shortest_path_nodes_dst_to_src
    # define "n_random_nodes" at random positions in the graph (n_random_nodes at a distance between "min_distance" and
    # "max_distance")
    for shortest_path_node in shortest_path_nodes:
        # define 2 distances (x and y) to set our random node
        rx = random.uniform(-max_distance, max_distance)
        ry = random.uniform(-max_distance, max_distance)

        # chose the node's coordinates from which the actual random node will be taken
        x, y = G_not_proj.nodes[shortest_path_node]['x'], G_not_proj.nodes[shortest_path_node]['y']

        # find the random node
        random_node = ox.distance.nearest_nodes(G_not_proj, x + rx, y + ry)
        # used to set a max number of "while" iterations
        j = 0
        while (not G_proj.has_node(random_node) or random_node_is_too_close(random_node, path_nodes_src_to_dst,
                                                                            G_not_proj, min_distance)):
            logger.info("RANDOM NODE NOT IN GRAPH OR TOO CLOSE :" + str(random_node))
            if j == 10:
                break
            j += 1
            rx = random.uniform(-max_distance, max_distance)
            ry = random.uniform(-max_distance, max_distance)
            random_node = ox.distance.nearest_nodes(G_not_proj, x + rx, y + ry)
        random_nodes.append(random_node)

    ##### USED TO TEST AND DEBUG #####
    # create a list of colors to highlight the random points
    #node_colors = ['blue' if node in random_nodes else 'gray' for node in G_proj.nodes()]
    # create a list of node sizes to set the size of nodes in random_nodes to 10 and
    # the size of nodes not in random_nodes to 0
    #node_sizes = [10 if node in random_nodes else 0 for node in G_proj.nodes()]
    # plot the graph with highlighted random points
    #ox.plot_graph(G_proj, node_color=node_colors, node_size=node_sizes, show=True)
    ##### USED TO TEST AND DEBUG #####

    logger.info("End of random_nodes")

    return random_nodes

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


def random_node_is_too_close(random_node, path_nodes, G_proj, min_distance):
    for node in path_nodes:
        distance_calculated = ox.distance.euclidean_dist_vec(G_proj.nodes[node]['x'],
                                                             G_proj.nodes[node]['y'],
                                                             G_proj.nodes[random_node]['x'],
                                                             G_proj.nodes[random_node]['y'])
        if distance_calculated < min_distance:
            return True
    return False


def random_nodes(G_proj, source_node, destination_node, user, sim, dist, nb_random_nodes):
    # Creation of logger
    os.makedirs("scripts/" + user, exist_ok=True)
    LOG_FILENAME = os.getcwd() + "/scripts/" + user + "/" + sim + "/random.log"
    logger = setup_logger(LOG_FILENAME, LOG_FILENAME)
    logger.info("Init of random_nodes")

    # define 2 distances that will be used around each one of the nodes to create random points (min and max distances)
    min_distance = dist / 6
    max_distance = dist / 3

    # create the random nodes list
    random_nodes = []

    # we will use those two path_nodes lists to have a better random selection of all the nodes that are on the shortest
    # path between the source and destination nodes
    path_nodes_src_to_dst = nx.shortest_path(G_proj, source=source_node, target=destination_node)
    path_nodes_dst_to_src = nx.shortest_path(G_proj, source=destination_node, target=source_node)

    logger.info("Number of random nodes asked : " + str(nb_random_nodes))
    # if the number of random nodes is not enough, reduce the number of random nodes that will be taken to the maximum
    # possible
    if nb_random_nodes > len(path_nodes_src_to_dst):
        logger.info("Can't generate " + str(nb_random_nodes) + " random nodes (too much for our shortest path), only a "
                                                               "maximum of " + str(
            len(path_nodes_src_to_dst)) + " will be generated")
        nb_random_nodes = len(path_nodes_src_to_dst)

    shortest_path_nodes_src_to_dst = random.sample(path_nodes_src_to_dst,
                                                   nb_random_nodes // 2 + nb_random_nodes % 2 - 1)
    shortest_path_nodes_dst_to_src = random.sample(path_nodes_dst_to_src, nb_random_nodes // 2 - 1)
    shortest_path_nodes = shortest_path_nodes_src_to_dst + shortest_path_nodes_dst_to_src
    logger.info("Shortest path nodes that will be used to generate our random nodes : " + str(shortest_path_nodes))

    # define "n_random_nodes" at random positions in the graph (n_random_nodes at a distance between "min_distance" and
    # "max_distance")
    logger.info("----------Start of the loop used to generate our random nodes----------")

    # generate (or not if too close) the remaining random nodes on the graph
    for shortest_path_node in shortest_path_nodes:
        logger.info("Trying to generate good random nodes from the shortest path node : " + str(shortest_path_node))
        # take the actual shortest path node's coordinates from which the actual random node will be taken
        x, y = G_proj.nodes[shortest_path_node]['x'], G_proj.nodes[shortest_path_node]['y']

        # define 2 distances (x and y) to set our random node
        rx = random.uniform(-max_distance, max_distance)
        ry = random.uniform(-max_distance, max_distance)

        # find the random node
        random_node = ox.distance.nearest_nodes(G_proj, x + rx, y + ry)

        # used to set a max number of "while" iterations
        j = 0
        max_loop_iterations = 25
        while random_node_is_too_close(random_node, path_nodes_src_to_dst, G_proj, min_distance):
            logger.info("\tRandom node generated but not valid in the graph : " + str(random_node))
            if j == max_loop_iterations:
                break
            j += 1
            logger.info("\tTrying to generate another random node")
            rx = random.uniform(-max_distance, max_distance)
            ry = random.uniform(-max_distance, max_distance)
            random_node = ox.distance.nearest_nodes(G_proj, x + rx, y + ry)

        if j == max_loop_iterations:
            logger.info(
                "\tToo much random nodes generated but not valid in the graph, "
                "stopped trying to generate another random node for this iteration")
        else:
            logger.info("\tRandom node generated and used because valid in the graph : " + str(random_node))
            random_nodes.append(random_node)
    logger.info("----------End of the loop used to generate our random nodes----------")

    if len(random_nodes)<nb_random_nodes :
        nb_to_generate = nb_random_nodes-len(random_nodes)

        # generate nb_to_generate random nodes on the graph
        for nb in range(nb_to_generate): #TODO remplacer shortest_path_node
            nod = random.choice(shortest_path_nodes)
            logger.info("Generate good random nodes from the shortest path node : " + str(nod))
            x, y = G_proj.nodes[nod]['x'], G_proj.nodes[nod]['y']

            # define 2 distances (x and y) to set our random node
            rx = random.uniform(-max_distance, max_distance)
            ry = random.uniform(-max_distance, max_distance)

            # find the random node
            random_node = ox.distance.nearest_nodes(G_proj, x + rx, y + ry)

            logger.info("\tRandom node generated and used : " + str(random_node))
            random_nodes.append(random_node)

    ############### USED TO TEST AND DEBUG BY PRINTING THE RANDOM NODES RESULT ###############
    # create a list of colors to highlight the source and destinations nodes
    #node_colors = ['red' if node in [source_node, destination_node] else 'gray' for node in G_proj.nodes()]
    # create a list of node sizes to set the size of nodes in random_nodes to 10 and
    # the size of nodes not in random_nodes to 0
    #node_sizes = [10 if node in [source_node, destination_node] else 0 for node in G_proj.nodes()]
    # plot the graph with highlighted source and destination nodes
    #ox.plot_graph(G_proj, node_color=node_colors, node_size=node_sizes, show=True)
    # create a list of colors to highlight the random nodes
    #node_colors = ['blue' if node in random_nodes else 'gray' for node in G_proj.nodes()]
    # create a list of node sizes to set the size of nodes in random_nodes to 10 and
    # the size of nodes not in random_nodes to 0
    #node_sizes = [10 if node in random_nodes else 0 for node in G_proj.nodes()]
    # plot the graph with highlighted random nodes
    #ox.plot_graph(G_proj, node_color=node_colors, node_size=node_sizes, show=True)
    ############### USED TO TEST AND DEBUG BY PRINTING THE RANDOM NODES RESULT ###############

    logger.info("All random nodes generated : " + str(random_nodes))
    logger.info("End of random_nodes")
    return random_nodes

import argparse
import osmnx as ox
import networkx as nx
import matplotlib.pyplot as plt
import random
import heapq


def random_nodes():
    # TEST DATA
    parser = argparse.ArgumentParser()
    # generation distance for the graph
    parser.add_argument("--dist", type=int, required=True)
    # Type of roads accepted in the graph
    parser.add_argument("--roads", type=lambda x: x.split(','), required=True)
    # middle coordinates of the two points
    parser.add_argument("--coords", type=lambda x: tuple(map(float, x.split(','))), required=True)
    # Parse the command-line arguments
    args = parser.parse_args()

    dist = args.dist
    roads = args.roads
    coords = args.coords

    # motorway,trunk,primary,secondary,tertiary,residential,service
    cf = '["highway"~"' + '|'.join(roads) + '"]'

    # create the graph
    G = ox.graph_from_point(coords, dist, network_type='drive', simplify=True, custom_filter=cf)
    # nodes, edges = ox.graph_to_gdfs(G, nodes=True, edges=True)
    # print(nodes.to_json())

    # define the number of the shortest paths to plot
    n_shortest_paths = 20
    # define a list of colors to use for each path
    colors = ['#FF0000', '#00FF00', '#0000FF', '#FFFF00', '#00FFFF', '#FF00FF', '#800000', '#008000', '#000080',
              '#FFA500', '#808080', '#800080', '#008080', '#FFC0CB', '#00FF7F', '#7FFFD4', '#F5DEB3', '#D2691E',
              '#800000', '#2F4F4F', '#000000']
    # initialize a list to store the shortest paths
    shortest_paths = []

    x1 = 2.63797
    y1 = 48.83106
    x2 = 2.5508
    y2 = 48.8364
    dist = 0.0075
    # Find the two nearest nodes to the first coordinate
    source_chosen = ox.distance.nearest_nodes(G, x1, y1)
    # Find the two nearest nodes to the second coordinate
    target_chosen = ox.distance.nearest_nodes(G, x2, y2)
    # add the shortest path for the 2 chosen nodes to the list
    shortest_path = nx.shortest_path(G, source=source_chosen, target=target_chosen, weight='length')
    # add the shortest path to the list
    shortest_paths.append(shortest_path)

    # add the shortest paths between the 10 nearest nodes of the first coordinate and the 10 nearest nodes of the
    # second coordinate
    for i in range(n_shortest_paths):
        dx1 = random.uniform(-dist, dist)
        dy1 = random.uniform(-dist, dist)
        dx2 = random.uniform(-dist, dist)
        dy2 = random.uniform(-dist, dist)
        print(dx1, " | ", dy1, " | ", dx2, " | ", dy2, " | ")
        source_node = ox.distance.nearest_nodes(G, x1 + dx1, y1 + dy1)
        target_node = ox.distance.nearest_nodes(G, x2 + dx2, y2 + dy2)
        # find the shortest path between the source and target nodes
        shortest_path = nx.shortest_path(G, source=source_node, target=target_node, weight='length')
        # add the shortest path to the list
        shortest_paths.append(shortest_path)

    # Chose random nodes for the shortest path
    # for i in range(n_shortest_paths):
    # source, target = random.sample(nodes, 2)
    # find the shortest path between the source and target nodes
    # shortest_path = nx.shortest_path(G, source=source, target=target, weight='length')
    # add the shortest path to the list
    # shortest_paths.append(shortest_path)

    # plot the graph with all the shortest paths
    fig, ax = ox.plot_graph_routes(G, shortest_paths, route_colors=colors[:n_shortest_paths + 1], node_size=0)
    # show the plot
    plt.show()


random_nodes()

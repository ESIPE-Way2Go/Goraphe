import logging
import os
import osmnx as ox
import networkx as nx
import pandas as pd
import time
import pyproj
from shapely import LineString
import geopandas as gpd
import random_nodes

def get_nodes_geojson(graph, osmid_list):
    # Create a GeoDataFrame of the nodes in the graph that are in osmid_list
    nodes_gdf = ox.graph_to_gdfs(graph, edges=False)
    nodes_gdf = nodes_gdf[nodes_gdf.index.isin(osmid_list)]
    # Convert the GeoDataFrame to a GeoJSON
    nodes_geojson = nodes_gdf.to_crs("EPSG:4326").to_json()
    return nodes_geojson


def shortest_path_geojson(G, point1, point2, weight, logger):
    route = nx.shortest_path(G, point1, point2, weight=weight)
    nodes = set(route)
    logger.info("Number of edges in selected route : " + str(len(route)))
    edges = G.subgraph(nodes)
    features = gpd.GeoDataFrame(edges.edges(keys=True))
    features['geometry'] = features.apply(
        lambda x: LineString(
            [(G.nodes[x[0]]['lon'], G.nodes[x[0]]['lat']), (G.nodes[x[1]]['lon'], G.nodes[x[1]]['lat'])]),
        axis=1)
    features['osmid'] = features.apply(lambda x: G.edges[(x[0], x[1], x[2])].get('osmid', ''), axis=1)
    features['name'] = features.apply(lambda x: G.edges[(x[0], x[1], x[2])].get('name', ''), axis=1)
    features['maxspeed'] = features.apply(lambda x: G.edges[(x[0], x[1], x[2])].get('fixedmaxspeed', ''), axis=1)
    features['timetravel'] = features.apply(lambda x: G.edges[(x[0], x[1], x[2])].get('traveltimes', ''), axis=1)
    features['evi_local'] = features.apply(lambda x: G.edges[(x[0], x[1], x[2])].get('evi_local', ''), axis=1)

    return features.to_json()


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


################################COMPUTE SHORTEST PATH###################################
def compute(graph_proj, graph_not_proj, point1, point2, dist, user, sim, nbPoints):
    time_start = time.perf_counter()
    # Directory
    directory = "scripts/" + user + "/" + sim + "/json"
    excel_directory = "scripts/" + user + "/" + sim

    # Creation of logger
    LOG_FILENAME = os.getcwd() + "/scripts/" + user + "/" + sim + "/compute.log"
    logger = setup_logger(LOG_FILENAME, LOG_FILENAME)
    logger.info("Init of compute")
    time_start = time.perf_counter()

    edges_proj = ox.graph_to_gdfs(graph_proj, nodes=False, edges=True)
    graph_geojson = edges_proj.to_json()
    logger.info("GEOJSON CREATED")
    with open(directory + "/GRAPHE.geojson", 'w') as f:
        f.write(graph_geojson)

    # TODO vérifier si dans graph proj
    # source_node = ox.distance.nearest_nodes(graph_not_proj, point1[0], point1[1])
    # dest_node = ox.distance.nearest_nodes(graph_not_proj, point2[0], point2[1])
    wgs84_crs = pyproj.CRS("EPSG:4326")
    projected_crs = pyproj.CRS(str(graph_proj.graph["crs"]))
    transformer = pyproj.Transformer.from_crs(wgs84_crs, projected_crs)

    x, y = transformer.transform(point1[1], point1[0])
    source_node = ox.nearest_nodes(graph_proj, x, y)

    x, y = transformer.transform(point2[1], point2[0])
    dest_node = ox.nearest_nodes(graph_proj, x, y)

    final_rand_nodes = []
    final_results = dict([])
    final_results_counter = dict([])
    final_evi_local_dict = dict([])
    final_timetravel_shortest_paths = dict([])
    final_timetravel_shortest_paths_counter = dict([])
    final_essential_mw_edges = dict([])
    # TODO Début iteration

    nb_iteration = 2
    logger.info("Iterations loop beginning with " + str(nb_iteration))

    for index_iteration in range(nb_iteration):
        logger.info("Executing random node in iteration loop in compute.py")
        rand_nodes = random_nodes.random_nodes(graph_proj, source_node, dest_node, user, sim,
                                               dist, nbPoints)
        final_rand_nodes.extend(rand_nodes)
        rand_nodes_geojson = get_nodes_geojson(graph_proj, rand_nodes)
        with open(directory + "/ITERATION" + str(index_iteration) + "_randomNodes.geojson", 'w') as f:
            f.write(rand_nodes_geojson)


        # base shortest paths
        routes_traveltimes = dict([])
        timetravel_shortest_paths = dict([])
        length_SPtraveltime_paths = dict([])

        # shortest paths with edges removed
        routes_er = dict([])
        timetravel_shortest_paths_er = dict([])

        # motorway edges impossible to remove
        essential_mw_edges = dict([])

        # motorway edges affecting the shortest paths
        impactful_mw_edges = []

        # function converting [u, v, key] to an edge name - string "u_v_key"
        def edge_to_str(item):
            return str(item[0]) + "_" + str(item[1]) + "_" + str(item[2])


        for origin in random_nodes:

            routes_traveltimes[origin] = dict([])
            timetravel_shortest_paths[origin] = dict([])
            if origin not in final_timetravel_shortest_paths :
                final_timetravel_shortest_paths[origin] = dict([])
                final_timetravel_shortest_paths_counter[origin] = dict([])
            length_SPtraveltime_paths[origin] = dict([])
            essential_mw_edges[origin] = dict([])
            if origin not in final_essential_mw_edges :
                final_essential_mw_edges[origin] = dict([])
            logger.info("Beginning iterative remove of edges in the selected route")

            for destination in random_nodes:
                #TODO Je suis ici

                # Calculate the shortest path
                route_traveltimes = nx.shortest_path(graph_proj, source=origin, target=destination, weight='traveltimes')
                routes_traveltimes[origin][destination] = route_traveltimes

                # compute shortest path length
                vind = []
                for u, v in zip(route_traveltimes[:-1], route_traveltimes[1:]):
                    indexes = []
                    lengths = []
                    traveltimes = []
                    for j in range(len(edges_proj.index)):
                        if edges_proj.index[j][0] == u and edges_proj.index[j][1] == v:
                            indexes.append(j)
                            lengths.append(edges_proj.at[edges_proj.index[j], 'length'])
                            traveltimes.append(edges_proj.at[edges_proj.index[j], 'traveltimes'])
                    vind.append(edges_proj.index[indexes[lengths.index(min(lengths))]])
                roads_traveltimesSP = edges_proj.loc[vind]
                #
                det_edges_path_traveltimesSP = []
                for k in roads_traveltimesSP.index:
                    det_edges_path_traveltimesSP.append(
                        [roads_traveltimesSP.at[k, 'highway'], roads_traveltimesSP.at[k, 'traveltimes']])

                timetravel_shortest_path = sum(n for _, n in det_edges_path_traveltimesSP)
                timetravel_shortest_paths[origin][destination] = timetravel_shortest_path

                #
                indMW = []
                for k in roads_traveltimesSP.index:
                    if roads_traveltimesSP.at[k, 'highway'] == 'motorway':
                        indMW.append([k[0], k[1], k[2]])

                for item in indMW:

                    G6 = graph_proj.copy()

                    G6.remove_edge(item[0], item[1], item[2])

                    # Retrieve only edges from the new graph

                    # G6_proj = ox.project_graph(G6)
                    G6_proj = G6

                    # Get new Edges
                    edges_proj6 = ox.graph_to_gdfs(G6_proj, nodes=False, edges=True)

                    edge_name = edge_to_str(item)

                    # Calculate the shortest path
                    try:
                        route_traveltimes = nx.shortest_path(G6_proj, source=origin, target=destination, weight='traveltimes')
                        # add new route to dict, entry origin->destination
                        if (not edge_name in routes_er):
                            routes_er[edge_name] = dict([])
                        if (not origin in routes_er[edge_name]):
                            routes_er[edge_name][origin] = dict([])
                        routes_er[edge_name][origin][destination] = route_traveltimes

                        # Plot the shortest path
                        # fig, ax = ox.plot_graph_route(G6_proj, route_traveltimes, figsize=(30,30), bgcolor='w',show=False, save=True,
                        #                               close=True,filepath=r'C:\Users\silvi\Desktop\PostDoc_PANOPTIS\Complex Network_RI\new_script\Set_37\images\Shortest_path_er_'+ str(origin)+'_'+ str(destination)+'.png',
                        #                               node_color='#999999', node_size=1, node_alpha=1, node_edgecolor='none',
                        #                               node_zorder=1, edge_color='#999999', edge_linewidth=1, edge_alpha=1,
                        #                               route_color='r',route_linewidth=1, route_alpha=0.5,orig_dest_size=100)
                        vind = []
                        for u, v in zip(route_traveltimes[:-1], route_traveltimes[1:]):
                            indexes = []
                            lengths = []
                            traveltimes = []
                            for j in range(len(edges_proj6.index)):
                                if edges_proj6.index[j][0] == u and edges_proj6.index[j][1] == v:
                                    indexes.append(j)
                                    lengths.append(edges_proj6.at[edges_proj6.index[j], 'length'])
                                    traveltimes.append(edges_proj6.at[edges_proj6.index[j], 'traveltimes'])
                            vind.append(edges_proj6.index[indexes[lengths.index(min(lengths))]])
                        roads_traveltimesSP = edges_proj6.loc[vind]
                        #
                        det_edges_path_traveltimesSP = []
                        for k in roads_traveltimesSP.index:
                            det_edges_path_traveltimesSP.append(
                                [roads_traveltimesSP.at[k, 'highway'], roads_traveltimesSP.at[k, 'traveltimes']])

                        timetravel_shortest_path = sum(n for _, n in det_edges_path_traveltimesSP)

                        # add new timetravel to dict, entry origin->destination
                        if (not edge_name in timetravel_shortest_paths_er):
                            timetravel_shortest_paths_er[edge_name] = dict([])
                        if (not origin in timetravel_shortest_paths_er[edge_name]):
                            timetravel_shortest_paths_er[edge_name][origin] = dict([])
                        timetravel_shortest_paths_er[edge_name][origin][destination] = timetravel_shortest_path

                    except nx.NetworkXNoPath:
                        # add edge to dict, entry origin->destination
                        if (not origin in essential_mw_edges):
                            essential_mw_edges[origin] = dict([])
                        if (not destination in essential_mw_edges[origin]):
                            essential_mw_edges[origin][destination] = []
                        essential_mw_edges[origin][destination].append(item)

                for item in indMW:
                    if (not item in impactful_mw_edges):
                        impactful_mw_edges.append(item)

        # outputs
        results = dict([])

        ref_alpha_traveltimes = 0
        for key in timetravel_shortest_paths:
            ref_alpha_traveltimes += sum(timetravel_shortest_paths[key].values())
        results["Base alpha traveltimes"] = ref_alpha_traveltimes

        for edge in impactful_mw_edges:
            # used to calculate ratio over non broken paths in case of an essential edge
            alpha_traveltimes = ref_alpha_traveltimes
            beta_traveltimes = 0
            # numer of paths broken by the removal of this edge (i.e. paths where this edge is essential)
            nbp = 0
            nip = 0

            edge_name = edge_to_str(edge)

            # if (edge in impactful_mw_edges):
            for origin in random_nodes:
                for destination in random_nodes:
                    if (origin in essential_mw_edges and destination in essential_mw_edges[origin] and edge in
                            essential_mw_edges[origin][destination]):
                        nbp += 1
                        alpha_traveltimes -= timetravel_shortest_paths[origin][destination]
                    else:
                        if (edge_name in timetravel_shortest_paths_er and origin in timetravel_shortest_paths_er[
                            edge_name] and destination in timetravel_shortest_paths_er[edge_name][origin]):
                            nip += 1
                            beta_traveltimes += timetravel_shortest_paths_er[edge_name][origin][destination]
                        else:
                            beta_traveltimes += timetravel_shortest_paths[origin][destination]
            # else:
            #    beta_traveltimes = alpha_traveltimes

            ref_ratio_traveltimes = ((beta_traveltimes - ref_alpha_traveltimes) / ref_alpha_traveltimes) * 100
            evi_local = ((beta_traveltimes - alpha_traveltimes) / alpha_traveltimes) * 100
            evi_average_nip = ((beta_traveltimes - alpha_traveltimes) / nip)

            results[edge_name] = dict([])
            results[edge_name]["Broken paths"] = nbp
            results[edge_name]["Impacted paths"] = nip
            results[edge_name]["Beta traveltimes"] = beta_traveltimes
            results[edge_name]["traveltimes ratio (ref)"] = ref_ratio_traveltimes
            results[edge_name]["evi_local"] = evi_local
            results[edge_name]["evi_average_nip"] = evi_average_nip

        # transform results dicionary in dataframes to save as xlsx file
        df_Results = pd.DataFrame.from_dict(results, orient='columns')
        df_Results.to_excel("Set_extremenodes_EVIs_test.xlsx")

        df_Res_traveltimeSP = pd.DataFrame.from_dict(timetravel_shortest_paths, orient='index')
        df_Res_traveltimeSP.to_excel("Set_37_traveltimesSP_test.xlsx")

        df_essential_mw_edges = pd.DataFrame.from_dict(essential_mw_edges, orient='columns')
        df_essential_mw_edges.to_excel("Set_37_essential_mw_edges_test.xlsx")

    # calculate computational time
    time_elapsed = (time.perf_counter() - time_start)
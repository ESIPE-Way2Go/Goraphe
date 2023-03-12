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

#Creates a geojson from a list of nodes (used for the random nodes)
def get_nodes_geojson(graph, osmid_list):
    # Create a GeoDataFrame of the nodes in the graph that are in osmid_list
    nodes_gdf = ox.graph_to_gdfs(graph, edges=False)
    nodes_gdf = nodes_gdf[nodes_gdf.index.isin(osmid_list)]
    # Convert the GeoDataFrame to a GeoJSON
    nodes_geojson = nodes_gdf.to_crs("EPSG:4326").to_json()
    return nodes_geojson

#Creates a geojson for the shortest path (used for the selected route)
def shortest_path_geojson(G, point1, point2, weight, logger):
    route = nx.shortest_path(G, point1, point2, weight=weight)
    nodes = set(route)
    logger.info("Number of edges in selected route : " + str(len(route)))
    edges = G.subgraph(nodes)

    #Adding features to the geojson
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
    features['evi_average_nip'] = features.apply(lambda x: G.edges[(x[0], x[1], x[2])].get('evi_average_nip', ''), axis=1)
    features['impacted_paths'] = features.apply(lambda x: G.edges[(x[0], x[1], x[2])].get('impacted_paths', ''), axis=1)
    features['broken_paths'] = features.apply(lambda x: G.edges[(x[0], x[1], x[2])].get('broken_paths', ''), axis=1)
    features['beta_traveltimes'] = features.apply(lambda x: G.edges[(x[0], x[1], x[2])].get('beta_traveltimes', ''), axis=1)
    features['timetravel_ratio'] = features.apply(lambda x: G.edges[(x[0], x[1], x[2])].get('timetravel_ratio', ''), axis=1)

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


def compute(graph_proj, point1, point2, dist, user, sim, nbPoints,source_node,dest_node):
    time_start = time.perf_counter()
    #############################################BEGIN OF DON'T REMOVE#############################################
    ###############################################################################################################
    # Directory
    directory = "scripts/" + user + "/" + sim + "/json"
    excel_directory = "scripts/" + user + "/" + sim

    # Creation of logger
    LOG_FILENAME = os.getcwd() + "/scripts/" + user + "/" + sim + "/compute.log"
    logger = setup_logger(LOG_FILENAME, LOG_FILENAME)
    logger.info("Init of compute")

    #reprojecting the graph to the default crs of Open Street Map (to send the graph to the graphical interface)
    g_reproj = ox.projection.project_graph(graph_proj,to_crs="EPSG:4326")
    edges_reproj = ox.graph_to_gdfs(g_reproj, nodes=False, edges=True)
    graph_geojson = edges_reproj.to_json()
    logger.info("GEOJSON CREATED")
    with open(directory + "/GRAPHE.geojson", 'w') as f:
        f.write(graph_geojson)
    logger.info("Geojson graph printed")




    #dicts to add attributes to the graph that will be used to write geojson and excel files
    final_rand_nodes = []
    final_results = dict([])
    final_results["Base alpha traveltimes"] = 0
    final_results_counter = dict([])
    final_timetravel_shortest_paths = dict([])
    final_timetravel_shortest_paths_counter = dict([])
    final_essential_mw_edges = dict([])

    final_broken_paths_dict = dict([])
    final_impacted_paths_dict = dict([])
    final_beta_traveltimes_dict = dict([])
    final_traveltimes_ratio_dict = dict([])
    final_evi_local_dict = dict([])
    final_evi_average_nip_dict = dict([])

    #Setting the number of iterations for the simulation
    nb_iteration = 6
    logger.info("Iterations loop beginning with " + str(nb_iteration))

    # the selected route to analyze
    path_nodes = nx.shortest_path(graph_proj, source=source_node, target=dest_node)
    path_edges = list(nx.utils.pairwise(path_nodes))
    indMW = [[u, v] for u, v in path_edges]

    #loop to do each iteration
    for index_iteration in range(nb_iteration):
        logger.info("Executing random node in iteration loop in compute.py")

        #generating random nodes
        rand_nodes = random_nodes.random_nodes(graph_proj, source_node, dest_node, user, sim,
                                               dist, nbPoints)
        #adding random nodes to the list containing all the random nodes of all iterations
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
        ##############################################END OF DON'T REMOVE##############################################
        ###############################################################################################################
        def edge_to_str(item):
            return str(item[0]) + "_" + str(item[1])

        for origin in rand_nodes:

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
            for destination in rand_nodes:
                # Calculate the shortest path
                try:
                    route_traveltimes = nx.shortest_path(graph_proj, source=origin, target=destination,
                                                         weight='traveltimes')
                    routes_traveltimes[origin][destination] = route_traveltimes
                except nx.NetworkXNoPath:
                    logger.info("no route beetween :" + str(origin) + " and " + str(destination))


                timetravel_shortest_path = 0
                roads_traveltimesSP = dict()
                for u, v in zip(route_traveltimes[:-1], route_traveltimes[1:]):
                    timetravel_shortest_path += graph_proj.get_edge_data(u, v)[0]['traveltimes']
                    roads_traveltimesSP[(u, v)] = graph_proj.get_edge_data(u, v)

                logger.info("TIMETRAVEL SHORTEST PATH = "+str(timetravel_shortest_path))
                timetravel_shortest_paths[origin][destination] = timetravel_shortest_path
                if destination not in final_timetravel_shortest_paths[origin] :
                    final_timetravel_shortest_paths[origin][destination] = timetravel_shortest_path
                    final_timetravel_shortest_paths_counter[origin][destination] = 1
                else :
                    final_timetravel_shortest_paths[origin][destination] += timetravel_shortest_path
                    final_timetravel_shortest_paths_counter[origin][destination] += 1

                for item in indMW:
                    G6 = graph_proj.copy()
                    G6.remove_edge(item[0], item[1])
                    # Retrieve only edges from the new graph
                    G6_proj = G6
                    # Get new Edges
                    edge_name = edge_to_str(item)
                    # Calculate the shortest path
                    try:
                        route_traveltimes = nx.shortest_path(G6_proj, source=origin, target=destination,
                                                             weight='traveltimes')
                        if not edge_name in routes_er:
                            routes_er[edge_name] = dict([])
                        if not origin in routes_er[edge_name]:
                            routes_er[edge_name][origin] = dict([])
                        routes_er[edge_name][origin][destination] = route_traveltimes

                        timetravel_shortest_path = 0
                        roads_traveltimesSP = dict()
                        for u, v in zip(route_traveltimes[:-1], route_traveltimes[1:]):
                            timetravel_shortest_path += graph_proj.get_edge_data(u, v)[0]['traveltimes']
                            roads_traveltimesSP[(u, v)] = graph_proj.get_edge_data(u, v)

                        if edge_name not in timetravel_shortest_paths_er:
                            timetravel_shortest_paths_er[edge_name] = dict([])
                        if origin not in timetravel_shortest_paths_er[edge_name]:
                            timetravel_shortest_paths_er[edge_name][origin] = dict([])
                        timetravel_shortest_paths_er[edge_name][origin][destination] = timetravel_shortest_path

                    except nx.NetworkXNoPath:
                        # add edge to dict, entry origin->destination
                        if origin not in essential_mw_edges:
                            essential_mw_edges[origin] = dict([])
                        if origin not in final_essential_mw_edges:
                            final_essential_mw_edges[origin] = dict([])
                        if destination not in essential_mw_edges[origin]:
                            essential_mw_edges[origin][destination] = []
                        if destination not in final_essential_mw_edges[origin]:
                            final_essential_mw_edges[origin][destination] = []
                        essential_mw_edges[origin][destination].append(item)
                        final_essential_mw_edges[origin][destination].append(item)

                for item in indMW:
                    if item not in impactful_mw_edges:
                        impactful_mw_edges.append(item)

        logger.info("Finish iterative remove of edges in the selected route")
        # outputs
        results = dict([])

        # dicts to add attributes to the graph that will be used to write geojson
        broken_paths_dict = dict([])
        impacted_paths_dict = dict([])
        beta_traveltimes_dict = dict([])
        traveltimes_ratio_dict = dict([])
        evi_local_dict = dict([])
        evi_average_nip_dict = dict([])

        ref_alpha_traveltimes = 0

        logger.info("Beginning processing of alpha, beta for LoS")
        for key in timetravel_shortest_paths:
            ref_alpha_traveltimes += sum(timetravel_shortest_paths[key].values())
        results["Base alpha traveltimes"] = ref_alpha_traveltimes
        final_results["Base alpha traveltimes"] += ref_alpha_traveltimes

        for edge in impactful_mw_edges:
            # used to calculate ratio over non broken paths in case of an essential edge
            alpha_traveltimes = ref_alpha_traveltimes
            beta_traveltimes = 0
            # number of paths broken by the removal of this edge (i.e. paths where this edge is essential)
            nbp = 0
            nip = 0

            edge_name = edge_to_str(edge)
            for origin in rand_nodes:
                for destination in rand_nodes:
                    if (origin in essential_mw_edges and destination in essential_mw_edges[origin] and edge in
                            essential_mw_edges[origin][destination]):
                        nbp += 1
                        alpha_traveltimes -= timetravel_shortest_paths[origin][destination]
                    else:
                        if edge_name in timetravel_shortest_paths_er and origin in timetravel_shortest_paths_er[
                            edge_name] \
                                and destination in timetravel_shortest_paths_er[edge_name][origin]:
                            nip += 1
                            beta_traveltimes += timetravel_shortest_paths_er[edge_name][origin][destination]
                        else:
                            beta_traveltimes += timetravel_shortest_paths[origin][destination]
            ref_ratio_traveltimes = ((beta_traveltimes - ref_alpha_traveltimes) / ref_alpha_traveltimes) * 100
            evi_local = ((beta_traveltimes - alpha_traveltimes) / alpha_traveltimes) * 100
            evi_average_nip = ((beta_traveltimes - alpha_traveltimes) / nip)
            evi_local_dict[(edge[0], edge[1], 0)] = float(evi_local)

            results[edge_name] = dict([])
            results[edge_name]["Broken paths"] = nbp
            results[edge_name]["Impacted paths"] = nip
            results[edge_name]["Beta traveltimes"] = beta_traveltimes
            results[edge_name]["traveltimes ratio (ref)"] = ref_ratio_traveltimes
            results[edge_name]["evi_local"] = evi_local
            results[edge_name]["evi_average_nip"] = evi_average_nip
            #############################################BEGIN OF DON'T REMOVE#############################################
            ###############################################################################################################
            #adding values to each dict used to create geojson for each iteration
            broken_paths_dict[(edge[0], edge[1], 0)] = float(nbp)
            impacted_paths_dict[(edge[0], edge[1], 0)] = float(nip)
            beta_traveltimes_dict[(edge[0], edge[1], 0)] = float(beta_traveltimes)
            traveltimes_ratio_dict[(edge[0], edge[1], 0)] = float(ref_ratio_traveltimes)
            evi_local_dict[(edge[0], edge[1], 0)] = float(evi_local)
            evi_average_nip_dict[(edge[0], edge[1], 0)] = float(evi_average_nip)
            if (edge_name in final_results):
                final_results_counter[edge_name] += 1
                final_results[edge_name]["Broken paths"] += nbp
                final_results[edge_name]["Impacted paths"] += nip
                final_results[edge_name]["Beta traveltimes"] += beta_traveltimes
                final_results[edge_name]["traveltimes ratio (ref)"] += ref_ratio_traveltimes
                final_results[edge_name]["evi_local"] += evi_local
                final_results[edge_name]["evi_average_nip"] += evi_average_nip

                final_broken_paths_dict[(edge[0], edge[1], 0)] += float(nbp)
                final_impacted_paths_dict[(edge[0], edge[1], 0)] += float(nip)
                final_beta_traveltimes_dict[(edge[0], edge[1], 0)] += float(beta_traveltimes)
                final_traveltimes_ratio_dict[(edge[0], edge[1], 0)] += float(ref_ratio_traveltimes)
                final_evi_local_dict[(edge[0], edge[1], 0)] += float(evi_local)
                final_evi_average_nip_dict[(edge[0], edge[1], 0)] += float(evi_average_nip)
            else:
                final_results_counter[edge_name] = 1
                final_results[edge_name] = dict([])
                final_results[edge_name]["Broken paths"] = nbp
                final_results[edge_name]["Impacted paths"] = nip
                final_results[edge_name]["Beta traveltimes"] = beta_traveltimes
                final_results[edge_name]["traveltimes ratio (ref)"] = ref_ratio_traveltimes
                final_results[edge_name]["evi_local"] = evi_local
                final_results[edge_name]["evi_average_nip"] = evi_average_nip

                final_broken_paths_dict[(edge[0], edge[1], 0)] = float(nbp)
                final_impacted_paths_dict[(edge[0], edge[1], 0)] = float(nip)
                final_beta_traveltimes_dict[(edge[0], edge[1], 0)] = float(beta_traveltimes)
                final_traveltimes_ratio_dict[(edge[0], edge[1], 0)] = float(ref_ratio_traveltimes)
                final_evi_local_dict[(edge[0], edge[1], 0)] = float(evi_local)
                final_evi_average_nip_dict[(edge[0], edge[1], 0)] = float(evi_average_nip)

        logger.info("Finish processing of alpha, beta for LoS")

        #if the file excel file is already created use mode 'a' to append data, if it isn't created use mode 'w' to create it
        #adding each iteration as worksheets in the excel file
        excel_writer_mode = 'a'
        if(index_iteration==0):
            excel_writer_mode = 'w'
        # Create or load the Excel file and transform results dictionary in dataframes to save as xlsx file
        with pd.ExcelWriter(excel_directory + "/extremenodes_EVIs.xlsx", mode=excel_writer_mode) as writer:
            # Create a new sheet in the file with the results
            df_Results = pd.DataFrame.from_dict(results, orient='columns')
            df_Results.to_excel(writer, sheet_name="Iteration_"+str(index_iteration))

        with pd.ExcelWriter(excel_directory +"/traveltimesSP.xlsx", mode=excel_writer_mode) as writer:
            # Create a new sheet in the file with the travel times
            df_Res_traveltimeSP = pd.DataFrame.from_dict(timetravel_shortest_paths, orient='index')
            df_Res_traveltimeSP.to_excel(writer, sheet_name="Iteration_"+str(index_iteration))

        with pd.ExcelWriter(excel_directory +"/essential_mw_edges.xlsx", mode=excel_writer_mode) as writer:
            # Create a new sheet in the file with the essential MW edges
            df_essential_mw_edges = pd.DataFrame.from_dict(essential_mw_edges, orient='columns')
            df_essential_mw_edges.to_excel(writer, sheet_name="Iteration_"+str(index_iteration))

        #setting attributes for each iteration to the graph (used to create geojson)
        nx.set_edge_attributes(graph_proj, broken_paths_dict, "broken_paths")
        nx.set_edge_attributes(graph_proj, impacted_paths_dict, "impacted_paths")
        nx.set_edge_attributes(graph_proj, beta_traveltimes_dict, "beta_traveltimes")
        nx.set_edge_attributes(graph_proj, traveltimes_ratio_dict, "timetravel_ratio")
        nx.set_edge_attributes(graph_proj, evi_local_dict, "evi_local")
        nx.set_edge_attributes(graph_proj, evi_average_nip_dict, "evi_average_nip")

        #creating geojson of the selected route using the data that was append to the graph
        selected_route_geojson = shortest_path_geojson(graph_proj, source_node, dest_node, 'traveltimes', logger)
        with open(directory + "/ITERATION" + str(index_iteration) + "_roadSelected.geojson", 'w') as f:
            f.write(selected_route_geojson)

    logger.info("Iterations loop finished")

    #dividing the final data by each time that an edge was used for the final result of all iterations
    for edge_name, counter in final_results_counter.items():
        final_results[edge_name]["Broken paths"] /= counter
        final_results[edge_name]["Impacted paths"] /= counter
        final_results[edge_name]["Beta traveltimes"] /= counter
        final_results[edge_name]["traveltimes ratio (ref)"] /= counter
        final_results[edge_name]["evi_local"] = evi_local
        final_results[edge_name]["evi_average_nip"] /= counter
        splited = edge_name.split("_", 2)
        final_broken_paths_dict[(float(splited[0]), float(splited[1]), 0)] /= counter
        final_impacted_paths_dict[(float(splited[0]), float(splited[1]), 0)] /= counter
        final_beta_traveltimes_dict[(float(splited[0]), float(splited[1]), 0)] /= counter
        final_traveltimes_ratio_dict[(float(splited[0]), float(splited[1]), 0)] /= counter
        final_evi_local_dict[(float(splited[0]), float(splited[1]), 0)] /= counter
        final_evi_average_nip_dict[(float(splited[0]), float(splited[1]), 0)] /= counter
    final_results["Base alpha traveltimes"] /= nb_iteration

    for origin in final_timetravel_shortest_paths_counter:
        for destination in final_timetravel_shortest_paths_counter[origin]:
            final_timetravel_shortest_paths[origin][destination] /= final_timetravel_shortest_paths_counter[origin][destination]

    # Create or load the Excel file and transform results dictionary in dataframes to save as xlsx file
    with pd.ExcelWriter(excel_directory + "/extremenodes_EVIs.xlsx", mode=excel_writer_mode) as writer:
        # Create a new sheet in the file with the results
        df_Results = pd.DataFrame.from_dict(final_results, orient='columns')
        df_Results.to_excel(writer, sheet_name="Final")

    with pd.ExcelWriter(excel_directory +"/traveltimesSP.xlsx", mode=excel_writer_mode) as writer:
        # Create a new sheet in the file with the travel times
        df_Res_traveltimeSP = pd.DataFrame.from_dict(final_timetravel_shortest_paths, orient='index')
        df_Res_traveltimeSP.to_excel(writer, sheet_name="Final")

    with pd.ExcelWriter(excel_directory +"/essential_mw_edges.xlsx", mode=excel_writer_mode) as writer:
        # Create a new sheet in the file with the essential MW edges
        df_essential_mw_edges = pd.DataFrame.from_dict(final_essential_mw_edges, orient='columns')
        df_essential_mw_edges.to_excel(writer, sheet_name="Final")

    rand_nodes_geojson = get_nodes_geojson(graph_proj, final_rand_nodes)
    with open(directory + "/FINAL_randomNodes.geojson", 'w') as f:
        f.write(rand_nodes_geojson)

    #set attributes on the graph using the final results
    nx.set_edge_attributes(graph_proj, final_broken_paths_dict, "broken_paths")
    nx.set_edge_attributes(graph_proj, final_impacted_paths_dict, "impacted_paths")
    nx.set_edge_attributes(graph_proj, final_beta_traveltimes_dict, "beta_traveltimes")
    nx.set_edge_attributes(graph_proj, final_traveltimes_ratio_dict, "timetravel_ratio")
    nx.set_edge_attributes(graph_proj, final_evi_local_dict, "evi_local")
    nx.set_edge_attributes(graph_proj, final_evi_average_nip_dict, "evi_average_nip")


    selected_route_geojson = shortest_path_geojson(graph_proj, source_node, dest_node, 'traveltimes', logger)
    with open(directory + "/FINAL_roadSelected.geojson", 'w') as f:
        f.write(selected_route_geojson)
    ##############################################END OF DON'T REMOVE##############################################
    ###############################################################################################################
    # calculate computational time
    time_elapsed = (time.perf_counter() - time_start)
    logger.info("Computing time : " + str(time_elapsed))
    logger.info("End")

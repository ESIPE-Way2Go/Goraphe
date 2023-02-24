import logging
import os

import networkx as nx
import pandas as pd
import time
import openpyxl #Shows as not used but is needed to export .xlsx files

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

def compute(graph_proj,rand_nodes,source_node,dest_node,user,sim):
    # Creation of logger
    os.makedirs("scripts/" + user, exist_ok=True)
    LOG_FILENAME = os.getcwd() + "/scripts/" + user + "/" + sim + "_3.log"
    logger = setup_logger(LOG_FILENAME, LOG_FILENAME)
    logger.info("Init of compute")
    time_start = time.perf_counter()
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

    path_nodes = nx.shortest_path(graph_proj, source=source_node, target=dest_node)
    path_edges = list(nx.utils.pairwise(path_nodes))
    # notre route selectionnée
    indMW = [[u, v] for u, v in path_edges]

    def edge_to_str(item):
        return str(item[0]) + "_" + str(item[1])
    for origin in rand_nodes:
        routes_traveltimes[origin] = dict([])
        timetravel_shortest_paths[origin] = dict([])
        length_SPtraveltime_paths[origin] = dict([])
        essential_mw_edges[origin] = dict([])
        for destination in rand_nodes:
            # Calculate the shortest path
            # TODO attention des fois aucun trajet trouvé entre source et target    raise nx.NetworkXNoPath(f"No path between {source} and {target}.")      networkx.exception.NetworkXNoPath: No path between 686687464 and 259190165.
            try:
                route_traveltimes = nx.shortest_path(graph_proj, source=origin, target=destination, weight='traveltimes')
                routes_traveltimes[origin][destination] = route_traveltimes
            except nx.NetworkXNoPath:
                logger.info("no route beetween :"+str(origin)+" and "+str(destination))

            timetravel_shortest_path = 0
            roads_traveltimesSP = dict()
            for u, v in zip(route_traveltimes[:-1], route_traveltimes[1:]):
                timetravel_shortest_path += graph_proj.get_edge_data(u, v)[0]['traveltimes']
                roads_traveltimesSP[(u, v)] = graph_proj.get_edge_data(u, v)

            timetravel_shortest_paths[origin][destination] = timetravel_shortest_path

            for item in indMW:
                G6 = graph_proj.copy()
                G6.remove_edge(item[0],item[1])
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
                    for u, v in zip(route_traveltimes[:-1], route_traveltimes[1:]):
                        timetravel_shortest_path = 0
                        roads_traveltimesSP = dict()
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
                    if destination not in essential_mw_edges[origin]:
                        essential_mw_edges[origin][destination] = []
                    essential_mw_edges[origin][destination].append(item)

            for item in indMW:
                if item not in impactful_mw_edges:
                    impactful_mw_edges.append(item)
        # outputs
    results = dict([])
    evi_local_dict = dict([])
    ref_alpha_traveltimes = 0
    for key in timetravel_shortest_paths:
        ref_alpha_traveltimes += sum(timetravel_shortest_paths[key].values())
    results["Base alpha traveltimes"] = ref_alpha_traveltimes
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


        evi_local_dict[(edge[0], edge[1], 0)] = float(evi_local)
        evi_average_nip = ((beta_traveltimes - alpha_traveltimes) / nip)

        results[edge_name] = dict([])
        results[edge_name]["Broken paths"] = nbp
        results[edge_name]["Impacted paths"] = nip
        results[edge_name]["Beta traveltimes"] = beta_traveltimes
        results[edge_name]["traveltimes ratio (ref)"] = ref_ratio_traveltimes
        results[edge_name]["evi_local"] = evi_local
        results[edge_name]["evi_average_nip"] = evi_average_nip

    # transform results dictionary in dataframes to save as xlsx file

    # df_Results = pd.DataFrame.from_dict(results, orient='columns')
    # df_Results.to_excel("Set_extremenodes_EVIs_test1.xlsx")
    # df_Res_traveltimeSP = pd.DataFrame.from_dict(timetravel_shortest_paths, orient='index')
    # df_Res_traveltimeSP.to_excel("Set_37_traveltimesSP_test1.xlsx")
    # df_essential_mw_edges = pd.DataFrame.from_dict(essential_mw_edges, orient='columns')
    # df_essential_mw_edges.to_excel("Set_37_essential_mw_edges_test1.xlsx")

    nx.set_edge_attributes(graph_proj, evi_local_dict, "evi_local")

    # calculate computational time
    time_elapsed = (time.perf_counter() - time_start)
    logger.info("Computing time : " + str(time_elapsed))
    logger.info("End of compute")
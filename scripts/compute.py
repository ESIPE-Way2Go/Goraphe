import logging

import osmnx as ox
import networkx as nx
import geopandas as gpd
import matplotlib.pyplot as plt
import pandas as pd
import math
import time
import openpyxl
import random_nodes


def compute(graph_proj):
    rand_nodes = random_nodes.random_nodes();
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

    def edge_to_str(item):
        return str(item[0]) + "_" + str(item[1]) + "_" + str(item[2])

    for origin in rand_nodes:
        routes_traveltimes[origin] = dict([])
        timetravel_shortest_paths[origin] = dict([])
        length_SPtraveltime_paths[origin] = dict([])
        essential_mw_edges[origin] = dict([])
        for destination in rand_nodes:
            # Calculate the shortest path
            # TODO
            route_traveltimes = nx.shortest_path(graph_proj, source=origin, target=destination, weight='traveltimes')
            routes_traveltimes[origin][destination] = route_traveltimes
            timetravel_shortest_path = 0
            roads_traveltimesSP = dict()
            for u, v in zip(route_traveltimes[:-1], route_traveltimes[1:]):
                print(graph_proj.get_edge_data(u, v)[0])
                timetravel_shortest_path += graph_proj.get_edge_data(u, v)[0]['traveltimes']
                roads_traveltimesSP[(u, v)] = graph_proj.get_edge_data(u, v)
            timetravel_shortest_paths[origin][destination] = timetravel_shortest_path
            indMW = []
            for k, v in roads_traveltimesSP.items():
                if v[0]['highway'] == 'motorway':
                    indMW.append(k)
            for k in indMW:
                G6 = graph_proj.copy()
                G6.remove_edge(*k)
                # Retrieve only edges from the new graph
                G6_proj = G6
                # Get new Edges
                edge_name = edge_to_str(item)
                # Calculate the shortest path
                try:
                    route_traveltimes = nx.shortest_path(G=G6_proj, source=origin, target=destination,
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

            results[edge_name] = dict([])
            results[edge_name]["Broken paths"] = nbp
            results[edge_name]["Impacted paths"] = nip
            results[edge_name]["Beta traveltimes"] = beta_traveltimes
            results[edge_name]["traveltimes ratio (ref)"] = ref_ratio_traveltimes
            results[edge_name]["evi_local"] = evi_local
            results[edge_name]["evi_average_nip"] = evi_average_nip
        # transform results dicionary in dataframes to save as xlsx file
        # TODO add return of graph and indices de résilience then see if we want to export excel using python or java
        df_Results = pd.DataFrame.from_dict(results, orient='columns')
        df_Results.to_excel("Set_extremenodes_EVIs_test1.xlsx")
        df_Res_traveltimeSP = pd.DataFrame.from_dict(timetravel_shortest_paths, orient='index')
        df_Res_traveltimeSP.to_excel("Set_37_traveltimesSP_test1.xlsx")
        df_essential_mw_edges = pd.DataFrame.from_dict(essential_mw_edges, orient='columns')
        df_essential_mw_edges.to_excel("Set_37_essential_mw_edges_test1.xlsx")

        # calculate computational time
        time_elapsed = (time.perf_counter() - time_start)
        logging.INFO("Computing time : " + time_elapsed)
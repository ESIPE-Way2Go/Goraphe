import osmnx as ox
import networkx as nx
import geopandas as gpd
import matplotlib.pyplot as plt
import pandas as pd
import math
import time


#################SELECTION OF THE ROADNETWORK#######################################################

# create a network from multiple places
places = ['Torija, Spain',
          'Trijueque,Spain',
          'Muduex, Spain',
          'Gajanejos, Spain',
          'Ledanca, Spain',
          'Argecilla, Spain',
          'Almadrones, Spain',
          'Mandayona, Spain',
          'Mirabueno, Spain',
          'Algora, Spain',
          'Torremocha del Campo, Spain',
          'Sauca, Spain',
          'Alcolea del pinar, Spain']

gdf=ox.geocode_to_gdf(places)
area = ox.project_gdf(gdf).unary_union.area


G = ox.graph_from_place(places, network_type='drive', simplify=False, buffer_dist=10000)
G_proj = ox.project_graph(G)
#fig, ax = ox.plot_graph(G_proj,fig_height=30,margin=0.02,
#                        show=False, save=True, close=True,
#                        file_format='png', filename='A2-Network Graph',
#                        dpi=300)

print("Nodes of graph:")
print(G.number_of_nodes())

print("Edges of graph:")
print(G.number_of_edges())

#################SIMPLIFY THE ROADNETWORK#######################################################

# simplify the network keeping the endpoints
G2 = G_proj.copy()
G2 = ox.simplify_graph(G2, strict=False)

# check the updated number of nodes/edges of simplified graph
print("Nodes of graph:")
print(G2.number_of_nodes())

print("Edges of graph:")
print(G2.number_of_edges())


# list edges according selected attribute

MW_edges=[(u,v,k,d) for u,v,k,d in G2.edges(keys=True, data=True) if  d['highway']=='motorway']
TR_edges=[(u,v,k,d) for u,v,k,d in G2.edges(keys=True, data=True) if  d['highway']=='trunk']
PRI_edges=[(u,v,k,d) for u,v,k,d in G2.edges(keys=True, data=True) if  d['highway']=='primary']
SEC_edges=[(u,v,k,d) for u,v,k,d in G2.edges(keys=True, data=True) if  d['highway']=='secondary']
TER_edges=[(u,v,k,d) for u,v,k,d in G2.edges(keys=True, data=True) if  d['highway']=='tertiary']
U_edges=[(u,v,k,d) for u,v,k,d in G2.edges(keys=True, data=True) if  d['highway']=='unclassified']
RES_edges=[(u,v,k,d) for u,v,k,d in G2.edges(keys=True, data=True) if  d['highway']=='residential']
LIVS_edges=[(u,v,k,d) for u,v,k,d in G2.edges(keys=True, data=True) if  d['highway']=='living_street']

MWL_edges=[(u,v,k,d) for u,v,k,d in G2.edges(keys=True, data=True) if  d['highway']=='motorway_link']
TRL_edges=[(u,v,k,d) for u,v,k,d in G2.edges(keys=True, data=True) if  d['highway']=='trunk_link']
PRIL_edges=[(u,v,k,d) for u,v,k,d in G2.edges(keys=True, data=True) if  d['highway']=='primary_link']
SECL_edges=[(u,v,k,d) for u,v,k,d in G2.edges(keys=True, data=True) if  d['highway']=='secondary_link']
TERL_edges=[(u,v,k,d) for u,v,k,d in G2.edges(keys=True, data=True) if  d['highway']=='tertiary_link']

remove_edges= RES_edges+LIVS_edges

# remove edges from the network
G3 = G2.copy()
G3.remove_edges_from(remove_edges)

#fig, ax = ox.plot_graph(G3,figsize=(30,30), bgcolor='w',
                        #show=False, save=True, close=True,
                        #filepath=r"C:\Users\silvi\Desktop\PostDoc_PANOPTIS\Complex Network_RI\new_script\Set_37\images\A2-Network_Graph_edges removed.png",
                        #node_color='b', node_edgecolor='k', node_size=30, node_zorder=3)

# remove nodes with degree=0 (no edges) from the network
n_degree=[G3.degree()]#list of degrees of all network's nodes
solitary=[ n for n,d in G3.degree() if d==0 ]

G4=G3.copy()
G4.remove_nodes_from(solitary)

# Generate connected components and select the largest:
largest = max(nx.strongly_connected_components(G4), key=len)

# Create a subgraph of G consisting only of this component:
G5 = G4.subgraph(largest)

# check the updated number of nodes/edges of simplified graph
print("Nodes of graph:")
print(G5.number_of_nodes())

print("Edges of graph:")
print(G5.number_of_edges())
#
#fig, ax = ox.plot_graph(G5,fig_height=30,show=False, save=True, close=True,
#                        file_format='png', filename='A2-Network Graph_simplified',
#                        dpi=300, node_color='b', node_edgecolor='k',
#                        node_size=30, node_zorder=3, use_geom=True)

#fig, ax = ox.plot_graph(G3,figsize=(30,30), bgcolor='w',
 #                       show=False, save=True, close=True,
#                        filepath=r"C:\Users\silvi\Desktop\PostDoc_PANOPTIS\Complex Network_RI\new_script\Set_37\images\A2-Network_Graph_simplified.png",
 #                       node_color='b', node_edgecolor='k', node_size=30, node_zorder=3)
################################ADD EDGE ATTRIBUTE###################################
graph=G5
graph_proj = graph

# Get Edges and Nodes
nodes_proj, edges_proj = ox.graph_to_gdfs(graph_proj, nodes=True, edges=True)

#base speeds if nan, maximum speed values according to edges classification
max_speed_MW_edges=120/3.6
max_speed_TR_edges=100/3.6
max_speed_PRI_edges=90/3.6
max_speed_SEC_edges=90/3.6
max_speed_TER_edges=90/3.6
max_speed_U_edges=50/3.6
max_speed_R_edges=50/3.6

max_speed_MWL_edges=120/3.6
max_speed_TRL_edges=50/3.6
max_speed_PRIL_edges=40/3.6
max_speed_SECL_edges=40/3.6
max_speed_TERL_edges=40/3.6

#new attribute list
traveltimes = dict([])
fixedmaxspeed = dict([])
for k in edges_proj.index:
    u = k[0]
    v = k[1]
    key = k[2]
    if math.isnan(float(edges_proj.at[k,'maxspeed'])):
        if edges_proj.at[k,'highway'] == 'motorway':
            fixedmaxspeed[(u,v,key)] = max_speed_MW_edges
        elif edges_proj.at[k,'highway'] == 'trunk':
            fixedmaxspeed[(u,v,key)] = max_speed_TR_edges
        elif edges_proj.at[k,'highway'] == 'primary':
            fixedmaxspeed[(u,v,key)] = max_speed_PRI_edges
        elif edges_proj.at[k,'highway'] == 'secondary':
            fixedmaxspeed[(u,v,key)] = max_speed_SEC_edges
        elif edges_proj.at[k,'highway'] == 'tertiary':
            fixedmaxspeed[(u,v,key)] = max_speed_TER_edges
        elif edges_proj.at[k,'highway'] == 'unclassified':
            fixedmaxspeed[(u,v,key)] = max_speed_U_edges
        elif edges_proj.at[k,'highway'] == 'motorway_link':
            fixedmaxspeed[(u,v,key)] = max_speed_MWL_edges
        elif edges_proj.at[k,'highway'] == 'trunk_link':
            fixedmaxspeed[(u,v,key)] = max_speed_TRL_edges
        elif edges_proj.at[k,'highway'] == 'primary_link':
            fixedmaxspeed[(u,v,key)] = max_speed_PRIL_edges
        elif edges_proj.at[k,'highway'] == 'secondary_link':
            fixedmaxspeed[(u,v,key)] = max_speed_SECL_edges
        elif edges_proj.at[k,'highway'] == 'tertiary_link':
            fixedmaxspeed[(u,v,key)] = max_speed_TERL_edges
        elif edges_proj.at[k,'highway'] == 'road':
            fixedmaxspeed[(u,v,key)] = max_speed_R_edges
        else:
            fixedmaxspeed[(u,v,key)] = 0
    else:
        fixedmaxspeed[(u,v,key)] = float(edges_proj.at[k,'maxspeed'])/3.6
    traveltimes[(u,v,key)] = float(edges_proj.at[k,'length'])/fixedmaxspeed[(u,v,key)]

nx.set_edge_attributes(graph_proj, fixedmaxspeed, 'fixedmaxspeed')
nx.set_edge_attributes(graph_proj, traveltimes, 'traveltimes')

################################COMPUTE SHORTEST PATH###################################

# Get Edges and Nodes
nodes_proj, edges_proj = ox.graph_to_gdfs(graph_proj, nodes=True, edges=True)


#list osmid number of origin-destination set nodes


random_nodes = [289455025,988941419,2093962711,3717146176,4448101620,4561569022]


#colour and size list to plot random nodes on the network graph
nc = ['b' if osmid in random_nodes
      else 'grey' for osmid in graph_proj.nodes()]

ns = [100 if osmid in random_nodes
      else 1 for osmid in graph_proj.nodes()]


#fig, ax = ox.plot_graph(graph_proj,figsize=(30,30), bgcolor='w',
 #                       show=False, save=True, close=True,
  #                      filepath=r"C:\Users\silvi\Desktop\PostDoc_PANOPTIS\Complex Network_RI\new_script\Set_37\images\Graph_random_nodes.png",
   #                     dpi=300, node_color=nc, node_edgecolor='grey',
    #                    node_size=ns, node_zorder=3)

time_start = time.perf_counter()

# base shortest paths
routes_traveltimes = dict([])
timetravel_shortest_paths = dict([])
length_SPtraveltime_paths = dict ([])

#shortest paths with edges removed
routes_er = dict([])
timetravel_shortest_paths_er = dict([])
length_SPtraveltime_paths_er = dict ([])

#motorway edges impossible to remove
essential_mw_edges = dict([])

#motorway edges affecting the shortest paths
impactful_mw_edges = []

#function converting [u, v, key] to an edge name - string "u_v_key"
def edge_to_str(item):
    return str(item[0])+"_"+str(item[1])+"_"+str(item[2])



for origin in random_nodes:

    routes_traveltimes       [origin] = dict([])
    timetravel_shortest_paths[origin] = dict([])
    length_SPtraveltime_paths[origin] = dict([])
    essential_mw_edges       [origin] = dict([])

    for destination in random_nodes:

        # Calculate the shortest path
        route_traveltimes = nx.shortest_path(G=graph_proj, source=origin, target=destination, weight='traveltimes')
        routes_traveltimes[origin][destination] = route_traveltimes


##         Plot the shortest path
#        fig, ax = ox.plot_graph_route(graph_proj, route_traveltimes, figsize=(30,30), bgcolor='w',show=False, save=True,
                              # close=True,filepath=r'C:\Users\silvi\Desktop\PostDoc_PANOPTIS\Complex Network_RI\new_script\Set_37\images\Shortest_path_'+ str(origin)+'_'+ str(destination)+'.png',
                              # node_color='#999999', node_size=1, node_alpha=1, node_edgecolor='none',
                              # node_zorder=1, edge_color='#999999', edge_linewidth=1, edge_alpha=1,
                              # route_color='r',route_linewidth=1, route_alpha=0.5,orig_dest_size=100)

        #compute shortest path length
        vind = []
        for u, v in zip(route_traveltimes[:-1], route_traveltimes[1:]):
            indexes = []
            lengths = []
            traveltimes=[]
            for j in range(len(edges_proj.index)):
                if edges_proj.index[j][0] == u and edges_proj.index[j][1] == v:
                    indexes.append(j)
                    lengths.append(edges_proj.at[edges_proj.index[j],'length'])
                    traveltimes.append(edges_proj.at[edges_proj.index[j],'traveltimes'])
            vind.append(edges_proj.index[indexes[lengths.index(min(lengths))]])
        roads_traveltimesSP=edges_proj.loc[vind]
        #
        det_edges_path_traveltimesSP=[]
        for k in roads_traveltimesSP.index:
            det_edges_path_traveltimesSP.append([roads_traveltimesSP.at[k,'highway'],roads_traveltimesSP.at[k,'traveltimes']])

        timetravel_shortest_path=sum(n for _, n in det_edges_path_traveltimesSP)
        timetravel_shortest_paths[origin][destination] = timetravel_shortest_path


        #PARTI OU ON RETIRE DES MORCEAUX DE CHEMINS APPAREMMENT
        indMW = []
        for k in roads_traveltimesSP.index:
            if roads_traveltimesSP.at[k,'highway'] == 'motorway':
                indMW.append([k[0],k[1],k[2]])

        for item in indMW:

            G6 = graph_proj.copy()

            G6.remove_edge(item[0],item[1],item[2])

            # Retrieve only edges from the new graph

            #G6_proj = ox.project_graph(G6)
            G6_proj = G6

            # Get new Edges
            edges_proj6 = ox.graph_to_gdfs(G6_proj, nodes=False, edges=True)

            edge_name = edge_to_str(item)
            print("FINISH!!!!!!!!!!!!!!!!!!")

            # Calculate the shortest path
            try:
                route_traveltimes = nx.shortest_path(G=G6_proj, source=origin, target=destination, weight='traveltimes')
                #add new route to dict, entry origin->destination
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
                    traveltimes=[]
                    for j in range(len(edges_proj6.index)):
                        if edges_proj6.index[j][0] == u and edges_proj6.index[j][1] == v:
                            indexes.append(j)
                            lengths.append(edges_proj6.at[edges_proj6.index[j],'length'])
                            traveltimes.append(edges_proj6.at[edges_proj6.index[j],'traveltimes'])
                    vind.append(edges_proj6.index[indexes[lengths.index(min(lengths))]])
                roads_traveltimesSP=edges_proj6.loc[vind]
                #
                det_edges_path_traveltimesSP=[]
                for k in roads_traveltimesSP.index:
                    det_edges_path_traveltimesSP.append([roads_traveltimesSP.at[k,'highway'],roads_traveltimesSP.at[k,'traveltimes']])

                timetravel_shortest_path=sum(n for _, n in det_edges_path_traveltimesSP)

                #add new timetravel to dict, entry origin->destination
                if (not edge_name in timetravel_shortest_paths_er):
                    timetravel_shortest_paths_er[edge_name] = dict([])
                if (not origin in timetravel_shortest_paths_er[edge_name]):
                    timetravel_shortest_paths_er[edge_name][origin] = dict([])
                timetravel_shortest_paths_er[edge_name][origin][destination] = timetravel_shortest_path

            except nx.NetworkXNoPath:
                #add edge to dict, entry origin->destination
                if (not origin in essential_mw_edges):
                    essential_mw_edges[origin] = dict([])
                if (not destination in essential_mw_edges[origin]):
                    essential_mw_edges[origin][destination] = []
                essential_mw_edges[origin][destination].append(item)

        for item in indMW:
            if (not item in impactful_mw_edges):
                impactful_mw_edges.append(item)

#outputs
results = dict([])

ref_alpha_traveltimes = 0
for key in timetravel_shortest_paths:
    ref_alpha_traveltimes += sum(timetravel_shortest_paths[key].values())
results["Base alpha traveltimes"] = ref_alpha_traveltimes

for edge in impactful_mw_edges:
    #used to calculate ratio over non broken paths in case of an essential edge
    alpha_traveltimes = ref_alpha_traveltimes
    beta_traveltimes = 0
    #numer of paths broken by the removal of this edge (i.e. paths where this edge is essential)
    nbp=0
    nip = 0

    edge_name = edge_to_str(edge)

 #if (edge in impactful_mw_edges):
    for origin in random_nodes:
        for destination in random_nodes:
            if (origin in essential_mw_edges and destination in essential_mw_edges[origin] and edge in essential_mw_edges[origin][destination]):
                nbp += 1
                alpha_traveltimes -= timetravel_shortest_paths[origin][destination]
            else:
                if (edge_name in timetravel_shortest_paths_er and origin in timetravel_shortest_paths_er[edge_name] and destination in timetravel_shortest_paths_er[edge_name][origin]):
                    nip+=1
                    beta_traveltimes += timetravel_shortest_paths_er[edge_name][origin][destination]
                else:
                    beta_traveltimes += timetravel_shortest_paths[origin][destination]
# else:
#    beta_traveltimes = alpha_traveltimes

    ref_ratio_traveltimes = ((beta_traveltimes-ref_alpha_traveltimes)/ref_alpha_traveltimes)*100
    evi_local = ((beta_traveltimes-alpha_traveltimes)/alpha_traveltimes)*100
    evi_average_nip = ((beta_traveltimes-alpha_traveltimes)/nip)

    results[edge_name] = dict([])
    results[edge_name]["Broken paths"] = nbp
    results[edge_name]["Impacted paths"] = nip
    results[edge_name]["Beta traveltimes"] = beta_traveltimes
    results[edge_name]["traveltimes ratio (ref)"] = ref_ratio_traveltimes
    results[edge_name]["evi_local"] = evi_local
    results[edge_name]["evi_average_nip"] = evi_average_nip

#transform results dicionary in dataframes to save as xlsx file
df_Results = pd.DataFrame.from_dict(results, orient='columns')
df_Results.to_excel("Set_extremenodes_EVIs_test.xlsx")

df_Res_traveltimeSP = pd.DataFrame.from_dict(timetravel_shortest_paths, orient='index')
df_Res_traveltimeSP.to_excel("Set_37_traveltimesSP_test.xlsx")

df_essential_mw_edges = pd.DataFrame.from_dict(essential_mw_edges, orient='columns')
df_essential_mw_edges.to_excel("Set_37_essential_mw_edges_test.xlsx")


#calculate computational time
time_elapsed = (time.perf_counter() - time_start)
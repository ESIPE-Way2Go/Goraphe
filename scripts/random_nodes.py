
def random_nodes():
    #esipe
    random_nodes = [4805558775, 2411236822, 2411236813, 10004464578, 250836394, 4805558771]

    #espagne
    #random_nodes = [289455025, 988941419, 2093962711, 3717146176, 4448101620, 4561569022]
    # colour and size list to plot random nodes on the network graph
    # nc = ['b' if osmid in random_nodes
    #       else 'grey' for osmid in graph_proj.nodes()]
    #
    # ns = [100 if osmid in random_nodes
    #       else 1 for osmid in graph_proj.nodes()]
    #
    # fig, ax = ox.plot_graph(graph_proj,figsize=(30,30), bgcolor='w',
    #                       show=False, save=True, close=True,
    #                      filepath=r"C:\Users\silvi\Desktop\PostDoc_PANOPTIS\Complex Network_RI\new_script\Set_37\images\Graph_random_nodes.png",
    #                     dpi=300, node_color=nc, node_edgecolor='grey',
    #                    node_size=ns, node_zorder=3)

    #########################################
    print("\nRandom_nodes : \n"+str(type(random_nodes))+"\n"+str(random_nodes))
    return random_nodes
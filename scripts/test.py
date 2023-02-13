import sys
import os
#import osmnx as ox
import logging
from datetime import datetime

# sys.argv[1] : user
# sys.argv[2] : simulation

# Build the directory of log
os.makedirs( "scripts/" + sys.argv[1], exist_ok=True)

LOG_FILENAME = os.getcwd()+ "/scripts/" + sys.argv[1] + "/" + sys.argv[2] + "_1.log";
logging.basicConfig(level=logging.DEBUG,
                    filename=LOG_FILENAME,
                    filemode="a+",
                    format='%(asctime)s - %(levelname)s - %(message)s')

logging.info("Commencement")

logging.debug("La fonction commence")

num = float(sys.argv[3])
result = num + 10
logging.debug("Change value")
a = 5
b = 0
try:
    x = a / b
except Exception as e:
    logging.error("Exception", exc_info=True)

print("LOG_FILENAME", LOG_FILENAME)
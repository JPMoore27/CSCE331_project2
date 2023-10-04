from datetime import date, timedelta
from random import randint
import numpy as np

orderFile = open("orders.csv", "w")
itemsFile = open("items.csv")
items = np.loadtxt(itemsFile, delimiter=",", skiprows=1)

file.write("orderID,items,time,name,takeOut,totalCost")
    
day = date(2022, 1, 1)

for i in range(365):
    idString = str(i)

    itemsString = ""
    #for i in randint(1,4):
        
    dateString = day.strftime("%Y-%m-%d")
    day += timedelta(days=1)

##LEAVING THIS UP IN CASE IT'S NEEDED LATER, BUT THIS WAS ABANDONED IN FAVOR OF USING CHATGPT

from datetime import date, timedelta
from random import randint
import numpy as np

orderFile = open("orders.csv", "w")
itemsFile = open("items.csv")
items = np.loadtxt(itemsFile, delimiter=",", skiprows=1)

file.write("orderID,items,time,name,takeOut,totalCost")
    
day = date(2022, 1, 1)

for i in range(365):
    
    numOrders = randint(30, 40)
    #peak day implementaion
    if(day == date(2022, 1, 11) or day == date(2022, 8, 23)):
        numOrders *= 4

    
    for j in range(numOrders)
        idString = str(i+j)
    
        for i in randint:

    dateString = day.strftime("%Y-%m-%d")
    day += timedelta(days=1)

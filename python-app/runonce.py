<<<<<<< HEAD
import mainloop
from firebase_admin import credentials, initialize_app
import firebase_service as fb
=======
import mainfile
from firebase_admin import credentials, initialize_app
import firebase_service as fb
import pandas as pd
from datetime import datetime
>>>>>>> d0fbcf65fac8304e7a8b1bed17500f95e858b421

cred = credentials.Certificate("firebaseadminkey.json")
initialize_app(cred, {'storageBucket': 'wropoznienia-a3395.appspot.com'})

<<<<<<< HEAD
response = mainloop.run()
if response:
    fb.upload("data/vehicles_data.csv")
=======
current_day_time = datetime.now()
trips_df = pd.read_csv("data/trips.txt")
stops_df = pd.read_csv("data/stops.txt")

response = mainfile.run(trips_df, stops_df, current_day_time)
if response:
    fb.upload("data/vehicles_data.csv")

# list1 = [1, 2, 3, 4, 5, 6]
# list2 = [1, 2, 5, 7]

# new_list = list(set(set(list1).difference(list2)).union(set(list2).difference(list1)))

# try:
#     new_list.remove(7)
# except:
#     pass

# print(new_list)
>>>>>>> d0fbcf65fac8304e7a8b1bed17500f95e858b421

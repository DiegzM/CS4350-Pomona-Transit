import sqlite3
import os

# Schema
'''
Trip ( TripNumber, StartLocationName, DestinationName)
TripOffering ( TripNumber, Date, ScheduledStartTime, ScheduledArrivalTime,
DriverName, BusID)
Bus ( BusID, Model,Year)
Driver( DriverName, DriverTelephoneNumber)
Stop (StopNumber, StopAddress)
ActualTripStopInfo (TripNumber, Date, ScheduledStartTime, StopNumber,
ScheduledArrivalTime, ActualStartTime, ActualArrivalTime, NumberOfPassengerIn,
NumberOf PassengerOut)
TripStopInfo ( TripNumber, StopNumber, SequenceNumber, DrivingTime)
'''

# Delete existing database file (recreate from scratch)
if os.path.exists('pomona_transit.db'):
    print("Removing old database file...")
    os.remove('pomona_transit.db')

# Create a new database connection
conn = sqlite3.connect('pomona_transit.db')
cursor = conn.cursor()

# ---- DATA CREATION ----- 

# Trip Table
cursor.execute('''
    CREATE TABLE Trip (
        TripNumber INTEGER PRIMARY KEY,
        StartLocationName TEXT,
        DestinationName TEXT
    )
''')

# TripOffering Table
cursor.execute('''
    CREATE TABLE TripOffering (
        TripNumber INTEGER,
        Date DATE,
        ScheduledStartTime TEXT,
        ScheduledArrivalTime TEXT,
        DriverName TEXT,
        BusID INTEGER,
        PRIMARY KEY (TripNumber, Date, ScheduledStartTime),
        FOREIGN KEY (TripNumber) REFERENCES Trip(TripNumber),
        FOREIGN KEY (DriverName) REFERENCES Driver(DriverName),
        FOREIGN KEY (BusID) REFERENCES Bus(BusID)
    )
''')

# Bus Data
cursor.execute('''
    CREATE TABLE Bus (
        BusID INTEGER PRIMARY KEY,
        Model TEXT,
        Year INTEGER
    )
''')

# Driver Data
cursor.execute('''
    CREATE TABLE Driver (
        DriverName TEXT PRIMARY KEY,
        DriverTelephoneNumber TEXT
    )
''')

# Stop Data
cursor.execute('''
    CREATE TABLE Stop (
        StopNumber INTEGER PRIMARY KEY,
        StopAddress TEXT
    )
''')

# ActualTripStopInfo Data
cursor.execute('''
    CREATE TABLE ActualTripStopInfo (
        TripNumber INTEGER,
        Date DATE,
        ScheduledStartTime TEXT,
        StopNumber INTEGER,
        ScheduledArrivalTime TEXT,
        ActualStartTime TEXT,
        ActualArrivalTime TEXT,
        NumberOfPassengerIn INTEGER,
        NumberOfPassengerOut INTEGER,
               
        PRIMARY KEY (TripNumber, Date, ScheduledStartTime, StopNumber),
        FOREIGN KEY (TripNumber, Date, ScheduledStartTime) REFERENCES TripOffering(TripNumber, Date, ScheduledStartTime),
        FOREIGN KEY (StopNumber) REFERENCES Stop(StopNumber)
    )
''')

# TripStopInfo Data
cursor.execute('''
    CREATE TABLE TripStopInfo (
        TripNumber INTEGER,
        StopNumber INTEGER,
        SequenceNumber INTEGER,
        DrivingTime INTEGER,
               
        PRIMARY KEY (TripNumber, StopNumber),
        FOREIGN KEY (TripNumber) REFERENCES Trip(TripNumber),
        FOREIGN KEY (StopNumber) REFERENCES Stop(StopNumber)
    )
''')

# ---- TEST ROWS ----- 
trip_data = [
    (1, 'Chino Hills', 'Mt. SAC'),
    (2, 'Narod', 'Temple/Diamond'),
    (3, 'Chino Hills', 'La Verne')
]

trip_offering_data = [
    (1, '2026-01-14', '08:00', '08:35', 'Jim Arlington', 101),
    (2, '2026-01-14', '09:30', '10:30', 'Dan Smith', 102),
    (3, '2026-01-14', '11:00', '12:00', 'Sara Castillo', 103)
]

bus_data = [
    (101, 'Ford Transit', 2018),
    (102, 'Chevrolet Express', 2020),
    (103, 'Mercedes-Benz Sprinter', 2019)
]

driver_data = [
    ('Jim Arlington', '909-999-2395'),
    ('Dan Smith', '840-314-4393'),
    ('Juan Martinez', '909-394-1963'),
    ('Sara Castillo', '909-555-0198')
]
stop_data = [
    (1, '123 Main St, Chino Hills, CA'),
    (2, '3012 W Temple Ave, Pomona, CA'),
    (3, '3801 W Temple Ave, Pomona, CA'),
    (4, '1100 N Grand Ave, Walnut, CA'),
    (5, '5650 Mission Blvd, Ontario, CA'),
    (6, '1195 E Mission Blvd, Pomona, CA'),
    (7, '101 W Mission Blvd, Pomona, CA'),
    (8, '3012 W Temple Ave, Pomona, CA'),
    (9, '123 Main St, Chino Hills, CA'),
    (10, '101 W Mission Blvd, Pomona, CA'),
    (11, '1908 N White Ave, Pomona, CA'),
    (12, '2252 D St, La Verne, CA')
]

actual_trip_stop_info_data = [
    (1, '2026-01-14', '08:00', 1, '08:10', '08:05', '08:12', 3, 0),
    (1, '2026-01-14', '08:00', 2, '08:20', '08:15', '08:22', 2, 1),
    (1, '2026-01-14', '08:00', 3, '08:30', '08:25', '08:32', 4, 0),
    (2, '2026-01-14', '09:30', 5, '09:50', '09:45', '09:52', 5, 0),
    (2, '2026-01-14', '09:30', 6, '10:10', '10:05', '10:12', 3, 2),
    (2, '2026-01-14', '09:30', 7, '10:30', '10:25', '10:32', 4, 1),
    (3, '2026-01-14', '11:00', 9, '11:20', '11:15', '11:22', 6, 0),
    (3, '2026-01-14', '11:00', 10, '11:40', '11:35', '11:42', 2, 3),
    (3, '2026-01-14', '11:00', 11, '12:00', '11:55', '12:02', 5, 1)
]
trip_stop_info_data = [
    (1, 1, 1, 5),
    (1, 2, 2, 10),
    (1, 3, 3, 10),
    (1, 4, 4, 10),
    (2, 5, 1, 15),
    (2, 6, 2, 15),
    (2, 7, 3, 15),
    (2, 8, 4, 15),
    (3, 9, 1, 20),
    (3, 10, 2, 10),
    (3, 11, 3, 10),
    (3, 12, 4, 20)
]

print("Inserting test data...")
cursor.executemany('INSERT INTO Trip VALUES (?, ?, ?)', trip_data)
cursor.executemany('INSERT INTO TripOffering VALUES (?, ?, ?, ?, ?, ?)', trip_offering_data)
cursor.executemany('INSERT INTO Bus VALUES (?, ?, ?)', bus_data)
cursor.executemany('INSERT INTO Driver VALUES (?, ?)', driver_data)
cursor.executemany('INSERT INTO Stop VALUES (?, ?)', stop_data)
cursor.executemany('INSERT INTO ActualTripStopInfo VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)', actual_trip_stop_info_data)
cursor.executemany('INSERT INTO TripStopInfo VALUES (?, ?, ?, ?)', trip_stop_info_data)

conn.commit()
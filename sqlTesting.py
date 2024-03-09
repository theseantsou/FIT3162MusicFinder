import mysql.connector
from mysql.connector import Error

try:
    # link to the database
    connection = mysql.connector.connect(
        host='23.94.123.109',        
        database='project',      
        user='project',          
        password='123'         
    )

    if connection.is_connected():
        db_Info = connection.get_server_info()
        print("Successfully connected to MySQL database. MySQL Server version: ", db_Info)

        # get all tables
        cursor = connection.cursor()
        cursor.execute("SHOW TABLES")
        tables = cursor.fetchall()

        # get all rows from all tables
        for (table_name,) in tables:
            print(f"\nContents of table {table_name}:")
            cursor.execute(f"SELECT * FROM {table_name}")
            rows = cursor.fetchall()
            for row in rows:
                print(row)

except Error as e:
    print("Error while connecting to MySQL", e)

finally:
    # close the connection
    if connection.is_connected():
        cursor.close()
        connection.close()
        print("\nMySQL connection is closed")

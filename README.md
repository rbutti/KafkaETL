# KafkaETL
README.md
Step 1 : Download the docker application and install it https://store.docker.com/editions/community/docker-ce-desktop-windows

Step 2: Start the Docker

Step 3: Open command prompt by going to the Start menu and searching for CMD

Step 4: Go to the project folder by running the following command in command prompt

cd ${PROJECT_DIRECTORY}\java-kafka-to-mysql-master

Run the following commands

c:> set COMPOSE_CONVERT_WINDOWS_PATHS=1 c:> docker-compose down && Docker-compose up -d cd:> docker ps

Sample output CONTAINER ID IMAGE COMMAND CREATED STATUS PORTS NAMES fcfc4628c1b6 wurstmeister/zookeeper "/bin/sh -c '/usr/sb…" 9 seconds ago Up 5 seconds 22/tcp, 2888/tcp, 3888/tcp, 0.0.0.0:2181->2181/tcp java-kafka-to-mysql_zookeeper_1 26675c1e59a1 wurstmeister/kafka:1.0.0 "start-kafka.sh" 9 seconds ago Up 4 seconds 0.0.0.0:9092->9092/tcp

Step 5: If the above command "docker ps" doesnt show the third entry for mysql then your system is not compatible to run docker mysql. Manually install mysql by going to the below URL

https://dev.mysql.com/downloads/

Step 6:(Only if Step 5 is executed) Open the Mysql workbench and run the file "init_schema.sql" present in the resource folder of the project. This will initialize the database

Step 7 : Right click on the event_input table created in Step 6 and choose the option "Table Data Import Wizard", provide the file "sampleData_Event_input.csv" present in the project

Step 8 : Import the project in eclipse as "Maven project" and run the following command

mvn clean install

Step 9 : Open the file "IntegrationSpec.java" and run it as "Java Application". You should see the data getting copied from "event_input" table to "event" table

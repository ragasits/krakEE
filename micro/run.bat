set KRAKEE_RUNTRADE=true
set KRAKEE_RUNCANDLE=false

java -jar payara-micro-5.2021.4.jar --logtofile .\log\krakee --noCluster --deploy krakEE-130.war --rootDir .\root

pause
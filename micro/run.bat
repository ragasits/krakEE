set KRAKEE_RUNTRADE=false
set KRAKEE_RUNCANDLE=true

java -jar payara-micro-5.2021.10.jar --logtofile .\log\krakee --noCluster --deploy krakEE-150.war --rootDir .\root

pause
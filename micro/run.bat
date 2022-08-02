set KRAKEE_RUNTRADE=false
set KRAKEE_RUNCANDLE=true

java -jar payara-micro-5.2022.2.jar --logtofile .\log\krakee --noCluster --deploy krakEE-160.war --rootDir .\root

pause
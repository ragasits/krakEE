set KRAKEE_RUNTRADE=false	
set KRAKEE_RUNCANDLE=true

java -jar payara-micro-5.2021.4.jar --logtofile .\log\krakee --noCluster --deploy krakEE-140.war --rootDir .\root

pause
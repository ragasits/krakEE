set KRAKEE_RUNTRADE=false
set KRAKEE_PROXYENABLED=false

set KRAKEE_RUNCANDLE=false

java -jar payara-micro-5.2021.1.jar --logtofile .\log\krakee --noCluster --deploy krakEE-120.war --rootDir .\root

pause
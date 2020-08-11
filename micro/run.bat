set KRAKEE_RUNTRADE=false
set KRAKEE_PROXYENABLED=false

set KRAKEE_RUNCANDLE=true

java -jar payara-micro-5.2020.3.jar --logtofile .\log\krakee --noCluster --deploy krakEE-90.war --rootDir .\root

pause
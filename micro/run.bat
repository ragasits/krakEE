set KRAKEE_RUNTRADE=false
set KRAKEE_PROXYENABLED=true

set KRAKEE_RUNCANDLE=false

java -jar payara-micro-5.201.jar --logtofile .\log\krakee --noCluster --deploy krakEE-60.war
pause
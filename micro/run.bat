set KRAKEE_RUNTRADE=false
set KRAKEE_PROXYENABLED=true

set KRAKEE_RUNCANDLE=true

java -jar payara-micro-5.194.jar --logtofile .\log\krakee --noCluster --deploy krakEE-60.war
pause
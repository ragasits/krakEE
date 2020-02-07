set KRAKEE_RUNTRADE=false
set KRAKEE_PROXYENABLED=false

set KRAKEE_RUNCANDLE=true

java -jar payara-micro-5.194.jar --logtofile .\log\krakee --noCluster --deploy krakEE-50.war
pause
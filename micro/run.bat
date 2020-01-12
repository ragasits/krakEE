set KRAKEE_RUNTRADE=false
set KRAKEE_PROXYENABLED=false

java -jar payara-micro-5.194.jar --logtofile .\log\krakee --noCluster --deploy krakEE-40.war
pause
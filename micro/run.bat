set KRAKEE_RUNTRADE=true
set KRAKEE_PROXYENABLED=false

java -jar payara-micro-5.194.jar --logtofile .\log --noCluster --deploy krakEE-40.war
pause
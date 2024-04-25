# Contents

This folder/package contains all the necessary classes/functionality for
querying for the machine's public ip address.

## IP_Getter

IP_Getter is class or a "structure" which only contains other classes/records
which can be used for forming an IP_Getter API and then multiple different
methods used to construct callables for the IP_Getter API.

These IP_Getter callables contact different websites that simply return the
user's public IP address as a String. Thus, the ip address of a machine could
be gained by the following:
```java
API getIp = IP_Getter.factory().create();
Optional<Response> response = getIP.call(IP_Getter.callables.IP_AWS());
String ip = response.map(responseObj -> responseObj.getData()).orElse("N/A");
```
Note!
In the above code I have only user the IP_AWS callable, which could be
switched out for any other callable found from IP_Getter.callables.

## IPService

IPService is basically an extension of IP_Getter as it simply is a singleton
that goes through all the different defined callables in IP_Getter.callables
and sets the IP address to the first valid result (can be obtained with the
getIP() method).

If in need of a recalculation of the machine IP, the solveIP() method can be
called.
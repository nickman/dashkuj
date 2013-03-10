dashkuj
=======

A Java API for Dashku. 

Dependencies
============

 - Netty
 - Morphia
 - Slf4J
 - Gson

Operations Implemented
======================
 - Get Dashboards
 - Get Dashboard
 - Create Dashboard
 - Update Dashboard
 - Delete Dashboard
 - Create Widget
 - Update Widget
 - Delete Widget
 - Transmit to Widget

Example Dashboard & Widget Operations:
======================================

```java
HTTPDashku http = new HTTPDashku("31e3b92f-dcf3-468d-bd97-53327c6786a9", "dashku", 3000);
Dashboard d = new Dashboard();
d.setName("JVM Monitor");
d.setCss("#salesNumber {\n font-weight: bold; \n font-size: 24pt;\n}");
d.setScreenWidth(ScreenWidth.fluid);
http.createDashboard(d);
log("Created new dashboard:" + d);
Widget w = new Widget();
w.setCss("#heapSavant {\n font-weight: bold; \n font-size: 24pt;\n}");
w.setHeight(150);
w.setWidth(300);
w.setHtml("<div id=\"heapSavant\"></div>");
w.setJson("{max=100,alloc=60,used=30}");
w.setName("HeapSpace");
w.setScriptType(ScriptType.javascript);
w.setScript(new String(URLHelper.getBytesFromURL(URLHelper.toURL(new File("src/test/resources/scripts/js/newWidgetScript.js")))));
http.createWidget(d.getId(), w);
log("Created new widget:" + w);

log("Deleting Widget");
String deletedWid = http.deleteWidget(d.getId(), w.getId());
log("Deleted Widget [" + deletedWid + "]");


log("Updating Dashboard");
d.setName("JVM2 Monitor");
http.updateDashboard(d);
log("Updated Dashboard:[" + d.getName() + "]");


log("Deleting Dashboard");
String deletedDid = http.deleteDashboard(d.getId());
log("Deleted Dashboard:[" + deletedDid + "]");
```

Example Transmission:
======================================
This is the simplified/manual version. A transmission builder is under development, as well as the ability to execute the downloaded Dashku Node.js transmission scripts.
That is to say, this will get less verbose.

```java
HTTPDashku http = new HTTPDashku("31e3b92f-dcf3-468d-bd97-53327c6786a9", "dashku", 3000);
JsonObject transmission = new JsonObject();
JsonObject colours = new JsonObject();
transmission.addProperty("amount", 50);
transmission.addProperty("total", 100);
colours.addProperty("amount", "#51FF00");
colours.addProperty("total", "#FF002B");
transmission.add("colours", colours);
http.transmit("513b768ea03ed86f05000058", transmission);
```


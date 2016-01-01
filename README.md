## opencpu-r-executor

[![Build Status](https://travis-ci.org/onetapbeyond/opencpu-r-executor.svg?branch=master)](https://travis-ci.org/onetapbeyond/opencpu-r-executor)

The opencpu-r-executor library offers a lightweight solution (~14 kB jar) for integrating R analytics executed on the [OpenCPU server](https://www.opencpu.org) into any application running on the JVM. This library is ideally suited for integrating R analytics into new or existing server, middleware and cluster computing solutions. The Javadoc for this library is available [here](http://www.javadoc.io/doc/io.onetapbeyond/opencpu-r-executor/).

The OpenCPU server provides an execution environment for scientific
computing, reproducible research and data analysis based on R. To learn
more about the OpenCPU server, see [here](https://www.opencpu.org).  To learn
more about the general capabilities of the R programming language and
environment for statistical computing, see [here](https://www.r-project.org/about.html).

### Gradle Dependency

```
compile 'io.onetapbeyond:opencpu-r-executor:1.1'
```

### Maven Dependency

```
<dependency>
  <groupId>io.onetapbeyond</groupId>
  <artifactId>opencpu-r-executor</artifactId>
  <version>1.1</version>
</dependency>
```

### OpenCPU Integration

- Simplified R code execution using [OCPUTask](http://www.javadoc.io/doc/io.onetapbeyond/opencpu-r-executor/), no boilerplate [java.net](http://docs.oracle.com/javase/8/docs/api/java/net/package-summary.html) code required.
- OpenCPU, CRAN, GitHub, Gist and Bioconductor R package support.
- Distributed cluster environment support through automatic [OCPUTask](http://www.javadoc.io/doc/io.onetapbeyond/opencpu-r-executor/) and [OCPUResult](http://www.javadoc.io/doc/io.onetapbeyond/opencpu-r-executor/) serialization.


### Usage

When working with this library the basic programming model is as follows:

```
import io.onetapbeyond.opencpu.r.executor.*;

OCPUTask rTask = OCPU.R()
					 .pkg(pName)
					 .function(fName)
					 .input(fInput)
					 .library();
OCPUResult rResult = rTask.execute();
Map rOutput = rResult.output();
```

### Example Usage

The following code snippet demonstrates the execution of the R stats::rnorm function:

```
import io.onetapbeyond.opencpu.r.executor.*;

Map data = new HashMap();
data.put("n", 10);
data.put("mean", 5);
OCPUTask rTask = OCPU.R()
					 .pkg("stats")
					 .function("rnorm")
					 .input(data)
					 .library();
OCPUResult rResult = rTask.execute();
Map rOutput = rResult.output();
```

The following code snippet demonstrates the execution of the R dpu.mobility::geodistance function by [openmhealth on github](https://github.com/openmhealth/dpu.mobility):

```
import io.onetapbeyond.opencpu.r.executor.*;

Map data = new HashMap();
data.put("long", Arrays.asList(-74.0064,-118.2430,-74.0064));
data.put("lat", Arrays.asList(40.7142,34.0522,40.7142));
OCPUTask rTask = OCPU.R()
                     .user("openmhealth")
                     .pkg("dpu.mobility")
                     .function("geodistance")
                     .input(data)
                     .github();
OCPUResult rResult = rTask.execute();
Map rOutput = rResult.output();
```


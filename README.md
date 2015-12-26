## opencpu-r-executor

[![Build Status](https://travis-ci.org/onetapbeyond/opencpu-r-executor.svg?branch=master)](https://travis-ci.org/onetapbeyond/opencpu-r-executor)

The opencpu-r-executor library offers a lightweight solution (~16 kB jar) for integrating R analytics executed on the [OpenCPU server](https://www.opencpu.org) into any application running on the JVM. This library is ideally suited for integrating R analytics into new or existing server, middleware and cluster computing solutions. The Javadoc for this library is available [here](http://www.javadoc.io/doc/io.onetapbeyond/opencpu-r-executor/).

The OpenCPU server provides an execution environment for scientific
computing, reproducible research and data analysis based on R. To learn
more about the OpenCPU server, see [here](https://www.opencpu.org).  To learn
more about the general capabilities of the R programming language and
environment for statistical computing, see [here](https://www.r-project.org/about.html).

### Gradle Dependency

```
compile 'io.onetapbeyond:opencpu-r-executor:1.0'
```

### Maven Dependency

```
<dependency>
  <groupId>io.onetapbeyond</groupId>
  <artifactId>opencpu-r-executor</artifactId>
  <version>1.0</version>
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

OCPUTask oTask = OCPU.R()
					 .pkg(pName)
					 .function(fName)
					 .input(fInput)
					 .library();
OCPUResult oResult = oTask.execute();
Map oData = oResult.data();
```

### Example Usage

The following code snippet demonstrates the execution of the R stats::rnorm function:

```
import io.onetapbeyond.opencpu.r.executor.*;

Map data = new HashMap();
data.put("n", 10);
data.put("mean", 5);
OCPUTask oTask = OCPU.R()
					 .pkg("stats")
					 .function("rnorm")
					 .input(data)
					 .library();
OCPUResult oResult = oTask.execute();
Map oData = oResult.data();
```

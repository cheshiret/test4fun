### Instruction to use testruner

#### It's designed to a distributed test runner.

#### first install RPCDaemon.jar into slave machine.

#### Then set up queue server and database

#### use Active MQ as the queue server
http://activemq.apache.org/getting-started.html#GettingStarted-TestingtheInstallationTestingtheInstallation

Windows Binary Installation
This procedure explains how to download and install the binary distribution on a Windows system.
1. Download the latest release
(see Download -> "The latest stable release" -> "apache-activemq-x.x.x-bin.zip")
2. Extract the files from the ZIP file into a directory of your choice.
3. Proceed to the #Starting ActiveMQ section of this document.
	1. cd [activemq_install_dir]
	2. cd bin
	3. Type activemq start
4. Following start-up, go to the #Testing the Installation section of this document.
	1. http://127.0.0.1:8161/admin/ admin/admin
	• Navigate to "Queues"
	• Add a queue name and click create

Send test message by klicking on "Send to"
#### Run test with cmd or shell.

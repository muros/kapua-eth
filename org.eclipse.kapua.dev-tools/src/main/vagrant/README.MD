# Manually Running the Kapua Dev-Machine
The development machine contains the external services needed for running a full instance of Kapua.

* ActiveMQ
* H2 Database
* Elasticsearch
* Tomcat

The development machine is a Vagrant machine that is based on a local vagrant box named trusty64/kapua-dev-box-0.x (the Kapua Dev-Box) which is in turn based on a ubuntu/trusty64 box. Before running the development machine it's necessary to create the Kapua Dev-Box.

**Note:** Before proceeding, check that Vagrant is installed in your PC, otherwise install it. In order to run Vagrant machines, VirtualBox or other virtualization software supported by Vagrant needs also to be installed.

## Creating the Kapua Dev-Box

Set the following as the current dir:

* <kapua_prj_home>/org.eclipse.kapua.dev-tools/src/main/vagrant/

Within the named directory, among other objects, there should be the following items:

* create_kapua_box.sh (bash script)
* Kapua-Box-Vagrantfile (Vagrant file for Dev-Box)
* Vagrantfile (Vagrant file for Dev-Machine)

You can check if there's already a Kapua Dev-Box installed with the command:

* $ vagrant box list

If the Dev-Box is not present it can be created running the script:

* $ sh ./create_kapua_box.sh

If the box is already present but needs to be replaced, then you have to first remove it from the local repository and then create it again:

1. $ vagrant box remove trusty64/kapua-dev-box-0.x
2. $ sh ./create_kapua_box.sh

The script uses the Vagrant machine definition in Kapua-Box-Vagrantfile to create a local box named:

* trusty64/kapua-dev-box-0.x

the box is added in the local Vagrant repository. 

**Note:** The creation of the vagrant box requires a connection to the internet since it downloads live the software for running the services. Due to the download, the box creation phase can take a some time.

## Running the Kapua Dev-Box

Once the box creation has been completed, you can run the development machine by executing the command:

* $ vagrant up

The machine starts and the services are launched. The machine is assigned the following IP:

* 192.168.33.10

The ports exposed are documented per service within the Vagrant file.





 